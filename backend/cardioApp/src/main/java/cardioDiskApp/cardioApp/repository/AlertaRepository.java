package cardioDiskApp.cardioApp.repository;

import cardioDiskApp.cardioApp.emuns.TipoAlerta;
import cardioDiskApp.cardioApp.entity.Alerta;
import cardioDiskApp.cardioApp.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    List<Alerta> findByPacienteOrderByFechaAlertaDesc(Usuario paciente);

    List<Alerta> findByPacienteAndAtendidaFalseOrderByFechaAlertaDesc(Usuario paciente);

    List<Alerta> findByAtendidaFalseOrderByFechaAlertaDesc();

    List<Alerta> findByTipo(TipoAlerta tipo);

    @Query("""
        SELECT a FROM Alerta a
        WHERE a.paciente.id = :pacienteId
        AND   a.atendida    = false
        ORDER BY a.fechaAlerta DESC
    """)
    List<Alerta> findAlertasActivasByPacienteId(@Param("pacienteId") Long pacienteId);

    long countByAtendidaFalse();

    boolean existsByPacienteAndTipoAndAtendidaFalse(Usuario paciente, TipoAlerta tipo);
}
