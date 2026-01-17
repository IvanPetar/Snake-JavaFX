package hr.algebra.snakejavafx.engine;

import hr.algebra.snakejavafx.model.Direction;
import hr.algebra.snakejavafx.model.GameState;
import hr.algebra.snakejavafx.model.Position;
import javafx.fxml.FXML;

import java.util.Random;

public class GameEngine {

    private final int rows;
    private final int cols;

    private final GameState state;
    private final Random random = new Random();

    public GameEngine(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.state = new GameState();
        spawnFood();
    }

    public GameState getState() {
        return state;
    }

    public void tick() {
        if (!state.isRunning() || state.isPaused()) return;

        Position nextHead = calculateNextHead();

        if (checkCollision(nextHead)) {
            state.setRunning(false);
            state.setGameOver(true);
            return;
        }

        applyMove(nextHead);
    }

    private Position calculateNextHead() {
        Position head = state.getSnake().get(0);
        return head.move(state.getDirection());
    }

    private boolean checkCollision(Position nextHead) {
        if (nextHead.row() < 0 || nextHead.row() >= rows ||
                nextHead.col() < 0 || nextHead.col() >= cols) {
            return true;
        }
        return state.getSnake().contains(nextHead);
    }

    private void applyMove(Position nextHead) {

        state.getSnake().add(0, nextHead);

        if (!nextHead.equals(state.getFood())) {
            state.getSnake().remove(state.getSnake().size() - 1);
        } else {
            state.setScore(state.getScore() + 10);
            spawnFood();
        }
    }

    private void spawnFood() {
        Position food;
        do {
            food = new Position(
                    random.nextInt(cols),
                    random.nextInt(rows)
            );
        } while (state.getSnake().contains(food));

        state.setFood(food);
    }

    public void changeDirection(Direction direction) {
        state.setDirection(direction);
    }

    public void togglePause() {
        if(state.isGameOver()) return;
        state.setPaused(!state.isPaused());
    }
    @FXML
    public void newGame() {
        state.getSnake().clear();
        state.getSnake().add(new Position(5, 5));
        state.setScore(0);
        state.setRunning(true);
        state.setPaused(false);
        state.setGameOver(false);
        state.setDirection(Direction.RIGHT);
        spawnFood();
    }
}

