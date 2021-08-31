package com.playtika.FinalProject.services.user;

import com.playtika.FinalProject.exceptions.UserException;
import com.playtika.FinalProject.exceptions.customErrors.UserErrorCode;
import com.playtika.FinalProject.models.Role;
import com.playtika.FinalProject.models.RoleType;
import com.playtika.FinalProject.models.User;
import com.playtika.FinalProject.models.dto.users.LoginResponse;
import com.playtika.FinalProject.models.dto.users.SignUpRequest;
import com.playtika.FinalProject.repositories.RoleRepository;
import com.playtika.FinalProject.repositories.UserRepository;
import com.playtika.FinalProject.security.services.JwtTokenService;
import com.playtika.FinalProject.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Autowired
    UserService userService;

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

    @BeforeEach
    void init() {
        authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        userRepository=mock(UserRepository.class);
    }

    @Test
    void testLogin() {

        when(user.getEmail()).thenReturn("");
        when(user.getUsername()).thenReturn("userName");
        List<Role> list=new ArrayList<>();
        when(authenticationManager.authenticate
                (eq(new UsernamePasswordAuthenticationToken("userName", "password",list))))
                .thenReturn(authentication);

        List<Role> roles= Arrays.asList(new Role(RoleType.ROLE_REGULAR_USER.name()));
        when(user.getRoles()).thenReturn(roles);
        when(jwtTokenService.createToken("userName",roles)).thenReturn("");
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        try{
            LoginResponse loginResponse = userService.login("userName", "password");
            loginResponse.setAccessToken("");
        }catch (UserException ex){
            Assertions.assertTrue(ex.getUserErrorCode().getMessage().contains(UserErrorCode.INVALID_CREDENTIALS.getMessage()));
        }


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
            Assertions.assertTrue(e.getUserErrorCode().getMessage().contains(UserErrorCode.INVALID_CREDENTIALS.getMessage()));
        }
    }

}

