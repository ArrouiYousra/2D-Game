package com.tlse1.twodgame.entities.handlers;

import com.tlse1.twodgame.managers.MapLoader;
import com.tlse1.twodgame.utils.Direction;

/**
 * Handler pour gérer les collisions avec la map.
 */
public class CollisionHandler {
    
    private MapLoader mapLoader;
    private float entityWidth;
    private float entityHeight;
    
    public CollisionHandler(MapLoader mapLoader, float entityWidth, float entityHeight) {
        this.mapLoader = mapLoader;
        this.entityWidth = entityWidth;
        this.entityHeight = entityHeight;
    }
    
    /**
     * Vérifie si une position est valide (pas de collision).
     * 
     * @param x Position X en pixels
     * @param y Position Y en pixels
     * @return true si la position est valide
     */
    public boolean isValidPosition(float x, float y) {
        if (mapLoader == null) {
            return true; // Pas de map = toujours valide
        }
        
        return !mapLoader.isColliding(x, y, entityWidth, entityHeight);
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
     * Essaie de se déplacer seulement sur un axe si l'autre est bloqué.
     * 
     * @param currentX Position X actuelle
     * @param currentY Position Y actuelle
     * @param desiredX Position X désirée
     * @param desiredY Position Y désirée
     * @return Tableau [x, y] avec la position ajustée
     */
    public float[] adjustPosition(float currentX, float currentY, float desiredX, float desiredY) {
        float[] result = new float[]{desiredX, desiredY};
        
        if (mapLoader == null) {
            return result;
        }
        
        // Si la position désirée est valide, on l'utilise
        if (isValidPosition(desiredX, desiredY)) {
            return result;
        }
        
        // Sinon, essayer de se déplacer seulement sur X
        if (isValidPosition(desiredX, currentY)) {
            result[0] = desiredX;
            result[1] = currentY;
            return result;
        }
        
        // Ou seulement sur Y
        if (isValidPosition(currentX, desiredY)) {
            result[0] = currentX;
            result[1] = desiredY;
            return result;
        }
        
        // Sinon, rester à la position actuelle
        result[0] = currentX;
        result[1] = currentY;
        return result;
    }
    
    // Getters et Setters
    public MapLoader getMapLoader() {
        return mapLoader;
    }
    
    public void setMapLoader(MapLoader mapLoader) {
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
}

