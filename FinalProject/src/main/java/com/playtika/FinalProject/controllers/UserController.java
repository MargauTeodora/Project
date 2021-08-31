package com.playtika.FinalProject.controllers;

import com.playtika.FinalProject.services.UserService;
import com.playtika.FinalProject.utils.BodyMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class UserController extends ExceptionsController {
    @Autowired
    UserService userService;


    @DeleteMapping(value = "user/delete")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity deleteUser(@RequestParam String userName) throws RuntimeException {
        userService.removeUser(userName);
        return ResponseEntity.ok(new BodyMessage("successfully deleted user: " + userName));
    }

}
