package org.levalnik.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class JsonProcessingException extends RuntimeException {
    public JsonProcessingException(String message) {super(message);}
}
