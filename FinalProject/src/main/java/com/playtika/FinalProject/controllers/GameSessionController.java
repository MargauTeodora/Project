package com.playtika.FinalProject.controllers;


import com.playtika.FinalProject.externalAPI.OnlineGameNameService;
import com.playtika.FinalProject.models.GameSession;
import com.playtika.FinalProject.models.dto.AddNewGameSessionDTO;
import com.playtika.FinalProject.services.GameSessionService;
import com.playtika.FinalProject.utils.BodyMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping
public class GameSessionController extends ExceptionsController {

    //TODO validate if game exists in link

    @Autowired
    GameSessionService gameSessionService;

    @Autowired
    OnlineGameNameService onlineGameNameService;

    @PostMapping(value = "/add")
    public ResponseEntity addSession(@RequestBody AddNewGameSessionDTO gameSessionDTO) throws ExecutionException, InterruptedException {
        String name=onlineGameNameService.getGameName(gameSessionDTO.getGameName()).get();
        return gameSessionService.addGameSession(gameSessionDTO);
    }

    @PostMapping(value = "/stop")
    public ResponseEntity stopSession() {
        gameSessionService.stop();
        return ResponseEntity.ok(new BodyMessage("You stopped the GAME SESSION"));
    }

    //    TODO pagination and filter
    @GetMapping(value = "/gamesessions")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity getAllUser() {
        return ResponseEntity.ok(gameSessionService.getAllGameSessions());
    }


}
