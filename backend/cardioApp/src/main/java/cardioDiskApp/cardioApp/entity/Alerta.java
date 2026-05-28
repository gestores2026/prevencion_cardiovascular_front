package cardioDiskApp.cardioApp.entity;

import cardioDiskApp.cardioApp.emuns.TipoAlerta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "alertas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Usuario paciente;

    @Enumerated(EnumType.STRING)
    private TipoAlerta tipo;

    private String descripcion;
    private Boolean atendida;
    private LocalDateTime fechaAlerta;

    @PrePersist
    public void prePersist() {
        this.fechaAlerta = LocalDateTime.now();
        this.atendida = false;
    }
}
