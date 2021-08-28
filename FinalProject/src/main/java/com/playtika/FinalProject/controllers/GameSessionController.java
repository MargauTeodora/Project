package com.playtika.FinalProject.controllers;


import com.playtika.FinalProject.models.GameSession;
import com.playtika.FinalProject.models.dto.AddNewGameSessionDTO;
import com.playtika.FinalProject.services.GameSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class GameSessionController {
    //TODO validate if game exists in link
    @Autowired
    GameSessionService gameSessionService;

    @PostMapping(value = "/add")
    public ResponseEntity<String> addSession( @RequestBody AddNewGameSessionDTO gameSessionDTO){
        GameSession gameSession;
        try {
            gameSession = gameSessionService.addGameSession(gameSessionDTO);
            return new ResponseEntity<>("Successful adding for GAME SESSION", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("SAAAD, YOU HAVE A PROBLEM !!", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/gamesessions")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<GameSession>> getAllUser() throws RuntimeException {
        try {
            return new ResponseEntity<>(gameSessionService.getAllGameSessions(), HttpStatus.OK);
        } catch (Exception e) {
            throw e;
        }
    }


}
