package hr.algebra.snakejavafx;

import hr.algebra.snakejavafx.engine.GameEngine;
import hr.algebra.snakejavafx.model.Direction;
import hr.algebra.snakejavafx.model.GameState;
import hr.algebra.snakejavafx.model.Position;
import hr.algebra.snakejavafx.utils.DialogUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class SnakeController {
    @FXML
    private Canvas gameCanvas;

    @FXML
    private Label scoreLabel;

    @FXML
    private Button pauseBtn;

    private GameEngine engine;
    private GraphicsContext gc;
    private ScheduledExecutorService loop;
    private boolean popupShown = false;
    private boolean wasRunning = true;

    private static final int TILE_SIZE = 20;
    private static final int TICK_MS = 150;

    @FXML
    private void initialize() {
        gc = gameCanvas.getGraphicsContext2D();
        initEngine();
        setupInput();
        startLoop();
    }

    private void initEngine() {
        int rows = (int) (gameCanvas.getHeight() / TILE_SIZE);
        int cols = (int) (gameCanvas.getWidth() / TILE_SIZE);
        engine = new GameEngine(rows, cols);
    }

    private void startLoop() {
        loop = Executors.newSingleThreadScheduledExecutor();
        loop.scheduleAtFixedRate(() -> {
            engine.tick();
            Platform.runLater(this::render);
        }, 0, TICK_MS, TimeUnit.MILLISECONDS);
    }

    private void setupInput() {
        gameCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(e -> {
                    switch (e.getCode()) {
                        case W -> engine.changeDirection(Direction.UP);
                        case S -> engine.changeDirection(Direction.DOWN);
                        case A -> engine.changeDirection(Direction.LEFT);
                        case D -> engine.changeDirection(Direction.RIGHT);
                    }
                });
            }
        });
    }

    private void render() {
        GameState s = engine.getState();

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        gc.setFill(Color.RED);
        gc.fillRect(
                s.getFood().col() * TILE_SIZE,
                s.getFood().row() * TILE_SIZE,
                TILE_SIZE, TILE_SIZE
        );

        gc.setFill(Color.GREEN);
        for (Position p : s.getSnake()) {
            gc.fillRect(
                    p.col() * TILE_SIZE,
                    p.row() * TILE_SIZE,
                    TILE_SIZE, TILE_SIZE
            );
        }

        scoreLabel.setText(String.valueOf(s.getScore()));

        if (!popupShown && s.isGameOver()) {
            popupShown = true;
            DialogUtils.showGameOver(s.getScore());
        }
    }

    @FXML
    private void onPause() {
        engine.togglePause();

        if (engine.getState().isPaused()) {
            pauseBtn.setText("Resume");
        } else {
            pauseBtn.setText("Pause");
        }

        gameCanvas.requestFocus();
    }

    public void startNewGame() {
        engine.newGame();
        popupShown = false;
        pauseBtn.setText("Pause");
        gameCanvas.requestFocus();
    }

    public void saveGame(){

    }

    public void loadGame(){

    }

}
