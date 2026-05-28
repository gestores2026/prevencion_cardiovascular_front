package cardioDiskApp.cardioApp.entity;

import cardioDiskApp.cardioApp.emuns.NivelRiesgo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tests_framingham")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestFramingham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Usuario paciente;

    private Integer edad;
    private String sexo;
    private Double colesterolTotal;
    private Double colesterolHdl;
    private Double presionSistolica;
    private Boolean tratamientoHipertension;
    private Boolean fumador;
    private Double imc;

    private Double porcentajeRiesgo;

    @Enumerated(EnumType.STRING)
    private NivelRiesgo nivelRiesgo;

    private LocalDateTime fechaTest;

    @PrePersist
    public void prePersist() {
        this.fechaTest = LocalDateTime.now();
    }
}
