package com.games.battleship.service;

import com.games.battleship.model.Player;
import com.games.battleship.model.Ship;
import com.games.battleship.model.ShipPosition;
import com.games.battleship.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {
    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private GameService gameService;

    @Test
    public void testSetupGame() {

        Long playerId = 1L;

        List<ShipPosition> shipPositions = Arrays.asList(
                createShipPosition("Aircraft Carrier", 5, Arrays.asList("A1", "A2", "A3", "A4", "A5"), "horizontal"),
                createShipPosition("Battleship", 4, Arrays.asList("B1", "B2", "B3", "B4"), "horizontal"),
                createShipPosition("Cruiser", 3, Arrays.asList("C1", "D1", "E1"), "vertical"),
                createShipPosition("Submarine", 3, Arrays.asList("B8", "C8", "D8"), "vertical"),
                createShipPosition("Destroyer", 2, Arrays.asList("E1", "E2"), "horizontal")
        );

        Player player = new Player();
        player.setId(playerId);
        player.setName("Abc");
        MockMvcBuilders.standaloneSetup(gameService).build();
        when(playerRepository.save(any(Player.class))).thenReturn(player);
        when(playerRepository.findById(playerId)).thenReturn(java.util.Optional.of(player));

        gameService.setupGame(playerId, shipPositions);

        verify(playerRepository).findById(playerId);
        verify(playerRepository).save(player);

        assertEquals(shipPositions.size(), player.getShips().size());

        for (int i = 0; i < shipPositions.size(); i++) {
            ShipPosition shipPosition = shipPositions.get(i);
            Ship ship = player.getShips().get(i);

            assertEquals(shipPosition.getType(), ship.getType());
            assertEquals(shipPosition.getSize(), ship.getSize());
            assertEquals(shipPosition.getCells().size(), ship.getCells().size());
        }
    }

    private ShipPosition createShipPosition(String type, int size, List<String> cells, String orientation) {
        ShipPosition shipPosition = new ShipPosition();
        shipPosition.setType(type);
        shipPosition.setSize(size);
        shipPosition.setCells(cells);
        shipPosition.setOrientation(orientation);
        return shipPosition;
    }
}

