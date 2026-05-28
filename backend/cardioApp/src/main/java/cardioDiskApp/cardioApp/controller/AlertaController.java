package cardioDiskApp.cardioApp.controller;

import cardioDiskApp.cardioApp.dto.response.AlertaResponseDTO;
import cardioDiskApp.cardioApp.service.AlertaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AlertaController {

    private final AlertaService alertaService;

    @GetMapping("/api/medico/alertas")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<List<AlertaResponseDTO>> listarAlertasActivas() {
        return ResponseEntity.ok(alertaService.listarAlertasActivas());
    }

    @GetMapping("/api/medico/alertas/count")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<Map<String, Long>> contarAlertas() {
        return ResponseEntity.ok(Map.of("total", alertaService.contarAlertasActivas()));
    }

    @PutMapping("/api/medico/alertas/{id}/atender")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<AlertaResponseDTO> atenderAlerta(@PathVariable Long id) {
        return ResponseEntity.ok(alertaService.atenderAlerta(id));
    }

    @GetMapping("/api/paciente/alertas/{pacienteId}")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<List<AlertaResponseDTO>> alertasPaciente(
            @PathVariable Long pacienteId) {
        return ResponseEntity.ok(alertaService.listarAlertasPaciente(pacienteId));
    }
}
