package com.games.battleship.controller;

import com.games.battleship.model.Player;
import com.games.battleship.model.ShipPosition;
import com.games.battleship.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/battleship")
public class GameController {
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/player/{playerName}")
    public Player createPlayer(@PathVariable String playerName) {
        return gameService.createPlayer(playerName);
    }

    @PostMapping("/player/{playerId}/setup")
    public void setupGame(@PathVariable Long playerId, @RequestBody List<ShipPosition> shipPositions) {
        gameService.setupGame(playerId, shipPositions);
    }

    @PostMapping("/player/{playerId}/attack")
    public String attack(@PathVariable Long playerId, @RequestParam String cell) {
        return gameService.attackCell(playerId, cell);
    }

    @GetMapping("/player/{playerId}/status")
    public String getGameStatus(@PathVariable Long playerId) {
        return gameService.getGameStatus(playerId);
    }
}