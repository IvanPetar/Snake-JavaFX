package hr.algebra.snakejavafx.engine;

import hr.algebra.snakejavafx.SnakeApplication;
import hr.algebra.snakejavafx.model.Direction;
import hr.algebra.snakejavafx.model.GameState;
import hr.algebra.snakejavafx.model.PlayerType;
import hr.algebra.snakejavafx.model.Position;
import lombok.Getter;

import java.util.List;
import java.util.Random;

public class GameEngine {

    private final int rows;
    private final int cols;

    @Getter
    private GameState state;

    private final Random random = new Random();

    public GameEngine(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.state = new GameState();
        spawnFood(SnakeApplication.playerType);
    }
    public void tick(PlayerType mode) {
        if (!state.isRunning() || state.isPaused() || state.isGameOver()) return;

        moveSnake(
                state.getSnakeP1(),
                state.getChangeDirectionP1(),
                PlayerType.PLAYER_1
        );

        if (mode != PlayerType.SINGLE_PLAYER) {
            moveSnake(
                    state.getSnakeP2(),
                    state.getChangeDirectionP2(),
                    PlayerType.PLAYER_2
            );
        }
    }
    private void moveSnake(List<Position> snake,
                           Direction direction,
                           PlayerType player) {

        Position head = snake.get(0);
        Position next = head.move(direction);

        if (checkWallCollision(next) ||
                checkSnakeCollision(next, snake) ||
                checkEnemyCollision(next, player)) {

            finishGame(player == PlayerType.PLAYER_1
                    ? PlayerType.PLAYER_2
                    : PlayerType.PLAYER_1);
            return;
        }

        snake.add(0, next);

        if (eatFood(player, next)){
            spawnFood(SnakeApplication.playerType);
            return;
        }

        snake.remove(snake.size() - 1);
    }

    private boolean checkWallCollision(Position p) {
        return p.row() < 0 || p.row() >= rows ||
                p.col() < 0 || p.col() >= cols;
    }

    private boolean checkSnakeCollision(Position p, List<Position> snake) {
        return snake.contains(p);
    }

    private boolean checkEnemyCollision(Position p, PlayerType player) {
        if (player == PlayerType.SINGLE_PLAYER) {
            return false;
        }
        if (player == PlayerType.PLAYER_1) {
            return state.getSnakeP2().contains(p);
        }
        if (player == PlayerType.PLAYER_2) {
            return state.getSnakeP1().contains(p);
        }
        return false;
    }

    private boolean eatFood(PlayerType player, Position head) {

        if ((player == PlayerType.SINGLE_PLAYER || player == PlayerType.PLAYER_1) && head.equals(state.getFood1())) {
                addScore(player);
                spawnFood1();
                return true;
            }
        // MULTIPLAYER - both fruits
        if (player != PlayerType.SINGLE_PLAYER && head.equals(state.getFood2())) {
                addScore(player);
                spawnFood2();
                return true;
            }

        return false;
    }

    private void addScore(PlayerType player) {
        if (player == PlayerType.PLAYER_1) {
            state.setScoreP1(state.getScoreP1() + 10);
        } else {
            state.setScoreP2(state.getScoreP2() + 10);
        }
    }
    private void spawnFood(PlayerType mode) {
        spawnFood1();
        if (mode != PlayerType.SINGLE_PLAYER) {
            spawnFood2();
        } else {
            state.setFood2(null);
        }
    }

    private void spawnFood1() {
        state.setFood1(randomFreePosition());
    }

    private void spawnFood2() {
        state.setFood2(randomFreePosition());
    }

    private Position randomFreePosition() {
        Position p;
        do {
            p = new Position(
            random.nextInt(cols),
            random.nextInt(rows));
        } while (
                state.getSnakeP1().contains(p) ||
                        state.getSnakeP2().contains(p)
        );
        return p;
    }

    public void changeDirectionP1(Direction d) {
        state.setChangeDirectionP1(d);
    }

    public void changeDirectionP2(Direction d) {
        state.setChangeDirectionP2(d);
    }

    public void togglePause() {
        if (!state.isGameOver()) {
            state.setPaused(!state.isPaused());
        }
    }

    public void newGame() {
        state.getSnakeP1().clear();
        state.getSnakeP1().add(new Position(5, 5));
        state.setChangeDirectionP1(Direction.RIGHT);
        state.setScoreP1(0);

        if (state.getWinner() != PlayerType.SINGLE_PLAYER) {
            state.getSnakeP2().clear();
            state.getSnakeP2().add(new Position(15, 15));
            state.setChangeDirectionP2(Direction.LEFT);
            state.setScoreP2(0);
        } else {
            // SINGLE PLAYER → nema P2 zmije
            state.getSnakeP2().clear();
        }
        state.setPaused(true);
        state.setRunning(true);
        state.setGameOver(false);
        state.setWinner(null);

        if (SnakeApplication.playerType == PlayerType.SINGLE_PLAYER) {
            spawnFood1();
        } else {
            spawnFood(SnakeApplication.playerType);
        }
    }

    private void finishGame(PlayerType winner) {
        state.setGameOver(true);
        state.setRunning(false);
        state.setWinner(winner);
    }

    public void loadGameState(GameState loaded) {
        this.state = loaded;
    }
}

