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
public class AlertaResponseDTO {
    private Long id;
    private Long pacienteId;
    private String pacienteNombre;
    private String tipo;
    private String descripcion;
    private Boolean atendida;
    private LocalDateTime fechaAlerta;
}
