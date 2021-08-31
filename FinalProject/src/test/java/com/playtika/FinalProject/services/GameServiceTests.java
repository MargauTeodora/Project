package com.playtika.FinalProject.services;
import com.playtika.FinalProject.repositories.GameSessionRepository;
import com.playtika.FinalProject.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.web.servlet.MockMvc;

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
    @Mock
    Authentication authentication;
    @Mock
    SecurityContext securityContext;

    @InjectMocks
    UserService userService;

    @Test
    public void unTest(){

    }
}
