package com.playtika.FinalProject.controllers;

import com.playtika.FinalProject.exceptions.UserException;
import com.playtika.FinalProject.exceptions.customErrors.ErrorCode;
import com.playtika.FinalProject.exceptions.customErrors.ErrorMessage;
import com.playtika.FinalProject.models.User;
import com.playtika.FinalProject.models.dto.LoginRequest;
import com.playtika.FinalProject.models.dto.LoginResponse;
import com.playtika.FinalProject.models.dto.SignUpRequest;
import com.playtika.FinalProject.models.dto.UserInfoDTO;
import com.playtika.FinalProject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

@RestController
@RequestMapping("/")
public class GeneralUserController extends ExceptionsController {
    @Autowired
    UserService userService;

    @GetMapping
    @RequestMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest request) throws RuntimeException {
        LoginResponse loginResponse = userService.login(request.getUserName(), request.getPassword());
        if (loginResponse == null) {
            throw new UserException(ErrorCode.INCOMPLETE_DATA);
        } else {
            return ResponseEntity.ok(loginResponse);
        }
    }

    @PostMapping(value = "/register")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest request) throws RuntimeException {

        userService.signUp(request);
        return ResponseEntity.ok("Successful register for USER");

    }

    @GetMapping(value = "user/info")
    @PreAuthorize("hasRole('ROLE_REGULAR_USER')")
    public ResponseEntity<UserInfoDTO> getUserInfo() throws RuntimeException {
        return new ResponseEntity<>(userService.getUserInfo(), HttpStatus.OK);
    }
}
