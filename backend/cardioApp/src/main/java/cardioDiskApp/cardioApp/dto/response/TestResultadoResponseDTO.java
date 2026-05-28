package cardioDiskApp.cardioApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestResultadoResponseDTO {
    private Long id;
    private Long pacienteId;
    private String pacienteNombre;
    private Integer edad;
    private String sexo;
    private Double colesterolTotal;
    private Double colesterolHdl;
    private Double presionSistolica;
    private Boolean tratamientoHipertension;
    private Boolean fumador;
    private Double imc;
    private Double porcentajeRiesgo;
    private String nivelRiesgo;
    private LocalDateTime fechaTest;
    private List<String> recomendacionesAutomaticas;
}
