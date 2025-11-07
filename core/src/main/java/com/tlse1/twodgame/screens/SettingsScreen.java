package com.tlse1.twodgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tlse1.twodgame.TwoDGame;
import com.tlse1.twodgame.utils.SettingsMapping;
import com.tlse1.twodgame.utils.TextMapping;

/**
 * Écran des paramètres du jeu.
 * Affiche simplement Settings.png pour commencer.
 */
public class SettingsScreen implements Screen {
    
    private TwoDGame game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private SettingsMapping settingsMapping;
    private TextMapping textMapping;
    
    // TextureRegion pour le screen à afficher
    private TextureRegion currentScreen;
    
    // Texte à afficher (liste de noms de sprites)
    private String[] textSprites;
    
    // Dimensions de l'écran
    private float screenWidth;
    private float screenHeight;
    
    // Position et scale du screen
    private float screenX, screenY, screenScale;
    
    public SettingsScreen(TwoDGame game) {
        this.game = game;
        
        // Initialiser la caméra avec les dimensions initiales
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        
        camera = new OrthographicCamera(screenWidth, screenHeight);
        camera.setToOrtho(false, screenWidth, screenHeight);
        camera.update();
        
        // Initialiser le SpriteBatch
        batch = new SpriteBatch();
        
        // Charger le mapping des sprites depuis le JSON
        settingsMapping = new SettingsMapping();
        textMapping = new TextMapping();
        
        // Commencer avec sprite1 (screen vide avec bouton X)
        // On remplira petit à petit avec les autres éléments
        currentScreen = settingsMapping.getScreen("emptyWithX");
        if (currentScreen == null) {
            // Si le mapping n'est pas trouvé, essayer directement avec sprite1
            currentScreen = settingsMapping.getSprite("sprite1");
        }
        
        if (currentScreen == null) {
            Gdx.app.error("SettingsScreen", "Impossible de charger le screen vide");
        } else {
            Gdx.app.log("SettingsScreen", "Screen vide (sprite1) chargé: " + 
                currentScreen.getRegionWidth() + "x" + currentScreen.getRegionHeight());
        }
        
        // Pour tester : créer le texte "SETTINGS"
        // Note: Il faudra identifier visuellement quels sprites correspondent à quelles lettres
        // Pour l'instant, on utilise des sprites arbitraires pour tester
        // Vous devrez ajuster ces numéros selon ce que vous voyez dans Text1.png
        textSprites = createTextSprites("SETTINGS");
        
        // Initialiser les positions (sera calculé dans render)
        screenX = 0;
        screenY = 0;
        screenScale = 1f;
    }
    
    @Override
    public void show() {
        // Appelé quand l'écran devient visible
    }
    
    @Override
    public void render(float delta) {
        // Mettre à jour la caméra
        camera.update();
        
        // Nettoyer l'écran
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Définir les matrices de projection pour utiliser la caméra
        batch.setProjectionMatrix(camera.combined);
        
        // Dessiner le screen des settings
        batch.begin();
        
        if (currentScreen != null) {
            // Calculer le scale pour que le screen s'adapte à l'écran
            float scaleX = screenWidth / currentScreen.getRegionWidth();
            float scaleY = screenHeight / currentScreen.getRegionHeight();
            screenScale = Math.min(scaleX, scaleY); // Garder les proportions
            
            float drawWidth = currentScreen.getRegionWidth() * screenScale;
            float drawHeight = currentScreen.getRegionHeight() * screenScale;
            screenX = (screenWidth - drawWidth) / 2f; // Centrer horizontalement
            screenY = (screenHeight - drawHeight) / 2f; // Centrer verticalement
            
            // Dessiner le screen
            batch.draw(currentScreen, screenX, screenY, drawWidth, drawHeight);
            
            // Calculer la position du texte pour le centrer sur la partie verte
            // La partie verte est en haut du screen (position zéro)
            float textY = screenY + drawHeight * 0.93f; // Position verticale en haut sur la partie verte
            float textWidth = calculateTextWidth(textSprites);
            float textX = screenX + (drawWidth - textWidth) / 2f; // Centrer horizontalement
            
            // Dessiner le texte par-dessus sur la partie verte
            drawText(textSprites, textX, textY);
        }
        
        batch.end();
    }
    
