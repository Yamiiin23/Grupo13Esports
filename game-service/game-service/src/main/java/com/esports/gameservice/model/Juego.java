package com.esports.gameservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "juegos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Juego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(nullable = false, length = 80)
    private String genero;

    @Column(nullable = false, length = 80)
    private String modalidad;

    @Column(nullable = false)
    private Integer jugadoresPorEquipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoJuego estado;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column
    private LocalDateTime fechaActualizacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoJuego.ACTIVO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    public enum EstadoJuego {
        ACTIVO, INACTIVO
    }
}
