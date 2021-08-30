package com.playtika.FinalProject.controllers;

import com.playtika.FinalProject.models.User;
import com.playtika.FinalProject.services.UserService;
import com.playtika.FinalProject.utils.BodyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

//    TODO pagination and filter

    @GetMapping(value = "/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<User>> getAllUser() {
        return ResponseEntity.ok(userService.getAllUser());
    }
}
