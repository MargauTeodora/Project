package com.playtika.FinalProject.controllers;

import com.playtika.FinalProject.exceptions.GameSessionException;
import com.playtika.FinalProject.exceptions.UserException;
import com.playtika.FinalProject.exceptions.customErrors.UserErrorCode;
import com.playtika.FinalProject.exceptions.customErrors.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.concurrent.ExecutionException;

public class ExceptionsController {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public ErrorMessage handleException(Exception ex) {
        if (ex instanceof UserException) {
            return new ErrorMessage(((UserException) ex).getUserErrorCode().getMessage(),
                    ((UserException) ex).getUserErrorCode().getCode());
        }
        if (ex instanceof GameSessionException) {
            return new ErrorMessage(((GameSessionException) ex).getUserErrorCode().getMessage(),
                    ((GameSessionException) ex).getUserErrorCode().getCode());
        }
        if(ex instanceof AccessDeniedException){
            return new ErrorMessage(UserErrorCode.NO_PERMISSION.getMessage(),
                    UserErrorCode.NO_PERMISSION.getCode());
        }
        if(ex instanceof ExecutionException || ex instanceof InterruptedException){
            return new ErrorMessage(GameSessionException.GameSessionErrorCode.GET_GAME_FAIL
                    .getMessage(),
                    GameSessionException.GameSessionErrorCode.GET_GAME_FAIL.getCode());
        }


        return new ErrorMessage(ex.getMessage(),404);
    }
}
