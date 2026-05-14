package com.streetcar.controller;

import com.streetcar.model.Veiculo;
import com.streetcar.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veiculos")
@CrossOrigin(origins = "*")
public class VeiculoController {

    @Autowired
    private VeiculoRepository repo;

    @GetMapping
    public List<Veiculo> listar() {
        return repo.findAll();
    }

    @GetMapping("/disponiveis")
    public List<Veiculo> disponiveis() {
        return repo.findByStatus("Disponível");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Veiculo> buscar(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Veiculo> criar(@RequestBody Veiculo v) {
        if (v.getStatus() == null || v.getStatus().isBlank()) {
            v.setStatus("Disponível");
        }
        return ResponseEntity.ok(repo.save(v));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Veiculo> atualizar(@PathVariable Long id, @RequestBody Veiculo dados) {
        return repo.findById(id).map(v -> {
            v.setModelo(dados.getModelo());
            v.setPlaca(dados.getPlaca());
            v.setAno(dados.getAno());
            v.setDiaria(dados.getDiaria());
            v.setStatus(dados.getStatus());
            return ResponseEntity.ok(repo.save(v));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
