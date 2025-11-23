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
 * Écran de développement - Interface minimale avec fond et boutons de base
 */
public class DevScreen implements Screen {

    // Constantes
    private static final float BUTTON_START_Y_PERCENT = 0.15f;
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
    private final Texture backgroundBlur;
    private final Texture backgroundPanel;
    private final Texture quitTexture;
    private final Texture crossTexture;
    private final Texture work;

    // Sprite regions
    private final TextureRegion menuBackground;
    private final TextureRegion resumeButton;

    // Zones cliquables
    private final Rectangle crossButtonBounds = new Rectangle();
    private final Rectangle quitButtonBounds = new Rectangle();

    // Dimensions
    private float screenWidth;
    private float screenHeight;
    private float menuX;
    private float menuY;
    private float menuScale;

    public DevScreen(TwoDGame game) {
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
        backgroundBlur = new Texture("gui/PNG/font_flou.png");
        backgroundPanel = new Texture("gui/PNG/Background.png");
        quitTexture = new Texture("gui/PNG/Quit_long.png");
        crossTexture = new Texture("gui/PNG/Cross_button.png");
        work = new Texture("gui/PNG/Work.png");

        // Chargement des sprites depuis le mapping
        menuBackground = menuMapping.getSprite("sprite6");
        resumeButton = menuMapping.getSprite("sprite7");

        validateAssets();

        menuX = 0;
        menuY = 0;
        menuScale = 1f;
    }

    /**
     * Vérifie que tous les assets essentiels sont chargés
     */
    private void validateAssets() {
        if (menuBackground == null || resumeButton == null) {
            Gdx.app.error("DevScreen", "Impossible de charger les sprites du menu");
        }
    }

    @Override
    public void show() {
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
        batch.draw(backgroundBlur, 0, 0, screenWidth, screenHeight);
    }

    /**
     * Dessine les boutons
     */
    private void drawButtons() {
        if (resumeButton == null) {
            return;
        }

        float drawWidth = menuBackground.getRegionWidth() * menuScale;
        float drawHeight = menuBackground.getRegionHeight() * menuScale;
        float buttonWidth = resumeButton.getRegionWidth() * menuScale;
        float buttonHeight = resumeButton.getRegionHeight() * menuScale;
        float buttonSpacing = BUTTON_SPACING_SCALE * menuScale;
        float startY = menuY + drawHeight * BUTTON_START_Y_PERCENT;
        float buttonX = menuX + (drawWidth - buttonWidth) / 2f;

        // Positions des boutons
        float quitY = startY;

        // Background panel basé sur la largeur de la fenêtre pour rester cohérent
        float panelWidth = screenWidth * 0.35f; // 35% de la largeur de l'écran
        float panelHeight = screenHeight * 0.9f; // 90% de la hauteur
        float panelX = (screenWidth - panelWidth) / 2f; // Centré horizontalement
        float panelY = (screenHeight - panelHeight) / 2f; // Centré verticalement

        // Dessiner le panneau de fond
        batch.draw(backgroundPanel, panelX, panelY, panelWidth, panelHeight);

        // Work au centre du background panel et contenu dans celui-ci
        // Work prend 80% de la largeur du panel pour rester bien dans les limites
        float workWidth = panelWidth * 0.8f;
        float workHeight = workWidth * 2f / 3f; // Garde le ratio 2/3
        float workX = panelX + (panelWidth - workWidth) / 2f; // Centré dans le panel
        float workY = panelY + (panelHeight - workHeight) / 2f + panelHeight * 0.1f; // Centré verticalement avec léger offset vers le haut

        batch.draw(work, workX, workY, workWidth, workHeight);

        // Calculer la taille du bouton cross proportionnelle au panel
        float crossSize = panelWidth * 0.08f; // 8% de la largeur du panel
        
        // Position du bouton cross en haut à droite du panel
        float crossX = panelX + panelWidth - crossSize - 10f; // 10px de marge
        float crossY = panelY + panelHeight - crossSize - 10f; // 10px de marge en haut

        // Dessiner le bouton cross
        //batch.draw(crossTexture, crossX, crossY, crossSize, crossSize);

        // Dessiner le bouton quit en bas du panel
        float quitWidth = panelWidth * 0.6f; // 60% de la largeur du panel
        float quitHeight = buttonHeight * (quitWidth / buttonWidth); // Garde le ratio
        float quitX = panelX + (panelWidth - quitWidth) / 2f; // Centré dans le panel
        quitY = panelY + panelHeight * 0.05f; // 5% du bas du panel

        //batch.draw(quitTexture, quitX, quitY, quitWidth, quitHeight);

        // Mettre à jour les zones cliquables
        crossButtonBounds.set(crossX - 20, crossY, crossSize, crossSize);
        //quitButtonBounds.set(quitX, quitY, quitWidth, quitHeight);
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
            returnToMenu();
        }
    }

    /**
     * Gère les clics sur les boutons
     */
    private void handleButtonClick(float x, float y) {
        if (crossButtonBounds.contains(x, y)) {
            returnToMenu();
        }
        
    }

    /**
     * Retourne au menu principal
     */
    private void returnToMenu() {
        game.setScreen(new MenuScreen(game));
        dispose();
    }

    /**
     * Quitte le jeu
     */
    private void quitGame() {
        Gdx.app.exit();
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
        backgroundBlur.dispose();
        backgroundPanel.dispose();
        quitTexture.dispose();
        crossTexture.dispose();
        work.dispose();
    }
}