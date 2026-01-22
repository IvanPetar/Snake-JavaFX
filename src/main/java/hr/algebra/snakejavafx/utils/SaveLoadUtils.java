package hr.algebra.snakejavafx.utils;

import hr.algebra.snakejavafx.model.GameState;

import java.io.*;

public class SaveLoadUtils {
    private SaveLoadUtils() {
    }
    private static final String SAVE_FILE = "game/snake.dat";

    public static void saveGame(GameState gameState) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {

            oos.writeObject(gameState);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static GameState loadGame() {
        GameState gameState = null;

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(SAVE_FILE))) {

            gameState = (GameState) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return gameState;
    }
}
