package com.streetcar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.streetcar.model.Veiculo;
import com.streetcar.repository.VeiculoRepository;
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
@DisplayName("VeiculoController — testes de integração")
class VeiculoControllerTest {

    @Autowired MockMvc mvc;
    @Autowired VeiculoRepository repo;
    @Autowired ObjectMapper mapper;

    private Veiculo salvar(String modelo, String placa, String status) {
        return repo.save(new Veiculo(null, modelo, placa, 2023, 150.0, status));
    }

    @Test
    @DisplayName("GET /api/veiculos → retorna lista")
    void listar() throws Exception {
        salvar("Civic", "V001", "Disponível"); salvar("Gol", "V002", "Disponível");
        mvc.perform(get("/api/veiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    @DisplayName("GET /api/veiculos/disponiveis → só status Disponível")
    void disponiveis() throws Exception {
        salvar("Civic",  "D001", "Disponível");
        salvar("Pulse",  "D002", "Manutenção");
        mvc.perform(get("/api/veiculos/disponiveis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].status", everyItem(is("Disponível"))));
    }

    @Test
    @DisplayName("GET /api/veiculos/{id} → encontrado")
    void buscar_encontrado() throws Exception {
        Veiculo v = salvar("Onix", "E001", "Disponível");
        mvc.perform(get("/api/veiculos/" + v.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo", is("Onix")));
    }

    @Test
    @DisplayName("GET /api/veiculos/{id} → não encontrado → 404")
    void buscar_404() throws Exception {
        mvc.perform(get("/api/veiculos/99999")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/veiculos → status padrão Disponível quando nulo")
    void criar_statusPadrao() throws Exception {
        Veiculo novo = new Veiculo(null, "HB20", "N001", 2024, 110.0, null);
        mvc.perform(post("/api/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(novo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("Disponível")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/veiculos → status explícito é mantido")
    void criar_statusExplicito() throws Exception {
        Veiculo novo = new Veiculo(null, "Renegade", "N002", 2023, 200.0, "Manutenção");
        mvc.perform(post("/api/veiculos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(novo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("Manutenção")));
    }

    @Test
    @DisplayName("PUT /api/veiculos/{id} → atualiza campos")
    void atualizar() throws Exception {
        Veiculo v = salvar("Celta", "U001", "Disponível");
        v.setModelo("Celta Plus"); v.setDiaria(95.0);
        mvc.perform(put("/api/veiculos/" + v.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(v)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo", is("Celta Plus")))
                .andExpect(jsonPath("$.diaria", is(95.0)));
    }

    @Test
    @DisplayName("PUT /api/veiculos/{id} → não encontrado → 404")
    void atualizar_404() throws Exception {
        Veiculo v = new Veiculo(null, "X", "Z999", 2020, 50.0, "Disponível");
        mvc.perform(put("/api/veiculos/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(v)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/veiculos/{id} → remove → 204")
    void deletar() throws Exception {
        Veiculo v = salvar("Palio", "X001", "Disponível");
        mvc.perform(delete("/api/veiculos/" + v.getId())).andExpect(status().isNoContent());
        mvc.perform(get("/api/veiculos/" + v.getId())).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/veiculos/{id} → não encontrado → 404")
    void deletar_404() throws Exception {
        mvc.perform(delete("/api/veiculos/99999")).andExpect(status().isNotFound());
    }
}
