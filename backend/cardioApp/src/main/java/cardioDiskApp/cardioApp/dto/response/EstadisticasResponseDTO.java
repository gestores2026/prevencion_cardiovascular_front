package cardioDiskApp.cardioApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadisticasResponseDTO {
    private Long totalPacientes;
    private Long pacientesRiesgoAlto;
    private Long pacientesRiesgoModerado;
    private Long pacientesRiesgoBajo;
    private Double promedioRiesgo;
    private Long pacientesFumadores;
    private Long totalTestsEsteMes;
    private Map<String, Double> riesgoPorGrupoEdad;
}