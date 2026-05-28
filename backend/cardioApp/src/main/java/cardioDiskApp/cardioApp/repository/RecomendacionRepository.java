package cardioDiskApp.cardioApp.repository;

import cardioDiskApp.cardioApp.entity.Recomendacion;
import cardioDiskApp.cardioApp.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface RecomendacionRepository extends JpaRepository<Recomendacion, Long> {

    List<Recomendacion> findByPacienteOrderByFechaEnvioDesc(Usuario paciente);

    List<Recomendacion> findByMedicoOrderByFechaEnvioDesc(Usuario medico);

    List<Recomendacion> findByMedicoAndPacienteOrderByFechaEnvioDesc(
            Usuario medico, Usuario paciente
    );

    List<Recomendacion> findByTipo(String tipo);

    @Query("""
        SELECT r FROM Recomendacion r
        WHERE r.paciente.id = :pacienteId
        ORDER BY r.fechaEnvio DESC
    """)
    List<Recomendacion> findByPacienteId(@Param("pacienteId") Long pacienteId);

    @Query("""
        SELECT r FROM Recomendacion r
        WHERE r.medico.id = :medicoId
        ORDER BY r.fechaEnvio DESC
    """)
    List<Recomendacion> findUltimasByMedico(@Param("medicoId") Long medicoId,
                                            Pageable pageable);
}
