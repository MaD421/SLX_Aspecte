package com.project.SLX.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class ActionNotAllowedException extends RuntimeException{

    public ActionNotAllowedException() {
        super("You are not allowed to do this action!");
    }

}
