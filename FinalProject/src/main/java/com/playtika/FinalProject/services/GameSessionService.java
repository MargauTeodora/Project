package com.playtika.FinalProject.services;

import com.playtika.FinalProject.exceptions.GameSessionException;
import com.playtika.FinalProject.exceptions.UserException;
import com.playtika.FinalProject.exceptions.customErrors.ErrorCode;
import com.playtika.FinalProject.models.CustomTime;
import com.playtika.FinalProject.models.GameSession;
import com.playtika.FinalProject.models.dto.AddNewGameSessionDTO;
import com.playtika.FinalProject.repositories.GameSessionRepository;
import com.playtika.FinalProject.models.User;
import com.playtika.FinalProject.repositories.UserRepository;
import com.playtika.FinalProject.utils.BodyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class GameSessionService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);
    User actualUser;


    @Autowired
    private GameSessionRepository gameSessionRepository;


    @Autowired
    private UserRepository userRepository;


    public ResponseEntity addGameSession(AddNewGameSessionDTO request) {
        actualUser = userRepository.findByUsername(getActualUserName());
        if (actualUser == null) {
            throw new UserException(ErrorCode.NOT_AUTHORIZED);
        }
        if (actualUser.isPlaying()) {
            throw new GameSessionException(GameSessionException.GameSessionErrorCode.IS_PLAYING);
        }
        actualUser.setPlaying(true);
        GameSession gameSession = new GameSession();
        gameSession.setGameName(request.getGameName());
        gameSession.setStartDate(new Date());
        gameSession.setUser(actualUser);
//        TODO setteri pt gamesession care vin din request
        actualUser.addGameSessions(gameSession);
        userRepository.saveAndFlush(actualUser);
        logger.info(actualUser.getPlayedTime().toString());
        logger.info(actualUser.getGameSessions().size()+"");
        actualUser.setExceedingDailyPlayTime(actualUser.getMaximumDailyPlayTime().compareTo(actualUser.getPlayedTime())==-1);
        if(actualUser.isExceedingDailyPlayTime()){
            return new ResponseEntity
                    (new BodyMessage("Successful adding for GAME SESSION!***Daily play time exceed"),HttpStatus.CONFLICT);
        }
        return new ResponseEntity(new BodyMessage("Successful adding for GAME SESSION"),HttpStatus.OK);
    }

    public void stop() {
        actualUser = userRepository.findByUsername(getActualUserName());
        if (actualUser.isPlaying()) {
            int len=actualUser.getGameSessions().size()-1;
            actualUser.getGameSessions().set(len,updateDuration(len));
            actualUser.setPlaying(false);
        }
        userRepository.saveAndFlush(actualUser);
    }

    private void updateGameSession(){

    }
    private GameSession updateDuration(int len){

        GameSession activeGameSession = actualUser.getGameSessions().get(len);
        Date startDay=activeGameSession.getStartDate();
        LocalDateTime test=convertToLocalDateTime(startDay);
        LocalDateTime test2=convertToLocalDateTime(new Date());

        long hour2= ChronoUnit.HOURS.between(test, test2);
        long minutes= ChronoUnit.MINUTES.between(test, test2)-hour2*60;
        activeGameSession.setDuration(new CustomTime((int)hour2,(int)minutes));
        return activeGameSession;
    }
    public LocalDateTime convertToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public List<GameSession> getAllGameSessions() {
        return gameSessionRepository.findAll();
    }

    private String getActualUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
}
