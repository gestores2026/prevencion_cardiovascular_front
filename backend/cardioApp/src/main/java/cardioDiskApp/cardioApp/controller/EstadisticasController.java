package cardioDiskApp.cardioApp.controller;

import cardioDiskApp.cardioApp.dto.response.EstadisticasResponseDTO;
import cardioDiskApp.cardioApp.service.EstadisticasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medico/estadisticas")
@RequiredArgsConstructor
public class EstadisticasController {

    private final EstadisticasService estadisticasService;

    @GetMapping
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<EstadisticasResponseDTO> obtener() {
        return ResponseEntity.ok(estadisticasService.obtenerEstadisticas());
    }

    @GetMapping("/{medicoId}")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<EstadisticasResponseDTO> obtenerPorMedico(@PathVariable Long medicoId) {
        return ResponseEntity.ok(estadisticasService.obtenerEstadisticasPorMedico(medicoId));
    }
}