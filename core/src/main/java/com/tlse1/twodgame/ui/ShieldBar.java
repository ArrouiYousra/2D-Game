package com.tlse1.twodgame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tlse1.twodgame.utils.CharacterPanelMapping;

/**
 * Classe pour afficher une barre de shield (bouclier) en utilisant les sprites du character_panel.
 * - Utilise le même panel_vide.png que HealthBar
 * - sprite9, sprite10, sprite11 : traits pour remplir la barre
 */
public class ShieldBar {
    
    private float x;
    private float y;
    private float scale;
    private int currentShield;
    private int maxShield;
    
    private CharacterPanelMapping panelMapping;
    
    // Texture du panel vide (partagée avec HealthBar, mais on ne le dessine pas ici)
    private Texture panelVideTexture;
    
    // Sprites depuis character_panel
    private TextureRegion emptyBarSprite;   // sprite8 (barre vide)
    private TextureRegion fillSprite9;       // sprite9 (trait 2px)
    private TextureRegion fillSprite10;      // sprite10 (trait 1px)
    private TextureRegion fillSprite11;      // sprite11 (trait 1px)
    
    // Dimensions du panel vide (à déterminer depuis l'image)
    private float panelWidth = 84f;
    private float panelHeight = 30f;
    private float barWidth = 43f;  // 73 - 30 = 43 pixels
    private float barHeight = 2f;
    private float fill9Width = 2f;
    private float fill10Width = 1f;
    private float fill11Width = 1f;
    
    // Position de la barre sur panel_vide.png
    // La barre : 43x2 pixels
    // Position sur panel_vide.png (coordonnées absolues dans l'image) :
    //   - Longueur (X) : commence à 30, se termine à 73 (43 pixels)
    //   - Largeur (Y) : commence à 15, se termine à 17 (2 pixels) - descendue de 2 pixels
    // Dans LibGDX, Y=0 est en bas, donc :
    //   - barOffsetX = 30 (depuis le bord gauche de panel_vide.png)
    //   - barOffsetY = panelHeight - 15 (depuis le bas, car Y=15 depuis le haut)
    private float barOffsetX = 30f; // Position X absolue dans panel_vide.png
    private float barOffsetY; // Sera calculé avec panelHeight - 15
    
    /**
     * Constructeur.
     * 
     * @param x Position X
     * @param y Position Y
     * @param scale Échelle pour agrandir/réduire
     * @param panelMapping CharacterPanelMapping pour accéder aux sprites
     */
    public ShieldBar(float x, float y, float scale, CharacterPanelMapping panelMapping) {
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.currentShield = 0;
        this.maxShield = 1;
        this.panelMapping = panelMapping;
        
        // Charger la texture panel_vide.png pour obtenir ses dimensions
        panelVideTexture = new Texture(Gdx.files.internal("gui/PNG/panel_vide.png"));
        panelVideTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        
        // Obtenir les dimensions réelles du panel
        panelWidth = panelVideTexture.getWidth();
        panelHeight = panelVideTexture.getHeight();
        
        // Recalculer barOffsetY avec les dimensions réelles du panel
        // Y=15 depuis le haut (descendue de 2 pixels), donc depuis le bas = panelHeight - 15
        barOffsetY = panelHeight - 15f;
        
        // Charger les sprites depuis character_panel pour la barre et les traits
        if (panelMapping != null) {
            emptyBarSprite = panelMapping.getSprite("sprite8");
            fillSprite9 = panelMapping.getSprite("sprite9");
            fillSprite10 = panelMapping.getSprite("sprite10");
            fillSprite11 = panelMapping.getSprite("sprite11");
        }
    }
    
    /**
     * Met à jour le shield affiché.
     * 
     * @param currentShield Shield actuel
     * @param maxShield Shield maximal
     */
    public void update(int currentShield, int maxShield) {
        this.currentShield = Math.max(0, currentShield);
        this.maxShield = Math.max(1, maxShield);
    }
    
    /**
     * Dessine la barre de shield.
     * Note: Le panel vide doit être dessiné séparément (par HealthBar ou ailleurs).
     * 
     * @param batch SpriteBatch pour dessiner
     */
    public void render(SpriteBatch batch) {
        if (panelMapping == null) {
            return;
        }
        
        // Note: Le panel vide (panel_vide.png) est dessiné par HealthBar
        // On dessine seulement la barre de shield et son remplissage
        
        // Si maxShield <= 0, on ne dessine rien (pas de barre)
        if (maxShield <= 0) {
            return;
        }
        
        // Calculer le ratio de shield
        float shieldRatio = (float) currentShield / (float) maxShield;
        
        // 2. Dessiner la barre vide (sprite8) - toujours, même si shield = 0
        if (emptyBarSprite != null) {
            float barX = x + barOffsetX * scale;
            float barY = y + barOffsetY * scale;
            batch.draw(emptyBarSprite, barX, barY, barWidth * scale, barHeight * scale);
        }
        
        // 3. Dessiner les traits pour remplir la barre (seulement si shield > 0)
        if (shieldRatio > 0 && fillSprite9 != null && fillSprite10 != null && fillSprite11 != null) {
            float fillStartX = x + barOffsetX * scale;
            float fillY = y + barOffsetY * scale;
            float currentFillWidth = barWidth * scale * shieldRatio;
            
            // Position actuelle dans la barre
            float currentX = fillStartX;
            
            // Utiliser la hauteur de la barre (2 pixels) pour tous les traits
            float fillSpriteHeight = barHeight * scale; // 2 pixels * scale
            
            // Répéter les sprites 9, 10, 11 pour remplir la barre
            while (currentX < fillStartX + currentFillWidth) {
                // Calculer combien de pixels il reste à remplir
                float remainingWidth = (fillStartX + currentFillWidth) - currentX;
                
                // Dessiner sprite9 (2px de large, 2px de haut) si possible
                if (remainingWidth >= fill9Width * scale) {
                    batch.draw(fillSprite9, currentX, fillY, fill9Width * scale, fillSpriteHeight);
                    currentX += fill9Width * scale;
                }
                // Sinon dessiner sprite10 (1px de large, 2px de haut) si possible
                else if (remainingWidth >= fill10Width * scale) {
                    batch.draw(fillSprite10, currentX, fillY, fill10Width * scale, fillSpriteHeight);
                    currentX += fill10Width * scale;
                }
                // Sinon dessiner sprite11 (1px de large, 2px de haut) si possible
                else if (remainingWidth >= fill11Width * scale) {
                    batch.draw(fillSprite11, currentX, fillY, fill11Width * scale, fillSpriteHeight);
                    currentX += fill11Width * scale;
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

