package com.playtika.FinalProject.controllers;

import com.playtika.FinalProject.exceptions.UserException;
import com.playtika.FinalProject.exceptions.customErrors.UserErrorCode;
import com.playtika.FinalProject.models.dto.*;
import com.playtika.FinalProject.services.UserService;
import com.playtika.FinalProject.utils.BodyMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class GeneralUserController extends ExceptionsController {
    @Autowired
    UserService userService;


    @GetMapping
    @RequestMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest request){
        LoginResponse loginResponse = userService.login(request.getUserName(), request.getPassword());
        if (loginResponse == null) {
            throw new UserException(UserErrorCode.INCOMPLETE_DATA);
        } else {
            return ResponseEntity.ok(loginResponse);
        }
    }

    @PostMapping(value = "/register")
    public ResponseEntity signUp(@RequestBody SignUpRequest request){
        userService.signUp(request);
        return ResponseEntity.ok(new BodyMessage("Successful register for USER"));

    }

    @GetMapping(value = "user/info")
    @PreAuthorize("hasRole('ROLE_REGULAR_USER')")
    public ResponseEntity getUserInfo(){
        return  ResponseEntity.ok(userService.getUserInfo());
    }

    @GetMapping(value = "user/gamesession")
    @PreAuthorize("hasRole('ROLE_REGULAR_USER')")
    public ResponseEntity getGameSessionInfo(){
        return  ResponseEntity.ok(userService.getGameSession());
    }


    @PatchMapping(value = "user/update")
    @PreAuthorize("hasRole('ROLE_REGULAR_USER')")
    public ResponseEntity updateUser(@RequestBody UpdateUserDTO user) throws RuntimeException {
        userService.updateUser(user);
        return  ResponseEntity.ok(new BodyMessage("Successfully update user"));
    }

}
