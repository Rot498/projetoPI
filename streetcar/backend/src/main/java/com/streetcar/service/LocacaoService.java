package com.streetcar.service;

import com.streetcar.model.Locacao;
import com.streetcar.model.Veiculo;
import com.streetcar.repository.LocacaoRepository;
import com.streetcar.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocacaoService {

    @Autowired
    private LocacaoRepository repo;

    @Autowired
    private VeiculoRepository veiculoRepo;

    public List<Locacao> listar() {
        return repo.findAll();
    }

    public List<Locacao> porCliente(Long clienteId) {
        return repo.findByClienteId(clienteId);
    }

    public Optional<Locacao> buscar(Long id) {
        return repo.findById(id);
    }

    /**
     * Cria uma nova locação.
     * Valida: veículo existe, está Disponível, e não há conflito de datas.
     */
    public Locacao criar(Locacao loc) {
        Long veiculoId = loc.getVeiculo().getId();

        Veiculo veiculo = veiculoRepo.findById(veiculoId)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));

        if (!"Disponível".equals(veiculo.getStatus())) {
            throw new IllegalStateException("Veículo não está disponível para locação");
        }

        if (repo.existeConflito(veiculoId, loc.getDataInicio(), loc.getDataFim())) {
            throw new IllegalStateException("Já existe uma locação ativa para este veículo neste período");
        }

        if (loc.getStatus() == null || loc.getStatus().isBlank()) {
            loc.setStatus("Ativa");
        }

        veiculo.setStatus("Alugado");
        veiculoRepo.save(veiculo);

        return repo.save(loc);
    }

    /**
     * Atualiza status/datas. Se encerrada ou cancelada, libera o veículo.
     */
    public Optional<Locacao> atualizar(Long id, Locacao dados) {
        return repo.findById(id).map(loc -> {
            loc.setDataInicio(dados.getDataInicio());
            loc.setDataFim(dados.getDataFim());
            loc.setStatus(dados.getStatus());
            loc.setValorTotal(dados.getValorTotal());

            if ("Encerrada".equals(dados.getStatus()) || "Cancelada".equals(dados.getStatus())) {
                Veiculo v = loc.getVeiculo();
                v.setStatus("Disponível");
                veiculoRepo.save(v);
            }

            return repo.save(loc);
        });
    }

    public boolean deletar(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        return true;
    }
}
