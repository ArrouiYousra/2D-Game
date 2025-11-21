package com.tlse1.twodgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.tlse1.twodgame.TwoDGame;
import com.tlse1.twodgame.utils.MenuMapping;

/**
 * Écran de pause du jeu.
 * Affiche un menu avec les options : Resume, Restart, Settings, Inventory et Quit.
 */
public class PauseScreen implements Screen {
    
    // Constantes
    private static final float BUTTON_START_Y_PERCENT = 0.7f;
    private static final float BUTTON_SPACING_SCALE = 8f;
    private static final float BACKGROUND_COLOR_R = 0.05f;
    private static final float BACKGROUND_COLOR_G = 0.05f;
    private static final float BACKGROUND_COLOR_B = 0.15f;
    
    // Core
    private final TwoDGame game;
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final MenuMapping menuMapping;
    
    // Textures
    private final Texture backgroundTexture;
    private final Texture resumeTexture;
    private final Texture restartTexture;
    private final Texture settingTexture;
    private final Texture inventoryTexture;
    private final Texture quitTexture;
    
    // Sprite regions (non utilisées actuellement mais gardées pour compatibilité)
    private final TextureRegion menuBackground;
    private final TextureRegion resumeButton;
    private final TextureRegion restartButton;
    private final TextureRegion settingsButton;
    private final TextureRegion quitButton;
    
    // Zones cliquables
    private final Rectangle resumeBounds = new Rectangle();
    private final Rectangle restartBounds = new Rectangle();
    private final Rectangle settingsBounds = new Rectangle();
    private final Rectangle inventoryBounds = new Rectangle();
    private final Rectangle quitBounds = new Rectangle();
    
    // Dimensions
    private float screenWidth;
    private float screenHeight;
    private float menuX;
    private float menuY;
    private float menuScale;
    
    public PauseScreen(TwoDGame game) {
        this.game = game;
        
        // Initialisation caméra
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(screenWidth, screenHeight);
        camera.setToOrtho(false, screenWidth, screenHeight);
        camera.update();
        
        batch = new SpriteBatch();
        menuMapping = new MenuMapping();
        
        // Chargement des textures
        backgroundTexture = new Texture("gui/PNG/font_flou.png");
        resumeTexture = new Texture("gui/PNG/Resume.png");
        restartTexture = new Texture("gui/PNG/Restart.png");
        settingTexture = new Texture("gui/PNG/Setting.png");
        inventoryTexture = new Texture("gui/PNG/Inventory_button.png");
        quitTexture = new Texture("gui/PNG/Quit_long.png");
        
        // Chargement des sprites depuis le mapping
        menuBackground = menuMapping.getSprite("sprite6");
        resumeButton = menuMapping.getSprite("sprite7");
        restartButton = menuMapping.getSprite("sprite11");
        settingsButton = menuMapping.getSprite("sprite15");
        quitButton = menuMapping.getSprite("sprite39");
        
        validateAssets();
        
        menuX = 0;
        menuY = 0;
        menuScale = 1f;
    }
    
    /**
     * Vérifie que tous les assets essentiels sont chargés
     */
    private void validateAssets() {
        if (menuBackground == null || resumeButton == null || restartButton == null 
            || settingsButton == null || quitButton == null) {
            Gdx.app.error("PauseScreen", "Impossible de charger les sprites du menu");
        }
    }
    
    @Override
    public void show() {
        // Appelé quand l'écran devient visible
    }
    
    @Override
    public void render(float delta) {
        updateCamera();
        clearScreen();
        
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        if (menuBackground != null) {
            calculateMenuDimensions();
            drawBackground();
            drawButtons();
        }
        
        batch.end();
        
        handleInput();
    }
    
    /**
     * Met à jour la caméra
     */
    private void updateCamera() {
        camera.update();
    }
    
