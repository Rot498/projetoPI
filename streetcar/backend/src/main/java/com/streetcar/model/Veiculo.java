package com.streetcar.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "veiculos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String modelo;

    @Column(nullable = false, unique = true)
    private String placa;

    @Column(nullable = false)
    private Integer ano;

    @Column(nullable = false)
    private Double diaria;

    // "Disponível", "Alugado", "Manutenção"
    @Column(nullable = false)
    private String status;
}
