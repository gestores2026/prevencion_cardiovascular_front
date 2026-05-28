package cardioDiskApp.cardioApp.controller;

import cardioDiskApp.cardioApp.dto.request.MensajeRequestDTO;
import cardioDiskApp.cardioApp.dto.response.MensajeResponseDTO;
import cardioDiskApp.cardioApp.service.MensajeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mensajes")
@RequiredArgsConstructor
public class MensajeController {

    private final MensajeService mensajeService;

    @PostMapping("/enviar/{emisorId}")
    public ResponseEntity<MensajeResponseDTO> enviar(
            @PathVariable Long emisorId,
            @Valid @RequestBody MensajeRequestDTO request) {
        return ResponseEntity.ok(mensajeService.enviar(emisorId, request));
    }

    @GetMapping("/conversacion/{usuarioA}/{usuarioB}")
    public ResponseEntity<List<MensajeResponseDTO>> conversacion(
            @PathVariable Long usuarioA,
            @PathVariable Long usuarioB) {
        return ResponseEntity.ok(mensajeService.obtenerConversacion(usuarioA, usuarioB));
    }

    @GetMapping("/no-leidos/{receptorId}")
    public ResponseEntity<Map<String, Long>> noLeidos(@PathVariable Long receptorId) {
        return ResponseEntity.ok(Map.of("total", mensajeService.contarNoLeidos(receptorId)));
    }
}