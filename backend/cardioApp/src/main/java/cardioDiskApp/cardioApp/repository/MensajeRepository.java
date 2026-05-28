package cardioDiskApp.cardioApp.repository;

import cardioDiskApp.cardioApp.entity.Mensaje;
import cardioDiskApp.cardioApp.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    @Query("""
        SELECT m FROM Mensaje m
        WHERE (m.emisor.id = :usuarioA AND m.receptor.id = :usuarioB)
        OR    (m.emisor.id = :usuarioB AND m.receptor.id = :usuarioA)
        ORDER BY m.fechaEnvio ASC
    """)
    List<Mensaje> findConversacion(@Param("usuarioA") Long usuarioA,
                                   @Param("usuarioB") Long usuarioB);

    long countByReceptorAndLeidoFalse(Usuario receptor);
}