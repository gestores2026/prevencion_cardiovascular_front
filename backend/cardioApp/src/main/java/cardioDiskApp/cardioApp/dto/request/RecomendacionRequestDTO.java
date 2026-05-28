package cardioDiskApp.cardioApp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecomendacionRequestDTO {

    @NotNull(message = "El ID del paciente es obligatorio")
    private Long pacienteId;

    @NotBlank(message = "El tipo es obligatorio")
    private String tipo;
    @NotBlank(message = "El mensaje es obligatorio")
    @Size(max = 500, message = "Máximo 500 caracteres")
    private String mensaje;
}