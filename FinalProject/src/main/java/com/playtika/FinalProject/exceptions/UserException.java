package com.playtika.FinalProject.exceptions;

import com.playtika.FinalProject.exceptions.customErrors.ErrorCode;

public class UserException extends RuntimeException{
    private ErrorCode userErrorCode;
    public UserException(ErrorCode userErrorCode) {
        this.userErrorCode = userErrorCode;
    }
    public ErrorCode getUserErrorCode() {
        return userErrorCode;
    }
}
