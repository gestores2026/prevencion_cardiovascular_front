package cardioDiskApp.cardioApp.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarUsuarioRequestDTO {

    private String nombre;
    private String documento;
    private Integer edad;
    private String sexo;
    private Double peso;
    private Double talla;

    @Size(min = 6, message = "Mínimo 6 caracteres")
    private String passwordNueva;
}
