package com.playtika.FinalProject.services;

import com.playtika.FinalProject.exceptions.GameSessionException;
import com.playtika.FinalProject.exceptions.UserException;
import com.playtika.FinalProject.exceptions.customErrors.UserErrorCode;
import com.playtika.FinalProject.utils.Converter;
import com.playtika.FinalProject.utils.CustomTime;
import com.playtika.FinalProject.models.GameSession;
import com.playtika.FinalProject.models.dto.GameSessionAddDTO;
import com.playtika.FinalProject.repositories.GameSessionRepository;
import com.playtika.FinalProject.models.User;
import com.playtika.FinalProject.repositories.UserRepository;
import com.playtika.FinalProject.security.services.JwtTokenService;
import com.playtika.FinalProject.utils.BodyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
public class GameSessionService {

    User actualUser;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private UserRepository userRepository;


    public ResponseEntity addGameSession(GameSessionAddDTO request) {
        actualUser = userRepository.findByUsername(getActualUserName());
        if (jwtTokenService.getValidity().compareTo(new Date()) == -1 || actualUser == null) {
            throw new UserException(UserErrorCode.NOT_AUTHORIZED);
        }
        if (actualUser.isPlaying()) {
            throw new GameSessionException(GameSessionException.GameSessionErrorCode.IS_PLAYING);
        }
        actualUser.setPlaying(true);
        GameSession gameSession = new GameSession();
        gameSession.setGameName(request.getGameName());
        gameSession.setStartDate(new Date());
        gameSession.setUser(actualUser);
        actualUser.addGameSessions(gameSession);
        userRepository.saveAndFlush(actualUser);
        actualUser.setExceedingDailyPlayTime(actualUser.getMaximumDailyPlayTime().compareTo(actualUser.getPlayedTime()) == -1);
        if (actualUser.isExceedingDailyPlayTime()) {
            return new ResponseEntity
                    (new BodyMessage("Successful adding GAME SESSION!***Daily play time exceed"), HttpStatus.CONFLICT);
        }
        return new ResponseEntity(new BodyMessage("Successful adding GAME SESSION"), HttpStatus.OK);
    }

    public void stop() {
        actualUser = userRepository.findByUsername(getActualUserName());
        if (actualUser.isPlaying()) {
            int len = actualUser.getGameSessions().size() - 1;
            actualUser.getGameSessions().set(len, updateDuration(len));
            actualUser.setPlaying(false);
        }
        userRepository.saveAndFlush(actualUser);
    }

    private GameSession updateDuration(int len) {

        GameSession activeGameSession = actualUser.getGameSessions().get(len);
        Date startDay = activeGameSession.getStartDate();
        LocalDateTime test = Converter.convertToLocalDateTime(startDay);
        LocalDateTime test2 = Converter.convertToLocalDateTime(new Date());

        long hour2 = ChronoUnit.HOURS.between(test, test2);
        long minutes = ChronoUnit.MINUTES.between(test, test2) - hour2 * 60;
        activeGameSession.setDuration(new CustomTime((int) hour2, (int) minutes));
        return activeGameSession;
    }

    public List<GameSession> getAllGameSessions(Pageable pageable) {
        return this.gameSessionRepository.findAll(pageable).getContent();
    }

    private String getActualUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.isAuthenticated()) {
            throw new UserException(UserErrorCode.NOT_AUTHORIZED);
        }
        return auth.getName();
    }
}
