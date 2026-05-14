package com.streetcar.service;

import com.streetcar.model.Usuario;
import com.streetcar.model.Veiculo;
import com.streetcar.repository.UsuarioRepository;
import com.streetcar.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private VeiculoRepository veiculoRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Cria usuário ADMIN padrão
        if (!usuarioRepo.existsByEmail("admin@streetcar.com")) {
            usuarioRepo.save(new Usuario(null, "Administrador", "admin@streetcar.com",
                    passwordEncoder.encode("admin123"), "(11) 99999-0000", "ADMIN"));
        }

        // Cria cliente de teste
        if (!usuarioRepo.existsByEmail("cliente@teste.com")) {
            usuarioRepo.save(new Usuario(null, "João Silva", "cliente@teste.com",
                    passwordEncoder.encode("1234"), "(11) 98888-1234", "CLIENTE"));
        }

        // Cria veículos de exemplo
        if (veiculoRepo.count() == 0) {
            veiculoRepo.save(new Veiculo(null, "Honda Civic",      "ABC-1234", 2022, 180.0, "Disponível"));
            veiculoRepo.save(new Veiculo(null, "Toyota Corolla",   "DEF-5678", 2023, 200.0, "Disponível"));
            veiculoRepo.save(new Veiculo(null, "Volkswagen Gol",   "GHI-9012", 2021, 120.0, "Disponível"));
            veiculoRepo.save(new Veiculo(null, "Chevrolet Onix",   "JKL-3456", 2022, 130.0, "Disponível"));
            veiculoRepo.save(new Veiculo(null, "Fiat Pulse",       "MNO-7890", 2023, 150.0, "Manutenção"));
        }

        System.out.println("======================================");
        System.out.println("  StreetCar iniciado com sucesso!");
        System.out.println("  Admin: admin@streetcar.com / admin123");
        System.out.println("  Cliente: cliente@teste.com / 1234");
        System.out.println("  H2 Console: http://localhost:8080/h2-console");
        System.out.println("  API: http://localhost:8080/api");
        System.out.println("======================================");
    }
}
