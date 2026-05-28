package cardioDiskApp.cardioApp.controller;

import cardioDiskApp.cardioApp.dto.request.ActualizarUsuarioRequestDTO;
import cardioDiskApp.cardioApp.dto.response.UsuarioResponseDTO;
import cardioDiskApp.cardioApp.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/api/medico/pacientes")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<List<UsuarioResponseDTO>> listarPacientes() {
        return ResponseEntity.ok(usuarioService.listarPacientes());
    }

    @GetMapping("/api/medico/mis-pacientes/{medicoId}")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<List<UsuarioResponseDTO>> misPacientes(@PathVariable Long medicoId) {
        return ResponseEntity.ok(usuarioService.listarPacientesPorMedico(medicoId));
    }

    @GetMapping("/api/medico/pacientes/buscar")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarPacientes(
            @RequestParam String busqueda) {
        return ResponseEntity.ok(usuarioService.buscarPacientes(busqueda));
    }

    @GetMapping("/api/medico/mis-pacientes/{medicoId}/buscar")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarMisPacientes(
            @PathVariable Long medicoId,
            @RequestParam String busqueda) {
        return ResponseEntity.ok(usuarioService.buscarPacientesPorMedico(medicoId, busqueda));
    }

    @GetMapping("/api/medico/pacientes/{id}")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<UsuarioResponseDTO> obtenerPaciente(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PutMapping("/api/medico/asignar-paciente/{pacienteId}/{medicoId}")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<UsuarioResponseDTO> asignarPaciente(
            @PathVariable Long pacienteId,
            @PathVariable Long medicoId) {
        return ResponseEntity.ok(usuarioService.asignarMedico(pacienteId, medicoId));
    }

    @PutMapping("/api/medico/quitar-paciente/{pacienteId}")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<Void> quitarPaciente(@PathVariable Long pacienteId) {
        usuarioService.quitarPaciente(pacienteId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/paciente/perfil/{id}")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<UsuarioResponseDTO> actualizarPerfil(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarUsuarioRequestDTO request) {
        return ResponseEntity.ok(usuarioService.actualizarPerfil(id, request));
    }

    @GetMapping("/api/paciente/perfil/{id}")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<UsuarioResponseDTO> verPerfil(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }
}