    @Override
    public void resize(int width, int height) {
        // Mettre à jour les dimensions de l'écran
        screenWidth = width;
        screenHeight = height;
        
        // Mettre à jour la caméra avec les nouvelles dimensions
        camera.setToOrtho(false, screenWidth, screenHeight);
        camera.update();
    }
    
    @Override
    public void pause() {
        // Appelé quand le jeu est mis en pause
    }
    
    @Override
    public void resume() {
        // Appelé quand le jeu reprend
    }
    
    @Override
    public void hide() {
        // Appelé quand l'écran devient invisible
    }
    
    /**
     * Crée un tableau de noms de sprites pour un texte donné.
     * Pour l'instant, c'est un mapping basique - vous devrez identifier visuellement
     * quels sprites correspondent à quelles lettres dans Text1.png.
     */
    private String[] createTextSprites(String text) {
        // Mapping temporaire : A=1, B=2, C=3, etc. (à ajuster selon Text1.png)
        // Pour "SETTINGS" : S=19, E=5, T=20, T=20, I=9, N=14, G=7, S=19
        // Mais il faut identifier visuellement les bons sprites !
        
        // Pour tester, on utilise des sprites arbitraires
        // Vous devrez créer un vrai mapping lettre -> sprite après avoir identifié visuellement
        String[] sprites = new String[text.length()];
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            // Mapping temporaire basé sur l'ordre alphabétique (probablement incorrect)
            // À remplacer par un vrai mapping après identification visuelle
            int spriteIndex = (c - 'A') + 1;
            if (spriteIndex >= 1 && spriteIndex <= 350) {
                sprites[i] = "sprite" + spriteIndex;
            } else {
                sprites[i] = null; // Caractère non supporté
            }
        }
        return sprites;
    }
    
    /**
     * Calcule la largeur totale d'un texte (pour le centrer)
     */
    private float calculateTextWidth(String[] spriteNames) {
        float totalWidth = 0f;
        float letterSpacing = 2f * screenScale;
        
        for (String spriteName : spriteNames) {
            if (spriteName == null) {
                totalWidth += 10 * screenScale;
                continue;
            }
            
            TextureRegion letterSprite = textMapping.getSprite(spriteName);
            if (letterSprite != null) {
                float letterWidth = letterSprite.getRegionWidth() * screenScale;
                totalWidth += letterWidth + letterSpacing;
            } else {
                totalWidth += 10 * screenScale;
            }
        }
        
        // Enlever le dernier espacement
        if (spriteNames.length > 0) {
            totalWidth -= letterSpacing;
        }
        
        return totalWidth;
    }
    
    /**
     * Dessine un texte composé de sprites à une position donnée
     */
    private void drawText(String[] spriteNames, float startX, float startY) {
        float currentX = startX;
        float letterSpacing = 2f * screenScale; // Espacement entre les lettres
        
        for (String spriteName : spriteNames) {
            if (spriteName == null) {
                currentX += 10 * screenScale; // Espace pour caractère non supporté
                continue;
            }
            
            TextureRegion letterSprite = textMapping.getSprite(spriteName);
            if (letterSprite != null) {
                // Calculer la taille de la lettre (scale proportionnel au screen)
                float letterWidth = letterSprite.getRegionWidth() * screenScale;
                float letterHeight = letterSprite.getRegionHeight() * screenScale;
                
                // Dessiner la lettre
                batch.draw(letterSprite, currentX, startY, letterWidth, letterHeight);
                
                // Avancer la position pour la prochaine lettre
                currentX += letterWidth + letterSpacing;
            } else {
                // Si le sprite n'existe pas, avancer quand même
                currentX += 10 * screenScale;
            }
        }
    }
    
    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
        if (settingsMapping != null) {
            settingsMapping.dispose();
        }
        if (textMapping != null) {
            textMapping.dispose();
        }
    }
}
