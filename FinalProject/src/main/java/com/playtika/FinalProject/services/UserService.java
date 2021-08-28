package com.playtika.FinalProject.services;

import com.playtika.FinalProject.exceptions.GameSessionException;
import com.playtika.FinalProject.exceptions.UserException;
import com.playtika.FinalProject.exceptions.customErrors.ErrorCode;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            LoginResponse loginResponse = new LoginResponse()
                    .setEmail(user.getEmail())
                    .setUserName(user.getUsername())
                    .setAccessToken(jwtTokenService.createToken(userName, user.getRoles()));
            return loginResponse;
        } catch (AuthenticationException e) {
            throw new UserException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    public User signUp(SignUpRequest request) {
        hasCredentialValid(request);
        if (userRepository.existsByUsername(request.getUserName())) {
            throw new UserException(ErrorCode.USER_EXISTS);
        }
        if(request.getMaximumDailyPlayTime()!=null){
            verifyMaxDailyPlayTime(request);
        }
        logger.info("------------------Bau!");
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

    private void hasCredentialValid(SignUpRequest request) {
        validateEmail(request);
        validateEmail(request);
        validateEmail(request);
    }

    private void validateUsername(SignUpRequest request) {
        String regex = "^[a-zA-Z]{4,}$[1-9]*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(request.getUserName());

        if (isEmptyString(request.getUserName()) || !matcher.find()) {
            throw new UserException(ErrorCode.INCORRECT_USERNAME);
        }
    }


    private void validateEmail(SignUpRequest request) {
        String regex = "\\S+@\\S+\\.\\S+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(request.getEmail());

        if (isEmptyString(request.getEmail()) || !matcher.find()) {
            throw new UserException(ErrorCode.INCORRECT_EMAIL);
        }
    }

    private void validatePassword(SignUpRequest request) {
        String regex = "^[a-zA-Z]{7,}$[1-9]*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(request.getEmail());
        if (isEmptyString(request.getEmail()) || !matcher.find()) {
            throw new UserException(ErrorCode.INCORRECT_PASSWORD);
        }
    }

    private boolean isEmptyString(String string) {
        return string == null
                || string.isBlank();
    }

    public void removeUser(String userName) {
        actualUser = userRepository.findByUsername(getActualUserName());
        if (!isManager()) {
            throw new UserException(ErrorCode.ACCESS_DENIED);
        }
        if (!userRepository.existsByUsername(userName)) {
            throw new UserException(ErrorCode.NO_DELETE_USER);
        }
        User userToDelete = userRepository.findByUsername(userName);
//        if (!actualUserIsAdmin()&&(userToDelete.isAdmin()||userToDelete.isManager())){
//            throw new UserException(ErrorCode.ACCESS_DENIED);
//        }

        if (hasNoPermission(userToDelete)) {
            throw new UserException(ErrorCode.ACCESS_DENIED);
        }
        userRepository.deleteByUsername(userName);
    }

    public void updateUser(UpdateUserDTO userFromBody) {
        actualUser = userRepository.findByUsername(getActualUserName());
        User userToUpdate = getUserToUpdate(userFromBody);
        if (hasNoPermission(userToUpdate)) {
            throw new UserException(ErrorCode.ACCESS_DENIED);
        }
        updateGivenField(userFromBody, userToUpdate);
        userToUpdate = userRepository.saveAndFlush(userToUpdate);
        this.userRepository.saveAndFlush(userToUpdate);
        logger.info("User updated successfully");
    }

    private void updateGivenField(UpdateUserDTO userFromBody, User userToUpdate) {
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
        if (userFromBody.getRole() == null) {
            logger.info("TE-AM MINTIT ");
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
//        if (userFromBody.getRole().name().equals(roleRepository.findByName(RoleType.ROLE_ADMIN.name()).getName())
        if (userFromBody.isAdmin() && !actualUserIsAdmin()) {
            logger.info("vrea sa actualizeze la un admin dar nu are permisiiuni");
            throw new UserException(ErrorCode.ACCESS_DENIED);
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
//        if (userFromBody.getRole().name().equals(roleRepository.findByName(RoleType.ROLE_MANAGER.name()).getName())) {
        if (userFromBody.isAdmin()) {
            roles.add(roleRepository.findByName(RoleType.ROLE_MANAGER.name()));
        }
        return roles;
    }

    private User getUserToUpdate(UpdateUserDTO userFromBody) {
        if (actualUser == null) {
            throw new UserException(ErrorCode.NOT_AUTHORIZED);
        }
        if (!isManager()) {
            throw new UserException(ErrorCode.ACCESS_DENIED);
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
        logger.info(actualUser.getUsername());
        return new UserInfoDTO(actualUser.getUsername(), actualUser.getEmail(), actualUser.getFirstName(), actualUser.getLastName());
    }

    private String getActualUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    private boolean hasNoPermission(User userToProcess) {
//        return (!actualUserIsAdmin() && userToProcess.getRoles()
//                .contains(new Role(RoleType.ROLE_ADMIN.name())));

        return (!actualUserIsAdmin()&&(userToProcess.isAdmin()||userToProcess.isManager()));
    }

    private boolean actualUserIsAdmin() {
//        return actualUser.getRoles().contains(new Role(RoleType.ROLE_ADMIN.name()));
        return actualUser.isAdmin();
    }

    private boolean isManager() {
//        return actualUser.getRoles().contains(new Role(RoleType.ROLE_MANAGER.name()));
        return actualUser.isManager();
    }

    private void verifyMaxDailyPlayTime(SignUpRequest request) {
        if (request.getMaximumDailyPlayTime().getHour() < 0 || request.getMaximumDailyPlayTime().getMinutes() < 0) {
            throw new GameSessionException(GameSessionException.GameSessionErrorCode.NEGATIVE_NUMBER);
        }
        if (request.getMaximumDailyPlayTime().getHour() > 23) {
            throw new GameSessionException(GameSessionException.GameSessionErrorCode.EXCEED_DAILY_HOURS);
        }
        if (request.getMaximumDailyPlayTime().getMinutes() > 59) {
            throw new GameSessionException(GameSessionException.GameSessionErrorCode.EXCEED_MINUTES);
        }
    }
}
