package com.playtika.FinalProject.controllers;

import com.playtika.FinalProject.exceptions.UserException;
import com.playtika.FinalProject.exceptions.customErrors.UserErrorCode;
import com.playtika.FinalProject.models.User;
import com.playtika.FinalProject.models.dto.*;
import com.playtika.FinalProject.services.UserService;
import com.playtika.FinalProject.utils.Convert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@WithMockUser(roles = {"MANAGER", "REGULAR_USER"})
public class GeneralUserControllerMVCMockTests {


    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    AuthenticationManager authenticationManager;


    @Test
    void loginTest() throws Exception {
        when(userService.login(any(), any())).thenReturn(new LoginResponse());
        MvcResult result = mockMvc.perform(get("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Convert.asJSONString(new LoginRequest())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void loginIncompleteDataTest() throws Exception {
        when(userService.login(any(), any())).thenReturn(null);
        MvcResult result = mockMvc.perform(get("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Convert.asJSONString(new LoginRequest())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(412))
                .andReturn();
        Assertions.assertTrue(result.getResponse().getContentAsString().contains("Data is not completed "));
    }


    @Test
    void registerTest() throws Exception {
        when(userService.signUp(any())).thenReturn(new User());
        MvcResult result = mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Convert.asJSONString(new SignUpRequest())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void getUserInfoTest() throws Exception {
        when(userService.getUserInfo()).thenReturn(new UserInfoDTO());
        MvcResult result = mockMvc.perform(get("/user/info"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {})
    void getUserInfoTestNoRoles() throws Exception {
        when(userService.getUserInfo()).thenReturn(new UserInfoDTO());
        MvcResult result = mockMvc.perform(get("/user/info"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(412))
                .andReturn();
    }

    @Test
    void getGameSessionInfoTest() throws Exception {
        when(userService.getGameSession()).thenReturn(new ArrayList<GameSessionInfoDTO>());
        MvcResult result = mockMvc.perform(get("/user/gamesession"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void updateUserTest() throws Exception {
        UpdateUserDTO userDTO = new UpdateUserDTO();
        doNothing().when(userService).updateUser(userDTO);
        MvcResult result = mockMvc.perform(patch("/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Convert.asJSONString(userDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }
}
