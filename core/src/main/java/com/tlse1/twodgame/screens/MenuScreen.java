package com.tlse1.twodgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tlse1.twodgame.TwoDGame;
import com.tlse1.twodgame.screens.SettingsScreen;
import com.tlse1.twodgame.utils.MenuMapping;

/**
 * Écran de menu principal.
 * Affiche le menu avec sprite6 comme fond et les boutons depuis Main_menu.png.
 */
public class MenuScreen implements Screen {
    
    private TwoDGame game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private MenuMapping menuMapping;
    
    // TextureRegion pour le fond (sprite6)
    private TextureRegion menuBackground;
    
    // TextureRegions pour les boutons
    private TextureRegion resumeButton; // sprite7
    private TextureRegion restartButton; // sprite11
    private TextureRegion settingsButton; // sprite15
    private TextureRegion quitButton; // sprite39
    
    // États des boutons (pour le survol)
    private boolean isResumeButtonHovered = false;
    private boolean isRestartButtonHovered = false;
    private boolean isSettingsButtonHovered = false;
    private boolean isQuitButtonHovered = false;
    
    // Dimensions de l'écran
    private float screenWidth;
    private float screenHeight;
    
    // Position et scale du menu
    private float menuX, menuY, menuScale;
    
    public MenuScreen(TwoDGame game) {
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
        menuMapping = new MenuMapping();
        
        // Charger le fond (sprite6)
        menuBackground = menuMapping.getSprite("sprite6");
        
        // Charger les boutons
        resumeButton = menuMapping.getSprite("sprite7");
        restartButton = menuMapping.getSprite("sprite11");
        settingsButton = menuMapping.getSprite("sprite15");
        quitButton = menuMapping.getSprite("sprite39");
        
        if (menuBackground == null || resumeButton == null || restartButton == null 
            || settingsButton == null || quitButton == null) {
            Gdx.app.error("MenuScreen", "Impossible de charger les sprites du menu");
        } else {
            Gdx.app.log("MenuScreen", "Sprites du menu chargés");
        }
        
        // Initialiser les positions (sera calculé dans render)
        menuX = 0;
        menuY = 0;
        menuScale = 1f;
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
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.15f, 1); // fond bleu très foncé
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Définir les matrices de projection pour utiliser la caméra
        batch.setProjectionMatrix(camera.combined);
        
        // Dessiner les éléments
        batch.begin();
        
        if (menuBackground != null) {
            // Calculer le scale pour que le menu s'adapte à l'écran
            float scaleX = screenWidth / menuBackground.getRegionWidth();
            float scaleY = screenHeight / menuBackground.getRegionHeight();
            menuScale = Math.min(scaleX, scaleY); // Garder les proportions
            
            float drawWidth = menuBackground.getRegionWidth() * menuScale;
            float drawHeight = menuBackground.getRegionHeight() * menuScale;
            menuX = (screenWidth - drawWidth) / 2f; // Centrer horizontalement
            menuY = (screenHeight - drawHeight) / 2f; // Centrer verticalement
            
            // Dessiner le fond (sprite6)
            batch.draw(menuBackground, menuX, menuY, drawWidth, drawHeight);
            
            // Dessiner les boutons
            drawButtons(drawWidth, drawHeight);
        }
        
        batch.end();
        
