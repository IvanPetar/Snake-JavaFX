package hr.algebra.snakejavafx.utils;


import hr.algebra.snakejavafx.jndi.ConfigurationKey;
import hr.algebra.snakejavafx.jndi.ConfigurationReader;
import hr.algebra.snakejavafx.model.Direction;
import hr.algebra.snakejavafx.model.GameState;
import javafx.application.Platform;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SnakeNetworkUtils {
    private SnakeNetworkUtils() {}

    public static void startServer() {
        new Thread(() -> {
            int port = ConfigurationReader.getIntegerValueForKey(
                    ConfigurationKey.PLAYER_1_SERVER_PORT);

            try (ServerSocket serverSocket = new ServerSocket(port)) {

                System.out.println("[PLAYER_1 SERVER] Listening on port " + port);

                while (true) {
                    Socket client = serverSocket.accept();
                    System.out.println("[PLAYER_1 SERVER] Client connected from port "
                            + client.getPort());

                    new Thread(() -> handleDirectionClient(client)).start();
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, "Snake-Server-P1").start();
    }

    private static void handleDirectionClient(Socket socket) {
        try (ObjectInputStream ois =
                     new ObjectInputStream(socket.getInputStream())) {

            Object obj = ois.readObject();

            if (obj instanceof Direction d) {
                SnakeControllerAccessor.changeP2Direction(d);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //PLAYER 2 → PLAYER 1 (SEND DIRECTION)
    public static void sendDirection(Direction d) {

        String host = ConfigurationReader.getStringValueForKey(
                ConfigurationKey.HOSTNAME);
        int port = ConfigurationReader.getIntegerValueForKey(
                ConfigurationKey.PLAYER_1_SERVER_PORT);

        try (Socket socket = new Socket(host, port);
             ObjectOutputStream oos =
                     new ObjectOutputStream(socket.getOutputStream())) {

            System.out.println("[CLIENT] Sending direction to "
                    + host + ":" + port);

            oos.writeObject(d);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //PLAYER 1 → PLAYER 2 (SEND GAME STATE)
    public static void sendState(GameState state) {

        String host = ConfigurationReader.getStringValueForKey(
                ConfigurationKey.HOSTNAME);
        int port = ConfigurationReader.getIntegerValueForKey(
                ConfigurationKey.PLAYER_2_SERVER_PORT);

        try (Socket socket = new Socket(host, port);
             ObjectOutputStream oos =
                     new ObjectOutputStream(socket.getOutputStream())) {

            System.out.println("[CLIENT] Sending GameState to "
                    + host + ":" + port);

            oos.writeObject(state);

        } catch (Exception ignored) {
        }
    }

    //PLAYER 2 SERVER – prima GameState od PLAYER_1
    public static void startClientListener() {
        new Thread(() -> {
            int port = ConfigurationReader.getIntegerValueForKey(
                    ConfigurationKey.PLAYER_2_SERVER_PORT);

            try (ServerSocket serverSocket = new ServerSocket(port)) {

                System.out.println("[PLAYER_2 SERVER] Listening on port " + port);

                while (true) {
                    Socket socket = serverSocket.accept();
                    System.out.println("[PLAYER_2 SERVER] Client connected from port "
                            + socket.getPort());

                    new Thread(() -> handleStateClient(socket)).start();
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, "Snake-Server-P2").start();
    }

    private static void handleStateClient(Socket socket) {
        try (ObjectInputStream ois =
                     new ObjectInputStream(socket.getInputStream())) {

            GameState state = (GameState) ois.readObject();

            Platform.runLater(() ->
                    SnakeControllerAccessor.restoreState(state)
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

