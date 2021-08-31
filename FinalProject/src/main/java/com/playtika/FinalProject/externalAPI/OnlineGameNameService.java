package com.playtika.FinalProject.externalAPI;

import com.playtika.FinalProject.exceptions.GameSessionException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class OnlineGameNameService {
    RestTemplate restTemplate;
    //            https://rawg.io/api/games/+nume?key=6e733e35e8ef43298200c1c79a6aa8d9
    public static final String URL = "https://rawg.io/api/games/";
    public static final String key = "6e733e35e8ef43298200c1c79a6aa8d9";
    public OnlineGameNameService(RestTemplateBuilder restTemplate) {
        this.restTemplate = restTemplate.build();
    }
    @Async
    public CompletableFuture<String> getGameName(String gameName){
        String slug=gameName.replace(" ","-");
        Game game=null;
        try{
            game = restTemplate.getForObject(URL+slug+"?key="+key, Game.class);
            if(game==null||game.getName()==null){
                return CompletableFuture.failedFuture
                        (new GameSessionException(GameSessionException.GameSessionErrorCode.NONEXISTENT_GAME));
            }
            return CompletableFuture.completedFuture(game.getName());
        }catch (HttpClientErrorException ex){
            return CompletableFuture.failedFuture
                    (new GameSessionException(GameSessionException.GameSessionErrorCode.GET_GAME_FAIL));
        }

    }
}
