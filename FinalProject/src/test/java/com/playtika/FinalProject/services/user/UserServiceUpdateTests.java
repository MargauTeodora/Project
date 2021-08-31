package com.playtika.FinalProject.services.user;

import com.playtika.FinalProject.exceptions.UserException;
import com.playtika.FinalProject.exceptions.customErrors.UserErrorCode;
import com.playtika.FinalProject.models.Role;
import com.playtika.FinalProject.models.RoleType;
import com.playtika.FinalProject.models.User;
import com.playtika.FinalProject.models.dto.users.UpdateUserDTO;
import com.playtika.FinalProject.repositories.RoleRepository;
import com.playtika.FinalProject.repositories.UserRepository;
import com.playtika.FinalProject.services.UserService;
import com.playtika.FinalProject.utils.CustomTime;
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

import static org.mockito.Mockito.when;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)

public class UserServiceUpdateTests {

    @Mock
    UserRepository userRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    Authentication authentication;

    @InjectMocks
    UserService userService;
    private UpdateUserDTO userDTO = new UpdateUserDTO();

    @BeforeEach
    void init() {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }


    @Test
    void testUpdateNotLogged() {
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
        User actualUser = new User();
        actualUser.setRoles(Arrays.asList(new Role(RoleType.ROLE_MANAGER.name())));
        when(userRepository.findByUsername("actualUser")).thenReturn(actualUser);
        when(authentication.getName()).thenReturn("actualUser");
        try {
            UpdateUserDTO updateUserDTO = new UpdateUserDTO();
            updateUserDTO.setUsername("userToDelete");
            userService.updateUser(updateUserDTO);
        } catch (UserException e) {
            Assertions.assertEquals(e.getUserErrorCode().getMessage(),
                    UserErrorCode.NO_UPDATE_USER.getMessage());
        }
    }

    @Test
    void testUpdateNotSame() {
        User userToUpdate = new User();
        userToUpdate.setUsername("userToUpdate");
        userToUpdate.setRoles(Arrays.asList(new Role(RoleType.ROLE_ADMIN.name())));
        when(userRepository.findByUsername("userToUpdate")).thenReturn(userToUpdate);
        when(authentication.getName()).thenReturn("actualUser");
        User actualUser = new User();
        actualUser.setUsername("actualUser");
        actualUser.setRoles(Arrays.asList(new Role(RoleType.ROLE_MANAGER.name())));
        when(userRepository.findByUsername("actualUser")).thenReturn(actualUser);
        try {
            UpdateUserDTO updateUserDTO = new UpdateUserDTO();
            updateUserDTO.setUsername("userToUpdate");
            updateUserDTO.setRole(RoleType.ROLE_ADMIN);
            userService.updateUser(updateUserDTO);
        } catch (UserException e) {
            Assertions.assertEquals(e.getUserErrorCode().getMessage(),
                    UserErrorCode.NO_PERMISSION.getMessage());
        }
    }

    @Test
    void testUpdateUserCorrect() {

        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setUsername("userToUpdate");
        updateUserDTO.setFirstName("ActualUser");
        updateUserDTO.setPassword("password");
        updateUserDTO.setLastName("LastName");
        updateUserDTO.setMaximumDailyPlayTime(new CustomTime(10,10));
        updateUserDTO.setRole(RoleType.ROLE_REGULAR_USER);


        User userToUpdate = new User();
        userToUpdate.setUsername("userToUpdate");
        userToUpdate.setRoles(Arrays.asList(new Role(RoleType.ROLE_ADMIN.name())));

        when(roleRepository.findByName(RoleType.ROLE_REGULAR_USER.name())).thenReturn(new Role(RoleType.ROLE_REGULAR_USER.name()) );
        when(roleRepository.findByName(RoleType.ROLE_ADMIN.name())).thenReturn(new Role(RoleType.ROLE_ADMIN.name()) );
        when(roleRepository.findByName(RoleType.ROLE_MANAGER.name())).thenReturn(new Role(RoleType.ROLE_MANAGER.name()) );

        when(userRepository.findByUsername("userToUpdate")).thenReturn(userToUpdate);
        when(authentication.getName()).thenReturn("actualUser");

        User actualUser = new User();
        actualUser.setUsername("actualUser");
        actualUser.setRoles(Arrays.asList(new Role(RoleType.ROLE_MANAGER.name()),
                new Role(RoleType.ROLE_ADMIN.name())));

        when(userRepository.findByUsername("actualUser")).thenReturn(actualUser);

        try {
            userService.updateUser(updateUserDTO);
        } catch (Exception e) {
            Assertions.assertNotNull(e.getMessage());
        }

    }
}
