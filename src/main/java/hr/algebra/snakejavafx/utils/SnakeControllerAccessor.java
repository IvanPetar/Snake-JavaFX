package hr.algebra.snakejavafx.utils;

import hr.algebra.snakejavafx.SnakeController;
import hr.algebra.snakejavafx.model.Direction;
import hr.algebra.snakejavafx.model.GameState;

public class SnakeControllerAccessor {
    private SnakeControllerAccessor() {
    }
    public static void changeP2Direction(Direction d) {
        SnakeController.getInstance()
                .getEngine()
                .changeDirectionP2(d);
    }

    public static void restoreState(GameState state) {
        SnakeController.restoreGameState(state);
    }
}
