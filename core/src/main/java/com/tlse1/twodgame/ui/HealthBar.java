package com.tlse1.twodgame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Classe représentant une barre de santé (HP).
 * Affiche la santé actuelle et maximale du joueur.
 */
public class HealthBar {
    
    // Textures
    private Texture barBackgroundTexture;  // Fond de la barre
    private Texture barFillTexture;        // Remplissage de la barre
    
    // Dimensions
    private float width;
    private float height;
    private float x;
    private float y;
    
    // Santé
    private int currentHealth;
    private int maxHealth;
    
    // Constantes
    public static final float DEFAULT_WIDTH = 200f;
    public static final float DEFAULT_HEIGHT = 20f;
    
    /**
     * Constructeur
     * @param x Position X
     * @param y Position Y
     */
    public HealthBar(float x, float y) {
        this.x = x;
        this.y = y;
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        this.currentHealth = 100;
        this.maxHealth = 100;
        
        loadTextures();
    }
    
    /**
     * Constructeur avec dimensions personnalisées
     */
    public HealthBar(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.currentHealth = 100;
        this.maxHealth = 100;
        
        loadTextures();
    }
    
    /**
     * Charge les textures de la barre de santé
     */
    private void loadTextures() {
        // Utiliser les assets HP disponibles
        barBackgroundTexture = new Texture(Gdx.files.internal("PostApocalypse_AssetPack_v1.1.2/UI/HP/HP-Bar.png"));
        barFillTexture = new Texture(Gdx.files.internal("PostApocalypse_AssetPack_v1.1.2/UI/HP/HP.png"));
    }
    
    /**
     * Met à jour la santé affichée
     * @param currentHealth Santé actuelle
     * @param maxHealth Santé maximale
     */
    public void updateHealth(int currentHealth, int maxHealth) {
        this.currentHealth = Math.max(0, Math.min(maxHealth, currentHealth));
        this.maxHealth = Math.max(1, maxHealth);
    }
    
    /**
     * Dessine la barre de santé
     * @param batch Le SpriteBatch pour le rendu
     */
    public void render(SpriteBatch batch) {
        if (barBackgroundTexture == null || barFillTexture == null) {
            return;
        }
        
        // Calculer le pourcentage de santé
        float healthPercentage = maxHealth > 0 ? (float) currentHealth / maxHealth : 0f;
        
        // Dessiner le fond de la barre
        batch.draw(barBackgroundTexture, x, y, width, height);
        
        // Dessiner le remplissage (proportionnel à la santé)
        float fillWidth = width * healthPercentage;
        if (fillWidth > 0) {
            // Utiliser une TextureRegion pour ne dessiner que la partie nécessaire
            TextureRegion fillRegion = new TextureRegion(barFillTexture);
            batch.draw(fillRegion, x, y, fillWidth, height);
        }
    }
    
    /**
     * Libère les ressources
     */
    public void dispose() {
        if (barBackgroundTexture != null) {
            barBackgroundTexture.dispose();
        }
        if (barFillTexture != null) {
            barFillTexture.dispose();
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
}

