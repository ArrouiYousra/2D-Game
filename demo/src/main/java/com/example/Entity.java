package com.example;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Base class for all game entities.
 * Provides basic sprite rendering and position management.
 */
public abstract class Entity {
    protected Node sprite;
    protected double x, y;

    /**
     * Creates a new entity with the specified sprite and position.
     *
     * @param spritePath Path to the sprite image file (empty string for no image)
     * @param startX Initial X position
     * @param startY Initial Y position
     */
    public Entity(String spritePath, double startX, double startY) {
        if (spritePath != null && !spritePath.isEmpty()) {
            try {
                Image image = new Image(spritePath);
                this.sprite = new ImageView(image);
            } catch (Exception e) {
                // If image not found, create a simple placeholder
                this.sprite = new ImageView();
            }
        }
        this.x = startX;
        this.y = startY;
        if (sprite != null) {
            sprite.setTranslateX(x);
            sprite.setTranslateY(y);
        }
    }

    /**
     * Gets the sprite Node for rendering.
     *
     * @return The sprite Node
     */
    public Node getSprite() {
        return sprite;
    }

    /**
     * Gets the current X position.
     *
     * @return X coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the current Y position.
     *
     * @return Y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Updates the entity each frame (as per bootstrap guide).
     * The update method should modify position depending on keys detected.
     *
     * @param eventHandler The input handler to query key states
     */
    public abstract void update(InputHandler eventHandler);
}

