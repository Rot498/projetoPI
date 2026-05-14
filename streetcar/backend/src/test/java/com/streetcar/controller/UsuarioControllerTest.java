package com.streetcar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.streetcar.model.Usuario;
import com.streetcar.service.UsuarioService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("UsuarioController — testes de integração")
class UsuarioControllerTest {

    @Autowired MockMvc mvc;
    @Autowired UsuarioService service;
    @Autowired ObjectMapper mapper;

    private Usuario salvar(String email) {
        return service.criar(new Usuario(null, "Teste", email, "senha", "(11)99999-0000", "CLIENTE"));
    }

    @Test
    @DisplayName("GET /api/usuarios → retorna lista")
    void listar() throws Exception {
        salvar("u1@t.com"); salvar("u2@t.com");
        mvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    @DisplayName("GET /api/usuarios/{id} → encontrado")
    void buscar_encontrado() throws Exception {
        Usuario u = salvar("find@t.com");
        mvc.perform(get("/api/usuarios/" + u.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("find@t.com")));
    }

    @Test
    @DisplayName("GET /api/usuarios/{id} → não encontrado → 404")
    void buscar_404() throws Exception {
        mvc.perform(get("/api/usuarios/99999")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/usuarios → cria com perfil padrão CLIENTE")
    void criar_perfilPadrao() throws Exception {
        Usuario novo = new Usuario(null, "N", "new@t.com", "pw", "", null);
        mvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(novo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perfil", is("CLIENTE")));
    }

    @Test
    @DisplayName("POST /api/usuarios → email duplicado → 400")
    void criar_emailDuplicado() throws Exception {
        salvar("dup@t.com");
        Usuario novo = new Usuario(null, "X", "dup@t.com", "pw", "", "CLIENTE");
        mvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(novo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/usuarios/{id} → atualiza nome")
    void atualizar() throws Exception {
        Usuario u = salvar("upd@t.com");
        u.setNome("Nome Novo");
        u.setSenha(""); // sem troca de senha
        mvc.perform(put("/api/usuarios/" + u.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(u)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Nome Novo")));
    }

    @Test
    @DisplayName("PUT /api/usuarios/{id} → senha em branco não sobrescreve")
    void atualizar_senhaBranca() throws Exception {
        Usuario u = salvar("pw@t.com");
        String hashOriginal = u.getSenha();
        u.setSenha("   ");
        mvc.perform(put("/api/usuarios/" + u.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(u)))
                .andExpect(status().isOk());

        // senha deve ser o hash original
        String hashAtual = service.buscar(u.getId()).orElseThrow().getSenha();
        org.junit.jupiter.api.Assertions.assertEquals(hashOriginal, hashAtual);
    }

    @Test
    @DisplayName("PUT /api/usuarios/{id} → não encontrado → 404")
    void atualizar_404() throws Exception {
        Usuario u = new Usuario(null, "X", "x@x.com", "pw", "", "CLIENTE");
        mvc.perform(put("/api/usuarios/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(u)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/usuarios/{id} → remove → 204")
    void deletar() throws Exception {
        Usuario u = salvar("del@t.com");
        mvc.perform(delete("/api/usuarios/" + u.getId())).andExpect(status().isNoContent());
        mvc.perform(get("/api/usuarios/" + u.getId())).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/usuarios/{id} → não encontrado → 404")
    void deletar_404() throws Exception {
        mvc.perform(delete("/api/usuarios/99999")).andExpect(status().isNotFound());
    }
}
