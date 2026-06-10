package com.esports.sanctionservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sanciones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sancion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long usuarioId;

    @Column
    private Long equipoId;

    @Column(nullable = false, length = 300)
    private String motivo;

    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    @Column(nullable = false)
    private LocalDateTime fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSancion estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeveridadSancion severidad;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column
    private LocalDateTime fechaActualizacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion      = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        if (this.estado == null) this.estado = EstadoSancion.ACTIVA;
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    public boolean esBloqueante() {
        LocalDateTime ahora = LocalDateTime.now();
        return this.estado == EstadoSancion.ACTIVA
                && this.severidad != SeveridadSancion.ADVERTENCIA
                && ahora.isAfter(this.fechaInicio)
                && ahora.isBefore(this.fechaFin);
    }

    public boolean estaVigente() {
        LocalDateTime ahora = LocalDateTime.now();
        if (this.estado == EstadoSancion.ANULADA) {
            return false;
        }
        return ahora.isAfter(this.fechaInicio) && ahora.isBefore(this.fechaFin);
    }

    public enum EstadoSancion {
        ACTIVA,
        CERRADA,
        APELADA,
        ANULADA //nosfaltaba
    }

    public enum SeveridadSancion {
        ADVERTENCIA, SUSPENSION, EXPULSION
    }
}