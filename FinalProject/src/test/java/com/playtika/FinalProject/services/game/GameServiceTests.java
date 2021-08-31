package com.playtika.FinalProject.services.game;

import com.playtika.FinalProject.exceptions.GameSessionException;
import com.playtika.FinalProject.exceptions.UserException;
import com.playtika.FinalProject.exceptions.customErrors.UserErrorCode;
import com.playtika.FinalProject.models.GameSession;
import com.playtika.FinalProject.models.User;
import com.playtika.FinalProject.models.dto.GameSessionAddDTO;
import com.playtika.FinalProject.repositories.GameSessionRepository;
import com.playtika.FinalProject.repositories.UserRepository;
import com.playtika.FinalProject.security.services.JwtTokenService;
import com.playtika.FinalProject.services.GameSessionService;
import com.playtika.FinalProject.services.UserService;
import com.playtika.FinalProject.utils.CustomTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class GameServiceTests {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    private GameSessionRepository gameSessionRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    AuthenticationManager authenticationManager;
    //    @Mock
    Authentication authentication;
    @Mock
    SecurityContext securityContext;

    @InjectMocks
    UserService userService;

    @InjectMocks
    GameSessionService gameSessionService;

    User actualUser = mock(User.class);
    GameSession gameSession = mock(GameSession.class);

    @Mock
    private JwtTokenService jwtTokenService;

    @BeforeEach
    void init() {
        authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void addGameSessionNotAuthenticatedTest() {
        actualUser.setUsername("user");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(null);
        try {
            gameSessionService.addGameSession(new GameSessionAddDTO());
        } catch (UserException e) {
            Assertions.assertEquals(e.getUserErrorCode().getMessage(),
                    UserErrorCode.NOT_AUTHORIZED.getMessage());
        }
    }

    @Test
    public void addGameSessionIsPlayingTest() {
        when(authentication.getName()).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(null);
        when(actualUser.isPlaying()).thenReturn(true);
        when(jwtTokenService.getValidity()).thenReturn(new Date());
        when(userRepository.findByUsername("user2")).thenReturn(new User());
        try {
            gameSessionService.addGameSession(new GameSessionAddDTO());
        } catch (GameSessionException e) {
            Assertions.assertEquals(e.getUserErrorCode().getMessage(),
                    GameSessionException.GameSessionErrorCode.IS_PLAYING.getMessage());
        }
    }
    @Test
    public void stopGameSessionNotPlaying() {
        when(authentication.getName()).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(actualUser);
        actualUser.setUsername("user");
        when(actualUser.isPlaying()).thenReturn(false);
        verify(actualUser,times(0)).getGameSessions();
    }

    @Test
    public void stopGameSession() {
        when(authentication.getName()).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(actualUser);
        actualUser.setUsername("user");
        when(actualUser.getGameSessions()).thenReturn(new ArrayList<>());
        try{
            gameSessionService.stop();
        }catch (GameSessionException  ex){
            Assertions.assertEquals(ex.getUserErrorCode().getMessage(),
                    GameSessionException.GameSessionErrorCode.NO_ACTIVE_GAME.getMessage());
        }
    }

}
