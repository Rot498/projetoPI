package com.streetcar.repository;

import com.streetcar.model.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@DisplayName("Repositories — testes unitários JPA")
class RepositoryTest {

    @Autowired UsuarioRepository usuarioRepo;
    @Autowired VeiculoRepository veiculoRepo;
    @Autowired LocacaoRepository locacaoRepo;
    @Autowired ReservaRepository reservaRepo;

    // ─── UsuarioRepository ───────────────────────────────────────────────
    @Nested
    @DisplayName("UsuarioRepository")
    class UsuarioTests {

        @Test
        @DisplayName("findByEmail → encontrado")
        void findByEmail_encontrado() {
            usuarioRepo.save(new Usuario(null, "Ana", "ana@test.com", "hash", "", "CLIENTE"));
            assertThat(usuarioRepo.findByEmail("ana@test.com")).isPresent();
        }

        @Test
        @DisplayName("findByEmail → não encontrado → vazio")
        void findByEmail_vazio() {
            assertThat(usuarioRepo.findByEmail("x@x.com")).isEmpty();
        }

        @Test
        @DisplayName("existsByEmail → true quando existe")
        void existsByEmail_existe() {
            usuarioRepo.save(new Usuario(null, "Bob", "bob@test.com", "hash", "", "CLIENTE"));
            assertThat(usuarioRepo.existsByEmail("bob@test.com")).isTrue();
        }

        @Test
        @DisplayName("existsByEmail → false quando não existe")
        void existsByEmail_naoExiste() {
            assertThat(usuarioRepo.existsByEmail("nada@test.com")).isFalse();
        }
    }

    // ─── VeiculoRepository ───────────────────────────────────────────────
    @Nested
    @DisplayName("VeiculoRepository")
    class VeiculoTests {

        @Test
        @DisplayName("findByStatus → retorna só os do status informado")
        void findByStatus() {
            veiculoRepo.save(new Veiculo(null, "Civic",  "V001", 2022, 180.0, "Disponível"));
            veiculoRepo.save(new Veiculo(null, "Gol",    "V002", 2021, 100.0, "Manutenção"));
            veiculoRepo.save(new Veiculo(null, "Onix",   "V003", 2023, 130.0, "Disponível"));

            List<Veiculo> disponiveis = veiculoRepo.findByStatus("Disponível");
            assertThat(disponiveis).hasSize(2)
                    .extracting(Veiculo::getStatus).containsOnly("Disponível");
        }
    }

    // ─── LocacaoRepository ───────────────────────────────────────────────
    @Nested
    @DisplayName("LocacaoRepository")
    class LocacaoTests {

        private Usuario cliente() {
            return usuarioRepo.save(new Usuario(null, "T", "lc@test.com", "h", "", "CLIENTE"));
        }
        private Veiculo veiculo(String placa) {
            return veiculoRepo.save(new Veiculo(null, "M", placa, 2023, 100.0, "Alugado"));
        }

        @Test
        @DisplayName("findByClienteId → retorna locações do cliente")
        void findByClienteId() {
            Usuario c = cliente();
            Veiculo v = veiculo("LC01");
            locacaoRepo.save(new Locacao(null, c, v, LocalDate.now(), LocalDate.now().plusDays(2), "Ativa", 200.0));
            assertThat(locacaoRepo.findByClienteId(c.getId())).hasSize(1);
        }

        @Test
        @DisplayName("findByStatus → filtra por status")
        void findByStatus() {
            Usuario c = cliente();
            Veiculo v1 = veiculo("LC02");
            Veiculo v2 = veiculo("LC03");
            locacaoRepo.save(new Locacao(null, c, v1, LocalDate.now(), LocalDate.now().plusDays(1), "Ativa", 100.0));
            locacaoRepo.save(new Locacao(null, c, v2, LocalDate.now(), LocalDate.now().plusDays(1), "Encerrada", 100.0));
            assertThat(locacaoRepo.findByStatus("Ativa")).hasSize(1);
            assertThat(locacaoRepo.findByStatus("Encerrada")).hasSize(1);
        }

        @Test
        @DisplayName("existeConflito → true quando datas se sobrepõem")
        void existeConflito_sim() {
            Usuario c = cliente();
            Veiculo v = veiculo("LC04");
            locacaoRepo.save(new Locacao(null, c, v,
                    LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 10), "Ativa", 900.0));

            assertThat(locacaoRepo.existeConflito(v.getId(),
                    LocalDate.of(2025, 6, 5), LocalDate.of(2025, 6, 15))).isTrue();
        }

        @Test
        @DisplayName("existeConflito → false quando datas não se sobrepõem")
        void existeConflito_nao() {
            Usuario c = cliente();
            Veiculo v = veiculo("LC05");
            locacaoRepo.save(new Locacao(null, c, v,
                    LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 5), "Ativa", 400.0));

            assertThat(locacaoRepo.existeConflito(v.getId(),
                    LocalDate.of(2025, 6, 6), LocalDate.of(2025, 6, 10))).isFalse();
        }
    }

    // ─── ReservaRepository ───────────────────────────────────────────────
    @Nested
    @DisplayName("ReservaRepository")
    class ReservaTests {

        private Usuario cliente() {
            return usuarioRepo.save(new Usuario(null, "T", "rc@test.com", "h", "", "CLIENTE"));
        }
        private Veiculo veiculo(String placa) {
            return veiculoRepo.save(new Veiculo(null, "M", placa, 2023, 100.0, "Disponível"));
        }

        @Test
        @DisplayName("findByClienteId → retorna reservas do cliente")
        void findByClienteId() {
            Usuario c = cliente();
            Veiculo v = veiculo("RC01");
            reservaRepo.save(new Reserva(null, c, v, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3), "Pendente"));
            assertThat(reservaRepo.findByClienteId(c.getId())).hasSize(1);
        }

        @Test
        @DisplayName("findByStatus → filtra por status")
        void findByStatus() {
            Usuario c = cliente();
            Veiculo v1 = veiculo("RC02");
            Veiculo v2 = veiculo("RC03");
            reservaRepo.save(new Reserva(null, c, v1, LocalDate.now(), LocalDate.now().plusDays(2), "Pendente"));
            reservaRepo.save(new Reserva(null, c, v2, LocalDate.now(), LocalDate.now().plusDays(2), "Confirmada"));
            assertThat(reservaRepo.findByStatus("Pendente")).hasSize(1);
            assertThat(reservaRepo.findByStatus("Cancelada")).isEmpty();
        }

        @Test
        @DisplayName("existeConflito → true quando datas se sobrepõem")
        void existeConflito_sim() {
            Usuario c = cliente();
            Veiculo v = veiculo("RC04");
            reservaRepo.save(new Reserva(null, c, v,
                    LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 10), "Pendente"));

            assertThat(reservaRepo.existeConflito(v.getId(),
                    LocalDate.of(2025, 7, 8), LocalDate.of(2025, 7, 15))).isTrue();
        }

        @Test
        @DisplayName("existeConflito → false fora do período")
        void existeConflito_nao() {
            Usuario c = cliente();
            Veiculo v = veiculo("RC05");
            reservaRepo.save(new Reserva(null, c, v,
                    LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 5), "Pendente"));

            assertThat(reservaRepo.existeConflito(v.getId(),
                    LocalDate.of(2025, 7, 6), LocalDate.of(2025, 7, 10))).isFalse();
        }
    }
}
