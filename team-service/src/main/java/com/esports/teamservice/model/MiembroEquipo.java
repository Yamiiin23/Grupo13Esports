package com.esports.teamservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore; // 1. SE AGREGÓ ESTE IMPORT
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "miembros_equipo",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"equipo_id", "usuario_id"})
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiembroEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RolEnEquipo rolDentroEquipo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaIngreso;

    @PrePersist
    public void prePersist() {
        this.fechaIngreso = LocalDateTime.now();
        if (this.rolDentroEquipo == null) this.rolDentroEquipo = RolEnEquipo.JUGADOR;
    }

    public enum RolEnEquipo {
        CAPITAN, JUGADOR, SUPLENTE
    }
}