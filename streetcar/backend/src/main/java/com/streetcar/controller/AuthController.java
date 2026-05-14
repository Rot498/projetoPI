package com.streetcar.controller;

import com.streetcar.model.Usuario;
import com.streetcar.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioService service;

    // POST /api/auth/login
    // Body: { "email": "...", "senha": "..." }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        return service.autenticar(body.get("email"), body.get("senha"))
                .map(u -> ResponseEntity.ok((Object) Map.of(
                        "id",     u.getId(),
                        "nome",   u.getNome(),
                        "email",  u.getEmail(),
                        "perfil", u.getPerfil()
                )))
                .orElse(ResponseEntity.status(401).body(Map.of("erro", "Email ou senha inválidos")));
    }

    // POST /api/auth/cadastro
    // Body: { "nome": "...", "email": "...", "senha": "...", "telefone": "..." }
    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrar(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        if (service.existeEmail(email)) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Email já cadastrado"));
        }

        Usuario u = new Usuario();
        u.setNome(body.get("nome"));
        u.setEmail(email);
        u.setSenha(body.get("senha"));
        u.setTelefone(body.getOrDefault("telefone", ""));
        u.setPerfil("CLIENTE");

        service.criar(u);
        return ResponseEntity.ok(Map.of("mensagem", "Cadastro realizado com sucesso!"));
    }
}
