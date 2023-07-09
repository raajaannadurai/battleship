package com.games.battleship.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class GameException extends RuntimeException {
    private HttpStatus httpStatus;
    private String errorCode;
    private String errorMessage;
}
