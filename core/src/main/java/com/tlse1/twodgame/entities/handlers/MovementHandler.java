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
        // Note: Les collisions utilisent les hitboxes fixes (centrées sur les sprites)
        // Le CollisionHandler vérifie les collisions en utilisant la position de la hitbox centrée
        // Pour cela, on doit calculer la position de la hitbox à partir de la position du sprite
        if (collisionHandler != null) {
            // Calculer la position de la hitbox centrée pour la position actuelle et la nouvelle position
            // La hitbox est centrée sur le sprite, donc on doit connaître les dimensions visuelles du sprite
            // Pour l'instant, on utilise directement x, y car le CollisionHandler utilise déjà les dimensions de la hitbox
            // TODO: Si nécessaire, ajuster pour tenir compte du centrage de la hitbox
            float[] adjustedPos = collisionHandler.adjustPosition(x, y, newX, newY);
            x = adjustedPos[0];
            y = adjustedPos[1];
        } else {
            // Pas de collision, déplacer normalement
            x = newX;
            y = newY;
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
    
    public CollisionHandler getCollisionHandler() {
        return collisionHandler;
    }
    
    public void setCollisionHandler(CollisionHandler collisionHandler) {
        this.collisionHandler = collisionHandler;
    }
}

