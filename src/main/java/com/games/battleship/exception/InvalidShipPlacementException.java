package com.games.battleship.exception;

import org.springframework.http.HttpStatus;

public class InvalidShipPlacementException extends GameException {
    public InvalidShipPlacementException(HttpStatus httpStatus, String errorCode, String errorMessage) {
        super(httpStatus, errorCode, errorMessage);
    }
}
