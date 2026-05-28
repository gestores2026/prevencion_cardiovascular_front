package cardioDiskApp.cardioApp.repository;

import cardioDiskApp.cardioApp.emuns.NivelRiesgo;
import cardioDiskApp.cardioApp.entity.TestFramingham;
import cardioDiskApp.cardioApp.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TestFraminghamRepository extends JpaRepository<TestFramingham, Long> {

    List<TestFramingham> findByPacienteOrderByFechaTestDesc(Usuario paciente);


    Optional<TestFramingham> findTopByPacienteOrderByFechaTestDesc(Usuario paciente);

    List<TestFramingham> findByNivelRiesgo(NivelRiesgo nivelRiesgo);

    long countByNivelRiesgo(NivelRiesgo nivelRiesgo);

    @Query("""
        SELECT t FROM TestFramingham t
        WHERE t.fechaTest BETWEEN :inicio AND :fin
        ORDER BY t.fechaTest DESC
    """)
    List<TestFramingham> findByRangoFecha(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin")    LocalDateTime fin
    );

    @Query("""
        SELECT t FROM TestFramingham t
        WHERE t.paciente = :paciente
        AND t.fechaTest BETWEEN :inicio AND :fin
        ORDER BY t.fechaTest DESC
    """)
    List<TestFramingham> findByPacienteAndRangoFecha(
            @Param("paciente") Usuario paciente,
            @Param("inicio")   LocalDateTime inicio,
            @Param("fin")      LocalDateTime fin
    );

    @Query("SELECT AVG(t.porcentajeRiesgo) FROM TestFramingham t")
    Double promedioRiesgoGeneral();

    @Query("""
        SELECT AVG(t.porcentajeRiesgo)
        FROM TestFramingham t
        WHERE t.paciente = :paciente
    """)
    Double promedioRiesgoPaciente(@Param("paciente") Usuario paciente);

    @Query("""
        SELECT t FROM TestFramingham t
        WHERE MONTH(t.fechaTest) = MONTH(CURRENT_DATE)
        AND   YEAR(t.fechaTest)  = YEAR(CURRENT_DATE)
    """)
    List<TestFramingham> findTestsEsteMes();

    @Query("""
        SELECT COUNT(t) FROM TestFramingham t
        WHERE MONTH(t.fechaTest) = MONTH(CURRENT_DATE)
        AND   YEAR(t.fechaTest)  = YEAR(CURRENT_DATE)
    """)
    long countTestsEsteMes();

    @Query("""
        SELECT COUNT(DISTINCT t.paciente)
        FROM TestFramingham t
        WHERE t.fumador = true
        AND t.fechaTest = (
            SELECT MAX(t2.fechaTest)
            FROM TestFramingham t2
            WHERE t2.paciente = t.paciente
        )
    """)
    long countPacientesFumadores();

    @Query("""
        SELECT t.edad, AVG(t.porcentajeRiesgo)
        FROM TestFramingham t
        GROUP BY t.edad
        ORDER BY t.edad
    """)
    List<Object[]> promedioRiesgoPorEdad();

    @Query("""
        SELECT t FROM TestFramingham t
        WHERE t.paciente.id = :pacienteId
        ORDER BY t.fechaTest DESC
    """)
    List<TestFramingham> findByPacienteId(@Param("pacienteId") Long pacienteId);
}
