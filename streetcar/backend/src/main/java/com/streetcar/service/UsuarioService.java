package com.streetcar.service;

import com.streetcar.model.Usuario;
import com.streetcar.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> listar() {
        return repo.findAll();
    }

    public Optional<Usuario> buscar(Long id) {
        return repo.findById(id);
    }

    public Usuario criar(Usuario u) {
        if (u.getPerfil() == null || u.getPerfil().isBlank()) {
            u.setPerfil("CLIENTE");
        }
        u.setSenha(passwordEncoder.encode(u.getSenha()));
        return repo.save(u);
    }

    public Optional<Usuario> atualizar(Long id, Usuario dados) {
        return repo.findById(id).map(u -> {
            u.setNome(dados.getNome());
            u.setEmail(dados.getEmail());
            u.setTelefone(dados.getTelefone());
            u.setPerfil(dados.getPerfil());
            if (dados.getSenha() != null && !dados.getSenha().isBlank()) {
                u.setSenha(passwordEncoder.encode(dados.getSenha()));
            }
            return repo.save(u);
        });
    }

    public boolean deletar(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        return true;
    }

    public boolean existeEmail(String email) {
        return repo.existsByEmail(email);
    }

    /** Autentica comparando senha em texto plano com o hash salvo */
    public Optional<Usuario> autenticar(String email, String senhaPlana) {
        return repo.findByEmail(email)
                .filter(u -> passwordEncoder.matches(senhaPlana, u.getSenha()));
    }
}
