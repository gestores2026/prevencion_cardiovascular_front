package cardioDiskApp.cardioApp.controller;

import cardioDiskApp.cardioApp.dto.request.RecomendacionRequestDTO;
import cardioDiskApp.cardioApp.dto.response.RecomendacionResponseDTO;
import cardioDiskApp.cardioApp.service.RecomendacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RecomendacionController {

    private final RecomendacionService recomendacionService;

    @PostMapping("/api/medico/recomendaciones/{medicoId}")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<RecomendacionResponseDTO> enviar(
            @PathVariable Long medicoId,
            @Valid @RequestBody RecomendacionRequestDTO request) {
        return ResponseEntity.ok(recomendacionService.enviarRecomendacion(medicoId, request));
    }

    @GetMapping("/api/medico/recomendaciones/enviadas/{medicoId}")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<List<RecomendacionResponseDTO>> enviadas(
            @PathVariable Long medicoId) {
        return ResponseEntity.ok(recomendacionService.listarEnviadasPorMedico(medicoId));
    }

    @GetMapping("/api/paciente/recomendaciones/{pacienteId}")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<List<RecomendacionResponseDTO>> recibidas(
            @PathVariable Long pacienteId) {
        return ResponseEntity.ok(recomendacionService.listarPorPaciente(pacienteId));
    }
}
