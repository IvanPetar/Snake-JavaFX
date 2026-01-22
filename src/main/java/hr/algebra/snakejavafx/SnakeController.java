package hr.algebra.snakejavafx;

import hr.algebra.snakejavafx.engine.GameEngine;
import hr.algebra.snakejavafx.input.InputHandler;
import hr.algebra.snakejavafx.model.GameState;
import hr.algebra.snakejavafx.model.PlayerType;
import hr.algebra.snakejavafx.model.Position;
import hr.algebra.snakejavafx.rmi.ChatRemoteService;
import hr.algebra.snakejavafx.utils.*;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyEvent;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SnakeController {

    private static final Logger log
            = LoggerFactory.getLogger(SnakeController.class);

    @Getter
    private static SnakeController instance;

    private static GameState remoteState;

    @FXML
    private Canvas gameCanvas;

    @FXML
    private Label scoreLabel;

    @FXML
    private Button pauseBtn;

    @FXML
    private TextField chatMessageTextField;

    @FXML
    private TextArea chatMessageTextArea;

    @Getter
    private GameEngine engine;
    private GraphicsContext gc;
    private ScheduledExecutorService loop;
    private boolean popupShown = false;
    private InputHandler  inputHandler;

    private static final int TILE_SIZE = 20;
    private static final int TICK_MS = 1;

    ChatRemoteService chatRemoteService;

    @FXML
    private void initialize() {
        instance = this;
        pauseBtn.setFocusTraversable(false);
        chatMessageTextField.setFocusTraversable(false);
        chatMessageTextArea.setFocusTraversable(false);

        gc = gameCanvas.getGraphicsContext2D();
        initEngine();
        inputHandler = new InputHandler();
        if (SnakeApplication.playerType != PlayerType.PLAYER_2) {
            startLoop();
            render();
        }
        if(SnakeApplication.playerType != PlayerType.SINGLE_PLAYER) {
            Optional<ChatRemoteService> chatRemoteServiceOptional = ChatUtils.initializeChatRemoteService();
            chatRemoteServiceOptional.ifPresent(remoteService -> chatRemoteService = remoteService);

            Timeline chatMessageRefreshTimeLine = ChatUtils.getChatRefreshTimeline(chatRemoteService, chatMessageTextArea);
            chatMessageRefreshTimeLine.play();
        }
        Platform.runLater(this::render);
        Platform.runLater(() -> gameCanvas.requestFocus());
    }

    private void initEngine() {
        int rows = (int) (gameCanvas.getHeight() / TILE_SIZE);
        int cols = (int) (gameCanvas.getWidth() / TILE_SIZE);

        if (SnakeApplication.playerType != PlayerType.PLAYER_2) {
            // SINGLE ili PLAYER_1
            engine = new GameEngine(rows, cols);
            engine.getState().setPaused(true);
        }
    }

    private void startLoop() {
        if (SnakeApplication.playerType == PlayerType.PLAYER_2) return;
        loop = Executors.newSingleThreadScheduledExecutor();
        loop.scheduleAtFixedRate(() -> {

            engine.tick(SnakeApplication.playerType == PlayerType.SINGLE_PLAYER
                    ? PlayerType.SINGLE_PLAYER
                    : PlayerType.PLAYER_1);

            if (SnakeApplication.playerType == PlayerType.PLAYER_1) {
                SnakeNetworkUtils.sendState(engine.getState());
            }
            Platform.runLater(this::render);
        }, 0, TICK_MS, TimeUnit.SECONDS);
    }

    public void handleKeyPressed(KeyEvent e) {
        inputHandler.handleKeyPressed(e);
    }

    private boolean isInsideCanvas(Position p) {
        if (p == null) return false;

        double x = p.col() * TILE_SIZE;
        double y = p.row() * TILE_SIZE;

        return x >= 0 && y >= 0
                && x + TILE_SIZE <= gameCanvas.getWidth()
                && y + TILE_SIZE <= gameCanvas.getHeight();
    }

    @FXML
    private void render() {

        GameState s;
        if (SnakeApplication.playerType == PlayerType.PLAYER_2) {
            s = remoteState;
        } else {
            s = engine.getState();
        }

        if (s == null) return;

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        gc.setFill(Color.RED);

        if (SnakeApplication.playerType == PlayerType.SINGLE_PLAYER) {
            // SINGLE → one fruit
            Position food = s.getFood1();
            if (isInsideCanvas(food)) {
                gc.fillRect(
                        food.col() * TILE_SIZE,
                        food.row() * TILE_SIZE,
                        TILE_SIZE,
                        TILE_SIZE
                );
            }
        } else {
            // MULTI → two fruit
            if (isInsideCanvas(s.getFood1())) {
                gc.fillRect(
                        s.getFood1().col() * TILE_SIZE,
                        s.getFood1().row() * TILE_SIZE,
                        TILE_SIZE,
                        TILE_SIZE
                );
            }
            if (isInsideCanvas(s.getFood2())) {
                gc.fillRect(
                        s.getFood2().col() * TILE_SIZE,
                        s.getFood2().row() * TILE_SIZE,
                        TILE_SIZE,
                        TILE_SIZE
                );
            }
        }

        gc.setFill(Color.GREEN);
        for (Position p : s.getSnakeP1()) {
            gc.fillRect(
                    p.col() * TILE_SIZE,
                    p.row() * TILE_SIZE,
                    TILE_SIZE,
                    TILE_SIZE
            );
        }
        if (SnakeApplication.playerType != PlayerType.SINGLE_PLAYER) {
            gc.setFill(Color.BLUE);
            for (Position p : s.getSnakeP2()) {
                gc.fillRect(
                        p.col() * TILE_SIZE,
                        p.row() * TILE_SIZE,
                        TILE_SIZE,
                        TILE_SIZE
                );
            }
        }
        scoreLabel.setText(
                SnakeApplication.playerType == PlayerType.SINGLE_PLAYER
                        ? String.valueOf(s.getScoreP1())
                        : s.getScoreP1() + " : " + s.getScoreP2()
        );
        if (!popupShown && s.isGameOver()) {
            popupShown = true;

            if (SnakeApplication.playerType == PlayerType.SINGLE_PLAYER) {
                DialogUtils.showGameOver(s.getScoreP1());
            } else {
                DialogUtils.showGameOverMultiplayer(
                        s.getWinner(),
                        s.getScoreP1(),
                        s.getScoreP2()
                );
            }
        }
    }

    @FXML
    private void onPause() {
        if (SnakeApplication.playerType == PlayerType.PLAYER_2) {
            return;
        }
        engine.togglePause();

        if (engine.getState().isPaused()) {
            pauseBtn.setText("Resume");
        } else {
            pauseBtn.setText("Pause");
        }
    }

    @FXML
    private void onStartGame(){
        if (SnakeApplication.playerType == PlayerType.PLAYER_2) {
            return;
        }
        engine.getState().setPaused(false);
        if (SnakeApplication.playerType == PlayerType.PLAYER_1) {
            SnakeNetworkUtils.sendState(engine.getState());
        }
    }

    public void startNewGame() {
        if (SnakeApplication.playerType == PlayerType.PLAYER_2) {
            return; // PLAYER_2 ne resetira igru
        }
        engine.newGame();
        engine.getState().setPaused(true);
        popupShown = false;
        pauseBtn.setText("Pause");

        if (SnakeApplication.playerType == PlayerType.PLAYER_1) {
            SnakeNetworkUtils.sendState(engine.getState());
        }
    }

    public void saveGame(){
        SaveLoadUtils.saveGame(engine.getState());
    }

    public void loadGame(){
        GameState loaded = SaveLoadUtils.loadGame();

        if (loaded != null) {
            engine.loadGameState(loaded);
            popupShown = false;
            pauseBtn.setText("Pause");
        }
    }

    public void generateDocumentation(){
        try {
            DocumentationUtils.generateHtmlDocumentationFile();
            DialogUtils.showDialog("Uspjesno generirana dokumentacija!",
                    "HTML dokumentacija za aplikaciju je uspjesno generirana"
                    , Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            String message = "Doslo do pogreske kod generiranje HTML DOKUMENTA";
            DialogUtils.showDialog("Pogreska!",
                    message,
                    Alert.AlertType.INFORMATION);
            log.error(e.getMessage());
        }
    }

    public static void restoreGameState(GameState received) {
        Platform.runLater(() -> {
            remoteState = received;

            // PLAYER_2 samo PRIKAZUJE, ne simulira
            if (SnakeApplication.playerType == PlayerType.PLAYER_2) {
                instance.render();
            }
        });
    }

    public void sendChatMessage(){
        ChatUtils.SendChatMessage(chatRemoteService, chatMessageTextField);
    }
}
