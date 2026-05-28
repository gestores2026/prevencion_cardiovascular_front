package cardioDiskApp.cardioApp.dto.request;

import cardioDiskApp.cardioApp.emuns.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Correo inválido")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @NotBlank(message = "El documento es obligatorio")
    private String documento;

    private Integer edad;
    private String sexo;
    private Double peso;
    private Double talla;

    @NotNull(message = "El rol es obligatorio")
    private Rol rol;
}
