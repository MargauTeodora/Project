package com.playtika.FinalProject.exceptions;

import com.playtika.FinalProject.exceptions.customErrors.ErrorCode;

public class GameSessionException extends RuntimeException{
//TODO make game sessio errror codes

    private GameSessionErrorCode userErrorCode;
    public GameSessionException(GameSessionErrorCode userErrorCode) {
        this.userErrorCode = userErrorCode;
    }
    public GameSessionErrorCode getUserErrorCode() {
        return userErrorCode;
    }


    public enum GameSessionErrorCode {

        EXCEED_DAILY_HOURS(501,"A day has just 23 hour!"),
         EXCEED_MINUTES(502,"An hour has just 59 minutes!"),
         NEGATIVE_NUMBER(503,"This value cannot be negative!"),
         IS_PLAYING(504,"You already are in a game! Stop that session and come back!"),
         NONEXISTENT_GAME(505,"This game is not available!"),
        ;
        private int code;
        private String message;

        GameSessionErrorCode(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}

