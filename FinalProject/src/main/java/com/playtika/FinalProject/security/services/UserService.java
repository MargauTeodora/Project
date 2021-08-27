package com.playtika.FinalProject.security.services;

import com.playtika.FinalProject.exceptions.UserException;
import com.playtika.FinalProject.exceptions.customErrors.ErrorCode;
import com.playtika.FinalProject.security.dto.LoginResponse;
import com.playtika.FinalProject.security.dto.SignUpRequest;
import com.playtika.FinalProject.security.dto.UpdateUserDTO;
import com.playtika.FinalProject.security.models.Role;
import com.playtika.FinalProject.security.models.RoleType;
import com.playtika.FinalProject.security.models.User;
import com.playtika.FinalProject.security.repositories.RoleRepository;
import com.playtika.FinalProject.security.repositories.UserRepository;
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
        if (userRepository.existsByUsername(request.getUserName())) {
            throw new UserException(ErrorCode.USER_EXISTS);
        }
        User user = new User().setUsername(request.getUserName())
                .setPassword(passwordEncoder.encode(request.getPassword()))
                .setEmail(request.getEmail())
                .setFirstName(request.getFirstName())
                .setLastName(request.getLastName())
                .setRoles(Arrays.asList(roleRepository.findByName(RoleType.ROLE_REGULAR_USER.name())));
        user = userRepository.saveAndFlush(user);
        return user;
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
        if(userFromBody.getRole().name().equals(roleRepository.findByName(RoleType.ROLE_ADMIN.name()).getName())
        &&
        !isAdmin()
        ){
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
        if (userFromBody.getRole().name().equals(roleRepository.findByName(RoleType.ROLE_MANAGER.name()).getName())) {
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
    public User getUserInfo() {
        actualUser = userRepository.findByUsername(getActualUserName());
        if(actualUser==null){
            throw new UserException(ErrorCode.NOT_AUTHORIZED);
        }
        logger.info(actualUser.getUsername());
        return userRepository.findByUsername(getActualUserName());
    }

    private String getActualUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    private boolean hasNoPermission(User userToProcess) {
        return (!isAdmin() && userToProcess.getRoles()
                .contains(new Role(RoleType.ROLE_ADMIN.name())));
    }

    private boolean isAdmin() {
        return actualUser.getRoles().contains(new Role(RoleType.ROLE_ADMIN.name()));
    }

    private boolean isManager() {
        return actualUser.getRoles().contains(new Role(RoleType.ROLE_MANAGER.name()));
    }

}
