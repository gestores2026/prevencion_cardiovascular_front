package cardioDiskApp.cardioApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecomendacionResponseDTO {
    private Long id;
    private String medicoNombre;
    private String pacienteNombre;
    private String tipo;
    private String mensaje;
    private LocalDateTime fechaEnvio;
}
