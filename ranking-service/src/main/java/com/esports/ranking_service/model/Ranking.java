package com.esports.ranking_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rankings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"torneo_id", "participante_id"})
})
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "torneo_id", nullable = false)
    private Long torneoId;

    @Column(name = "participante_id", nullable = false)
    private Long participanteId;

    @Column(nullable = false)
    @Builder.Default
    private Integer puntos = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer victorias = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer derrotas = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer diferencia = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer posicion = 0;

    @PrePersist
    public void prePersist() {
        if (this.puntos == null) this.puntos = 0;
        if (this.victorias == null) this.victorias = 0;
        if (this.derrotas == null) this.derrotas = 0;
        if (this.diferencia == null) this.diferencia = 0;
        if (this.posicion == null) this.posicion = 0;
    }

    public void registrarVictoria(int puntajePropio, int puntajeRival) {
        this.victorias++;
        this.puntos += 3;
        this.diferencia += (puntajePropio - puntajeRival);
    }

    public void registrarDerrota(int puntajePropio, int puntajeRival) {
        this.derrotas++;
        this.diferencia += (puntajePropio - puntajeRival);
    }

}