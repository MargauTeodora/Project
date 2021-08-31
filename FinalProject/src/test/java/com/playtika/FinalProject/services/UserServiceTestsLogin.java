package com.playtika.FinalProject.services;

import com.playtika.FinalProject.exceptions.GameSessionException;
import com.playtika.FinalProject.exceptions.UserException;
import com.playtika.FinalProject.exceptions.customErrors.UserErrorCode;
import com.playtika.FinalProject.models.Role;
import com.playtika.FinalProject.models.User;
import com.playtika.FinalProject.models.dto.LoginRequest;
import com.playtika.FinalProject.models.dto.LoginResponse;
import com.playtika.FinalProject.models.dto.SignUpRequest;
import com.playtika.FinalProject.repositories.RoleRepository;
import com.playtika.FinalProject.repositories.UserRepository;
import com.playtika.FinalProject.security.services.JwtTokenService;
import com.playtika.FinalProject.utils.Converter;
import com.playtika.FinalProject.utils.CustomTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

import static org.mockito.Mockito.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class UserServiceTestsLogin {

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

//    @Autowired
//    UserService userService;

    @Mock
    User user;
    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    SignUpRequest signUpRequest;


    @Test
    void testLogin() {
        when(authenticationManager.authenticate
                (new UsernamePasswordAuthenticationToken("", "password")))
                .thenReturn(authentication);
        when(user.getEmail()).thenReturn("email@yahoo.com");
        when(user.getUsername()).thenReturn("userName");
        when(user.getRoles()).thenReturn(new ArrayList<>());
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        LoginResponse loginResponse = userService.login("userName", "password");
        Assertions.assertEquals( loginResponse.getUserName(),"userName");
    }

    @Test
    void testLoginInvalidCredentials() {
        doThrow(new UserException(UserErrorCode.INVALID_CREDENTIALS)).when(authenticationManager)
                .authenticate
                (new UsernamePasswordAuthenticationToken("userName", "password"));
        when(user.getEmail()).thenReturn("email@yahoo.com");
        when(user.getUsername()).thenReturn("userName");
        when(user.getRoles()).thenReturn(new ArrayList<>());
        when(userRepository.findByUsername(anyString())).thenReturn(user);

        try {
            userService.login("userName", "password");
        } catch (UserException e) {
            Assertions.assertTrue(e.getUserErrorCode().getMessage().contains("Invalid username/password supplied"));
        }
    }

}

