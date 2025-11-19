package com.tlse1.twodgame.entities;

import com.badlogic.gdx.Gdx;
import com.tlse1.twodgame.managers.JsonMapLoader;
import com.tlse1.twodgame.utils.Direction;

/**
 * Classe représentant un projectile lancé par un vampire.
 * Rectangle qui se déplace en ligne droite.
 */
public class Projectile {
    
    // Position
    private float x;
    private float y;
    
    // Dimensions (configurables selon le niveau du vampire)
    private float width;
    private float height;
    
    // Direction du mouvement (fixée à la création)
    private Direction direction;
    
    // Vitesse du projectile (configurable)
    private float speed;
    
    // Dégâts infligés par seconde
    private float damagePerSecond;
    
    // Temps depuis la création (pour calculer les dégâts)
    private float lifetime = 0f;
    
    // Temps depuis le dernier dégât infligé
    private float damageCooldown = 0f;
    private static final float DAMAGE_INTERVAL = 1f; // 1 seconde entre chaque dégât
    
    // Si le projectile est actif (n'a pas touché de collision ou le joueur)
    private boolean active = true;
    
    // Référence à la map pour les collisions
    private JsonMapLoader mapLoader;
    
    /**
     * Constructeur.
     * 
     * @param startX Position X de départ
     * @param startY Position Y de départ
     * @param direction Direction du mouvement (fixée à la création)
     * @param mapLoader Référence à la map pour les collisions
     * @param width Largeur du projectile
     * @param height Hauteur du projectile
     * @param damagePerSecond Dégâts infligés par seconde
     * @param speed Vitesse du projectile en pixels/seconde
     */
    public Projectile(float startX, float startY, Direction direction, JsonMapLoader mapLoader, 
                     float width, float height, float damagePerSecond, float speed) {
        this.x = startX;
        this.y = startY;
        this.direction = direction;
        this.mapLoader = mapLoader;
        this.width = width;
        this.height = height;
        this.damagePerSecond = damagePerSecond;
        this.speed = speed;
        this.lifetime = 0f;
        this.damageCooldown = 0f;
        this.active = true;
    }
    
    /**
     * Met à jour le projectile.
     * 
     * @param deltaTime Temps écoulé depuis la dernière frame
     */
    public void update(float deltaTime) {
        if (!active) {
            return;
        }
        
        lifetime += deltaTime;
        damageCooldown += deltaTime;
        
        // Calculer le mouvement selon la direction
        float moveDistance = speed * deltaTime;
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
        
        // Vérifier les collisions avec la map
        if (mapLoader != null && mapLoader.isColliding(newX, newY, width, height)) {
            // Collision avec la map, désactiver le projectile
            active = false;
            return;
        }
        
        // Mettre à jour la position
        x = newX;
        y = newY;
    }
    
    /**
     * Vérifie si le projectile touche le joueur et inflige des dégâts si nécessaire.
     * 
     * @param player Le joueur
     * @return true si le projectile a touché le joueur et doit être désactivé
     */
    public boolean checkPlayerCollision(Player player) {
        if (!active || player == null || !player.isAlive()) {
            return false;
        }
        
        // Position de la hitbox du joueur
        float playerHitboxX = player.getHitboxX();
        float playerHitboxY = player.getHitboxY();
        float playerHitboxWidth = player.getHitboxWidth();
        float playerHitboxHeight = player.getHitboxHeight();
        
        // Vérifier si le projectile touche la hitbox du joueur
        boolean collides = (x < playerHitboxX + playerHitboxWidth &&
                           x + width > playerHitboxX &&
                           y < playerHitboxY + playerHitboxHeight &&
                           y + height > playerHitboxY);
        
        if (collides) {
            // Infliger des dégâts toutes les secondes
            if (damageCooldown >= DAMAGE_INTERVAL) {
                player.takeDamage((int)damagePerSecond);
                damageCooldown = 0f;
                Gdx.app.log("Projectile", String.format("Projectile inflige %.0f dégât au joueur", damagePerSecond));
            }
            
            // Le projectile continue à travers le joueur (ne s'arrête pas)
            // Il s'arrêtera seulement sur une collision de map
            return false;
        }
        
        return false;
    }
    
    /**
     * Vérifie si le projectile est actif.
     * 
     * @return true si actif
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Désactive le projectile.
     */
    public void setActive(boolean active) {
        this.active = active;
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
    
    public Direction getDirection() {
        return direction;
    }
}

