package com.playtika.FinalProject.repositories;

import com.playtika.FinalProject.models.GameSession;
import com.playtika.FinalProject.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    @Query(value = "SELECT * FROM game_sessions g WHERE g.user_id in " +
            "(select u.id from users u where username like :userName)", nativeQuery = true)
    List<GameSession> findAllGameSession(String userName);

    @Query(value = "SELECT u.id FROM users u WHERE u.id in " +
            "(select g.user_id from game_sessions g where g.id = :id)", nativeQuery = true)
    long findIdByGameId(long id);
}
