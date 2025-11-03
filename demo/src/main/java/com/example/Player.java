package com.example;

import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

/**
 * Represents the player character in the game.
 * Extends Entity to provide movement and rendering capabilities.
 */
public class Player extends Entity {
    private double speed; // pixels per second
    private Rectangle playerRect;

    /**
     * Creates a new player entity with a blue rectangle as sprite.
     *
     * @param startX Initial X position
     * @param startY Initial Y position
     * @param speed Movement speed in pixels per second
     */
    public Player(double startX, double startY, double speed) {
        this(startX, startY, speed, Color.BLUE);
    }

    /**
     * Creates a player with a colored rectangle as sprite.
     *
     * @param startX Initial X position
     * @param startY Initial Y position
     * @param speed Movement speed in pixels per second
     * @param color Color of the player rectangle
     */
    public Player(double startX, double startY, double speed, Color color) {
        super("", startX, startY);
        this.speed = speed;
        
        // Create a simple colored rectangle as the player sprite
        playerRect = new Rectangle(32, 32);
        playerRect.setFill(color);
        playerRect.setStroke(Color.BLACK);
        playerRect.setStrokeWidth(2);
        
        // Create a simple image view that we can use for positioning
        // We'll override getSprite to return the rectangle wrapped in a node
    }

    /**
     * Gets the player rectangle node for rendering.
     *
     * @return The rectangle node
     */
    public Rectangle getPlayerRect() {
        return playerRect;
    }

    /**
     * Updates the player position based on input (as per bootstrap guide).
     * The update method should modify position depending on keys detected.
     *
     * @param eventHandler The input handler to query key states
     */
    @Override
    public void update(InputHandler eventHandler) {
        double dx = 0, dy = 0;
        if (eventHandler.up())    dy -= 1;
        if (eventHandler.down())  dy += 1;
        if (eventHandler.left())  dx -= 1;
        if (eventHandler.right()) dx += 1;

        // Normalize diagonal movement
        if (dx != 0 && dy != 0) {
            double inv = 1 / Math.sqrt(2);
            dx *= inv;
            dy *= inv;
        }

        // Update position (fixed movement per frame for Timeline-based loop)
        double frameSpeed = speed * (16.0 / 1000.0); // 16ms per frame = ~60 FPS
        x += dx * frameSpeed;
        y += dy * frameSpeed;
        playerRect.setTranslateX(x);
        playerRect.setTranslateY(y);
    }
    
    /**
     * Legacy update method with delta time (kept for compatibility).
     * @deprecated Use update(InputHandler) instead
     */
    @Deprecated
    public void update(double dt, InputHandler input) {
        double dx = 0, dy = 0;
        if (input.up())    dy -= 1;
        if (input.down())  dy += 1;
        if (input.left())  dx -= 1;
        if (input.right()) dx += 1;

        if (dx != 0 && dy != 0) {
            double inv = 1 / Math.sqrt(2);
            dx *= inv;
            dy *= inv;
        }

        x += dx * speed * dt;
        y += dy * speed * dt;
        playerRect.setTranslateX(x);
        playerRect.setTranslateY(y);
    }

    /**
     * Gets the player's movement speed.
     *
     * @return Speed in pixels per second
     */
    public double getSpeed() {
        return speed;
    }
}

