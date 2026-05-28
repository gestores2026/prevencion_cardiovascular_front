package cardioDiskApp.cardioApp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeRequestDTO {

    @NotNull(message = "El receptor es obligatorio")
    private Long receptorId;

    @NotBlank(message = "El mensaje no puede estar vacío")
    private String contenido;
}