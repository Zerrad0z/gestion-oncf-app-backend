package com.oncf.gare_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UniqueConstraintViolationException extends RuntimeException {
    public UniqueConstraintViolationException(String message) {
        super(message);
    }
}