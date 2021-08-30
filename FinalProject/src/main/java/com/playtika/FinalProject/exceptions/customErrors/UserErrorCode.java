package com.playtika.FinalProject.exceptions.customErrors;

public enum UserErrorCode {

    NO_PERMISSION(99,"You do not have permissions for this operation "),
    INVALID_CREDENTIALS(100,"Invalid username/password supplied"),
    USER_EXISTS(101,"User already exists in system"),
    NO_DELETE_USER(102,"User given for delete doesn't exists"),
    NOT_AUTHORIZED(103,"You should be logged to do this operation!"),
    NO_UPDATE_USER(104,"User given for update doesn't exists"),
    INVALID_TOKEN(105,"Token is not available"),
    INCOMPLETE_DATA(106,"Data is not completed "),
    MISSING_CREDENTIALS(107,"Credential is missing! "),
    INCORRECT_USERNAME(108,"Invalid username! Username should contains only a-z characters and 1-9 numbers "),
    INCORRECT_EMAIL(109,"Invalid email! "),
    INCORRECT_PASSWORD(110,"Invalid password! "),
    NOT_ALLOWED(111," You cannot do that! "),
    ;


    private int code;
    private String message;

    UserErrorCode(int code, String message) {
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
