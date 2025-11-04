package com.tlse1.twodgame.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Classe abstraite de base pour toutes les entités du jeu.
 * Contient les propriétés communes (position, vitesse, dimensions)
 * et définit les méthodes abstraites que chaque entité doit implémenter.
 */
public abstract class Entity {
    
    // Position de l'entité
    protected float x;
    protected float y;
    
    // Vitesse de l'entité (en pixels par seconde)
    protected float speed;
    
    // Dimensions de l'entité
    protected float width;
    protected float height;
    
    // État de l'entité
    protected boolean isActive;
    
    /**
     * Constructeur par défaut
     */
    public Entity() {
        this.x = 0;
        this.y = 0;
        this.speed = 0;
        this.width = 0;
        this.height = 0;
        this.isActive = true;
    }
    
    /**
     * Constructeur avec position initiale
     */
    public Entity(float x, float y) {
        this.x = x;
        this.y = y;
        this.speed = 0;
        this.width = 0;
        this.height = 0;
        this.isActive = true;
    }
    
    /**
     * Constructeur complet
     */
    public Entity(float x, float y, float speed, float width, float height) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.width = width;
        this.height = height;
        this.isActive = true;
    }
    
    /**
     * Met à jour l'entité à chaque frame.
     * Méthode abstraite à implémenter dans les classes filles.
     * 
     * @param deltaTime Temps écoulé depuis la dernière frame (en secondes)
     */
    public abstract void update(float deltaTime);
    
    /**
     * Dessine l'entité à l'écran.
     * Méthode abstraite à implémenter dans les classes filles.
     * 
     * @param batch Le SpriteBatch utilisé pour le rendu
     */
    public abstract void render(SpriteBatch batch);
    
    /**
     * Libère les ressources utilisées par l'entité.
     * Méthode abstraite à implémenter dans les classes filles.
     */
    public abstract void dispose();
    
    /**
     * Retourne le centre X de l'entité
     */
    public float getCenterX() {
        return x + width / 2;
    }
    
    /**
     * Retourne le centre Y de l'entité
     */
    public float getCenterY() {
        return y + height / 2;
    }
    
    /**
     * Vérifie si cette entité est en collision avec une autre entité
     * (détection basique AABB - Axis-Aligned Bounding Box)
     */
    public boolean collidesWith(Entity other) {
        return this.x < other.x + other.width &&
               this.x + this.width > other.x &&
               this.y < other.y + other.height &&
               this.y + this.height > other.y;
    }
    
    /**
     * Vérifie si cette entité est dans les limites de l'écran
     */
    public boolean isInBounds(float screenWidth, float screenHeight) {
        return x >= 0 && y >= 0 && 
               x + width <= screenWidth && 
               y + height <= screenHeight;
    }
    
    /**
     * Limite l'entité dans les bounds de l'écran
     */
    public void clampToBounds(float screenWidth, float screenHeight) {
        x = Math.max(0, Math.min(screenWidth - width, x));
        y = Math.max(0, Math.min(screenHeight - height, y));
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
    
    public float getWidth() {
        return width;
    }
    
    public void setWidth(float width) {
        this.width = width;
    }
    
    public float getHeight() {
        return height;
    }
    
    public void setHeight(float height) {
        this.height = height;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
}
