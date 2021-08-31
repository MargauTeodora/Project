package com.playtika.FinalProject.controllers;

import com.playtika.FinalProject.models.User;
import com.playtika.FinalProject.models.dto.game.GameSessionInfoDTO;
import com.playtika.FinalProject.models.dto.game.GameSessionModifyDTO;
import com.playtika.FinalProject.models.dto.users.UserInfoAdminDTO;
import com.playtika.FinalProject.services.AdminService;
import com.playtika.FinalProject.services.GameSessionService;
import com.playtika.FinalProject.utils.BodyMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController extends ExceptionsController{

    @Autowired
    AdminService adminService;

    @GetMapping(value = "/users")
    public ResponseEntity<List<UserInfoAdminDTO>> getAllUser(Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllUser(pageable));
    }

    @PostMapping(value = "/users/gamesession/{userName}")
    public ResponseEntity addGameSession(@PathVariable String userName,@RequestBody GameSessionModifyDTO gameName) throws ParseException, ExecutionException, InterruptedException {
        return ResponseEntity.ok(adminService.addUserGameSession(userName, gameName));
    }

    @PatchMapping(params = {"id"})
    @RequestMapping(value = "/users/gamesession/")
    public ResponseEntity updateGameSession(@RequestParam int id,
                                            @RequestBody GameSessionModifyDTO gameName) throws ParseException, ExecutionException, InterruptedException {
        return ResponseEntity.ok(adminService.updateUserGameSession(id, gameName));
    }

    @GetMapping(value = "/users/gameSessions/{userName}")
    public ResponseEntity<List<GameSessionInfoDTO>> getUserGameSessions(@PathVariable String userName) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(adminService.getUserGameSession(userName));
    }


}
