package com.example;

import javafx.application.Application;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * JavaFX App - Bootstrap GUI for 2D Game
 * Following the EPITECH Bootstrap guide
 */
public class App extends Application {

    private Scene scene;
    private Pane root;
    private InputHandler eventHandler;
    private Player player;

    @Override
    public void start(Stage stage) {
        // Create root pane for game (1280x720 as per bootstrap)
        this.root = new Pane();
        this.scene = new Scene(root, 1280, 720);
        stage.setScene(scene);
        stage.setTitle("2D Game - Bootstrap");
        stage.show();

        // Set up input handler (pollEvents as per bootstrap)
        eventHandler = new InputHandler();
        eventHandler.pollEvents(scene);

        // Create player character
        player = new Player(200, 100, 220); // x, y, speed
        root.getChildren().add(player.getPlayerRect());

        // Game loop using Timeline (60 FPS as per bootstrap)
        Duration frameDuration = Duration.millis(16); // ~60 FPS
        KeyFrame keyFrame = new KeyFrame(frameDuration, event -> update());
        Timeline gameLoop = new Timeline(keyFrame);
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();
    }

    /**
     * Update method called each frame by the game loop.
     */
    public void update() {
        player.update(eventHandler);
    }

    /**
     * Sets the root scene (legacy method for FXML controllers, not used in current implementation).
     * @param fxml FXML file name (unused)
     */
    static void setRoot(String fxml) {
        // This method is kept for compatibility with FXML controllers
        // but is not used in the current game implementation
    }

    public static void main(String[] args) {
        launch();
    }
}