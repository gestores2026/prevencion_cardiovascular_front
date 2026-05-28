package cardioDiskApp.cardioApp.controller;

import cardioDiskApp.cardioApp.dto.request.TestFraminghamRequestDTO;
import cardioDiskApp.cardioApp.dto.response.TestResultadoResponseDTO;
import cardioDiskApp.cardioApp.emuns.NivelRiesgo;
import cardioDiskApp.cardioApp.service.TestFraminghamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TestFraminghamController {

    private final TestFraminghamService testService;

    @PostMapping("/api/paciente/test/{pacienteId}")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<TestResultadoResponseDTO> realizarTest(
            @PathVariable Long pacienteId,
            @Valid @RequestBody TestFraminghamRequestDTO request) {
        return ResponseEntity.ok(testService.realizarTest(pacienteId, request));
    }

    @GetMapping("/api/paciente/test/historial/{pacienteId}")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<List<TestResultadoResponseDTO>> historialPaciente(
            @PathVariable Long pacienteId) {
        return ResponseEntity.ok(testService.historialPaciente(pacienteId));
    }

    @GetMapping("/api/medico/test/historial/{pacienteId}")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<List<TestResultadoResponseDTO>> historialPacienteMedico(
            @PathVariable Long pacienteId) {
        return ResponseEntity.ok(testService.historialPaciente(pacienteId));
    }

    @GetMapping("/api/medico/test/nivel/{nivel}")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<List<TestResultadoResponseDTO>> listarPorNivel(
            @PathVariable NivelRiesgo nivel) {
        return ResponseEntity.ok(testService.listarPorNivel(nivel));
    }
}