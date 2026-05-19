package com.esports.rankingservice.repository;

import com.esports.rankingservice.model.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long> {


    @Query("SELECT r FROM Ranking r WHERE r.torneoId = :torneoId " +
           "ORDER BY r.puntos DESC, r.diferencia DESC, r.victorias DESC")
    List<Ranking> findByTorneoIdOrderByPosicion(@Param("torneoId") Long torneoId);

    Optional<Ranking> findByTorneoIdAndParticipanteId(Long torneoId, Long participanteId);

    boolean existsByTorneoIdAndParticipanteId(Long torneoId, Long participanteId);

    List<Ranking> findByParticipanteId(Long participanteId);
}
