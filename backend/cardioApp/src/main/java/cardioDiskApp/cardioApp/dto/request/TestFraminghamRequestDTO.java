package cardioDiskApp.cardioApp.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestFraminghamRequestDTO {

    @NotNull(message = "La edad es obligatoria")
    private Integer edad;

    @NotBlank(message = "El sexo es obligatorio")
    private String sexo;

    @NotNull(message = "El colesterol total es obligatorio")
    @Min(value = 100, message = "Valor mínimo 100")
    private Double colesterolTotal;

    @NotNull(message = "El HDL es obligatorio")
    @Min(value = 20, message = "Valor mínimo 20")
    private Double colesterolHdl;

    @NotNull(message = "La presión sistólica es obligatoria")
    @Min(value = 90, message = "Valor mínimo 90")
    private Double presionSistolica;

    @NotNull(message = "Indica si tiene tratamiento para hipertensión")
    private Boolean tratamientoHipertension;

    @NotNull(message = "Indica si fuma")
    private Boolean fumador;

    @NotNull(message = "El IMC es obligatorio")
    private Double imc;
}