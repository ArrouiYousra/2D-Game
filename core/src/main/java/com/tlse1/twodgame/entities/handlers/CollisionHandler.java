package com.tlse1.twodgame.entities.handlers;

import com.tlse1.twodgame.managers.JsonMapLoader;
import com.tlse1.twodgame.utils.Direction;

/**
 * Handler pour gérer les collisions avec la map.
 */
public class CollisionHandler {
    
    private JsonMapLoader mapLoader;
    private float entityWidth;
    private float entityHeight;
    private float spriteWidth;
    private float spriteHeight;
    
    public CollisionHandler(JsonMapLoader mapLoader, float entityWidth, float entityHeight) {
        this(mapLoader, entityWidth, entityHeight, 0f, 0f);
    }
    
    public CollisionHandler(JsonMapLoader mapLoader, float entityWidth, float entityHeight, float spriteWidth, float spriteHeight) {
        this.mapLoader = mapLoader;
        this.entityWidth = entityWidth;
        this.entityHeight = entityHeight;
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
    }
    
    /**
     * Calcule la position de la hitbox centrée à partir de la position du sprite.
     * 
     * @param spriteX Position X du sprite (coin bas-gauche)
     * @param spriteY Position Y du sprite (coin bas-gauche)
     * @return Position [x, y] de la hitbox (coin bas-gauche)
     */
    private float[] calculateHitboxPosition(float spriteX, float spriteY) {
        // Si les dimensions du sprite ne sont pas définies, utiliser directement la position du sprite
        if (spriteWidth <= 0 || spriteHeight <= 0) {
            return new float[]{spriteX, spriteY};
        }
        
        // Calculer le centre du sprite
        float spriteCenterX = spriteX + spriteWidth / 2f;
        float spriteCenterY = spriteY + spriteHeight / 2f;
        
        // Calculer la position de la hitbox centrée (coin bas-gauche)
        float hitboxX = spriteCenterX - entityWidth / 2f;
        float hitboxY = spriteCenterY - entityHeight / 2f;
        
        return new float[]{hitboxX, hitboxY};
    }
    
    /**
     * Vérifie si une position est valide (pas de collision).
     * Accepte la position du sprite et calcule automatiquement la position de la hitbox centrée.
     * 
     * @param spriteX Position X du sprite en pixels (coin bas-gauche)
     * @param spriteY Position Y du sprite en pixels (coin bas-gauche)
     * @return true si la position est valide
     */
    public boolean isValidPosition(float spriteX, float spriteY) {
        if (mapLoader == null) {
            return true; // Pas de map = toujours valide
        }
        
        // Calculer la position de la hitbox centrée
        float[] hitboxPos = calculateHitboxPosition(spriteX, spriteY);
        float hitboxX = hitboxPos[0];
        float hitboxY = hitboxPos[1];
        
        boolean colliding = mapLoader.isColliding(hitboxX, hitboxY, entityWidth, entityHeight);
        return !colliding;
    }
    
    /**
     * Vérifie si un mouvement dans une direction est possible.
     * 
     * @param currentX Position X actuelle
     * @param currentY Position Y actuelle
     * @param direction Direction du mouvement
     * @param distance Distance du mouvement
     * @return true si le mouvement est possible
     */
    public boolean canMove(float currentX, float currentY, Direction direction, float distance) {
        if (mapLoader == null) {
            return true;
        }
        
        float newX = currentX;
        float newY = currentY;
        
        switch (direction) {
            case UP:
                newY += distance;
                break;
            case DOWN:
                newY -= distance;
                break;
            case SIDE:
                newX += distance;
                break;
            case SIDE_LEFT:
                newX -= distance;
                break;
        }
        
        return isValidPosition(newX, newY);
    }
    
    /**
     * Ajuste une position pour éviter les collisions.
     * Accepte les positions du sprite et calcule automatiquement les positions de la hitbox centrée.
     * Essaie de se déplacer seulement sur un axe si l'autre est bloqué.
     * 
     * @param currentSpriteX Position X actuelle du sprite (coin bas-gauche)
     * @param currentSpriteY Position Y actuelle du sprite (coin bas-gauche)
     * @param desiredSpriteX Position X désirée du sprite (coin bas-gauche)
     * @param desiredSpriteY Position Y désirée du sprite (coin bas-gauche)
     * @return Tableau [x, y] avec la position ajustée du sprite
     */
    public float[] adjustPosition(float currentSpriteX, float currentSpriteY, float desiredSpriteX, float desiredSpriteY) {
        float[] result = new float[]{desiredSpriteX, desiredSpriteY};
        
        if (mapLoader == null) {
            return result;
        }
        
        // Si la position désirée est valide, on l'utilise
        if (isValidPosition(desiredSpriteX, desiredSpriteY)) {
            return result;
        }
        
        // Sinon, essayer de se déplacer seulement sur X
        if (isValidPosition(desiredSpriteX, currentSpriteY)) {
            result[0] = desiredSpriteX;
            result[1] = currentSpriteY;
            return result;
        }
        
        // Ou seulement sur Y
        if (isValidPosition(currentSpriteX, desiredSpriteY)) {
            result[0] = currentSpriteX;
            result[1] = desiredSpriteY;
            return result;
        }
        
        // Sinon, rester à la position actuelle
        result[0] = currentSpriteX;
        result[1] = currentSpriteY;
        return result;
    }
    
    // Getters et Setters
    public JsonMapLoader getMapLoader() {
        return mapLoader;
    }
    
    public void setMapLoader(JsonMapLoader mapLoader) {
        this.mapLoader = mapLoader;
    }
    
    public float getEntityWidth() {
        return entityWidth;
    }
    
    public void setEntityWidth(float entityWidth) {
        this.entityWidth = entityWidth;
    }
    
    public float getEntityHeight() {
        return entityHeight;
    }
    
    public void setEntityHeight(float entityHeight) {
        this.entityHeight = entityHeight;
    }
    
    public float getSpriteWidth() {
        return spriteWidth;
    }
    
    public void setSpriteWidth(float spriteWidth) {
        this.spriteWidth = spriteWidth;
    }
    
    public float getSpriteHeight() {
        return spriteHeight;
    }
    
    public void setSpriteHeight(float spriteHeight) {
        this.spriteHeight = spriteHeight;
    }
}

