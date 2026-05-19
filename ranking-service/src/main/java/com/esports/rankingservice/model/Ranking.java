package com.esports.rankingservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(
    name = "rankings",
    uniqueConstraints = { @UniqueConstraint(columnNames = {"torneo_id", "participante_id"})
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "torneo_id", nullable = false)
    private Long torneoId;

    @Column(name = "participante_id", nullable = false)
    private Long participanteId;

    @Column(nullable = false)
    private Integer puntos;

    @Column(nullable = false)
    private Integer victorias;

    @Column(nullable = false)
    private Integer derrotas;

    /** Diferencia de puntaje acumulada (puntajeA - puntajeB en victorias) */
    @Column(nullable = false)
    private Integer diferencia;

    /** Posición calculada dentro del torneo (1 = primero) */
    @Column(nullable = false)
    private Integer posicion;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column
    private LocalDateTime fechaActualizacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion      = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        if (this.puntos    == null) this.puntos    = 0;
        if (this.victorias == null) this.victorias = 0;
        if (this.derrotas  == null) this.derrotas  = 0;
        if (this.diferencia== null) this.diferencia= 0;
        if (this.posicion  == null) this.posicion  = 0;
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }


    public void registrarVictoria(int puntajePropio, int puntajeRival) {
        this.victorias++;
        this.puntos    += 3;
        this.diferencia += (puntajePropio - puntajeRival);
    }

    public void registrarDerrota(int puntajePropio, int puntajeRival) {
        this.derrotas++;
        this.diferencia += (puntajePropio - puntajeRival);
    }
}
