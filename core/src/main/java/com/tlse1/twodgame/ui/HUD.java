package com.tlse1.twodgame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tlse1.twodgame.entities.Player;

/**
 * HUD (Heads-Up Display) - Interface utilisateur affichée pendant le jeu.
 * Contient la barre de santé et la barre d'XP.
 */
public class HUD {
    
    private HealthBar healthBar;
    private Player player;
    
    // Marges depuis les bords de l'écran
    private static final float MARGIN_X = 20f;
    private static final float MARGIN_Y = 20f;
    
    // Espacement entre les barres
    private static final float BAR_SPACING = 10f;
    
    /**
     * Constructeur
     * @param player Référence au joueur pour récupérer les stats
     */
    public HUD(Player player) {
        this.player = player;
        
        // Calculer la position de la barre de santé (en haut à gauche)
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        
        float healthBarX = MARGIN_X;
        float healthBarY = screenHeight - MARGIN_Y - HealthBar.DEFAULT_HEIGHT;
        
        // Créer la barre de santé
        healthBar = new HealthBar(healthBarX, healthBarY);
    }
    
    /**
     * Met à jour le HUD (appelé chaque frame)
     */
    public void update() {
        if (player != null) {
            // Mettre à jour la barre de santé avec les stats du joueur
            healthBar.updateHealth(player.getHealth(), player.getMaxHealth());
        }
    }
    
    /**
     * Dessine le HUD
     * @param batch Le SpriteBatch pour le rendu
     */
    public void render(SpriteBatch batch) {
        // Dessiner la barre de santé
        if (healthBar != null) {
            healthBar.render(batch);
        }
        
        // TODO: Dessiner la barre d'XP quand elle sera implémentée
    }
    
    /**
     * Gère le redimensionnement de l'écran
     * @param width Nouvelle largeur
     * @param height Nouvelle hauteur
     */
    public void resize(int width, int height) {
        // Repositionner la barre de santé
        float healthBarX = MARGIN_X;
        float healthBarY = height - MARGIN_Y - HealthBar.DEFAULT_HEIGHT;
        
        if (healthBar != null) {
            healthBar.setX(healthBarX);
            healthBar.setY(healthBarY);
        }
    }
    
    /**
     * Libère les ressources
     */
    public void dispose() {
        if (healthBar != null) {
            healthBar.dispose();
        }
    }
}

