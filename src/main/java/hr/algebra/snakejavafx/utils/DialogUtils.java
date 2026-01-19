package hr.algebra.snakejavafx.utils;

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

    public static void showDialog(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
