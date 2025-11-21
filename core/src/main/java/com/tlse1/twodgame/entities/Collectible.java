package com.tlse1.twodgame.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tlse1.twodgame.utils.ActionPanelMapping;

/**
 * Classe représentant un collectible droppé par un ennemi mort.
 * Peut être ramassé par le joueur avec la touche T.
 */
public class Collectible {
    
    private float x;
    private float y;
    private float width;
    private float height;
    private Inventory.ItemType itemType;
    private ActionPanelMapping actionPanelMapping;
    private boolean collected;
    
    // Distance de pickup (en pixels)
    private static final float PICKUP_RANGE = 20f;
    
    /**
     * Constructeur.
     * 
     * @param x Position X
     * @param y Position Y
     * @param itemType Type d'item (DAMAGE_BOOST, SPEED_BOOST, SHIELD_POTION, HEAL_POTION)
     * @param actionPanelMapping Mapping pour les sprites
     */
    public Collectible(float x, float y, Inventory.ItemType itemType, ActionPanelMapping actionPanelMapping) {
        this.x = x;
        this.y = y;
        this.itemType = itemType;
        this.actionPanelMapping = actionPanelMapping;
        this.collected = false;
        
        // Définir la taille fixe pour les collectibles droppés (6x6 pixels)
        // Tous les collectibles ont la même taille quand ils sont droppés
        this.width = 6f;
        this.height = 6f;
    }
    
    /**
     * Récupère le sprite correspondant au type d'item.
     */
    private TextureRegion getSpriteForType() {
        if (actionPanelMapping == null) {
            return null;
        }
        
        switch (itemType) {
            case DAMAGE_BOOST:
                return actionPanelMapping.getSprite("sprite1");
            case SPEED_BOOST:
                return actionPanelMapping.getSprite("sprite2");
            case SHIELD_POTION:
                return actionPanelMapping.getSprite("sprite5");
            case HEAL_POTION:
                return actionPanelMapping.getSprite("sprite6");
            default:
                return null;
        }
    }
    
    /**
     * Dessine le collectible.
     */
    public void render(SpriteBatch batch) {
        if (collected) {
            return;
        }
        
        TextureRegion sprite = getSpriteForType();
        if (sprite != null) {
            batch.draw(sprite, x, y, width, height);
        }
    }
    
    /**
     * Vérifie si le joueur est assez proche pour ramasser le collectible.
     * 
     * @param playerX Position X du joueur
     * @param playerY Position Y du joueur
     * @param playerWidth Largeur du joueur
     * @param playerHeight Hauteur du joueur
     * @return true si le joueur est assez proche
     */
    public boolean canBePickedUp(float playerX, float playerY, float playerWidth, float playerHeight) {
        if (collected) {
            return false;
        }
        
        // Calculer le centre du joueur
        float playerCenterX = playerX + playerWidth / 2f;
        float playerCenterY = playerY + playerHeight / 2f;
        
        // Calculer le centre du collectible
        float collectibleCenterX = x + width / 2f;
        float collectibleCenterY = y + height / 2f;
        
        // Calculer la distance
        float dx = playerCenterX - collectibleCenterX;
        float dy = playerCenterY - collectibleCenterY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        return distance <= PICKUP_RANGE;
    }
    
    /**
     * Marque le collectible comme ramassé.
     */
    public void collect() {
        this.collected = true;
    }
    
    /**
     * Vérifie si le collectible a été ramassé.
     */
    public boolean isCollected() {
        return collected;
    }
    
    /**
     * Retourne le type d'item.
     */
    public Inventory.ItemType getItemType() {
        return itemType;
    }
    
    // Getters
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public float getWidth() {
        return width;
    }
    
    public float getHeight() {
        return height;
    }
}

