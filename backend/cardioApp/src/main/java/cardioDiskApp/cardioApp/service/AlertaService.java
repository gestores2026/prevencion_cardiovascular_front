package cardioDiskApp.cardioApp.service;

import cardioDiskApp.cardioApp.dto.response.AlertaResponseDTO;
import cardioDiskApp.cardioApp.entity.Alerta;
import cardioDiskApp.cardioApp.entity.Usuario;
import cardioDiskApp.cardioApp.repository.AlertaRepository;
import cardioDiskApp.cardioApp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final UsuarioRepository usuarioRepository;

    public List<AlertaResponseDTO> listarAlertasActivas() {
        return alertaRepository.findByAtendidaFalseOrderByFechaAlertaDesc()
                .stream()
                .map(this::mapearAlerta)
                .collect(Collectors.toList());
    }

    public List<AlertaResponseDTO> listarAlertasPaciente(Long pacienteId) {
        Usuario paciente = usuarioRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        return alertaRepository.findByPacienteOrderByFechaAlertaDesc(paciente)
                .stream()
                .map(this::mapearAlerta)
                .collect(Collectors.toList());
    }

    public AlertaResponseDTO atenderAlerta(Long alertaId) {
        Alerta alerta = alertaRepository.findById(alertaId)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada"));

        alerta.setAtendida(true);
        alertaRepository.save(alerta);
        return mapearAlerta(alerta);
    }

    public long contarAlertasActivas() {
        return alertaRepository.countByAtendidaFalse();
    }

    private AlertaResponseDTO mapearAlerta(Alerta a) {
        return AlertaResponseDTO.builder()
                .id(a.getId())
                .pacienteId(a.getPaciente().getId())
                .pacienteNombre(a.getPaciente().getNombre())
                .tipo(a.getTipo().name())
                .descripcion(a.getDescripcion())
                .atendida(a.getAtendida())
                .fechaAlerta(a.getFechaAlerta())
                .build();
    }
}
