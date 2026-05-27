package com.esports.ranking_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rankings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long torneoId;

    @Column(nullable = false)
    private Long participanteId;

    @Builder.Default
    private Integer puntos = 0;

    @Builder.Default
    private Integer victorias = 0;

    @Builder.Default
    private Integer derrotas = 0;

    @Builder.Default
    private Integer diferencia = 0;

    @Builder.Default
    private Integer posicion = 0;

    public void registrarVictoria(int puntosGanados, int difRondas) {
        this.puntos += puntosGanados;
        this.victorias += 1;
        this.diferencia += difRondas;
    }

    public void registrarDerrota(int difRondas) {
        this.derrotas += 1;
        this.diferencia -= difRondas;
    }
}