package com.streetcar.service;

import com.streetcar.model.Reserva;
import com.streetcar.model.Veiculo;
import com.streetcar.repository.ReservaRepository;
import com.streetcar.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository repo;

    @Autowired
    private VeiculoRepository veiculoRepo;

    public List<Reserva> listar() {
        return repo.findAll();
    }

    public List<Reserva> porCliente(Long clienteId) {
        return repo.findByClienteId(clienteId);
    }

    public Optional<Reserva> buscar(Long id) {
        return repo.findById(id);
    }

    /**
     * Cria reserva validando que o veículo existe e não há conflito de datas.
     */
    public Reserva criar(Reserva r) {
        Long veiculoId = r.getVeiculo().getId();

        veiculoRepo.findById(veiculoId)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));

        if (repo.existeConflito(veiculoId, r.getDataInicio(), r.getDataFim())) {
            throw new IllegalStateException("Já existe uma reserva para este veículo neste período");
        }

        if (r.getStatus() == null || r.getStatus().isBlank()) {
            r.setStatus("Pendente");
        }

        return repo.save(r);
    }

    public Optional<Reserva> atualizar(Long id, Reserva dados) {
        return repo.findById(id).map(r -> {
            r.setDataInicio(dados.getDataInicio());
            r.setDataFim(dados.getDataFim());
            r.setStatus(dados.getStatus());
            return repo.save(r);
        });
    }

    public boolean deletar(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        return true;
    }
}
