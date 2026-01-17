package hr.algebra.snakejavafx.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Data
@AllArgsConstructor
public class GameState implements Serializable {

    // getters & setters
    private List<Position> snake;
    private Position food;
    private Direction direction;
    private int score;
    private boolean running;
    private boolean paused;
    private boolean gameOver;

    public GameState() {
        snake = new LinkedList<>();
        snake.add(new Position(5, 5)); // početna glava
        direction = Direction.RIGHT;
        score = 0;
        running = true;
        paused = false;
        gameOver = false;
    }
}
