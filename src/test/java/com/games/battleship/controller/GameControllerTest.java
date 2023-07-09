package com.games.battleship.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.games.battleship.model.ShipPosition;
import com.games.battleship.service.GameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameControllerTest {
    @Mock
    private GameService gameService;

    @InjectMocks
    private GameController gameController;

    @Test
    public void testSetupGame() throws Exception {
        Long playerId = 1L;

        List<ShipPosition> shipPositions = Arrays.asList(
                createShipPosition("Aircraft Carrier", 5, Arrays.asList("A1", "A2", "A3", "A4", "A5"), "horizontal"),
                createShipPosition("Battleship", 4, Arrays.asList("B1", "B2", "B3", "B4"), "horizontal"),
                createShipPosition("Cruiser", 3, Arrays.asList("C1", "D1", "E1"), "vertical"),
                createShipPosition("Submarine", 3, Arrays.asList("B8", "C8", "D8"), "vertical"),
                createShipPosition("Destroyer", 2, Arrays.asList("E1", "E2"), "horizontal")
        );

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();
        when(gameService.setupGame(eq(playerId), anyList())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/battleship/player/{playerId}/setup", playerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(shipPositions)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(gameService).setupGame(eq(playerId), anyList());
    }

    private ShipPosition createShipPosition(String type, int size, List<String> cells, String orientation) {
        ShipPosition shipPosition = new ShipPosition();
        shipPosition.setType(type);
        shipPosition.setSize(size);
        shipPosition.setCells(cells);
        shipPosition.setOrientation(orientation);
        return shipPosition;
    }

    private String toJson(Object object) throws JsonProcessingException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }
}
