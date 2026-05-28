package cardioDiskApp.cardioApp.service;

import cardioDiskApp.cardioApp.dto.request.LoginRequestDTO;
import cardioDiskApp.cardioApp.dto.request.RegisterRequestDTO;
import cardioDiskApp.cardioApp.dto.response.AuthResponseDTO;
import cardioDiskApp.cardioApp.entity.Usuario;
import cardioDiskApp.cardioApp.repository.UsuarioRepository;
import cardioDiskApp.cardioApp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDTO registrar(RegisterRequestDTO request) {
        System.out.println(">>> Registrando: " + request.getCorreo() + " rol: " + request.getRol());

        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }
        if (usuarioRepository.existsByDocumento(request.getDocumento())) {
            throw new RuntimeException("El documento ya está registrado");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .correo(request.getCorreo())
                .password(passwordEncoder.encode(request.getPassword()))
                .documento(request.getDocumento())
                .edad(request.getEdad())
                .sexo(request.getSexo())
                .peso(request.getPeso())
                .talla(request.getTalla())
                .rol(request.getRol())
                .fechaRegistro(LocalDate.now())
                .build();

        System.out.println(">>> Usuario construido: " + usuario.getNombre());

        usuarioRepository.save(usuario);

        System.out.println(">>> Usuario guardado con id: " + usuario.getId());

        String token = jwtService.generarToken(usuario);

        return AuthResponseDTO.builder()
                .token(token)
                .tipo("Bearer")
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol().name())
                .build();
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getCorreo(),
                        request.getPassword()
                )
        );

        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = jwtService.generarToken(usuario);

        return AuthResponseDTO.builder()
                .token(token)
                .tipo("Bearer")
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol().name())
                .build();
    }
}