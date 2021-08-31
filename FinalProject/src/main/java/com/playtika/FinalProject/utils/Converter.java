package com.playtika.FinalProject.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.playtika.FinalProject.models.GameSession;
import com.playtika.FinalProject.models.User;
import com.playtika.FinalProject.models.dto.game.GameSessionInfoDTO;
import com.playtika.FinalProject.models.dto.users.UserInfoAdminDTO;
import com.playtika.FinalProject.models.dto.users.UserInfoDTO;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Converter {
    public static String asJSONString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static LocalDateTime convertToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static String convertDateToString(Date date) {
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime.getDayOfMonth() + "/" + localDateTime.getMonthValue() + "/" + localDateTime.getYear();
    }

    public static Date convertStringToDate(String dateStr) throws ParseException {
        Date date = new SimpleDateFormat("'yyyy-MM-dd'T'HH:mm:ss").parse(dateStr);
        return date;
    }

    public static String convertStringToDate(Date date) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("'yyyy-MM-dd'T'HH:mm:ss");
        String dateStr = dateFormat.format(dateFormat);
        return dateStr;
    }

    public static List<UserInfoAdminDTO> convertUsersToDTOList(List<User> users) {
        List<UserInfoAdminDTO> list = new ArrayList<>();
        for (User user : users) {
            UserInfoAdminDTO userDTO = new UserInfoAdminDTO();
            userDTO.setUserName(user.getUsername());
            userDTO.setEmail(user.getEmail());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            userDTO.setMaximumDailyPlayTime(user.getMaximumDailyPlayTime());
            userDTO.setRoles(user.getRoles());
            list.add(userDTO);
        }
        return list;
    }
    public static List<GameSessionInfoDTO> convertGamesToDTOList(List<GameSession> gameSessions) {
        List<GameSessionInfoDTO> list = new ArrayList<>();
        for (GameSession gameSession : gameSessions) {
            GameSessionInfoDTO gameSessionInfoDTO = new GameSessionInfoDTO();
            gameSessionInfoDTO.setGameName(gameSession.getGameName());
            gameSessionInfoDTO.setDuration(gameSession.getDuration());
            gameSessionInfoDTO.setStartDate(Converter.convertDateToString(gameSession.getStartDate()));
            list.add(gameSessionInfoDTO);
        }
        return list;
    }


}
