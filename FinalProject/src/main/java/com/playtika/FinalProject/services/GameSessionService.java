package com.playtika.FinalProject.services;

import com.playtika.FinalProject.exceptions.UserException;
import com.playtika.FinalProject.exceptions.customErrors.ErrorCode;
import com.playtika.FinalProject.models.GameSession;
import com.playtika.FinalProject.models.dto.AddNewGameSessionDTO;
import com.playtika.FinalProject.repositories.GameSessionRepository;
import com.playtika.FinalProject.models.User;
import com.playtika.FinalProject.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class GameSessionService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);
    User actualUser;


    @Autowired
    private GameSessionRepository gameSessionRepository;


    @Autowired
    private UserRepository userRepository;


    public GameSession addGameSession(AddNewGameSessionDTO request) {
        actualUser = userRepository.findByUsername(getActualUserName());
        if (gameSessionRepository.findById(request.getId()).isPresent()){
            throw new UserException(ErrorCode.USER_EXISTS);
        }
        GameSession gameSession = new GameSession();
        gameSession.setGameName(request.getGameName());
        gameSession.setStartDate(new Date());
        gameSession.setUser(actualUser);
//        TODO setteri pt gamesession care vin din request
        actualUser.addGameSessions(gameSession);
        userRepository.saveAndFlush(actualUser);
        return gameSession;
    }
    public List<GameSession> getAllGameSessions() {
        return gameSessionRepository.findAll();
    }

    private String getActualUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
}
