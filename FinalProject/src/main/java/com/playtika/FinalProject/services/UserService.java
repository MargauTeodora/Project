package com.playtika.FinalProject.services;

import com.playtika.FinalProject.exceptions.UserException;
import com.playtika.FinalProject.exceptions.customErrors.UserErrorCode;
import com.playtika.FinalProject.models.GameSession;
import com.playtika.FinalProject.models.dto.*;
import com.playtika.FinalProject.models.Role;
import com.playtika.FinalProject.models.RoleType;
import com.playtika.FinalProject.models.User;
import com.playtika.FinalProject.repositories.RoleRepository;
import com.playtika.FinalProject.repositories.UserRepository;
import com.playtika.FinalProject.security.services.JwtTokenService;
import org.apache.juli.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);
    User actualUser;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private AuthenticationManager authenticationManager;


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        final User user = userRepository.findByUsername(userName);
        if (user == null) {
            throw new UsernameNotFoundException("User '" + userName + "' not found");
        }
        return org.springframework.security.core.userdetails.User
                .withUsername(userName)
                .password(user.getPassword())
                .authorities(user.getRoles())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    public LoginResponse login(String userName, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
            User user = userRepository.findByUsername(userName);
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setEmail(user.getEmail());
            loginResponse.setUserName(user.getUsername());
            loginResponse.setAccessToken(jwtTokenService.createToken(userName, user.getRoles()));
            return loginResponse;
        } catch (AuthenticationException e) {
            throw new UserException(UserErrorCode.INVALID_CREDENTIALS);
        }
    }

    public User signUp(SignUpRequest request) {
        if (userRepository.existsByUsername(request.getUserName())) {
            throw new UserException(UserErrorCode.USER_EXISTS);
        }
        User user = new User();
        user.setUsername(request.getUserName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        if (request.getMaximumDailyPlayTime() != null) {
            user.setMaximumDailyPlayTime(request.getMaximumDailyPlayTime());
        }

        user.setRoles(Arrays.asList(roleRepository.findByName(RoleType.ROLE_REGULAR_USER.name())));
        user = userRepository.saveAndFlush(user);
        return user;
    }

    public void removeUser(String userName) {
        actualUser = userRepository.findByUsername(getActualUserName());
        if (!actualUser.isManager()) {
            throw new UserException(UserErrorCode.NO_PERMISSION);
        }
        if (!userRepository.existsByUsername(userName)) {
            throw new UserException(UserErrorCode.NO_DELETE_USER);
        }
        User userToDelete = userRepository.findByUsername(userName);
        if (hasNoPermissionToDelete(userToDelete)) {
            throw new UserException(UserErrorCode.NOT_ALLOWED);
        }
        userRepository.deleteByUsername(userName);
    }

    public void updateUser(UpdateUserDTO userFromBody) {
        actualUser = userRepository.findByUsername(getActualUserName());
        User userToUpdate = getUserToUpdate(userFromBody);
        if (!isSameUser(userToUpdate)) {
            if (hasNoPermission(userToUpdate)) {
                throw new UserException(UserErrorCode.NO_PERMISSION);
            }
            updateGivenField(userFromBody, userToUpdate);
        }
        updateAllowedField(userFromBody, userToUpdate);
        userToUpdate = userRepository.saveAndFlush(userToUpdate);
        this.userRepository.saveAndFlush(userToUpdate);
    }


    public List<User> getAllUser(Pageable pageable) {
        return userRepository.findAll(pageable).toList();
    }

    public UserInfoDTO getUserInfo() {
        actualUser = userRepository.findByUsername(getActualUserName());
        if (actualUser == null) {
            throw new UserException(UserErrorCode.NOT_AUTHORIZED);
        }
        return new UserInfoDTO(actualUser.getUsername(), actualUser.getEmail(), actualUser.getFirstName(), actualUser.getLastName(),
                actualUser.getMaximumDailyPlayTime());
    }

    public List<GameSessionInfoDTO> getGameSession() {
        actualUser = userRepository.findByUsername(getActualUserName());
        if (actualUser == null) {
            throw new UserException(UserErrorCode.NOT_AUTHORIZED);
        }
        return convertGameSessionToDTOList(actualUser.getGameSessions());
    }


    private void updateAllowedField(UpdateUserDTO userFromBody, User userToUpdate) {
        if (userFromBody.getFirstName() != null) {
            userToUpdate.setFirstName(userFromBody.getFirstName());
        }
        if (userFromBody.getPassword() != null) {
            userToUpdate.setPassword(passwordEncoder.encode(userFromBody.getPassword()));
        }
        if (userFromBody.getLastName() != null) {
            userToUpdate.setLastName(userFromBody.getLastName());
        }
        if (userFromBody.getMaximumDailyPlayTime() != null) {
            userToUpdate.setMaximumDailyPlayTime(userFromBody.getMaximumDailyPlayTime());
        }
    }

    private void updateGivenField(UpdateUserDTO userFromBody, User userToUpdate) {
        updateAllowedField(userFromBody, userToUpdate);
        if (userFromBody.getRole() == null) {
            return;
        }
        List<Role> roles = updateUserToAdmin(userFromBody);
        if (roles.isEmpty()) {
            roles = updateUserToManager(userFromBody);
            if (roles.isEmpty()) {
                roles = new ArrayList<>();
            }
        }
        roles.add(roleRepository.findByName(RoleType.ROLE_REGULAR_USER.name()));
        roleRepository.saveAll(roles);
        userToUpdate.setRoles(roles);
    }

    private List<Role> updateUserToAdmin(UpdateUserDTO userFromBody) {
        if (userFromBody.isAdmin() && !actualUser.isAdmin()) {
            throw new UserException(UserErrorCode.NO_PERMISSION);
        }
        List<Role> roles = new ArrayList<>();
        if (userFromBody.getRole().name().equals(roleRepository.findByName(RoleType.ROLE_ADMIN.name()).getName())) {
            roles.add(roleRepository.findByName(RoleType.ROLE_MANAGER.name()));
            roles.add(roleRepository.findByName(RoleType.ROLE_ADMIN.name()));
        }
        return roles;
    }

    private List<Role> updateUserToManager(UpdateUserDTO userFromBody) {
        List<Role> roles = new ArrayList<>();
        if (actualUser.isAdmin()) {
            roles.add(roleRepository.findByName(RoleType.ROLE_MANAGER.name()));
        }
        return roles;
    }

    private User getUserToUpdate(UpdateUserDTO userFromBody) {
        if (actualUser == null) {
            throw new UserException(UserErrorCode.NOT_AUTHORIZED);
        }
        User userToUpdate = userRepository.findByUsername(userFromBody.getUsername());
        if (userToUpdate == null) {
            throw new UserException(UserErrorCode.NO_UPDATE_USER);
        }
        return userToUpdate;
    }


    private List<GameSessionInfoDTO> convertGameSessionToDTOList(List<GameSession> gameSessions) {
        List<GameSessionInfoDTO> gameSessionInfoDTOList = new ArrayList<>();
        for (GameSession gameSession : gameSessions) {
            gameSessionInfoDTOList.add(new GameSessionInfoDTO(gameSession.getGameName()
                    , convertDateToString(gameSession.getStartDate()), gameSession.getDuration()));
        }
        return gameSessionInfoDTOList;
    }

    private String convertDateToString(Date date) {
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime.getDayOfMonth() + "/" + localDateTime.getMonthValue() + "/" + localDateTime.getYear();
    }


    private String getActualUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    private boolean hasNoPermission(User userToProcess) {
        return (!actualUser.isAdmin() && (userToProcess.isAdmin()
                || userToProcess.isManager()
                || !isSameUser(userToProcess)));
    }

    private boolean hasNoPermissionToDelete(User userToProcess) {
        boolean tryToDeleteMyself = actualUser.isAdmin() && isSameUser(userToProcess);
        return (tryToDeleteMyself || !actualUser.isAdmin() &&
                (userToProcess.isAdmin()
                        || userToProcess.isManager()));
    }

    private boolean isSameUser(User userToProcess) {
        return actualUser.equals(userToProcess);
    }


}
