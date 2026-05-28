package cardioDiskApp.cardioApp.service;

import cardioDiskApp.cardioApp.dto.request.MensajeRequestDTO;
import cardioDiskApp.cardioApp.dto.response.MensajeResponseDTO;
import cardioDiskApp.cardioApp.entity.Mensaje;
import cardioDiskApp.cardioApp.entity.Usuario;
import cardioDiskApp.cardioApp.repository.MensajeRepository;
import cardioDiskApp.cardioApp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MensajeService {

    private final MensajeRepository mensajeRepository;
    private final UsuarioRepository usuarioRepository;

    public MensajeResponseDTO enviar(Long emisorId, MensajeRequestDTO request) {
        Usuario emisor = usuarioRepository.findById(emisorId)
                .orElseThrow(() -> new RuntimeException("Emisor no encontrado"));
        Usuario receptor = usuarioRepository.findById(request.getReceptorId())
                .orElseThrow(() -> new RuntimeException("Receptor no encontrado"));

        Mensaje mensaje = Mensaje.builder()
                .emisor(emisor)
                .receptor(receptor)
                .contenido(request.getContenido())
                .build();

        mensajeRepository.save(mensaje);
        return mapear(mensaje);
    }

    public List<MensajeResponseDTO> obtenerConversacion(Long usuarioA, Long usuarioB) {
        List<Mensaje> mensajes = mensajeRepository.findConversacion(usuarioA, usuarioB);

        mensajes.stream()
                .filter(m -> m.getReceptor().getId().equals(usuarioA) && !m.getLeido())
                .forEach(m -> {
                    m.setLeido(true);
                    mensajeRepository.save(m);
                });

        return mensajes.stream().map(this::mapear).collect(Collectors.toList());
    }

    public long contarNoLeidos(Long receptorId) {
        Usuario receptor = usuarioRepository.findById(receptorId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return mensajeRepository.countByReceptorAndLeidoFalse(receptor);
    }

    private MensajeResponseDTO mapear(Mensaje m) {
        return MensajeResponseDTO.builder()
                .id(m.getId())
                .emisorId(m.getEmisor().getId())
                .emisorNombre(m.getEmisor().getNombre())
                .receptorId(m.getReceptor().getId())
                .receptorNombre(m.getReceptor().getNombre())
                .contenido(m.getContenido())
                .leido(m.getLeido())
                .fechaEnvio(m.getFechaEnvio())
                .build();
    }
}