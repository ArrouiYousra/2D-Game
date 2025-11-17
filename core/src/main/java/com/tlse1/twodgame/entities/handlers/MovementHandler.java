package com.tlse1.twodgame.entities.handlers;

import com.tlse1.twodgame.utils.Direction;

/**
 * Handler pour gérer le déplacement et la vitesse d'un personnage.
 */
public class MovementHandler {
    
    private float x;
    private float y;
    private float speed;
    private float runSpeedMultiplier;
    private AnimationHandler animationHandler;
    
    public MovementHandler(float x, float y, float speed, AnimationHandler animationHandler) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.runSpeedMultiplier = 1.5f;
        this.animationHandler = animationHandler;
    }
    
    /**
     * Déplace le personnage dans une direction.
     * 
     * @param direction Direction du mouvement
     * @param deltaTime Temps écoulé
     * @param isRunning Si le personnage court
     */
    public void move(Direction direction, float deltaTime, boolean isRunning) {
        float currentSpeed = isRunning ? speed * runSpeedMultiplier : speed;
        float moveDistance = currentSpeed * deltaTime;
        
        switch (direction) {
            case UP:
                y += moveDistance;
                break;
            case DOWN:
                y -= moveDistance;
                break;
            case SIDE:
                x += moveDistance;
                break;
            case SIDE_LEFT:
                x -= moveDistance;
                break;
        }
        
        if (animationHandler != null) {
            animationHandler.setCurrentDirection(direction);
            animationHandler.setMoving(true);
            animationHandler.setRunning(isRunning);
        }
    }
    
    /**
     * Arrête le mouvement.
     */
    public void stop() {
        if (animationHandler != null) {
            animationHandler.setMoving(false);
            animationHandler.setRunning(false);
        }
    }
    
    // Getters et Setters
    public float getX() {
        return x;
    }
    
    public void setX(float x) {
        this.x = x;
    }
    
    public float getY() {
        return y;
    }
    
    public void setY(float y) {
        this.y = y;
    }
    
    public float getSpeed() {
        return speed;
    }
    
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    
    public float getRunSpeedMultiplier() {
        return runSpeedMultiplier;
    }
    
    public void setRunSpeedMultiplier(float runSpeedMultiplier) {
        this.runSpeedMultiplier = runSpeedMultiplier;
    }
}

