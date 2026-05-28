package cardioDiskApp.cardioApp.controller;

import cardioDiskApp.cardioApp.dto.request.LoginRequestDTO;
import cardioDiskApp.cardioApp.dto.request.RegisterRequestDTO;
import cardioDiskApp.cardioApp.dto.response.AuthResponseDTO;
import cardioDiskApp.cardioApp.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registrar")
    public ResponseEntity<AuthResponseDTO> registrar(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.registrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
