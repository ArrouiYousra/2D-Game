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
    private String[] fullscreenTextSprites;
    private String[] windowModeTextSprites;
    
    // Sprites pour le son
    private TextureRegion soundIcon1; // sprite45
    private TextureRegion soundIcon2; // sprite46
    private TextureRegion soundBar; // sprite73 pour les barres marrons
    
    // Sprites pour la musique
    private TextureRegion musicIcon; // sprite65
    
    // Sprites pour full screen et window mode
    private TextureRegion fullscreenIcon; // sprite21
    private TextureRegion windowModeIcon; // sprite36
    
    // Sprites pour les boutons
    private TextureRegion saveButton; // sprite5
    
    // Nombre de barres (16 au total pour son et musique)
    private static final int TOTAL_SOUND_BARS = 16;
    
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
        
        // Créer les textes pour full screen et window mode
        // Note: Vérifier visuellement si U=sprite21 ou V=sprite22 dans Text1.png
        fullscreenTextSprites = createTextSprites("FULL SCREEN");
        windowModeTextSprites = createTextSprites("WINDOW MODE");
        
        // Charger les sprites pour le son
        soundIcon1 = settingsMapping.getSprite("sprite45");
        soundIcon2 = settingsMapping.getSprite("sprite46");
        soundBar = settingsMapping.getSprite("sprite73");
        
        // Charger les sprites pour la musique
        musicIcon = settingsMapping.getSprite("sprite65");
        
        // Charger les sprites pour full screen et window mode
        fullscreenIcon = settingsMapping.getSprite("sprite21");
        windowModeIcon = settingsMapping.getSprite("sprite36");
        
        // Charger les sprites pour les boutons
        saveButton = settingsMapping.getSprite("sprite5");
        
        if (soundIcon1 == null || soundIcon2 == null || soundBar == null || musicIcon == null 
            || fullscreenIcon == null || windowModeIcon == null || saveButton == null) {
            Gdx.app.error("SettingsScreen", "Impossible de charger tous les sprites");
        } else {
            Gdx.app.log("SettingsScreen", "Tous les sprites chargés");
        }
        
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
            
            // Dessiner l'icône de son et les barres
            drawSoundControls(drawWidth, drawHeight);
            
            // Dessiner l'icône de musique et les barres
            drawMusicControls(drawWidth, drawHeight);
            
            // Dessiner les icônes full screen et window mode
            drawDisplayModeControls(drawWidth, drawHeight);
            
            // Dessiner le bouton Save
            drawSaveButton(drawWidth, drawHeight);
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
            
            // Correction : U = sprite22 au lieu de sprite21
            if (c == 'U') {
                spriteIndex = 22;
            }
            
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
     * Dessine les contrôles de son (icône + barres)
     */
    private void drawSoundControls(float drawWidth, float drawHeight) {
        if (soundIcon1 == null || soundIcon2 == null || soundBar == null) {
            return;
        }
        
        // Position de l'icône de son (alignée avec musique)
        float iconX = screenX + drawWidth * 0.15f; // Position horizontale fixe pour alignement
        float iconY = screenY + drawHeight * 0.75f; // Position fixe 0.75
        
        // Calculer les dimensions des sprites avec le scale
        float icon1Width = soundIcon1.getRegionWidth() * screenScale;
        float icon1Height = soundIcon1.getRegionHeight() * screenScale;
        float icon2Width = soundIcon2.getRegionWidth() * screenScale;
        float icon2Height = soundIcon2.getRegionHeight() * screenScale;
        float totalIconWidth = icon1Width + icon2Width; // Largeur totale de l'icône de son
        
        // Dessiner les deux parties de l'icône de son
        batch.draw(soundIcon1, iconX, iconY, icon1Width, icon1Height);
        batch.draw(soundIcon2, iconX + icon1Width, iconY, icon2Width, icon2Height);
        
        // Position des barres de son (à droite de l'icône, avec moins d'espace)
        float barStartX = iconX + totalIconWidth + 5 * screenScale; // Après toute l'icône de son
        float barY = iconY + (icon1Height - soundBar.getRegionHeight() * screenScale) / 2f;
        float barWidth = soundBar.getRegionWidth() * screenScale;
        float barHeight = soundBar.getRegionHeight() * screenScale;
        float barSpacing = 0.5f * screenScale; // Espacement réduit entre les barres
        
        // Calculer la largeur totale des barres pour vérifier qu'elles ne sortent pas
        float totalBarsWidth = TOTAL_SOUND_BARS * barWidth + (TOTAL_SOUND_BARS - 1) * barSpacing;
        float maxX = screenX + drawWidth * 0.9f; // Limite à 90% de la largeur du screen
        
        // Ajuster si les barres sortent du décor
        if (barStartX + totalBarsWidth > maxX) {
            // Réduire encore l'espacement si nécessaire
            barSpacing = Math.max(0.2f * screenScale, (maxX - barStartX - TOTAL_SOUND_BARS * barWidth) / (TOTAL_SOUND_BARS - 1));
        }
        
        // Dessiner les 16 barres marrons (toutes pour l'instant, logique d'interaction plus tard)
        for (int i = 0; i < TOTAL_SOUND_BARS; i++) {
            float barX = barStartX + i * (barWidth + barSpacing);
            batch.draw(soundBar, barX, barY, barWidth, barHeight);
        }
    }
    
    /**
     * Dessine les contrôles de musique (icône + barres)
     */
    private void drawMusicControls(float drawWidth, float drawHeight) {
        if (musicIcon == null || soundBar == null || soundIcon1 == null || soundIcon2 == null) {
            return;
        }
        
        // Position de l'icône de musique (alignée avec son)
        float iconX = screenX + drawWidth * 0.15f; // Même position horizontale que le son
        float iconY = screenY + drawHeight * 0.6f; // Position fixe 0.6
        
        // Calculer les dimensions de l'icône avec le scale
        float iconWidth = musicIcon.getRegionWidth() * screenScale;
        float iconHeight = musicIcon.getRegionHeight() * screenScale;
        
        // Calculer la largeur totale de l'icône de son pour aligner les barres
        float soundIcon1Width = soundIcon1.getRegionWidth() * screenScale;
        float soundIcon2Width = soundIcon2.getRegionWidth() * screenScale;
        float totalSoundIconWidth = soundIcon1Width + soundIcon2Width;
        
        // Dessiner l'icône de musique
        batch.draw(musicIcon, iconX, iconY, iconWidth, iconHeight);
        
        // Position des barres de musique (alignée avec les barres de son)
        // Les barres doivent commencer à la même position X que les barres de son
        float barStartX = iconX + totalSoundIconWidth + 5 * screenScale; // Même position que les barres de son
        float barY = iconY + (iconHeight - soundBar.getRegionHeight() * screenScale) / 2f;
        float barWidth = soundBar.getRegionWidth() * screenScale;
        float barHeight = soundBar.getRegionHeight() * screenScale;
        float barSpacing = 0.5f * screenScale; // Espacement réduit entre les barres
        
        // Calculer la largeur totale des barres pour vérifier qu'elles ne sortent pas
        float totalBarsWidth = TOTAL_SOUND_BARS * barWidth + (TOTAL_SOUND_BARS - 1) * barSpacing;
        float maxX = screenX + drawWidth * 0.9f; // Limite à 90% de la largeur du screen
        
        // Ajuster si les barres sortent du décor
        if (barStartX + totalBarsWidth > maxX) {
            // Réduire encore l'espacement si nécessaire
            barSpacing = Math.max(0.2f * screenScale, (maxX - barStartX - TOTAL_SOUND_BARS * barWidth) / (TOTAL_SOUND_BARS - 1));
        }
        
        // Dessiner les 16 barres marrons (toutes pour l'instant, logique d'interaction plus tard)
        for (int i = 0; i < TOTAL_SOUND_BARS; i++) {
            float barX = barStartX + i * (barWidth + barSpacing);
            batch.draw(soundBar, barX, barY, barWidth, barHeight);
        }
    }
    
    /**
     * Dessine les contrôles d'affichage (full screen et window mode)
     */
    private void drawDisplayModeControls(float drawWidth, float drawHeight) {
        if (fullscreenIcon == null || windowModeIcon == null) {
            return;
        }
        
        // Calculer les dimensions des icônes avec le scale
        float fullscreenWidth = fullscreenIcon.getRegionWidth() * screenScale;
        float fullscreenHeight = fullscreenIcon.getRegionHeight() * screenScale;
        float windowModeWidth = windowModeIcon.getRegionWidth() * screenScale;
        float windowModeHeight = windowModeIcon.getRegionHeight() * screenScale;
        
        // Position horizontale (même que les autres éléments)
        float iconX = screenX + drawWidth * 0.15f;
        
        // Position verticale fixe
        float fullscreenY = screenY + drawHeight * 0.45f; // Position fixe 0.45
        float windowModeY = screenY + drawHeight * 0.3f; // Position fixe 0.3
        
        // Dessiner les icônes (l'une en dessous de l'autre)
        batch.draw(fullscreenIcon, iconX, fullscreenY, fullscreenWidth, fullscreenHeight);
        batch.draw(windowModeIcon, iconX, windowModeY, windowModeWidth, windowModeHeight);
        
        // Dessiner les textes à droite des icônes, avec un scale réduit pour qu'ils soient plus petits
        float textScale = 0.6f; // Réduire la taille du texte à 50%
        
        // Espacement après chaque icône (augmenté car le texte est plus petit maintenant)
        float fullscreenTextSpacing = 25f; // Espacement augmenté pour pousser le texte plus à droite
        float windowModeTextSpacing = 25f; // Espacement augmenté pour pousser le texte plus à droite
        
        // Position du texte pour full screen (à droite de son icône)
        float fullscreenTextX = iconX + fullscreenWidth + fullscreenTextSpacing;
        float fullscreenTextY = fullscreenY + (fullscreenHeight - getTextHeight(fullscreenTextSprites) * textScale) / 2f;
        
        // Position du texte pour window mode (à droite de son icône)
        float windowModeTextX = iconX + windowModeWidth + windowModeTextSpacing;
        float windowModeTextY = windowModeY + (windowModeHeight - getTextHeight(windowModeTextSprites) * textScale) / 2f;
        
        // Limite pour rester dans le sprite1 (90% de la largeur)
        float maxX = screenX + drawWidth * 0.9f;
        float fullscreenTextWidth = calculateTextWidth(fullscreenTextSprites) * textScale;
        float windowModeTextWidth = calculateTextWidth(windowModeTextSprites) * textScale;
        
        // Ajuster si le texte sort du décor
        if (fullscreenTextX + fullscreenTextWidth > maxX) {
            fullscreenTextX = maxX - fullscreenTextWidth - 5 * screenScale;
        }
        if (windowModeTextX + windowModeTextWidth > maxX) {
            windowModeTextX = maxX - windowModeTextWidth - 5 * screenScale;
        }
        
        // Dessiner les textes avec le scale réduit
        drawTextScaled(fullscreenTextSprites, fullscreenTextX, fullscreenTextY, textScale);
        drawTextScaled(windowModeTextSprites, windowModeTextX, windowModeTextY, textScale);
    }
    
    /**
     * Calcule la hauteur d'un texte (pour le centrer verticalement)
     */
    private float getTextHeight(String[] spriteNames) {
        float maxHeight = 0f;
        
        for (String spriteName : spriteNames) {
            if (spriteName == null) {
                continue;
            }
            
            TextureRegion letterSprite = textMapping.getSprite(spriteName);
            if (letterSprite != null) {
                float letterHeight = letterSprite.getRegionHeight() * screenScale;
                maxHeight = Math.max(maxHeight, letterHeight);
            }
        }
        
        return maxHeight;
    }
    
    /**
     * Dessine le bouton Save en bas, centré
     */
    private void drawSaveButton(float drawWidth, float drawHeight) {
        if (saveButton == null) {
            return;
        }
        
        // Calculer les dimensions du bouton avec le scale
        float buttonWidth = saveButton.getRegionWidth() * screenScale;
        float buttonHeight = saveButton.getRegionHeight() * screenScale;
        
        // Position verticale fixe
        float buttonY = screenY + drawHeight * 0.15f; // Position fixe 0.15
        
        // Position horizontale centrée
        float buttonX = screenX + (drawWidth - buttonWidth) / 2f;
        
        // Dessiner le bouton Save
        batch.draw(saveButton, buttonX, buttonY, buttonWidth, buttonHeight);
    }
    
    /**
     * Dessine un texte composé de sprites à une position donnée
     */
    private void drawText(String[] spriteNames, float startX, float startY) {
        drawTextScaled(spriteNames, startX, startY, 1.0f);
    }
    
    /**
     * Dessine un texte composé de sprites à une position donnée avec un scale personnalisé
     */
    private void drawTextScaled(String[] spriteNames, float startX, float startY, float textScale) {
        float currentX = startX;
        float letterSpacing = 2f * screenScale * textScale; // Espacement entre les lettres
        
        for (String spriteName : spriteNames) {
            if (spriteName == null) {
                currentX += 10 * screenScale * textScale; // Espace pour caractère non supporté
                continue;
            }
            
            TextureRegion letterSprite = textMapping.getSprite(spriteName);
            if (letterSprite != null) {
                // Calculer la taille de la lettre avec le scale personnalisé
                float letterWidth = letterSprite.getRegionWidth() * screenScale * textScale;
                float letterHeight = letterSprite.getRegionHeight() * screenScale * textScale;
                
                // Dessiner la lettre
                batch.draw(letterSprite, currentX, startY, letterWidth, letterHeight);
                
                // Avancer la position pour la prochaine lettre
                currentX += letterWidth + letterSpacing;
            } else {
                // Si le sprite n'existe pas, avancer quand même
                currentX += 10 * screenScale * textScale;
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
