package com.playtika.FinalProject.services;

import com.playtika.FinalProject.exceptions.GameSessionException;
import com.playtika.FinalProject.exceptions.UserException;
import com.playtika.FinalProject.exceptions.customErrors.ErrorCode;
import com.playtika.FinalProject.models.GameSession;
import com.playtika.FinalProject.models.dto.*;
import com.playtika.FinalProject.models.Role;
import com.playtika.FinalProject.models.RoleType;
import com.playtika.FinalProject.models.User;
import com.playtika.FinalProject.repositories.RoleRepository;
import com.playtika.FinalProject.repositories.UserRepository;
import com.playtika.FinalProject.security.services.JwtTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
            return new LoginResponse()
                    .setEmail(user.getEmail())
                    .setUserName(user.getUsername())
                    .setAccessToken(jwtTokenService.createToken(userName, user.getRoles()));
        } catch (AuthenticationException e) {
            throw new UserException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    public User signUp(SignUpRequest request) {
        if (userRepository.existsByUsername(request.getUserName())) {
            throw new UserException(ErrorCode.USER_EXISTS);
        }
        User user = new User().setUsername(request.getUserName())
                .setPassword(passwordEncoder.encode(request.getPassword()))
                .setEmail(request.getEmail())
                .setFirstName(request.getFirstName())
                .setLastName(request.getLastName())
                .setMaximumDailyPlayTime(request.getMaximumDailyPlayTime())
                .setRoles(Arrays.asList(roleRepository.findByName(RoleType.ROLE_REGULAR_USER.name())));
        user = userRepository.saveAndFlush(user);
        return user;
    }

    public void removeUser(String userName) {
        actualUser = userRepository.findByUsername(getActualUserName());
        if (!actualUserIsManager()) {
            throw new UserException(ErrorCode.NO_PERMISSION);
        }
        if (!userRepository.existsByUsername(userName)) {
            throw new UserException(ErrorCode.NO_DELETE_USER);
        }
        User userToDelete = userRepository.findByUsername(userName);
        if (hasNoPermissionToDelete(userToDelete)) {
            throw new UserException(ErrorCode.NO_PERMISSION);
        }
        userRepository.deleteByUsername(userName);
    }

    public void updateUser(UpdateUserDTO userFromBody) {
        actualUser = userRepository.findByUsername(getActualUserName());
        User userToUpdate = getUserToUpdate(userFromBody);
        if (!isSameUser(userToUpdate)) {
            if (hasNoPermission(userToUpdate)) {
                throw new UserException(ErrorCode.NO_PERMISSION);
            }
            updateGivenField(userFromBody, userToUpdate);
        }
        updateAllowedField(userFromBody, userToUpdate);
        userToUpdate = userRepository.saveAndFlush(userToUpdate);
        this.userRepository.saveAndFlush(userToUpdate);
        logger.info("User updated successfully");

    }

    private void updateAllowedField(UpdateUserDTO userFromBody, User userToUpdate) {
        logger.info("DOR METODELE PERMISE");
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
            logger.info("AICI ESTE PROBLEMA ");
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
            if (roles == null || roles.isEmpty()) {
                roles = new ArrayList<>();
            }
        }
        roles.add(roleRepository.findByName(RoleType.ROLE_REGULAR_USER.name()));
        roleRepository.saveAll(roles);
        userToUpdate.setRoles(roles);
    }

    private List<Role> updateUserToAdmin(UpdateUserDTO userFromBody) {
        if (userFromBody.isAdmin() && !actualUserIsAdmin()) {
            throw new UserException(ErrorCode.NO_PERMISSION);
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
            throw new UserException(ErrorCode.NOT_AUTHORIZED);
        }
        User userToUpdate = userRepository.findByUsername(userFromBody.getUsername());
        if (userFromBody == null || userToUpdate == null) {
            throw new UserException(ErrorCode.NO_UPDATE_USER);
        }
        return userToUpdate;
    }


    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public UserInfoDTO getUserInfo() {
        actualUser = userRepository.findByUsername(getActualUserName());
        if (actualUser == null) {
            throw new UserException(ErrorCode.NOT_AUTHORIZED);
        }
        return new UserInfoDTO(actualUser.getUsername(), actualUser.getEmail(), actualUser.getFirstName(), actualUser.getLastName(),
                actualUser.getMaximumDailyPlayTime());
    }

    public List<GameSessionInfoDTO> getGameSession() {
        actualUser = userRepository.findByUsername(getActualUserName());
        if (actualUser == null) {
            throw new UserException(ErrorCode.NOT_AUTHORIZED);
        }
        return convertGameSessionToDTOList(actualUser.getGameSessions());
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
        return (!actualUserIsAdmin() && (userToProcess.isAdmin()
                || userToProcess.isManager()
                || !isSameUser(userToProcess)));
    }

    private boolean hasNoPermissionToDelete(User userToProcess) {
        boolean tryToDeleteMyself=actualUserIsAdmin()&&isSameUser(userToProcess);
        return (tryToDeleteMyself||!actualUserIsAdmin() &&
                (userToProcess.isAdmin()
                || userToProcess.isManager()));
    }
    private boolean isSameUser(User userToProcess) {
        return actualUser.equals(userToProcess);
    }

    private boolean actualUserIsAdmin() {
        return actualUser.isAdmin();
    }

    private boolean actualUserIsManager() {
        return actualUser.isManager();
    }


}
