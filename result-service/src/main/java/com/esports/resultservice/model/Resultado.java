package com.esports.resultservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(
    name = "resultados",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"partida_id"})
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resultado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "partida_id", nullable = false, unique = true)
    private Long partidaId;

    @Column(nullable = false)
    private Long ganadorId;

    @Column(nullable = false)
    private Integer puntajeA;

    @Column(nullable = false)
    private Integer puntajeB;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoValidacion estadoValidacion;

    @Column(length = 300)
    private String observaciones;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column
    private LocalDateTime fechaActualizacion;


    @PrePersist
    public void prePersist() {
        this.fechaRegistro      = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        if (this.estadoValidacion == null)
            this.estadoValidacion = EstadoValidacion.PENDIENTE;
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }


    public boolean estaValidado() { return this.estadoValidacion == EstadoValidacion.VALIDADO; }
    public boolean estaAnulado()  { return this.estadoValidacion == EstadoValidacion.ANULADO; }


    public enum EstadoValidacion {
        PENDIENTE, VALIDADO, ANULADO
    }
}
