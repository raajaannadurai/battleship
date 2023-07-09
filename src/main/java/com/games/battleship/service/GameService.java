package com.games.battleship.service;

import com.games.battleship.constants.Constants;
import com.games.battleship.exception.PlayerNotFoundException;
import com.games.battleship.model.Cell;
import com.games.battleship.model.Attack;
import com.games.battleship.model.Player;
import com.games.battleship.model.Ship;
import com.games.battleship.model.ShipPosition;
import com.games.battleship.repository.AttackRepository;
import com.games.battleship.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.games.battleship.constants.Constants.GAME_IN_PROGRESS;
import static com.games.battleship.constants.Constants.GAME_LOST;
import static com.games.battleship.constants.Constants.GAME_WON;
import static com.games.battleship.constants.Constants.HIT;
import static com.games.battleship.constants.Constants.MISS;
import static com.games.battleship.constants.Constants.SUNK;

@Service
public class GameService {
    private final PlayerRepository playerRepository;
    private final AttackRepository attackRepository;

    @Autowired
    public GameService(PlayerRepository playerRepository, AttackRepository attackRepository) {
        this.playerRepository = playerRepository;
        this.attackRepository = attackRepository;
    }

    public Player createPlayer(String playerName) {
        Player player = new Player();
        player.setName(playerName);
        return playerRepository.save(player);
    }

    public Player setupGame(Long playerId, List<ShipPosition> shipPositions) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(HttpStatus.NOT_FOUND,
                        Constants.PLAYER_NOT_FOUND_ERROR_CODE, Constants.PLAYER_NOT_FOUND_ERROR_MESSAGE));

        List<Ship> ships = new ArrayList<>();
        for (ShipPosition shipPosition : shipPositions) {
            String type = shipPosition.getType();
            int size = shipPosition.getSize();
            List<String> cellPositions = shipPosition.getCells();
            String orientation = shipPosition.getOrientation();

            Ship ship = new Ship();
            ship.setPlayer(player);
            ship.setType(type);
            ship.setSize(size);
            ship.setSunk(false);

            List<Cell> cells = new ArrayList<>();
            for (String cellPosition : cellPositions) {
                Cell cell = new Cell();
                cell.setPosition(cellPosition);
                cell.setOccupied(true);
                cell.setHit(false);
                cell.setShip(ship);
                cells.add(cell);
            }

            if (!isValidShipPlacement(cells, orientation)) {
                throw new PlayerNotFoundException(HttpStatus.BAD_REQUEST,
                        Constants.INVALID_PLACEMENT_ERROR_CODE, Constants.INVALID_PLACEMENT_ERROR_MESSAGE);
            }

            ship.setCells(cells);
            ships.add(ship);
        }

        player.getShips().clear();
        player.getShips().addAll(ships);
        playerRepository.save(player);
        return player;
    }

    private boolean isValidShipPlacement(List<Cell> cells, String orientation) {
        if (orientation.equalsIgnoreCase(Constants.HORIZONTAL)) {
            return areCellsAdjacentHorizontally(cells) && !areCellsOverlapping(cells);
        } else if (orientation.equalsIgnoreCase(Constants.VERTICAL)) {
            return areCellsAdjacentVertically(cells) && !areCellsOverlapping(cells);
        } else {
            return false;
        }
    }

    private boolean areCellsAdjacentHorizontally(List<Cell> cells) {
        List<Integer> columnNumbers = cells.stream()
                .map(cell -> cell.getPosition().charAt(1) - '0')
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        for (int i = 0; i < columnNumbers.size() - 1; i++) {
            if (columnNumbers.get(i + 1) - columnNumbers.get(i) != 1) {
                return false;
            }
        }
        return true;
    }

    private boolean areCellsAdjacentVertically(List<Cell> cells) {
        List<Character> rowLetters = cells.stream()
                .map(cell -> cell.getPosition().charAt(0))
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        for (int i = 0; i < rowLetters.size() - 1; i++) {
            if (rowLetters.get(i + 1) - rowLetters.get(i) != 1) {
                return false;
            }
        }
        return true;
    }

    private boolean areCellsOverlapping(List<Cell> cells) {
        List<String> positions = cells.stream()
                .map(Cell::getPosition)
                .collect(Collectors.toList());

        return positions.size() != positions.stream().distinct().count();
    }

    public String getGameStatus(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(HttpStatus.NOT_FOUND,
                        Constants.PLAYER_NOT_FOUND_ERROR_CODE, Constants.PLAYER_NOT_FOUND_ERROR_MESSAGE));

        Player opponentPlayer = playerRepository.findAll().stream()
                .filter(p -> !p.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new PlayerNotFoundException(HttpStatus.NOT_FOUND,
                        Constants.OPPONENT_PLAYER_NOT_FOUND_ERROR_CODE, Constants.OPPONENT_PLAYER_NOT_FOUND_ERROR_MESSAGE));

        if (player.getShips().stream().allMatch(Ship::isSunk)) {
            return GAME_LOST;
        } else if(opponentPlayer.getShips().stream().allMatch(Ship::isSunk)){
            return GAME_WON;
        } else {
            return GAME_IN_PROGRESS;
        }
    }

    public String attackCell(Long playerId, String cell) {
        Player attackingPlayer = playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(HttpStatus.NOT_FOUND,
                        Constants.PLAYER_NOT_FOUND_ERROR_CODE, Constants.PLAYER_NOT_FOUND_ERROR_MESSAGE));

        Player opponentPlayer = playerRepository.findAll().stream()
                .filter(p -> !p.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new PlayerNotFoundException(HttpStatus.NOT_FOUND,
                        Constants.OPPONENT_PLAYER_NOT_FOUND_ERROR_CODE, Constants.OPPONENT_PLAYER_NOT_FOUND_ERROR_MESSAGE));

        Optional<Cell> attackedCell = opponentPlayer.getShips().stream()
                .flatMap(ship -> ship.getCells().stream())
                .filter(c -> c.getPosition().equals(cell))
                .findFirst();

        Attack attack = new Attack();
        attack.setPlayer(attackingPlayer);
        attack.setCell(cell);

        if (attackedCell.isPresent()) {
            Cell cellToAttack = attackedCell.get();
            attack.setHit(true);
            cellToAttack.setHit(true);

            Ship attackedShip = cellToAttack.getShip();
            if (attackedShip.getCells().stream().allMatch(Cell::isHit)) {
                attack.setSunk(true);
                attackedShip.setSunk(true);
            }
        } else {
            attack.setHit(false);
        }

        attackingPlayer.getAttacks().add(attack);
        playerRepository.save(attackingPlayer);
        attackRepository.save(attack);

        return attack.isHit() ? (attack.isSunk() ? SUNK : HIT) : MISS;
    }
}