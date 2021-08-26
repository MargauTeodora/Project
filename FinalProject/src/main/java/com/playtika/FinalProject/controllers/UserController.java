package com.playtika.FinalProject.controllers;
import com.playtika.FinalProject.exceptions.AuthenticationCustomException;
import com.playtika.FinalProject.exceptions.NotAuthorizedException;
import com.playtika.FinalProject.security.dto.LoginRequest;
import com.playtika.FinalProject.security.dto.LoginResponse;
import com.playtika.FinalProject.security.dto.SignUpRequest;
import com.playtika.FinalProject.security.dto.UserDTO;
import com.playtika.FinalProject.security.models.Role;
import com.playtika.FinalProject.security.models.RoleType;
import com.playtika.FinalProject.security.models.User;
import com.playtika.FinalProject.security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping
    @RequestMapping("/login")
    public ResponseEntity<LoginResponse> login(HttpServletRequest requestHeader, @RequestBody LoginRequest request)throws RuntimeException {
       try{
           LoginResponse loginResponse = userService.login(request.getUserName(), request.getPassword());
           if(loginResponse == null){
               return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
           }else{
               return new ResponseEntity<>(loginResponse, HttpStatus.OK);
           }
       }catch (AuthenticationCustomException ex){
           return new ResponseEntity<>(null,  HttpStatus.NOT_FOUND);
       }

    }

    @PostMapping(value = "/register")
    public ResponseEntity<String> signUp(HttpServletRequest requestHeader, @RequestBody SignUpRequest request) throws RuntimeException {
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

    @PutMapping(value = "/update")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<String> updateUser(@RequestParam String userName,@RequestBody User role) throws RuntimeException {
        try {
            userService.updateUser(userName);
        } catch (Exception e) {
            throw e;
        }
        return new ResponseEntity<>(userName, HttpStatus.OK);
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

    @GetMapping(value = "/search")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<UserDTO> searchUser(@RequestParam String userName) throws RuntimeException {

        UserDTO userResponse = userService.searchUser(userName);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @GetMapping("/refresh")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public String refreshToken(HttpServletRequest req) {
        return userService.refreshToken(req.getRemoteUser());
    }


    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public  ResponseEntity handleExceptions(Exception ex){
        if(ex instanceof AuthenticationCustomException){
            return ResponseEntity.status(((AuthenticationCustomException) ex).getHttpStatus()).build();
        }
        if(ex instanceof NotAuthorizedException){
            return ResponseEntity.status( HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
    }
}
