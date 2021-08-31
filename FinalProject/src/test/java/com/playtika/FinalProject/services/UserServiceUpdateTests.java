package com.playtika.FinalProject.services;

import com.playtika.FinalProject.exceptions.UserException;
import com.playtika.FinalProject.exceptions.customErrors.UserErrorCode;
import com.playtika.FinalProject.models.Role;
import com.playtika.FinalProject.models.RoleType;
import com.playtika.FinalProject.models.User;
import com.playtika.FinalProject.models.dto.LoginResponse;
import com.playtika.FinalProject.models.dto.SignUpRequest;
import com.playtika.FinalProject.models.dto.UpdateUserDTO;
import com.playtika.FinalProject.repositories.RoleRepository;
import com.playtika.FinalProject.repositories.UserRepository;
import com.playtika.FinalProject.security.services.JwtTokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)

public class UserServiceUpdateTests {

    @Mock
    UserRepository userRepository;
    @Mock
    Authentication authentication;

    @InjectMocks
    UserService userService;
    private UpdateUserDTO userDTO=new UpdateUserDTO();


    @Test
    void testUpdateNotLogged() {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(null);
        try {
            userService.updateUser(userDTO);
        } catch (UserException e) {
            Assertions.assertEquals(e.getUserErrorCode().getMessage(),
                    UserErrorCode.NOT_AUTHORIZED.getMessage());
        }
    }
    @Test
    void testUpdateUserNotExist() {
        when(userRepository.findByUsername("userToDelete")).thenReturn(null);
        User actualUser=new User();
        actualUser.setRoles(Arrays.asList(new Role(RoleType.ROLE_MANAGER.name())));
        when(userRepository.findByUsername("actualUser")).thenReturn(actualUser);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("actualUser");

        try {
            UpdateUserDTO updateUserDTO=new UpdateUserDTO();
            updateUserDTO.setUsername("userToDelete");
            userService.updateUser(updateUserDTO);
        } catch (UserException e) {
            Assertions.assertEquals(e.getUserErrorCode().getMessage(),
                    UserErrorCode.NO_UPDATE_USER.getMessage());
        }
    }

    @Test
    void testUpdateNotSame() {
        User userToUpdate=new User();
        userToUpdate.setUsername("userToUpdate");
        when(userRepository.findByUsername("userToUpdate")).thenReturn(userToUpdate);

        User actualUser=new User();
        actualUser.setRoles(Arrays.asList(new Role(RoleType.ROLE_MANAGER.name())));
        
        when(userRepository.findByUsername("actualUser")).thenReturn(actualUser);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("actualUser");

        try {
            UpdateUserDTO updateUserDTO=new UpdateUserDTO();
            updateUserDTO.setUsername("userToDelete");
            userService.updateUser(updateUserDTO);
        } catch (UserException e) {
            Assertions.assertEquals(e.getUserErrorCode().getMessage(),
                    UserErrorCode.NO_UPDATE_USER.getMessage());
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
