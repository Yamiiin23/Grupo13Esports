package com.esports.match_service.config;

import com.esports.match_service.model.Partida;
import com.esports.match_service.repository.PartidaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);
    private final PartidaRepository partidaRepository;

    public DataLoader(PartidaRepository partidaRepository) {
        this.partidaRepository = partidaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (partidaRepository.count() == 0) {
            log.info("[match-service] Base de datos vacía. Población automática de datos de prueba en marcha...");

            Partida partida1 = Partida.builder()
                    .torneoId(1L) // Copa de Campeones (Asumiendo que existe en tournament-service)
                    .equipoLocalId(10L)
                    .equipoVisitanteId(11L)
                    .estado(Partida.EstadoPartida.PENDIENTE)
                    .build();

            Partida partida2 = Partida.builder()
                    .torneoId(1L)
                    .equipoLocalId(12L)
                    .equipoVisitanteId(13L)
                    .estado(Partida.EstadoPartida.EN_CURSO)
                    .resultado("1 - 0 provisional")
                    .build();

            Partida partida3 = Partida.builder()
                    .torneoId(2L) // Liga de Verano
                    .equipoLocalId(14L)
                    .equipoVisitanteId(15L)
                    .estado(Partida.EstadoPartida.FINALIZADA)
                    .resultado("3 - 2")
                    .build();

            partidaRepository.saveAll(List.of(partida1, partida2, partida3));

            log.info("[match-service] Población finalizada con éxito. Se inyectaron {} partidas de prueba.", partidaRepository.count());
        } else {
            log.info("[match-service] La base de datos ya cuenta con registros de partidas. Omitiendo DataLoader.");
        }
    }
}