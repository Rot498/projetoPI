package com.streetcar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.streetcar.model.*;
import com.streetcar.repository.*;
import com.streetcar.service.UsuarioService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("ReservaController — testes de integração")
class ReservaControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ReservaRepository reservaRepo;
    @Autowired VeiculoRepository veiculoRepo;
    @Autowired UsuarioService usuarioService;
    @Autowired ObjectMapper mapper;

    private Usuario cliente;
    private Veiculo veiculo;

    @BeforeEach
    void setup() {
        cliente = usuarioService.criar(new Usuario(null, "CLI", "res@t.com", "pw", "", "CLIENTE"));
        veiculo = veiculoRepo.save(new Veiculo(null, "Corolla", "RES01", 2023, 200.0, "Disponível"));
    }

    private Reserva salvarReserva() {
        return reservaRepo.save(new Reserva(null, cliente, veiculo,
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(4), "Pendente"));
    }

    @Test
    @DisplayName("GET /api/reservas → retorna lista")
    void listar() throws Exception {
        salvarReserva();
        mvc.perform(get("/api/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /api/reservas/cliente/{id} → filtra por cliente")
    void porCliente() throws Exception {
        salvarReserva();
        mvc.perform(get("/api/reservas/cliente/" + cliente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cliente.id", is(cliente.getId().intValue())));
    }

    @Test
    @DisplayName("GET /api/reservas/{id} → encontrado")
    void buscar_encontrado() throws Exception {
        Reserva r = salvarReserva();
        mvc.perform(get("/api/reservas/" + r.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("Pendente")));
    }

    @Test
    @DisplayName("GET /api/reservas/{id} → não encontrado → 404")
    void buscar_404() throws Exception {
        mvc.perform(get("/api/reservas/99999")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/reservas → cria com status Pendente")
    void criar() throws Exception {
        Reserva r = new Reserva(null, cliente, veiculo,
                LocalDate.now().plusDays(5), LocalDate.now().plusDays(8), null);

        mvc.perform(post("/api/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(r)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("Pendente")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/reservas → conflito de datas → 409")
    void criar_conflito() throws Exception {
        salvarReserva(); // período: +1 a +4 dias

        Reserva r = new Reserva(null, cliente, veiculo,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(6), null);

        mvc.perform(post("/api/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(r)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro", notNullValue()));
    }

    @Test
    @DisplayName("PUT /api/reservas/{id} → atualiza status para Confirmada")
    void confirmar() throws Exception {
        Reserva r = salvarReserva();
        r.setStatus("Confirmada");
        mvc.perform(put("/api/reservas/" + r.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(r)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("Confirmada")));
    }

    @Test
    @DisplayName("PUT /api/reservas/{id} → não encontrado → 404")
    void atualizar_404() throws Exception {
        Reserva r = new Reserva(null, cliente, veiculo,
                LocalDate.now(), LocalDate.now().plusDays(1), "Pendente");
        mvc.perform(put("/api/reservas/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(r)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/reservas/{id} → remove → 204")
    void deletar() throws Exception {
        Reserva r = salvarReserva();
        mvc.perform(delete("/api/reservas/" + r.getId())).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/reservas/{id} → não encontrado → 404")
    void deletar_404() throws Exception {
        mvc.perform(delete("/api/reservas/99999")).andExpect(status().isNotFound());
    }
}
