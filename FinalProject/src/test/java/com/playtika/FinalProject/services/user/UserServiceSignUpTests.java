package com.playtika.FinalProject.services.user;

import com.playtika.FinalProject.exceptions.GameSessionException;
import com.playtika.FinalProject.exceptions.UserException;
import com.playtika.FinalProject.exceptions.customErrors.UserErrorCode;
import com.playtika.FinalProject.models.Role;
import com.playtika.FinalProject.models.User;
import com.playtika.FinalProject.models.dto.LoginResponse;
import com.playtika.FinalProject.models.dto.SignUpRequest;
import com.playtika.FinalProject.repositories.RoleRepository;
import com.playtika.FinalProject.repositories.UserRepository;
import com.playtika.FinalProject.security.services.JwtTokenService;
import com.playtika.FinalProject.services.UserService;
import com.playtika.FinalProject.utils.CustomTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class UserServiceSignUpTests {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

//    @Autowired
//    UserService userService;

    @Mock
    User user;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    SignUpRequest signUpRequest;

    @Mock
    Authentication authentication;

    @BeforeEach
    void init() {
        authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        userRepository=mock(UserRepository.class);
    }

    @Test
    void testSignUpUserAlreadyExists() {
        when(userRepository.existsByUsername("admin")).thenReturn(true);
        try {
            signUpRequest.setUserName("admin");
            userService.signUp(signUpRequest);
        } catch (UserException e) {
            Assertions.assertEquals(e.getUserErrorCode().getMessage()
                    , UserErrorCode.USER_EXISTS.getMessage());

        }
    }

    @Test
    void testSignUpInvalidEmail() {
        try {
            when(userRepository.existsByUsername(any())).thenReturn(false);
            when(user.setEmail("gigel123")).thenReturn(new User().setEmail("gigel123"));
            when(roleRepository.findByName(any())).thenReturn(new Role());
            when(userRepository.saveAndFlush(any())).thenReturn(new User());
            signUpRequest.setUserName("gigel");
            signUpRequest.setEmail("gigel123");
            signUpRequest.setPassword("gigel");
            userService.signUp(signUpRequest);
        } catch (UserException e) {
            Assertions.assertEquals(e.getUserErrorCode().getMessage(),
                    UserErrorCode.INCORRECT_EMAIL.getMessage());

        }
    }

    @Test
    void testSignUpInvalidPassword() {
        try {
            when(userRepository.existsByUsername(any())).thenReturn(false);
            when(user.setEmail("gigel123@yahoo.com")).thenReturn(new User().setEmail("gigel123@yahoo.com"));
            when(user.setPassword("aa")).thenReturn(new User().setPassword("aa"));
            when(roleRepository.findByName(any())).thenReturn(new Role());
            when(userRepository.saveAndFlush(any())).thenReturn(new User());
            signUpRequest.setUserName("gigel");
            signUpRequest.setEmail("gigel123@yahoo.com");
            signUpRequest.setPassword("aa");
            userService.signUp(signUpRequest);
        } catch (UserException e) {
            Assertions.assertEquals(e.getUserErrorCode().getMessage(),
                    UserErrorCode.INCORRECT_PASSWORD.getMessage());

        }
    }

    @Test
    void testSignUpInvalidUserName() {
        try {
            when(userRepository.existsByUsername(any())).thenReturn(false);
            when(user.setEmail("gigel123@yahoo.com")).thenReturn(new User().setEmail("gigel123@yahoo.com"));
            when(user.setPassword("longer")).thenReturn(new User().setPassword("longer"));
            when(user.setUsername("bba")).thenReturn(new User().setUsername("bba"));
            when(roleRepository.findByName(any())).thenReturn(new Role());
            when(userRepository.saveAndFlush(any())).thenReturn(new User());
            signUpRequest.setUserName("bba");
            signUpRequest.setEmail("gigel123@yahoo.com");
            signUpRequest.setPassword("longer");
            userService.signUp(signUpRequest);
        } catch (UserException e) {
            Assertions.assertEquals(e.getUserErrorCode().getMessage(),
                    UserErrorCode.INCORRECT_USERNAME.getMessage());

        }
    }

    @Test
    void testSignUpInvalidMaxDailyPlayTimeHours() {
        try {
            when(userRepository.existsByUsername(any())).thenReturn(false);
            when(user.setEmail("gigel123@yahoo.com")).thenReturn(new User().setEmail("gigel123@yahoo.com"));
            when(user.setPassword("longer")).thenReturn(new User().setPassword("longer"));
            when(user.setUsername("gigel")).thenReturn(new User().setUsername("gigel"));
            when(user.setMaximumDailyPlayTime(new CustomTime(26, 10)))
                    .thenReturn(new User().setMaximumDailyPlayTime(new CustomTime(26, 10)));
            when(roleRepository.findByName(any())).thenReturn(new Role());
            when(userRepository.saveAndFlush(any())).thenReturn(new User());
            signUpRequest.setUserName("gigel");
            signUpRequest.setEmail("gigel123@yahoo.com");
            signUpRequest.setPassword("longer");
            signUpRequest.setMaximumDailyPlayTime(new CustomTime(26, 10));
            userService.signUp(signUpRequest);
        } catch (GameSessionException e) {
            Assertions.assertEquals(e.getUserErrorCode().getMessage(),
                    GameSessionException.GameSessionErrorCode.EXCEED_DAILY_HOURS.getMessage());

        }
    }

    @Test
    void testSignUpInvalidMaxDailyPlayTimeMinutes() {
        try {
            when(userRepository.existsByUsername(any())).thenReturn(false);
            when(user.setEmail("gigel123@yahoo.com")).thenReturn(new User().setEmail("gigel123@yahoo.com"));
            when(user.setPassword("longer")).thenReturn(new User().setPassword("longer"));
            when(user.setUsername("gigel")).thenReturn(new User().setUsername("gigel"));
            when(user.setMaximumDailyPlayTime(new CustomTime(10, 70)))
                    .thenReturn(new User().setMaximumDailyPlayTime(new CustomTime(10, 70)));
            when(roleRepository.findByName(any())).thenReturn(new Role());
            when(userRepository.saveAndFlush(any())).thenReturn(new User());
            signUpRequest.setUserName("gigel");
            signUpRequest.setEmail("gigel123@yahoo.com");
            signUpRequest.setPassword("longer");
            signUpRequest.setMaximumDailyPlayTime(new CustomTime(10, 70));
            userService.signUp(signUpRequest);
        } catch (GameSessionException e) {
            Assertions.assertEquals(e.getUserErrorCode().getMessage(),
                    GameSessionException.GameSessionErrorCode.EXCEED_MINUTES.getMessage());
        }
    }

    @Test
    void testSignUpInvalidMaxDailyPlayTimeNegatives() {
        try {
            User user1=new User();
            when(userRepository.existsByUsername(any())).thenReturn(false);
            when(user.setEmail("gigel123@yahoo.com")).thenReturn(user1.setEmail("gigel123@yahoo.com"));
            when(user.setPassword("longer")).thenReturn(user1.setPassword("longer"));
            when(user.setUsername("gigel")).thenReturn(user1.setUsername("gigel"));
            when(user.setMaximumDailyPlayTime(new CustomTime(-10, 0)))
                    .thenReturn(user1.setMaximumDailyPlayTime(new CustomTime(-10, 0)));
            when(roleRepository.findByName(any())).thenReturn(new Role());
            when(userRepository.saveAndFlush(any())).thenReturn(user1);
            signUpRequest.setUserName("gigel");
            signUpRequest.setEmail("gigel123@yahoo.com");
            signUpRequest.setPassword("longer");
            signUpRequest.setMaximumDailyPlayTime(new CustomTime(-10, 0));
            userService.signUp(signUpRequest);
        } catch (GameSessionException e) {
            Assertions.assertEquals(e.getUserErrorCode().getMessage(),
                    GameSessionException.GameSessionErrorCode.NEGATIVE_NUMBER.getMessage());

        }
    }
}
