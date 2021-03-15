package com.project.SLX.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserNotAuthenticatedException extends RuntimeException{

    public UserNotAuthenticatedException() {
        super("User is not authenticated!");
    }

}
