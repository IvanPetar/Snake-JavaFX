package hr.algebra.snakejavafx.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Data
@AllArgsConstructor

public class GameState implements Serializable {

    private List<Position> snakeP1;
    private Direction changeDirectionP1;
    private int scoreP1;

    private List<Position> snakeP2;
    private Direction changeDirectionP2;
    private int scoreP2;

    private Position food1;
    private Position food2;

    private boolean running;
    private boolean paused;
    private boolean gameOver;
    private PlayerType winner;

    public GameState() {

        // PLAYER 1
        snakeP1 = new LinkedList<>();
        snakeP1.add(new Position(5, 5));
        changeDirectionP1 = Direction.RIGHT;
        scoreP1 = 0;

        // PLAYER 2
        snakeP2 = new LinkedList<>();
        snakeP2.add(new Position(15, 15));
        changeDirectionP2 = Direction.LEFT;
        scoreP2 = 0;

        paused = true;
        running = true;
        gameOver = false;
        winner = null;
    }
}


