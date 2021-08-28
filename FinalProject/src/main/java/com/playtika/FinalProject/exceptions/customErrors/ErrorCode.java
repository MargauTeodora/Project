package com.playtika.FinalProject.exceptions.customErrors;

public enum ErrorCode {

    ACCESS_DENIED(99,"You do not have permissions for this operation "),
    INVALID_CREDENTIALS(100,"Invalid username/password supplied"),
    USER_EXISTS(101,"User already exists in system"),
    NO_DELETE_USER(102,"User given for delete doesn't exists"),
    NOT_AUTHORIZED(103,"You should be logged to do this operation!"),
    NO_UPDATE_USER(104,"User given for update doesn't exists"),
    INVALID_TOKEN(105,"Token is not available"),
    INCOMPLETE_DATA(106,"Data is not completed ");


    private int code;
    private String message;

    ErrorCode(int code, String message) {
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
