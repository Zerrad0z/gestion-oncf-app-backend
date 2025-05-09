package com.oncf.gare_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SectionAlreadyExistsException extends RuntimeException {
    public SectionAlreadyExistsException(String message) {
        super(message);
    }
}

