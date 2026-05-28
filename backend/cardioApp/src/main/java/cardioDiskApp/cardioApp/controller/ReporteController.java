package cardioDiskApp.cardioApp.controller;

import cardioDiskApp.cardioApp.dto.response.TestResultadoResponseDTO;
import cardioDiskApp.cardioApp.dto.response.UsuarioResponseDTO;
import cardioDiskApp.cardioApp.dto.response.RecomendacionResponseDTO;
import cardioDiskApp.cardioApp.service.RecomendacionService;
import cardioDiskApp.cardioApp.service.TestFraminghamService;
import cardioDiskApp.cardioApp.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medico/reportes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReporteController {

    private final UsuarioService usuarioService;
    private final TestFraminghamService testService;
    private final RecomendacionService recomendacionService;

    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<Map<String, Object>> datosPaciente(@PathVariable Long pacienteId) {
        UsuarioResponseDTO paciente = usuarioService.obtenerPorId(pacienteId);
        List<TestResultadoResponseDTO> tests = testService.historialPaciente(pacienteId);
        List<RecomendacionResponseDTO> recomendaciones = recomendacionService.listarPorPaciente(pacienteId);

        return ResponseEntity.ok(Map.of(
                "paciente", paciente,
                "tests", tests,
                "recomendaciones", recomendaciones
        ));
    }

    @GetMapping("/general")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<Map<String, Object>> datosGenerales() {
        return ResponseEntity.ok(Map.of(
                "pacientes", usuarioService.listarPacientes()
        ));
    }
}