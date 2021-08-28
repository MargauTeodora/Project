package com.playtika.FinalProject.controllers;

import com.playtika.FinalProject.exceptions.UserException;
import com.playtika.FinalProject.exceptions.customErrors.ErrorCode;
import com.playtika.FinalProject.exceptions.customErrors.ErrorMessage;
import com.playtika.FinalProject.models.dto.LoginRequest;
import com.playtika.FinalProject.models.dto.LoginResponse;
import com.playtika.FinalProject.models.dto.SignUpRequest;
import com.playtika.FinalProject.models.dto.UpdateUserDTO;
import com.playtika.FinalProject.models.User;
import com.playtika.FinalProject.services.UserService;
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
public class UserController extends ExceptionsController {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    UserService userService;


    @DeleteMapping(value = "user/delete")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<String> deleteUser(@RequestParam String userName) throws RuntimeException {
        try {
            userService.removeUser(userName);
        } catch (Exception e) {
            throw e;
        }
        return new ResponseEntity<>(userName, HttpStatus.OK);
    }

    @PatchMapping(value = "user/update")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<String> updateUser(@RequestBody UpdateUserDTO user) throws RuntimeException {
        userService.updateUser(user);
        return new ResponseEntity<>(user.getRole().name(), HttpStatus.OK);
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
}
