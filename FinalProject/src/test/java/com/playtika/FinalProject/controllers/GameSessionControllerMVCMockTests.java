package com.playtika.FinalProject.controllers;


import com.playtika.FinalProject.externalAPI.OnlineGameNameService;
import com.playtika.FinalProject.models.dto.AddNewGameSessionDTO;
import com.playtika.FinalProject.services.GameSessionService;
import com.playtika.FinalProject.utils.BodyMessage;
import com.playtika.FinalProject.utils.Convert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(roles = {"MANAGER", "REGULAR_USER", "ADMIN"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class GameSessionControllerMVCMockTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    GameSessionService gameSessionService;
    @MockBean
    OnlineGameNameService onlineGameNameService;

    @Test
    void addGameSessionCorrectTest() throws Exception {
        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                return CompletableFuture.completedFuture("game-test");
            }
        }).when(onlineGameNameService).getGameName(any());
        AddNewGameSessionDTO gameSessionDTO = new AddNewGameSessionDTO();
        when(gameSessionService.addGameSession(gameSessionDTO))
                .thenReturn(new ResponseEntity(new BodyMessage("Successful adding GAME SESSION"), HttpStatus.OK));
        MvcResult result = mockMvc.perform(post("/gamesession/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Convert.asJSONString(gameSessionDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }
//    NONEXISTENT_GAME

    @Test
    void addGameSessionEmptyGameNameTest() throws Exception {
        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                return CompletableFuture.completedFuture("");
            }
        }).when(onlineGameNameService).getGameName(any());
        AddNewGameSessionDTO gameSessionDTO = new AddNewGameSessionDTO();
        when(gameSessionService.addGameSession(gameSessionDTO))
                .thenReturn(new ResponseEntity(new BodyMessage("Successful adding GAME SESSION"), HttpStatus.OK));
        MvcResult result = mockMvc.perform(post("/gamesession/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Convert.asJSONString(gameSessionDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(412))
                .andReturn();
        Assertions.assertTrue(result.getResponse().getContentAsString().contains("This game is not available!"));
    }

    @Test
    void stopGameSessionTest() throws Exception {
        doNothing().when(gameSessionService).stop();
        MvcResult result = mockMvc.perform(post("/gamesession/stop"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void getAllGameSessions() throws Exception {
        when(gameSessionService.getAllGameSessions(any()))
                .thenReturn(new ArrayList<>());
        MvcResult result = mockMvc.perform(get("/gamesessions"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {})
    void getAllGameSessionsNotAllow() throws Exception {
        when(gameSessionService.getAllGameSessions(any()))
                .thenReturn(new ArrayList<>());
        MvcResult result = mockMvc.perform(get("/gamesessions"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(412))
                .andReturn();
    }

}
