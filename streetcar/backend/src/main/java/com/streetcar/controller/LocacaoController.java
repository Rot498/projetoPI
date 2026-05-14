package com.streetcar.controller;

import com.streetcar.model.Locacao;
import com.streetcar.service.LocacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/locacoes")
@CrossOrigin(origins = "*")
public class LocacaoController {

    @Autowired
    private LocacaoService service;

    @GetMapping
    public List<Locacao> listar() {
        return service.listar();
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Locacao> porCliente(@PathVariable Long clienteId) {
        return service.porCliente(clienteId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Locacao> buscar(@PathVariable Long id) {
        return service.buscar(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Locacao loc) {
        try {
            return ResponseEntity.ok(service.criar(loc));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of("erro", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Locacao> atualizar(@PathVariable Long id, @RequestBody Locacao dados) {
        return service.atualizar(id, dados)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!service.deletar(id)) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }
}
