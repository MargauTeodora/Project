package com.playtika.FinalProject.controllers;

import com.playtika.FinalProject.exceptions.GameSessionException;
import com.playtika.FinalProject.externalAPI.OnlineGameNameService;
import com.playtika.FinalProject.models.dto.game.GameSessionAddDTO;
import com.playtika.FinalProject.services.GameSessionService;
import com.playtika.FinalProject.utils.BodyMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/")
public class GameSessionController extends ExceptionsController {

    @Autowired
    GameSessionService gameSessionService;
    @Autowired
    OnlineGameNameService onlineGameNameService;

    @PostMapping(value = "/gamesession/add")
    public ResponseEntity addSession(@RequestBody GameSessionAddDTO gameSessionDTO) {
        String name = null;
        try {
            name = onlineGameNameService.getGameName(gameSessionDTO.getGameName()).get();
        } catch (InterruptedException | ExecutionException ex) {
            return new ResponseEntity(new BodyMessage("Game not found !"), HttpStatus.NOT_FOUND);
        }
        if (name == null || name.isEmpty()) {
            return new ResponseEntity("Game not found", HttpStatus.NOT_FOUND);
        }
        return gameSessionService.addGameSession(gameSessionDTO);


    }

    @PostMapping(value = "/gamesession/stop")
    public ResponseEntity stopSession() {
        gameSessionService.stop();
        return ResponseEntity.ok(new BodyMessage("You stopped the GAME SESSION"));
    }

    @GetMapping(value = "/gamesessions")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity getAllGameSessions(Pageable pageable) {
        return ResponseEntity.ok(gameSessionService.getAllGameSessions(pageable));
    }


}
