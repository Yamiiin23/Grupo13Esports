package com.esports.ranking_service;

import com.esports.ranking_service.model.Ranking;
import com.esports.ranking_service.repository.RankingRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Random;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private RankingRepository rankingRepository;

    @Override
    public void run(String... args) throws Exception {
        Faker faker = new Faker();
        Random random = new Random();

        for (int i = 0; i < 30; i++) {
            Ranking ranking = new Ranking();

            ranking.setTorneoId((long) faker.number().numberBetween(1, 5));
            ranking.setParticipanteId((long) faker.number().numberBetween(1, 100));

            int victorias = faker.number().numberBetween(0, 15);
            int derrotas = faker.number().numberBetween(0, 10);

            ranking.setVictorias(victorias);
            ranking.setDerrotas(derrotas);
            ranking.setPuntos(victorias * 3);
            ranking.setDiferencia(victorias - derrotas);
            ranking.setPosicion(faker.number().numberBetween(1, 20));

            ranking.setMotivoBaja(null);

            rankingRepository.save(ranking);
        }
    }
}
