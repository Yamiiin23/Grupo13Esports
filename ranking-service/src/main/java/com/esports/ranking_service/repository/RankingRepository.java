package com.esports.ranking_service.repository;

import com.esports.ranking_service.model.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long> {

    List<Ranking> findByTorneoIdOrderByPosicion(Long torneoId);

    Optional<Ranking> findByTorneoIdAndParticipanteId(Long torneoId, Long participanteId);

    boolean existsByTorneoIdAndParticipanteId(Long torneoId, Long participanteId);
}