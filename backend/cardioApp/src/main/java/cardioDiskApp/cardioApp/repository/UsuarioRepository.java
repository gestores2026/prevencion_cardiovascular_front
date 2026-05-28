package cardioDiskApp.cardioApp.repository;

import cardioDiskApp.cardioApp.emuns.Rol;
import cardioDiskApp.cardioApp.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    boolean existsByDocumento(String documento);

    @Query("""
        SELECT u FROM Usuario u
        WHERE u.rol = 'PACIENTE'
        AND (
            LOWER(u.nombre)  LIKE LOWER(CONCAT('%', :busqueda, '%')) OR
            LOWER(u.correo)  LIKE LOWER(CONCAT('%', :busqueda, '%')) OR
            u.documento      LIKE CONCAT('%', :busqueda, '%')
        )
    """)
    List<Usuario> buscarPacientes(@Param("busqueda") String busqueda);

    List<Usuario> findByRol(Rol rol);

    long countByRol(Rol rol);

    List<Usuario> findByMedicoId(Long medicoId);

    long countByMedicoId(Long medicoId);

    @Query("""
        SELECT u FROM Usuario u
        WHERE u.medico.id = :medicoId
        AND u.rol = 'PACIENTE'
        AND (
            LOWER(u.nombre)  LIKE LOWER(CONCAT('%', :busqueda, '%')) OR
            LOWER(u.correo)  LIKE LOWER(CONCAT('%', :busqueda, '%')) OR
            u.documento      LIKE CONCAT('%', :busqueda, '%')
        )
    """)
    List<Usuario> buscarPacientesPorMedico(@Param("medicoId") Long medicoId,
                                           @Param("busqueda") String busqueda);
}