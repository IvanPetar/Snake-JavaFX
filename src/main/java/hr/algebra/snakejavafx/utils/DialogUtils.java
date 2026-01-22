package hr.algebra.snakejavafx.utils;

import hr.algebra.snakejavafx.model.PlayerType;
import javafx.scene.control.Alert;

public class DialogUtils {
    private DialogUtils() {
    }

    public static void showGameOver(int score) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Game Over!");
        alert.setContentText("Your score: " + score);

        alert.showAndWait();
    }

    public static void showGameOverMultiplayer(
            PlayerType winner,
            int scoreP1,
            int scoreP2
    ) {String winnerText =
                winner == PlayerType.PLAYER_1
                        ? "PLAYER 1 is winner!"
                        : "PLAYER 2 is winner!";

        String content = """
            %s
            SCORE: %d : %d
            """.formatted(winnerText, scoreP1, scoreP2);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Game Over!");
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showDialog(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
