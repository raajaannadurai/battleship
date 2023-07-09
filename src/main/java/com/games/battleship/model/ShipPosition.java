package com.games.battleship.model;

import lombok.Data;

import java.util.List;

@Data
public class ShipPosition {
    private String type;
    private int size;
    private List<String> cells;
    private String orientation;
}

