package cardioDiskApp.cardioApp.service;

import cardioDiskApp.cardioApp.dto.request.TestFraminghamRequestDTO;
import cardioDiskApp.cardioApp.dto.response.TestResultadoResponseDTO;
import cardioDiskApp.cardioApp.emuns.NivelRiesgo;
import cardioDiskApp.cardioApp.emuns.TipoAlerta;
import cardioDiskApp.cardioApp.entity.Alerta;
import cardioDiskApp.cardioApp.entity.TestFramingham;
import cardioDiskApp.cardioApp.entity.Usuario;
import cardioDiskApp.cardioApp.repository.AlertaRepository;
import cardioDiskApp.cardioApp.repository.TestFraminghamRepository;
import cardioDiskApp.cardioApp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestFraminghamService {

    private final TestFraminghamRepository testRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlertaRepository alertaRepository;

    public TestResultadoResponseDTO realizarTest(Long pacienteId,
                                                 TestFraminghamRequestDTO request) {
        Usuario paciente = usuarioRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        double riesgo = calcularFramingham(request);
        NivelRiesgo nivel = determinarNivel(riesgo);

        TestFramingham test = TestFramingham.builder()
                .paciente(paciente)
                .edad(request.getEdad())
                .sexo(request.getSexo())
                .colesterolTotal(request.getColesterolTotal())
                .colesterolHdl(request.getColesterolHdl())
                .presionSistolica(request.getPresionSistolica())
                .tratamientoHipertension(request.getTratamientoHipertension())
                .fumador(request.getFumador())
                .imc(request.getImc())
                .porcentajeRiesgo(riesgo)
                .nivelRiesgo(nivel)
                .build();

        testRepository.save(test);
        generarAlertas(paciente, request, riesgo);

        return mapearTest(test);
    }

    public List<TestResultadoResponseDTO> historialPaciente(Long pacienteId) {
        Usuario paciente = usuarioRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        return testRepository.findByPacienteOrderByFechaTestDesc(paciente)
                .stream()
                .map(this::mapearTest)
                .collect(Collectors.toList());
    }

    public List<TestResultadoResponseDTO> listarPorNivel(NivelRiesgo nivel) {
        return testRepository.findByNivelRiesgo(nivel)
                .stream()
                .map(this::mapearTest)
                .collect(Collectors.toList());
    }

    private double calcularFramingham(TestFraminghamRequestDTO r) {
        int puntos = 0;

        int edad = r.getEdad();
        if      (edad <= 34) puntos += 0;
        else if (edad <= 39) puntos += 2;
        else if (edad <= 44) puntos += 5;
        else if (edad <= 49) puntos += 6;
        else if (edad <= 54) puntos += 8;
        else if (edad <= 59) puntos += 10;
        else if (edad <= 64) puntos += 11;
        else if (edad <= 69) puntos += 12;
        else                 puntos += 13;

        double col = r.getColesterolTotal();
        if      (col < 160) puntos += 0;
        else if (col < 200) puntos += 1;
        else if (col < 240) puntos += 2;
        else if (col < 280) puntos += 3;
        else                puntos += 4;

        double hdl = r.getColesterolHdl();
        if      (hdl >= 60) puntos -= 1;
        else if (hdl >= 50) puntos += 0;
        else if (hdl >= 45) puntos += 1;
        else if (hdl >= 35) puntos += 2;
        else                puntos += 3;

        double pas = r.getPresionSistolica();
        boolean hta = Boolean.TRUE.equals(r.getTratamientoHipertension());
        if      (pas < 120) puntos += 0;
        else if (pas < 130) puntos += hta ? 1 : 0;
        else if (pas < 140) puntos += hta ? 2 : 1;
        else if (pas < 160) puntos += hta ? 3 : 2;
        else                puntos += hta ? 4 : 3;

        if (Boolean.TRUE.equals(r.getFumador())) puntos += 3;

        int[] tabla = {1,1,1,1,1,2,2,3,4,5,6,8,10,12,16,20,25,30,35,42};
        int idx = Math.min(Math.max(puntos, 0), tabla.length - 1);
        return tabla[idx];
    }

    private NivelRiesgo determinarNivel(double riesgo) {
        if (riesgo < 10)  return NivelRiesgo.BAJO;
        if (riesgo < 20)  return NivelRiesgo.MODERADO;
        return NivelRiesgo.ALTO;
    }

    private void generarAlertas(Usuario paciente,
                                TestFraminghamRequestDTO r,
                                double riesgo) {
        if (r.getPresionSistolica() >= 140
                && !alertaRepository.existsByPacienteAndTipoAndAtendidaFalse(
                paciente, TipoAlerta.PRESION_ALTA)) {
            alertaRepository.save(Alerta.builder()
                    .paciente(paciente)
                    .tipo(TipoAlerta.PRESION_ALTA)
                    .descripcion("Presión sistólica elevada: " + r.getPresionSistolica() + " mmHg")
                    .build());
        }

        if (r.getColesterolTotal() >= 240
                && !alertaRepository.existsByPacienteAndTipoAndAtendidaFalse(
                paciente, TipoAlerta.COLESTEROL_ELEVADO)) {
            alertaRepository.save(Alerta.builder()
                    .paciente(paciente)
                    .tipo(TipoAlerta.COLESTEROL_ELEVADO)
                    .descripcion("Colesterol elevado: " + r.getColesterolTotal() + " mg/dL")
                    .build());
        }

        if (r.getImc() >= 30
                && !alertaRepository.existsByPacienteAndTipoAndAtendidaFalse(
                paciente, TipoAlerta.IMC_PELIGROSO)) {
            alertaRepository.save(Alerta.builder()
                    .paciente(paciente)
                    .tipo(TipoAlerta.IMC_PELIGROSO)
                    .descripcion("IMC en rango de obesidad: " + r.getImc())
                    .build());
        }

        if (Boolean.TRUE.equals(r.getFumador())
                && !alertaRepository.existsByPacienteAndTipoAndAtendidaFalse(
                paciente, TipoAlerta.FUMADOR)) {
            alertaRepository.save(Alerta.builder()
                    .paciente(paciente)
                    .tipo(TipoAlerta.FUMADOR)
                    .descripcion("Paciente fumador activo")
                    .build());
        }

        if (riesgo >= 20
                && !alertaRepository.existsByPacienteAndTipoAndAtendidaFalse(
                paciente, TipoAlerta.RIESGO_ALTO)) {
            alertaRepository.save(Alerta.builder()
                    .paciente(paciente)
                    .tipo(TipoAlerta.RIESGO_ALTO)
                    .descripcion("Riesgo cardiovascular alto: " + riesgo + "%")
                    .build());
        }
    }

    private TestResultadoResponseDTO mapearTest(TestFramingham t) {
        return TestResultadoResponseDTO.builder()
                .id(t.getId())
                .pacienteId(t.getPaciente().getId())
                .pacienteNombre(t.getPaciente().getNombre())
                .edad(t.getEdad())
                .sexo(t.getSexo())
                .colesterolTotal(t.getColesterolTotal())
                .colesterolHdl(t.getColesterolHdl())
                .presionSistolica(t.getPresionSistolica())
                .tratamientoHipertension(t.getTratamientoHipertension())
                .fumador(t.getFumador())
                .imc(t.getImc())
                .porcentajeRiesgo(t.getPorcentajeRiesgo())
                .nivelRiesgo(t.getNivelRiesgo().name())
                .fechaTest(t.getFechaTest())
                .recomendacionesAutomaticas(generarRecomendaciones(t))
                .build();
    }

    private List<String> generarRecomendaciones(TestFramingham t) {
        List<String> recs = new ArrayList<>();
        if (t.getPorcentajeRiesgo() >= 20)
            recs.add("Consulta a tu médico lo antes posible");
        if (Boolean.TRUE.equals(t.getFumador()))
            recs.add("Dejar de fumar reduce tu riesgo hasta un 50%");
        if (t.getColesterolTotal() >= 200)
            recs.add("Reducir consumo de grasas saturadas");
        if (t.getPresionSistolica() >= 130)
            recs.add("Reducir consumo de sodio y monitorear presión arterial");
        if (t.getImc() >= 25)
            recs.add("Mantener un peso saludable con dieta balanceada");
        recs.add("Realizar actividad física moderada al menos 150 min/semana");
        recs.add("Dormir 7-8 horas por noche");
        return recs;
    }
}