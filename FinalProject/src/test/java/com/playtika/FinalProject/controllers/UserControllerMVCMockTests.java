package com.playtika.FinalProject.controllers;


import com.playtika.FinalProject.exceptions.customErrors.UserErrorCode;
import com.playtika.FinalProject.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@WithMockUser(roles = {"MANAGER", "REGULAR_USER"})
public class UserControllerMVCMockTests {


    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;


    @Test
    void delete() throws Exception {
        doNothing().when(userService).removeUser(any());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/user/delete?userName=name"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }
    @Test
    @WithMockUser(roles = {"REGULAR_USER"})
    void deleteNotAllow() throws Exception {
        doNothing().when(userService).removeUser(any());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/user/delete?userName=name"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(412))
                .andReturn();
        Assertions.assertTrue(result.getResponse().getContentAsString().contains(UserErrorCode.NO_PERMISSION.getMessage()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN","MANAGER"})
    void getAllUsers() throws Exception {
        when(userService.getAllUser(any()))
                .thenReturn(new ArrayList<>());
        MvcResult result = mockMvc.perform(get("/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
 }
    @Test
    @WithMockUser(roles = {"MANAGER"})
    void getAllUsersNotAllow() throws Exception {
        when(userService.getAllUser(any()))
                .thenReturn(new ArrayList<>());
        MvcResult result = mockMvc.perform(get("/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(412))
                .andReturn();
    }
}
