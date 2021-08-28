package com.playtika.FinalProject.exceptions;

import com.playtika.FinalProject.exceptions.customErrors.ErrorCode;

public class GameSessionException extends RuntimeException{
//TODO make game sessio errror codes


    private ErrorCode userErrorCode;
    public GameSessionException(ErrorCode userErrorCode) {
        this.userErrorCode = userErrorCode;
    }
    public ErrorCode getUserErrorCode() {
        return userErrorCode;
    }
}