        // Gérer les interactions avec les boutons
        handleInput();
    }
    
    /**
     * Dessine les boutons du menu
     */
    private void drawButtons(float drawWidth, float drawHeight) {
        if (resumeButton == null || restartButton == null || settingsButton == null || quitButton == null) {
            return;
        }
        
        // Calculer les dimensions des boutons avec le scale
        float buttonWidth = resumeButton.getRegionWidth() * menuScale;
        float buttonHeight = resumeButton.getRegionHeight() * menuScale;
        
        // Espacement entre les boutons (inspiré de sprite5)
        float buttonSpacing = 8f * menuScale;
        
        // Position de départ pour les boutons (centré verticalement dans le menu)
        // On place les boutons verticalement, en commençant par le haut
        float startY = menuY + drawHeight * 0.7f; // Commencer à 70% de la hauteur du menu
        
        // Calculer la position X pour centrer les boutons
        float buttonX = menuX + (drawWidth - buttonWidth) / 2f;
        
        // Dessiner les boutons verticalement
        // Resume (sprite7) - en haut
        float resumeY = startY;
        batch.draw(resumeButton, buttonX, resumeY, buttonWidth, buttonHeight);
        
        // Restart (sprite11) - deuxième
        float restartY = resumeY - (buttonHeight + buttonSpacing);
        batch.draw(restartButton, buttonX, restartY, buttonWidth, buttonHeight);
        
        // Settings (sprite15) - troisième
        float settingsY = restartY - (buttonHeight + buttonSpacing);
        batch.draw(settingsButton, buttonX, settingsY, buttonWidth, buttonHeight);
        
        // Quit (sprite39) - en bas
        float quitY = settingsY - (buttonHeight + buttonSpacing);
        batch.draw(quitButton, buttonX, quitY, buttonWidth, buttonHeight);
    }
    
    /**
     * Gère les interactions avec les boutons (survol et clic)
     */
    private void handleInput() {
        // Convertir les coordonnées de l'écran vers les coordonnées de la caméra
        float touchX = Gdx.input.getX();
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Inverser Y car LibGDX utilise le bas comme origine
        
        // Calculer les positions des boutons (même logique que dans drawButtons)
        if (menuBackground == null || resumeButton == null) {
            return;
        }
        
        float drawWidth = menuBackground.getRegionWidth() * menuScale;
        float drawHeight = menuBackground.getRegionHeight() * menuScale;
        float buttonWidth = resumeButton.getRegionWidth() * menuScale;
        float buttonHeight = resumeButton.getRegionHeight() * menuScale;
        float buttonSpacing = 8f * menuScale;
        float startY = menuY + drawHeight * 0.7f;
        float buttonX = menuX + (drawWidth - buttonWidth) / 2f;
        
        float resumeY = startY;
        float restartY = resumeY - (buttonHeight + buttonSpacing);
        float settingsY = restartY - (buttonHeight + buttonSpacing);
        float quitY = settingsY - (buttonHeight + buttonSpacing);
        
        // Vérifier le survol des boutons
        boolean isMouseOverResume = (touchX >= buttonX && touchX <= buttonX + buttonWidth &&
                                    touchY >= resumeY && touchY <= resumeY + buttonHeight);
        
        boolean isMouseOverRestart = (touchX >= buttonX && touchX <= buttonX + buttonWidth &&
                                     touchY >= restartY && touchY <= restartY + buttonHeight);
        
        boolean isMouseOverSettings = (touchX >= buttonX && touchX <= buttonX + buttonWidth &&
                                      touchY >= settingsY && touchY <= settingsY + buttonHeight);
        
        boolean isMouseOverQuit = (touchX >= buttonX && touchX <= buttonX + buttonWidth &&
                                  touchY >= quitY && touchY <= quitY + buttonHeight);
        
        // Mettre à jour l'état des boutons (pour le survol - à implémenter plus tard avec des textures différentes)
        isResumeButtonHovered = isMouseOverResume;
        isRestartButtonHovered = isMouseOverRestart;
        isSettingsButtonHovered = isMouseOverSettings;
        isQuitButtonHovered = isMouseOverQuit;
        
        // Détecter les clics sur les boutons
        if (Gdx.input.justTouched()) {
            if (isMouseOverResume) {
                // TODO: Reprendre la partie (si une partie est en cours)
                Gdx.app.log("MenuScreen", "Bouton Resume cliqué");
            } else if (isMouseOverRestart) {
                // Redémarrer la partie (TODO: créer GameScreen)
                Gdx.app.log("MenuScreen", "Bouton Restart cliqué (GameScreen non disponible)");
            } else if (isMouseOverSettings) {
                // Aller aux paramètres
                game.setScreen(new SettingsScreen(game));
            } else if (isMouseOverQuit) {
                // Quitter le jeu
                Gdx.app.exit();
            }
        }
        
        // Détecter la touche ESC pour revenir au jeu (si le menu est un menu pause)
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            // TODO: Reprendre la partie si c'est un menu pause
            Gdx.app.log("MenuScreen", "ESC pressé");
        }
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
    
    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
        if (menuMapping != null) {
            menuMapping.dispose();
        }
    }
}
