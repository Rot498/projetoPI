package com.streetcar.repository;

import com.streetcar.model.Locacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface LocacaoRepository extends JpaRepository<Locacao, Long> {
    List<Locacao> findByClienteId(Long clienteId);
    List<Locacao> findByStatus(String status);

    /** Verifica se existe locação ativa para o veículo no período informado */
    @Query("SELECT COUNT(l) > 0 FROM Locacao l WHERE l.veiculo.id = :veiculoId " +
           "AND l.status = 'Ativa' " +
           "AND l.dataInicio <= :fim AND l.dataFim >= :inicio")
    boolean existeConflito(@Param("veiculoId") Long veiculoId,
                           @Param("inicio") LocalDate inicio,
                           @Param("fim") LocalDate fim);
}
