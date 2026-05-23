package com.esports.registration_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inscripciones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long torneoId; // ID lógico correspondiente a tournament-service

    @Column(nullable = false)
    private Long equipoId; // ID lógico correspondiente a team-service

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoInscripcion estado;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaInscripcion;

    @PrePersist
    public void prePersist() {
        this.fechaInscripcion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoInscripcion.PENDIENTE;
        }
    }

    public enum EstadoInscripcion {
        PENDIENTE, ACEPTADA, RECHAZADA
    }
}