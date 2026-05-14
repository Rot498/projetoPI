package com.streetcar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.streetcar.repository.UsuarioRepository;
import com.streetcar.service.UsuarioService;
import com.streetcar.model.Usuario;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("AuthController — testes de integração")
class AuthControllerTest {

    @Autowired MockMvc mvc;
    @Autowired UsuarioService service;
    @Autowired UsuarioRepository repo;
    @Autowired ObjectMapper mapper;

    private void cadastrarUsuario(String email, String senha) {
        Usuario u = new Usuario(null, "Test", email, senha, "", "CLIENTE");
        service.criar(u);
    }

    @Test
    @DisplayName("POST /api/auth/login → credenciais corretas → 200 com dados")
    void login_sucesso() throws Exception {
        cadastrarUsuario("ok@test.com", "senha123");

        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of("email", "ok@test.com", "senha", "senha123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("ok@test.com")))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.perfil", is("CLIENTE")));
    }

    @Test
    @DisplayName("POST /api/auth/login → senha errada → 401")
    void login_senhaErrada() throws Exception {
        cadastrarUsuario("err@test.com", "certa");

        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of("email", "err@test.com", "senha", "errada"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.erro", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/auth/login → email não existe → 401")
    void login_emailInexistente() throws Exception {
        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of("email", "none@test.com", "senha", "x"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/cadastro → novo usuário → 200 e perfil CLIENTE")
    void cadastro_sucesso() throws Exception {
        mvc.perform(post("/api/auth/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                        "nome", "Novo",
                        "email", "novo@test.com",
                        "senha", "pw123",
                        "telefone", "(11)99999-0000"
                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem", notNullValue()));

        // login deve funcionar após cadastro
        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of("email", "novo@test.com", "senha", "pw123"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/auth/cadastro → email duplicado → 400")
    void cadastro_emailDuplicado() throws Exception {
        cadastrarUsuario("dup@test.com", "pw");

        mvc.perform(post("/api/auth/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                        "nome", "Outro", "email", "dup@test.com", "senha", "pw2"
                ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro", notNullValue()));
    }
}
