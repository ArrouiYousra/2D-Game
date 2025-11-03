package com.example;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import java.util.HashSet;
import java.util.Set;

/**
 * Event handler for keyboard input (as per bootstrap guide).
 * Tracks which keys are currently pressed.
 */
public class InputHandler {
    private final Set<String> down = new HashSet<>();
    private final Set<KeyCode> keysDown = new HashSet<>();

    /**
     * Polls events from the scene (as per bootstrap guide).
     * Attaches key pressed and released handlers.
     *
     * @param scene The JavaFX scene to attach to
     */
    public void pollEvents(Scene scene) {
        scene.setOnKeyPressed(e -> {
            String keyText = e.getText().toLowerCase();
            handleKeyPressed(keyText);
            keysDown.add(e.getCode());
        });
        scene.setOnKeyReleased(e -> {
            String keyText = e.getText().toLowerCase();
            handleKeyReleased(keyText);
            keysDown.remove(e.getCode());
        });
    }

    /**
     * Handles key pressed event.
     *
     * @param keyText The text representation of the key
     */
    private void handleKeyPressed(String keyText) {
        if (!keyText.isEmpty()) {
            down.add(keyText);
        }
    }

    /**
     * Handles key released event.
     *
     * @param keyText The text representation of the key
     */
    private void handleKeyReleased(String keyText) {
        if (!keyText.isEmpty()) {
            down.remove(keyText);
        }
    }

    // ZQSD on AZERTY -> up(Z), left(Q), down(S), right(D)
    // WASD on QWERTY -> up(W), left(A), down(S), right(D)
    
    /**
     * Checks if the up key is pressed (Z on AZERTY, W on QWERTY, or UP arrow).
     *
     * @return true if up key is pressed
     */
    public boolean up() {
        return down.contains("z") || down.contains("w") || 
               keysDown.contains(KeyCode.UP) || keysDown.contains(KeyCode.Z) || keysDown.contains(KeyCode.W);
    }

    /**
     * Checks if the left key is pressed (Q on AZERTY, A on QWERTY, or LEFT arrow).
     *
     * @return true if left key is pressed
     */
    public boolean left() {
        return down.contains("q") || down.contains("a") || 
               keysDown.contains(KeyCode.LEFT) || keysDown.contains(KeyCode.Q) || keysDown.contains(KeyCode.A);
    }

    /**
     * Checks if the down key is pressed (S or DOWN arrow).
     *
     * @return true if down key is pressed
     */
    public boolean down() {
        return down.contains("s") || keysDown.contains(KeyCode.DOWN) || keysDown.contains(KeyCode.S);
    }

    /**
     * Checks if the right key is pressed (D or RIGHT arrow).
     *
     * @return true if right key is pressed
     */
    public boolean right() {
        return down.contains("d") || keysDown.contains(KeyCode.RIGHT) || keysDown.contains(KeyCode.D);
    }
}

