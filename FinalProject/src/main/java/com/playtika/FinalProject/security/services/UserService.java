package com.playtika.FinalProject.security.services;

import com.playtika.FinalProject.exceptions.AuthenticationCustomException;
import com.playtika.FinalProject.exceptions.NotAuthorizedException;
import com.playtika.FinalProject.security.dto.LoginResponse;
import com.playtika.FinalProject.security.dto.SignUpRequest;
import com.playtika.FinalProject.security.dto.UpdateUserDTO;
import com.playtika.FinalProject.security.dto.UserDTO;
import com.playtika.FinalProject.security.models.Role;
import com.playtika.FinalProject.security.models.RoleType;
import com.playtika.FinalProject.security.models.User;
import com.playtika.FinalProject.security.repositories.RoleRepository;
import com.playtika.FinalProject.security.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    //required by the UserDetailsService
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

            logger.info("Login successfully");

            return loginResponse;
        } catch (AuthenticationException e) {
            throw new AuthenticationCustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public User signUp(SignUpRequest request) {
        if (userRepository.existsByUsername(request.getUserName())) {
            throw new AuthenticationCustomException("User already exists in system", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        User user = new User();
        user.setUsername(request.getUserName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRoles(Arrays.asList(roleRepository.findByName(RoleType.ROLE_REGULAR_USER.name())));
        request.setPassword(user.getPassword());

        user = userRepository.saveAndFlush(user);
        logger.info("Register successfully");
        return user;
    }


    public void removeUser(String userName) {
        User actualUser = userRepository.findByUsername(getActualUserName());
        if (actualUser.getRoles().contains(new Role(RoleType.ROLE_ADMIN.name()))) {
            if (!userRepository.existsByUsername(userName)) {
                throw new RuntimeException("User given for delete doesn't exists");
            }
            userRepository.deleteByUsername(userName);
            logger.info("User remove successfully");
        } else {
            throw new NotAuthorizedException();
        }
    }

    public void updateUser(UpdateUserDTO userFromBody) {
        User actualUser = userRepository.findByUsername(getActualUserName());

        logger.info("Hi there???");
        if (actualUser.getRoles().contains(new Role(RoleType.ROLE_ADMIN.name()))) {
            if (userFromBody == null ||userRepository.findByUsername(userFromBody.getUsername())==null) {
                    throw new RuntimeException("User given for update doesn't exists");
            }
            User userToUpdate = userRepository.findByUsername(userFromBody.getUsername());
            if (userFromBody.getFirstName() != null) {
                userToUpdate.setFirstName(userFromBody.getFirstName());
            }
            if (userFromBody.getPassword() != null) {
                userToUpdate.setPassword(passwordEncoder.encode(userFromBody.getPassword()));
            }
            if (userFromBody.getLastName() != null) {
                userToUpdate.setLastName(userFromBody.getLastName());
            }
            if (userFromBody.getRoles() != null) {
                List<Role> roles = new ArrayList<>();
                logger.info("-------------------"+userFromBody.getRoles().contains(roleRepository.findByName(RoleType.ROLE_ADMIN.name()))+"------------");
                if (userFromBody.getRoles().contains(roleRepository.findByName(RoleType.ROLE_ADMIN.name()))) {
                    roles.add(roleRepository.findByName(RoleType.ROLE_MANAGER.name()));
                    roles.add(roleRepository.findByName(RoleType.ROLE_ADMIN.name()));
                }
                else if (userFromBody.getRoles().contains(roleRepository.findByName(RoleType.ROLE_MANAGER.name()))) {
                    roles.add(roleRepository.findByName(RoleType.ROLE_MANAGER.name()));
                }
                    roles.add(roleRepository.findByName(RoleType.ROLE_REGULAR_USER.name()));

                roleRepository.saveAll(roles);
                userToUpdate.setRoles(roles);
            }
            userToUpdate = userRepository.saveAndFlush(userToUpdate);
            this.userRepository.saveAndFlush(userToUpdate);
            logger.info("User updated successfully");
        } else {
            throw new NotAuthorizedException();
        }
    }

    public UserDTO searchUser(String userName) {
        User user = userRepository.findByUsername(userName);
        if (user == null) {
//            throw new MyCustomException("Provided user doesn't exist", HttpStatus.NOT_FOUND);
        }
        UserDTO userResponse = new UserDTO(user.getUsername(), user.getEmail());

        return userResponse;
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public String refreshToken(String userName) {
        return jwtTokenService.createToken(userName, userRepository.findByUsername(userName).getRoles());
    }

    private String getActualUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

}
