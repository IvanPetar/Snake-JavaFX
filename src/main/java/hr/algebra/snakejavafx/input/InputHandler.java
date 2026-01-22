package hr.algebra.snakejavafx.input;

import hr.algebra.snakejavafx.SnakeApplication;
import hr.algebra.snakejavafx.SnakeController;
import hr.algebra.snakejavafx.engine.GameEngine;
import hr.algebra.snakejavafx.model.Direction;
import hr.algebra.snakejavafx.model.PlayerType;
import hr.algebra.snakejavafx.utils.SnakeNetworkUtils;
import javafx.scene.input.KeyEvent;

public class InputHandler {

    public void handleKeyPressed(KeyEvent e) {
        if (SnakeApplication.playerType == PlayerType.SINGLE_PLAYER
                || SnakeApplication.playerType == PlayerType.PLAYER_1) {
            handleLocalPlayerInput(e);
        }

        if (SnakeApplication.playerType == PlayerType.PLAYER_2) {
            handleRemotePlayerInput(e);
        }
    }

    private void handleLocalPlayerInput(KeyEvent e) {
        GameEngine engine = SnakeController.getInstance().getEngine();

        if (engine == null) return;

        switch (e.getCode()) {
            case W -> engine.changeDirectionP1(Direction.UP);
            case S -> engine.changeDirectionP1(Direction.DOWN);
            case A -> engine.changeDirectionP1(Direction.LEFT);
            case D -> engine.changeDirectionP1(Direction.RIGHT);
        }
    }

    private void handleRemotePlayerInput(KeyEvent e) {
        switch (e.getCode()) {
            case UP -> SnakeNetworkUtils.sendDirection(Direction.UP);
            case DOWN -> SnakeNetworkUtils.sendDirection(Direction.DOWN);
            case LEFT -> SnakeNetworkUtils.sendDirection(Direction.LEFT);
            case RIGHT -> SnakeNetworkUtils.sendDirection(Direction.RIGHT);
        }
    }
}