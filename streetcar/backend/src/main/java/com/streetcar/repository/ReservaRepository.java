package com.streetcar.repository;

import com.streetcar.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByClienteId(Long clienteId);
    List<Reserva> findByStatus(String status);

    /** Verifica se existe reserva ativa/pendente para o veículo no período */
    @Query("SELECT COUNT(r) > 0 FROM Reserva r WHERE r.veiculo.id = :veiculoId " +
           "AND r.status IN ('Pendente', 'Confirmada') " +
           "AND r.dataInicio <= :fim AND r.dataFim >= :inicio")
    boolean existeConflito(@Param("veiculoId") Long veiculoId,
                           @Param("inicio") LocalDate inicio,
                           @Param("fim") LocalDate fim);
}
