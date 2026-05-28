package cardioDiskApp.cardioApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {
    private Long id;
    private String nombre;
    private String correo;
    private String documento;
    private Integer edad;
    private String sexo;
    private Double peso;
    private Double talla;
    private String rol;
    private LocalDate fechaRegistro;
    private Double ultimoRiesgo;
    private String ultimoNivelRiesgo;
    private Long medicoId;
    private String medicoNombre;
}
