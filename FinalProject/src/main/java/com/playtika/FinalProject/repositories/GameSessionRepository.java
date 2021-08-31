package com.playtika.FinalProject.repositories;

import com.playtika.FinalProject.models.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
}
