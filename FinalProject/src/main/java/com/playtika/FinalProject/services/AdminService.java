package com.playtika.FinalProject.services;

import com.playtika.FinalProject.exceptions.GameSessionException;
import com.playtika.FinalProject.exceptions.UserException;
import com.playtika.FinalProject.exceptions.customErrors.UserErrorCode;
import com.playtika.FinalProject.externalAPI.OnlineGameNameService;
import com.playtika.FinalProject.models.GameSession;
import com.playtika.FinalProject.models.User;
import com.playtika.FinalProject.models.dto.game.GameSessionInfoDTO;
import com.playtika.FinalProject.models.dto.game.GameSessionModifyDTO;
import com.playtika.FinalProject.models.dto.users.UserInfoAdminDTO;
import com.playtika.FinalProject.repositories.GameSessionRepository;
import com.playtika.FinalProject.repositories.RoleRepository;
import com.playtika.FinalProject.repositories.UserRepository;
import com.playtika.FinalProject.security.services.JwtTokenService;
import com.playtika.FinalProject.utils.BodyMessage;
import com.playtika.FinalProject.utils.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class AdminService {
    @Autowired
    UserService userService;

    public AdminService(UserService userService) {
        this.userService = userService;
    }


    private static Logger logger = LoggerFactory.getLogger(UserService.class);
    User actualUser;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameSessionRepository gameSessionRepository;


    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    OnlineGameNameService onlineGameNameService;

    public List<UserInfoAdminDTO> getAllUser(Pageable pageable) {
        return Converter.convertUsersToDTOList(userRepository.findAll(pageable).toList());
    }
    public BodyMessage addUserGameSession(String userName, GameSessionModifyDTO gameSessionModifyDTO) throws ExecutionException, InterruptedException, ParseException {
        User userToUpdate = userRepository.findByUsername(userName);
        String gameNameOk = onlineGameNameService.getGameName(gameSessionModifyDTO.getGameName()).get();
        if(gameNameOk==null||gameNameOk.isEmpty())
        {
            throw new GameSessionException(GameSessionException.GameSessionErrorCode.NONEXISTENT_GAME);
        }
        GameSession gameSession= gameSessionModifyDTO.convertToGameSession();
        gameSession.setUser(userToUpdate);
        userToUpdate.addGameSessions(gameSession);
        userToUpdate = userRepository.saveAndFlush(userToUpdate);
        this.userRepository.saveAndFlush(userToUpdate);
        return new BodyMessage("Success adding game Session !");
    }

    public BodyMessage updateUserGameSession(long id, GameSessionModifyDTO gameSessionModifyDTO) throws ExecutionException, InterruptedException, ParseException {
        long userId= gameSessionRepository.findIdByGameId(id);
        Optional<GameSession> gameSessionToUpdate=gameSessionRepository.findById(id);
        if(!gameSessionToUpdate.isPresent()){
            throw  new GameSessionException(GameSessionException.GameSessionErrorCode.NONEXISTENT_GAME);
        }
        gameSessionRepository.delete(gameSessionToUpdate.get());
        Optional<User> user=userRepository.findById(userId);
        GameSession gameSession= gameSessionModifyDTO.convertToGameSession();
        gameSession.setId(id);
        gameSession.setUser(user.get());
//        userToUpdate.addGameSessions(gameSession);
//        userToUpdate = userRepository.saveAndFlush(userToUpdate);
        gameSessionRepository.saveAndFlush(gameSession);
        return new BodyMessage("Success updating game Session!");
    }

    public List<GameSessionInfoDTO> getUserGameSession(String userName) throws ExecutionException, InterruptedException {
        User user = userRepository.findByUsername(userName);
        if(user==null){
            throw new UserException(UserErrorCode.INCORRECT_USERNAME);
        }
        List<GameSession> gameSessions=gameSessionRepository.findAllGameSession(userName);
        logger.info(gameSessions.size()+"");
        return Converter.convertGamesToDTOList(gameSessions);
    }
    //    `(date eq '2016-05-01') AND ((daily_play_time gt 2) OR (daily_play_time lt 5))

}
