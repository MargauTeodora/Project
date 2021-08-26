package com.playtika.FinalProject.controllers;

import com.playtika.FinalProject.exceptions.AuthenticationCustomException;
import com.playtika.FinalProject.exceptions.NotAuthorizedException;
import com.playtika.FinalProject.security.dto.*;
import com.playtika.FinalProject.security.models.User;
import com.playtika.FinalProject.security.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/")
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    UserService userService;

    @GetMapping
    @RequestMapping("/login")
    public ResponseEntity<LoginResponse> login(HttpServletRequest requestHeader, @RequestBody LoginRequest request) throws RuntimeException {
        try {
            LoginResponse loginResponse = userService.login(request.getUserName(), request.getPassword());
            if (loginResponse == null) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(loginResponse, HttpStatus.OK);
            }
        } catch (AuthenticationCustomException ex) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping(value = "/register")
    public ResponseEntity<String> signUp(HttpServletRequest requestHeader,
                                         @RequestBody SignUpRequest request) throws RuntimeException {
        User user;
        try {
            user = userService.signUp(request);
            return new ResponseEntity<>("Successful register for USER", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("USER already exists", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/delete")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<String> deleteUser(@RequestParam String userName) throws RuntimeException {
        try {
            userService.removeUser(userName);
        } catch (Exception e) {
            throw e;
        }
        return new ResponseEntity<>(userName, HttpStatus.OK);
    }

    @PatchMapping(value = "/update")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<String> updateUser(@RequestBody UpdateUserDTO user) throws RuntimeException {
        logger.info("UPDATE");
        try {
            logger.info("update2");
            userService.updateUser(user);
            logger.info("UPDATE3");
        } catch (Exception e) {
            throw e;
        }
        return new ResponseEntity<>(user.getRoles().get(0).getName(), HttpStatus.OK);
    }

    @GetMapping(value = "/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<User>> getAllUser() throws RuntimeException {
        try {
            return new ResponseEntity<>(userService.getAllUser(), HttpStatus.OK);
        } catch (Exception e) {
            throw e;
        }

    }
    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity handleExceptions(Exception ex) {
        if (ex instanceof AuthenticationCustomException) {
            return ResponseEntity.status(((AuthenticationCustomException) ex).getHttpStatus()).build();
        }
        if (ex instanceof NotAuthorizedException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
    }
}
