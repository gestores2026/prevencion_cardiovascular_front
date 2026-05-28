package cardioDiskApp.cardioApp.service;

import cardioDiskApp.cardioApp.dto.request.ActualizarUsuarioRequestDTO;
import cardioDiskApp.cardioApp.dto.response.UsuarioResponseDTO;
import cardioDiskApp.cardioApp.emuns.Rol;
import cardioDiskApp.cardioApp.entity.TestFramingham;
import cardioDiskApp.cardioApp.entity.Usuario;
import cardioDiskApp.cardioApp.repository.TestFraminghamRepository;
import cardioDiskApp.cardioApp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TestFraminghamRepository testRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioResponseDTO> listarPacientes() {
        return usuarioRepository.findByRol(Rol.PACIENTE)
                .stream()
                .map(this::mapearUsuario)
                .collect(Collectors.toList());
    }

    public List<UsuarioResponseDTO> listarPacientesPorMedico(Long medicoId) {
        return usuarioRepository.findByMedicoId(medicoId)
                .stream()
                .map(this::mapearUsuario)
                .collect(Collectors.toList());
    }

    public List<UsuarioResponseDTO> buscarPacientes(String busqueda) {
        return usuarioRepository.buscarPacientes(busqueda)
                .stream()
                .map(this::mapearUsuario)
                .collect(Collectors.toList());
    }

    public List<UsuarioResponseDTO> buscarPacientesPorMedico(Long medicoId, String busqueda) {
        return usuarioRepository.buscarPacientesPorMedico(medicoId, busqueda)
                .stream()
                .map(this::mapearUsuario)
                .collect(Collectors.toList());
    }

    public UsuarioResponseDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return mapearUsuario(usuario);
    }

    public UsuarioResponseDTO asignarMedico(Long pacienteId, Long medicoId) {
        Usuario paciente = usuarioRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        Usuario medico = usuarioRepository.findById(medicoId)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));
        paciente.setMedico(medico);
        usuarioRepository.save(paciente);
        return mapearUsuario(paciente);
    }

    public UsuarioResponseDTO actualizarPerfil(Long id, ActualizarUsuarioRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (request.getNombre()    != null) usuario.setNombre(request.getNombre());
        if (request.getDocumento() != null) usuario.setDocumento(request.getDocumento());
        if (request.getEdad()      != null) usuario.setEdad(request.getEdad());
        if (request.getSexo()      != null) usuario.setSexo(request.getSexo());
        if (request.getPeso()      != null) usuario.setPeso(request.getPeso());
        if (request.getTalla()     != null) usuario.setTalla(request.getTalla());

        if (request.getPasswordNueva() != null && !request.getPasswordNueva().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.getPasswordNueva()));
        }

        usuarioRepository.save(usuario);
        return mapearUsuario(usuario);
    }

    public void quitarPaciente(Long pacienteId) {
        Usuario paciente = usuarioRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        paciente.setMedico(null);
        usuarioRepository.save(paciente);
    }

    private UsuarioResponseDTO mapearUsuario(Usuario u) {
        Optional<TestFramingham> ultimoTest =
                testRepository.findTopByPacienteOrderByFechaTestDesc(u);

        return UsuarioResponseDTO.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .correo(u.getCorreo())
                .documento(u.getDocumento())
                .edad(u.getEdad())
                .sexo(u.getSexo())
                .peso(u.getPeso())
                .talla(u.getTalla())
                .rol(u.getRol().name())
                .fechaRegistro(u.getFechaRegistro())
                .medicoId(u.getMedico() != null ? u.getMedico().getId() : null)
                .ultimoRiesgo(ultimoTest.map(TestFramingham::getPorcentajeRiesgo).orElse(null))
                .medicoNombre(u.getMedico() != null ? u.getMedico().getNombre() : null)
                .ultimoNivelRiesgo(ultimoTest.map(t -> t.getNivelRiesgo().name()).orElse(null))
                .build();
    }
}