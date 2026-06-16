package com.esports.tournament_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "torneos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Torneo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String nombre;

    @Column(nullable = false)
    private Long gameId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTorneo estado;

    @Column(nullable = false)
    private Integer maxParticipantes;

    @Column(nullable = false)
    private Integer participantesActuales;

    @Column(length = 300)
    private String motivoAnulacion;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.estado == null) this.estado == EstadoTorneo.INSCRIPCION;
        if this.participantesActuales == null) this.participantesActuales = 0;
        if (this.maxParticipantes == null) this.maxParticipantes = 16;
    }
    public enum EstadoTorneo {
        INSCRIPCION,
        EN_CURSO,
        FINALIZADO,
        ANULADO
    }
}
