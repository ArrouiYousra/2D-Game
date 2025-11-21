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
    private CollisionHandler collisionHandler;
    
    public MovementHandler(float x, float y, float speed, AnimationHandler animationHandler) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.runSpeedMultiplier = 1.5f;
        this.animationHandler = animationHandler;
        this.collisionHandler = null;
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
        
        // Sauvegarder la position actuelle pour vérifier si le mouvement a réellement eu lieu
        float oldX = x;
        float oldY = y;
        
        float newX = x;
        float newY = y;
        
        switch (direction) {
            case UP:
                newY += moveDistance;
                break;
            case DOWN:
                newY -= moveDistance;
                break;
            case SIDE:
                newX += moveDistance;
                break;
            case SIDE_LEFT:
                newX -= moveDistance;
                break;
        }
        
        // Vérifier les collisions si un CollisionHandler est disponible
        if (collisionHandler != null) {
            float[] adjustedPos = collisionHandler.adjustPosition(x, y, newX, newY);
            x = adjustedPos[0];
            y = adjustedPos[1];
        } else {
            x = newX;
            y = newY;
        }
        
        // Vérifier si le mouvement a réellement eu lieu
        boolean hasMoved = (Math.abs(x - oldX) > 0.01f || Math.abs(y - oldY) > 0.01f);
        
        if (animationHandler != null) {
            // Toujours permettre le changement de direction (pour que le joueur puisse faire face à l'ennemi même bloqué)
            animationHandler.setCurrentDirection(direction);
            
            // Mais seulement mettre l'animation en "moving" si le mouvement a réellement eu lieu
            if (hasMoved) {
                animationHandler.setMoving(true);
                animationHandler.setRunning(isRunning);
            } else {
                // Le mouvement est bloqué, mais on garde la direction pour que le joueur puisse attaquer
                animationHandler.setMoving(false);
                animationHandler.setRunning(false);
            }
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
    
    public CollisionHandler getCollisionHandler() {
        return collisionHandler;
    }
    
    public void setCollisionHandler(CollisionHandler collisionHandler) {
        this.collisionHandler = collisionHandler;
    }
}

