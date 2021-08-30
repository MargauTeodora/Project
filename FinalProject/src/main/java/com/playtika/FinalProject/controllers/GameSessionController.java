package com.playtika.FinalProject.controllers;

import com.playtika.FinalProject.exceptions.GameSessionException;
import com.playtika.FinalProject.externalAPI.OnlineGameNameService;
import com.playtika.FinalProject.models.dto.AddNewGameSessionDTO;
import com.playtika.FinalProject.services.GameSessionService;
import com.playtika.FinalProject.utils.BodyMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("")
public class GameSessionController extends ExceptionsController {

    @Autowired
    GameSessionService gameSessionService;
    @Autowired
    OnlineGameNameService onlineGameNameService;

    @PostMapping(value = "/gamesession/add")
    public ResponseEntity addSession(@RequestBody AddNewGameSessionDTO gameSessionDTO) {
        try {
            String name = onlineGameNameService.getGameName(gameSessionDTO.getGameName()).get();
            if (name == null || name.isEmpty()) {
                throw new GameSessionException(GameSessionException.GameSessionErrorCode.NONEXISTENT_GAME);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new GameSessionException(GameSessionException.GameSessionErrorCode.GET_GAME_FAIL);
        }
        return gameSessionService.addGameSession(gameSessionDTO);
    }

    @PostMapping(value = "/gamesession/stop")
    public ResponseEntity stopSession() {
        gameSessionService.stop();
        return ResponseEntity.ok(new BodyMessage("You stopped the GAME SESSION"));
    }


    //    TODO pagination and filter
    @GetMapping(value = "/gamesessions")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity getAllGameSessions(Pageable pageable) {
        return ResponseEntity.ok(gameSessionService.getAllGameSessions(pageable));
    }


}
