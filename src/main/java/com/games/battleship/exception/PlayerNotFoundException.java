package com.games.battleship.exception;

import org.springframework.http.HttpStatus;

public class PlayerNotFoundException extends GameException {
    public PlayerNotFoundException(HttpStatus httpStatus, String errorCode, String errorMessage) {
        super(httpStatus, errorCode, errorMessage);
    }
}
