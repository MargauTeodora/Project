package com.playtika.FinalProject.exceptions;

import com.playtika.FinalProject.exceptions.customErrors.UserErrorCode;

public class UserException extends RuntimeException{
    private UserErrorCode userErrorCode;
    public UserException(UserErrorCode userErrorCode) {
        this.userErrorCode = userErrorCode;
    }
    public UserErrorCode getUserErrorCode() {
        return userErrorCode;
    }
}
