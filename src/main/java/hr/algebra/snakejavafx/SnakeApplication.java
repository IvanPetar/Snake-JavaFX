package hr.algebra.snakejavafx;

import hr.algebra.snakejavafx.jndi.ConfigurationKey;
import hr.algebra.snakejavafx.jndi.ConfigurationReader;
import hr.algebra.snakejavafx.model.PlayerType;
import hr.algebra.snakejavafx.utils.SnakeNetworkUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;

public class SnakeApplication extends Application {
    public static  PlayerType playerType;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SnakeApplication.class.getResource("snake.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
        scene.setOnKeyPressed(e -> {
            SnakeController.getInstance().handleKeyPressed(e);
            e.consume();
        });
        stage.setTitle("Snake Game!" + playerType.toString());
        stage.setScene(scene);
        stage.show();

        if (playerType == PlayerType.PLAYER_1) {
            int port = ConfigurationReader.getIntegerValueForKey(
                    ConfigurationKey.PLAYER_1_SERVER_PORT);

            System.out.println("[PLAYER_1] Starting server on port " + port);
            SnakeNetworkUtils.startServer();
        }
        if (playerType == PlayerType.PLAYER_2) {
            int port = ConfigurationReader.getIntegerValueForKey(
                    ConfigurationKey.PLAYER_2_SERVER_PORT);

            System.out.println("[PLAYER_2] Starting server on port " + port);
            SnakeNetworkUtils.startClientListener();
        }
    }
    public static void main(String[] args) {
        String firstCommandLineArg =  args[0];

        Boolean playerTypeExists = false;


        for (PlayerType playerType : PlayerType.values()) {
            if (firstCommandLineArg.equals(playerType.toString())) {
                playerTypeExists = true;
                break;
            }
        }

        if (!playerTypeExists) {
            System.out.println("Player type not found!");
            JOptionPane.showMessageDialog(null,
                    "Player type not found!");
            System.exit(0);
        }
        else{
            playerType = PlayerType.valueOf(firstCommandLineArg);
            launch();
        }
    }


}
