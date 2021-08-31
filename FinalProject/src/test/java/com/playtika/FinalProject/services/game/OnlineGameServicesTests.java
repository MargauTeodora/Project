package com.playtika.FinalProject.services.game;

import com.playtika.FinalProject.externalAPI.Game;
import com.playtika.FinalProject.externalAPI.OnlineGameNameService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class OnlineGameServicesTests {

    @Mock
    RestTemplate restTemplate;

    @Autowired
    OnlineGameNameService onlineGameNameService;

    @Test
    void testGetGameName(){
        Game game=new Game();
        game.setName("game");
        when(restTemplate
                .getForObject("https://rawg.io/api/games/name/?key=6e733e35e8ef43298200c1c79a6aa8d9",
                        Game.class)).thenReturn(game);
        onlineGameNameService.getGameName(game.getName()).thenAccept(
                (name)-> Assertions.assertEquals(game.getName(),name)
        );

    }
}
