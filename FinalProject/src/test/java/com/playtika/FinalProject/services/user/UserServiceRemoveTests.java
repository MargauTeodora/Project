package com.playtika.FinalProject.services.user;

import com.playtika.FinalProject.exceptions.UserException;
import com.playtika.FinalProject.exceptions.customErrors.UserErrorCode;
import com.playtika.FinalProject.models.Role;
import com.playtika.FinalProject.models.RoleType;
import com.playtika.FinalProject.models.User;
import com.playtika.FinalProject.repositories.UserRepository;
import com.playtika.FinalProject.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)

public class UserServiceRemoveTests {

    @Mock
    UserRepository userRepository;
    @Mock
    Authentication authentication;

    @InjectMocks
    UserService userService;


    @BeforeEach
    void init() {
        authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }


    @Test
    void testRemoveNotManager() {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        User actualUser=new User();
        actualUser.setRoles(Arrays.asList(new Role(RoleType.ROLE_REGULAR_USER.name())));
        when(userRepository.findByUsername("user")).thenReturn(actualUser);
        when(authentication.getName()).thenReturn("user");
        try {
            userService.removeUser("userDelete");
        } catch (UserException e) {
            Assertions.assertEquals(e.getUserErrorCode().getMessage(),
                    UserErrorCode.NO_PERMISSION.getMessage());
        }
    }
    @Test
    void testRemoveUserNotExist() {
        when(userRepository.existsByUsername("")).thenReturn(false);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        User actualUser=new User();
        actualUser.setRoles(Arrays.asList(new Role(RoleType.ROLE_MANAGER.name())));
        when(userRepository.findByUsername("user")).thenReturn(actualUser);
        when(authentication.getName()).thenReturn("user");
        try {
            userService.removeUser("userDelete");
        } catch (UserException e) {
            Assertions.assertEquals(e.getUserErrorCode().getMessage(),
                    UserErrorCode.NO_DELETE_USER.getMessage());
        }
    }

    @Test
    void testRemoveNotAllowed() {
        when(userRepository.existsByUsername("user")).thenReturn(true);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        User actualUser=new User();
        actualUser.setRoles(Arrays.asList(new Role(RoleType.ROLE_ADMIN.name()),
                new Role(RoleType.ROLE_MANAGER.name()) ));
        when(userRepository.findByUsername("user")).thenReturn(actualUser);
        when(authentication.getName()).thenReturn("user");
        try {
            userService.removeUser("user");
        } catch (UserException e) {
            Assertions.assertEquals(e.getUserErrorCode().getMessage(),
                    UserErrorCode.NOT_ALLOWED.getMessage());
        }
    }
    @Test
    void testRemoveCorrect() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        User actualUser=new User();
        actualUser.setRoles(Arrays.asList(new Role(RoleType.ROLE_ADMIN.name()),
                new Role(RoleType.ROLE_MANAGER.name()) ));
        when(userRepository.findByUsername("user")).thenReturn(actualUser);
        when(userRepository.findByUsername("userDelete")).thenReturn(new User());
        when(authentication.getName()).thenReturn("user");
        try {
            userService.removeUser("user");
        } catch (UserException e) {
            Assertions.assertEquals(e.getUserErrorCode().getMessage(),
                    UserErrorCode.NOT_ALLOWED.getMessage());
        }
    }
}