    /**
     * Nettoie l'écran avec la couleur de fond
     */
    private void clearScreen() {
        Gdx.gl.glClearColor(BACKGROUND_COLOR_R, BACKGROUND_COLOR_G, BACKGROUND_COLOR_B, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
    
    /**
     * Calcule les dimensions et la position du menu
     */
    private void calculateMenuDimensions() {
        float scaleX = screenWidth / menuBackground.getRegionWidth();
        float scaleY = screenHeight / menuBackground.getRegionHeight();
        menuScale = Math.min(scaleX, scaleY);
        
        float drawWidth = menuBackground.getRegionWidth() * menuScale;
        float drawHeight = menuBackground.getRegionHeight() * menuScale;
        menuX = (screenWidth - drawWidth) / 2f;
        menuY = (screenHeight - drawHeight) / 2f;
    }
    
    /**
     * Dessine le fond d'écran
     */
    private void drawBackground() {
        batch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight);
    }
    
    /**
     * Dessine tous les boutons du menu
     */
    private void drawButtons() {
        if (!areButtonTexturesValid()) {
            return;
        }
        
        ButtonLayout layout = calculateButtonLayout();
        
        // Dessiner chaque bouton et mettre à jour sa zone cliquable
        drawButton(resumeTexture, layout.buttonX, layout.resumeY, layout.buttonWidth, layout.buttonHeight, resumeBounds);
        drawButton(restartTexture, layout.buttonX, layout.restartY, layout.buttonWidth, layout.buttonHeight, restartBounds);
        drawButton(settingTexture, layout.buttonX, layout.settingsY, layout.buttonWidth, layout.buttonHeight, settingsBounds);
        drawButton(inventoryTexture, layout.buttonX, layout.inventoryY, layout.buttonWidth, layout.buttonHeight, inventoryBounds);
        drawButton(quitTexture, layout.buttonX, layout.quitY, layout.buttonWidth, layout.buttonHeight, quitBounds);
    }
    
    /**
     * Vérifie que toutes les textures de boutons sont valides
     */
    private boolean areButtonTexturesValid() {
        return resumeButton != null && restartButton != null 
            && settingsButton != null && quitButton != null;
    }
    
    /**
     * Calcule le layout des boutons
     */
    private ButtonLayout calculateButtonLayout() {
        float drawWidth = menuBackground.getRegionWidth() * menuScale;
        float drawHeight = menuBackground.getRegionHeight() * menuScale;
        float buttonWidth = resumeButton.getRegionWidth() * menuScale;
        float buttonHeight = resumeButton.getRegionHeight() * menuScale;
        float buttonSpacing = BUTTON_SPACING_SCALE * menuScale;
        float startY = menuY + drawHeight * BUTTON_START_Y_PERCENT;
        float buttonX = menuX + (drawWidth - buttonWidth) / 2f;
        
        ButtonLayout layout = new ButtonLayout();
        layout.buttonX = buttonX;
        layout.buttonWidth = buttonWidth;
        layout.buttonHeight = buttonHeight;
        layout.resumeY = startY;
        layout.restartY = layout.resumeY - (buttonHeight + buttonSpacing);
        layout.settingsY = layout.restartY - (buttonHeight + buttonSpacing);
        layout.inventoryY = layout.settingsY - (buttonHeight + buttonSpacing);
        layout.quitY = layout.inventoryY - (buttonHeight + buttonSpacing);
        
        return layout;
    }
    
    /**
     * Dessine un bouton et met à jour sa zone cliquable
     */
    private void drawButton(Texture texture, float x, float y, float width, float height, Rectangle bounds) {
        batch.draw(texture, x, y, width, height);
        bounds.set(x, y, width, height);
    }
    
    /**
     * Gère les interactions utilisateur
     */
    private void handleInput() {
        float touchX = Gdx.input.getX();
        float touchY = screenHeight - Gdx.input.getY();
        
        if (Gdx.input.justTouched()) {
            handleButtonClick(touchX, touchY);
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            // TODO: Reprendre la partie
        }
    }
    
    /**
     * Gère les clics sur les boutons
     */
    private void handleButtonClick(float x, float y) {
        if (resumeBounds.contains(x, y)) {
            // TODO: Reprendre la partie
            
        } else if (restartBounds.contains(x, y)) {
            game.setScreen(new GameScreen(game));
            
        } else if (settingsBounds.contains(x, y)) {
            game.setScreen(new SettingsScreen(game));
            
        } else if (inventoryBounds.contains(x, y)) {
            // TODO: Ouvrir l'inventaire
            
        } else if (quitBounds.contains(x, y)) {
            Gdx.app.exit();
        }
    }
    
    @Override
    public void resize(int width, int height) {
        screenWidth = width;
        screenHeight = height;
        camera.setToOrtho(false, screenWidth, screenHeight);
        camera.update();
    }
    
    @Override
    public void pause() {
    }
    
    @Override
    public void resume() {
    }
    
    @Override
    public void hide() {
    }
    
    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
        if (menuMapping != null) {
            menuMapping.dispose();
        }
        
        // Dispose des textures
        backgroundTexture.dispose();
        resumeTexture.dispose();
        restartTexture.dispose();
        settingTexture.dispose();
        inventoryTexture.dispose();
        quitTexture.dispose();
    }
    
    /**
     * Classe interne pour stocker le layout des boutons
     */
    private static class ButtonLayout {
        float buttonX;
        float buttonWidth;
        float buttonHeight;
        float resumeY;
        float restartY;
        float settingsY;
        float inventoryY;
        float quitY;
    }
}