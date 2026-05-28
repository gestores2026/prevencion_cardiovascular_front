package cardioDiskApp.cardioApp.service;

import cardioDiskApp.cardioApp.dto.response.EstadisticasResponseDTO;
import cardioDiskApp.cardioApp.emuns.NivelRiesgo;
import cardioDiskApp.cardioApp.emuns.Rol;
import cardioDiskApp.cardioApp.entity.TestFramingham;
import cardioDiskApp.cardioApp.entity.Usuario;
import cardioDiskApp.cardioApp.repository.TestFraminghamRepository;
import cardioDiskApp.cardioApp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EstadisticasService {

    private final UsuarioRepository usuarioRepository;
    private final TestFraminghamRepository testRepository;

    public EstadisticasResponseDTO obtenerEstadisticas() {
        Map<String, Double> riesgoPorEdad = new LinkedHashMap<>();
        testRepository.promedioRiesgoPorEdad().forEach(row -> {
            int edad = ((Number) row[0]).intValue();
            double promedio = ((Number) row[1]).doubleValue();
            String grupo = clasificarGrupoEdad(edad);
            riesgoPorEdad.merge(grupo, promedio, (a, b) -> Math.round((a + b) / 2.0 * 10) / 10.0);
        });

        return EstadisticasResponseDTO.builder()
                .totalPacientes(usuarioRepository.countByRol(Rol.PACIENTE))
                .pacientesRiesgoAlto(testRepository.countByNivelRiesgo(NivelRiesgo.ALTO))
                .pacientesRiesgoModerado(testRepository.countByNivelRiesgo(NivelRiesgo.MODERADO))
                .pacientesRiesgoBajo(testRepository.countByNivelRiesgo(NivelRiesgo.BAJO))
                .promedioRiesgo(testRepository.promedioRiesgoGeneral())
                .pacientesFumadores(testRepository.countPacientesFumadores())
                .totalTestsEsteMes(testRepository.countTestsEsteMes())
                .riesgoPorGrupoEdad(riesgoPorEdad)
                .build();
    }

    public EstadisticasResponseDTO obtenerEstadisticasPorMedico(Long medicoId) {
        List<Usuario> pacientes = usuarioRepository.findByMedicoId(medicoId);
        long total = pacientes.size();

        long alto = pacientes.stream().filter(p -> {
            Optional<TestFramingham> t = testRepository.findTopByPacienteOrderByFechaTestDesc(p);
            return t.isPresent() && t.get().getNivelRiesgo() == NivelRiesgo.ALTO;
        }).count();

        long moderado = pacientes.stream().filter(p -> {
            Optional<TestFramingham> t = testRepository.findTopByPacienteOrderByFechaTestDesc(p);
            return t.isPresent() && t.get().getNivelRiesgo() == NivelRiesgo.MODERADO;
        }).count();

        long bajo = pacientes.stream().filter(p -> {
            Optional<TestFramingham> t = testRepository.findTopByPacienteOrderByFechaTestDesc(p);
            return t.isPresent() && t.get().getNivelRiesgo() == NivelRiesgo.BAJO;
        }).count();

        double promedio = pacientes.stream()
                .mapToDouble(p -> testRepository
                        .findTopByPacienteOrderByFechaTestDesc(p)
                        .map(TestFramingham::getPorcentajeRiesgo)
                        .orElse(0.0))
                .average()
                .orElse(0.0);

        long fumadores = pacientes.stream().filter(p -> {
            Optional<TestFramingham> t = testRepository.findTopByPacienteOrderByFechaTestDesc(p);
            return t.isPresent() && Boolean.TRUE.equals(t.get().getFumador());
        }).count();

        return EstadisticasResponseDTO.builder()
                .totalPacientes(total)
                .pacientesRiesgoAlto(alto)
                .pacientesRiesgoModerado(moderado)
                .pacientesRiesgoBajo(bajo)
                .promedioRiesgo(Math.round(promedio * 10.0) / 10.0)
                .pacientesFumadores(fumadores)
                .totalTestsEsteMes(testRepository.countTestsEsteMes())
                .riesgoPorGrupoEdad(new LinkedHashMap<>())
                .build();
    }

    private String clasificarGrupoEdad(int edad) {
        if (edad <= 40) return "30-40";
        if (edad <= 50) return "41-50";
        if (edad <= 60) return "51-60";
        return "61+";
    }
}