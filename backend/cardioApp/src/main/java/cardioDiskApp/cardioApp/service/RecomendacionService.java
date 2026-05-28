package cardioDiskApp.cardioApp.service;

import cardioDiskApp.cardioApp.dto.request.RecomendacionRequestDTO;
import cardioDiskApp.cardioApp.dto.response.RecomendacionResponseDTO;
import cardioDiskApp.cardioApp.entity.Recomendacion;
import cardioDiskApp.cardioApp.entity.Usuario;
import cardioDiskApp.cardioApp.repository.RecomendacionRepository;
import cardioDiskApp.cardioApp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecomendacionService {

    private final RecomendacionRepository recomendacionRepository;
    private final UsuarioRepository usuarioRepository;

    public RecomendacionResponseDTO enviarRecomendacion(Long medicoId,
                                                        RecomendacionRequestDTO request) {
        Usuario medico = usuarioRepository.findById(medicoId)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));

        Usuario paciente = usuarioRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        Recomendacion rec = Recomendacion.builder()
                .medico(medico)
                .paciente(paciente)
                .tipo(request.getTipo())
                .mensaje(request.getMensaje())
                .build();

        recomendacionRepository.save(rec);
        return mapearRecomendacion(rec);
    }

    public List<RecomendacionResponseDTO> listarPorPaciente(Long pacienteId) {
        return recomendacionRepository.findByPacienteId(pacienteId)
                .stream()
                .map(this::mapearRecomendacion)
                .collect(Collectors.toList());
    }

    public List<RecomendacionResponseDTO> listarEnviadasPorMedico(Long medicoId) {
        Usuario medico = usuarioRepository.findById(medicoId)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));

        return recomendacionRepository.findByMedicoOrderByFechaEnvioDesc(medico)
                .stream()
                .map(this::mapearRecomendacion)
                .collect(Collectors.toList());
    }

    private RecomendacionResponseDTO mapearRecomendacion(Recomendacion r) {
        return RecomendacionResponseDTO.builder()
                .id(r.getId())
                .medicoNombre(r.getMedico().getNombre())
                .pacienteNombre(r.getPaciente().getNombre())
                .tipo(r.getTipo())
                .mensaje(r.getMensaje())
                .fechaEnvio(r.getFechaEnvio())
                .build();
    }
}
