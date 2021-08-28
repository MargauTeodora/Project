package com.playtika.FinalProject.controllers;

import com.playtika.FinalProject.exceptions.GameSessionException;
import com.playtika.FinalProject.exceptions.UserException;
import com.playtika.FinalProject.exceptions.customErrors.ErrorCode;
import com.playtika.FinalProject.exceptions.customErrors.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ExceptionsController {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public ErrorMessage handleException(RuntimeException ex) {
        if (ex instanceof UserException) {
            return new ErrorMessage(((UserException) ex).getUserErrorCode().getMessage(),
                    ((UserException) ex).getUserErrorCode().getCode());
        }
        if (ex instanceof GameSessionException) {
            return new ErrorMessage(((GameSessionException) ex).getUserErrorCode().getMessage(),
                    ((GameSessionException) ex).getUserErrorCode().getCode());
        }
        if(ex instanceof AccessDeniedException){
            return new ErrorMessage(ErrorCode.NO_PERMISSION.getMessage(),
                    ErrorCode.NO_PERMISSION.getCode());
        }
        return new ErrorMessage("Unknown error",404);
    }
}
