package com.tlse1.twodgame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tlse1.twodgame.utils.CharacterPanelMapping;

/**
 * Classe pour afficher une barre de santé (HP) en utilisant les sprites du character_panel.
 * - sprite1 : panel de base
 * - sprite3 : barre rouge vide
 * - sprite5, sprite6, sprite7 : traits rouges pour remplir la barre
 */
public class HealthBar {
    
    private float x;
    private float y;
    private float scale;
    private int currentHealth;
    private int maxHealth;
    
    private CharacterPanelMapping panelMapping;
    
    // Texture du panel vide
    private Texture panelVideTexture;
    private TextureRegion panelVideSprite;
    
    // Sprites depuis character_panel
    private TextureRegion emptyBarSprite;   // sprite3
    private TextureRegion fillSprite5;       // sprite5 (trait 2px)
    private TextureRegion fillSprite6;       // sprite6 (trait 1px)
    private TextureRegion fillSprite7;       // sprite7 (trait 1px)
    
    // Dimensions du panel vide (à déterminer depuis l'image)
    private float panelWidth = 84f;
    private float panelHeight = 30f;
    private float barWidth = 52f;
    private float barHeight = 2f;
    private float fill5Width = 2f;
    private float fill6Width = 1f;
    private float fill7Width = 1f;
    private float fillHeight = 2f;
    
    // Position de la barre sur panel_vide.png
    // La barre (sprite3) : 52x2 pixels
    // Position sur panel_vide.png (coordonnées absolues dans l'image) :
    //   - Longueur (X) : commence à 28, se termine à 80 (52 pixels)
    //   - Largeur (Y) : commence à 8, se termine à 10 (2 pixels)
    // Dans LibGDX, Y=0 est en bas, donc :
    //   - barOffsetX = 28 (depuis le bord gauche de panel_vide.png)
    //   - barOffsetY = panelHeight - 8 = 30 - 8 = 22 (depuis le bas, car Y=8 depuis le haut)
    //   La barre va de Y=8 à Y=10, donc elle fait 2 pixels de haut
    private float barOffsetX = 28f; // Position X absolue dans panel_vide.png
    private float barOffsetY = 22f; // panelHeight - 8 = 30 - 8 = 22 (position Y=8 depuis le haut)
    
    /**
     * Constructeur.
     * 
     * @param x Position X
     * @param y Position Y
     * @param scale Échelle pour agrandir/réduire
     * @param panelMapping CharacterPanelMapping pour accéder aux sprites
     */
    public HealthBar(float x, float y, float scale, CharacterPanelMapping panelMapping) {
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.currentHealth = 0;
        this.maxHealth = 1;
        this.panelMapping = panelMapping;
        
        // Charger la texture panel_vide.png
        panelVideTexture = new Texture(Gdx.files.internal("gui/PNG/panel_vide.png"));
        panelVideTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        panelVideSprite = new TextureRegion(panelVideTexture);
        
        // Obtenir les dimensions réelles du panel
        panelWidth = panelVideTexture.getWidth();
        panelHeight = panelVideTexture.getHeight();
        
        // Recalculer barOffsetY avec les dimensions réelles du panel
        // Y=10 depuis le haut (descendu de 2 pixels), donc depuis le bas = panelHeight - 10
        barOffsetY = panelHeight - 10f;
        
        // Charger les sprites depuis character_panel pour la barre et les traits
        if (panelMapping != null) {
            emptyBarSprite = panelMapping.getSprite("sprite3");
            fillSprite5 = panelMapping.getSprite("sprite5");
            fillSprite6 = panelMapping.getSprite("sprite6");
            fillSprite7 = panelMapping.getSprite("sprite7");
        }
    }
    
    /**
     * Met à jour la santé affichée.
     * 
     * @param currentHealth Santé actuelle
     * @param maxHealth Santé maximale
     */
    public void update(int currentHealth, int maxHealth) {
        this.currentHealth = Math.max(0, currentHealth);
        this.maxHealth = Math.max(1, maxHealth);
    }
    
    /**
     * Dessine la barre de santé.
     * 
     * @param batch SpriteBatch pour dessiner
     */
    public void render(SpriteBatch batch) {
        if (maxHealth <= 0 || panelMapping == null) {
            return;
        }
        
        // Calculer le ratio de santé
        float healthRatio = (float) currentHealth / (float) maxHealth;
        
        // 1. Dessiner le panel vide (panel_vide.png)
        if (panelVideSprite != null) {
            batch.draw(panelVideSprite, x, y, panelWidth * scale, panelHeight * scale);
        }
        
        // 2. Dessiner la barre rouge vide (sprite3)
        if (emptyBarSprite != null) {
            float barX = x + barOffsetX * scale;
            float barY = y + barOffsetY * scale; // Position en bas du panel
            batch.draw(emptyBarSprite, barX, barY, barWidth * scale, barHeight * scale);
        }
        
        // 3. Dessiner les traits rouges pour remplir la barre
        if (healthRatio > 0 && fillSprite5 != null && fillSprite6 != null && fillSprite7 != null) {
            float fillStartX = x + barOffsetX * scale;
            float fillY = y + barOffsetY * scale;
            float currentFillWidth = barWidth * scale * healthRatio;
            
            // Position actuelle dans la barre
            float currentX = fillStartX;
            
            // Répéter les sprites 5, 6, 7 pour remplir la barre
            while (currentX < fillStartX + currentFillWidth) {
                // Calculer combien de pixels il reste à remplir
                float remainingWidth = (fillStartX + currentFillWidth) - currentX;
                
                // Utiliser la hauteur de la barre (2 pixels) pour tous les traits
                float fillSpriteHeight = barHeight * scale; // 2 pixels * scale
                
                // Dessiner sprite5 (2px de large, 2px de haut) si possible
                if (remainingWidth >= fill5Width * scale) {
                    batch.draw(fillSprite5, currentX, fillY, fill5Width * scale, fillSpriteHeight);
                    currentX += fill5Width * scale;
                }
                // Sinon dessiner sprite6 (1px de large, 2px de haut) si possible
                else if (remainingWidth >= fill6Width * scale) {
                    batch.draw(fillSprite6, currentX, fillY, fill6Width * scale, fillSpriteHeight);
                    currentX += fill6Width * scale;
                }
                // Sinon dessiner sprite7 (1px de large, 2px de haut) si possible
                else if (remainingWidth >= fill7Width * scale) {
                    batch.draw(fillSprite7, currentX, fillY, fill7Width * scale, fillSpriteHeight);
                    currentX += fill7Width * scale;
                } else {
                    // Plus de place, on arrête
                    break;
                }
            }
        }
    }
    
    /**
     * Définit la position de la barre.
     * 
     * @param x Position X
     * @param y Position Y
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Définit l'échelle.
     * 
     * @param scale Échelle
     */
    public void setScale(float scale) {
        this.scale = scale;
    }
    
    // Getters
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public float getWidth() {
        return panelWidth * scale;
    }
    
    public float getHeight() {
        return panelHeight * scale;
    }
    
    public float getScale() {
        return scale;
    }
    
    /**
     * Libère les ressources.
     */
    public void dispose() {
        if (panelVideTexture != null) {
            panelVideTexture.dispose();
        }
    }
}
