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
import com.tlse1.twodgame.screens.SettingsScreen;
import com.tlse1.twodgame.utils.MenuMapping;

/**
 * Écran de menu principal.
 * Affiche le logo, les options de menu et permet de naviguer vers différents écrans.
 */
public class MenuScreen implements Screen {

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

    // Textures - Background et logo
    private final Texture backgroundBlur;
    private final Texture abyssLogo;
    private final Texture abyssZomb1;
    private final Texture abyssChelou;
    private final Texture abyssSos;
    private final Texture abyssZomb;

    // Textures - Boutons de menu
    private final Texture levelsTexture;
    private final Texture inventoryTexture;
    private final Texture stuffTexture;
    private final Texture shopTexture;
    private final Texture playTexture;
    private final Texture quitTexture;
    private final Texture settingsIcon;

    // Sprite regions (compatibilité)
    private final TextureRegion menuBackground;
    private final TextureRegion resumeButton;
    private final TextureRegion restartButton;
    private final TextureRegion settingsButton;
    private final TextureRegion quitButton;

    // Zones cliquables
    private final Rectangle settingsIconBounds = new Rectangle();
    private final Rectangle levelsButtonBounds = new Rectangle();
    private final Rectangle inventoryButtonBounds = new Rectangle();
    private final Rectangle stuffButtonBounds = new Rectangle();
    private final Rectangle shopButtonBounds = new Rectangle();
    private final Rectangle playButtonBounds = new Rectangle();
    private final Rectangle quitButtonBounds = new Rectangle();

    // Dimensions
    private float screenWidth;
    private float screenHeight;
    private float menuX;
    private float menuY;
    private float menuScale;

    public MenuScreen(TwoDGame game) {
        this.game = game;

        // Initialisation caméra
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(screenWidth, screenHeight);
        camera.setToOrtho(false, screenWidth, screenHeight);
        camera.update();

        batch = new SpriteBatch();
        menuMapping = new MenuMapping();

        // Chargement des textures - Background et logo
        backgroundBlur = new Texture("gui/PNG/font_flou.png");
        abyssLogo = new Texture("gui/PNG/Abyss_logo.png");
        abyssZomb1 = new Texture("gui/PNG/Abyss_zomb1.png");
        abyssChelou = new Texture("gui/PNG/Abyss_chelou.png");
        abyssSos = new Texture("gui/PNG/Abyss_sos.png");
        abyssZomb = new Texture("gui/PNG/Abyss_zomb.png");

        // Chargement des textures - Boutons
        levelsTexture = new Texture("gui/PNG/Levels.png");
        inventoryTexture = new Texture("gui/PNG/Inventory_button.png");
        stuffTexture = new Texture("gui/PNG/Stuffs.png");
        shopTexture = new Texture("gui/PNG/Shop.png");
        playTexture = new Texture("gui/PNG/Play.png");
        quitTexture = new Texture("gui/PNG/Quit_long.png");
        settingsIcon = new Texture("gui/PNG/Param.png");

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
        if (menuBackground == null || resumeButton == null) {
            Gdx.app.error("MenuScreen", "Impossible de charger les sprites du menu");
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
            drawLogos();
            drawMenuButtons();
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
     * Dessine les logos Abyss
     */
    private void drawLogos() {
        float logoWidth = 200;
        float logoHeight = 50;
        float drawWidth = menuBackground.getRegionWidth() * menuScale;
        float drawHeight = menuBackground.getRegionHeight() * menuScale;

        batch.draw(abyssZomb1, drawWidth * 0.56f, drawHeight * 0.8f, logoWidth * 2f, logoHeight * 2f);
    }

    /**
     * Dessine tous les boutons du menu
     */
    private void drawMenuButtons() {
        if (resumeButton == null) {
            return;
        }

        MenuLayout layout = calculateMenuLayout();

        // Icône paramètres
        drawSettingsIcon(layout);

        // Boutons principaux
        drawMainButtons(layout);

        // Bouton Play (à droite du quit)
        drawPlayButton(layout);
    }

    /**
     * Calcule le layout du menu
     */
    private MenuLayout calculateMenuLayout() {
        float drawWidth = menuBackground.getRegionWidth() * menuScale;
        float drawHeight = menuBackground.getRegionHeight() * menuScale;
        float buttonWidth = resumeButton.getRegionWidth() * menuScale;
        float buttonHeight = resumeButton.getRegionHeight() * menuScale;
        float buttonSpacing = BUTTON_SPACING_SCALE * menuScale;
        float startY = menuY + drawHeight * BUTTON_START_Y_PERCENT;
        float buttonX = menuX + (drawWidth - buttonWidth) / 2f;

        MenuLayout layout = new MenuLayout();
        layout.drawWidth = drawWidth;
        layout.drawHeight = drawHeight;
        layout.buttonX = buttonX;
        layout.buttonWidth = buttonWidth;
        layout.buttonHeight = buttonHeight;
        layout.buttonSpacing = buttonSpacing;
        layout.startY = startY;

        return layout;
    }

    /**
     * Dessine l'icône des paramètres
     */
    private void drawSettingsIcon(MenuLayout layout) {
        float iconWidth = (settingsIcon.getWidth() * menuScale) * 0.02f;
        float iconHeight = (settingsIcon.getHeight() * menuScale) * 0.02f;
        float iconX = menuX + layout.drawWidth * 1.4f;
        float iconY = menuY + layout.drawWidth * 1.7f;

        batch.draw(settingsIcon, iconX, iconY, iconHeight, iconWidth);
        settingsIconBounds.set(iconX, iconY, iconHeight, iconWidth);
    }

    /**
     * Dessine les boutons principaux du menu
     */
    private void drawMainButtons(MenuLayout layout) {
        float currentY = layout.startY;

        // Levels
        batch.draw(levelsTexture, layout.buttonX, currentY, layout.buttonWidth, layout.buttonHeight);
        levelsButtonBounds.set(layout.buttonX, currentY, layout.buttonWidth, layout.buttonHeight);
        currentY -= (layout.buttonHeight + layout.buttonSpacing);

        // Inventory
        batch.draw(inventoryTexture, layout.buttonX, currentY, layout.buttonWidth, layout.buttonHeight);
        inventoryButtonBounds.set(layout.buttonX, currentY, layout.buttonWidth, layout.buttonHeight);
        currentY -= (layout.buttonHeight + layout.buttonSpacing);

        // Stuff
        batch.draw(stuffTexture, layout.buttonX, currentY, layout.buttonWidth, layout.buttonHeight);
        stuffButtonBounds.set(layout.buttonX, currentY, layout.buttonWidth, layout.buttonHeight);
        currentY -= (layout.buttonHeight + layout.buttonSpacing);

        // Shop
        batch.draw(shopTexture, layout.buttonX, currentY, layout.buttonWidth, layout.buttonHeight);
        shopButtonBounds.set(layout.buttonX, currentY, layout.buttonWidth, layout.buttonHeight);
        currentY -= (layout.buttonHeight + layout.buttonSpacing);

        // Quit
        batch.draw(quitTexture, layout.buttonX, currentY, layout.buttonWidth, layout.buttonHeight);
        quitButtonBounds.set(layout.buttonX, currentY, layout.buttonWidth, layout.buttonHeight);
    }

    /**
     * Dessine le bouton Play
     */
    private void drawPlayButton(MenuLayout layout) {
        float playX = layout.buttonX + layout.drawWidth * 0.8f;
        float playY = layout.startY - (layout.buttonHeight + layout.buttonSpacing) * 4 - 5;
        float playWidth = layout.buttonWidth / 2;
        float playHeight = layout.buttonHeight + 10;

        batch.draw(playTexture, playX, playY, playWidth, playHeight);
        playButtonBounds.set(playX, playY, playWidth, playHeight);
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
            // ESC pressé
        }
    }

    /**
     * Gère les clics sur les boutons
     */
    private void handleButtonClick(float x, float y) {
        if (settingsIconBounds.contains(x, y)) {
            navigateToSettings();

        } else if (levelsButtonBounds.contains(x, y)) {
            navigateToLevels();

        } else if (inventoryButtonBounds.contains(x, y)) {
            navigateToInventory();

        } else if (stuffButtonBounds.contains(x, y)) {
            navigateToStuff();

        } else if (shopButtonBounds.contains(x, y)) {
            navigateToShop();

        } else if (playButtonBounds.contains(x, y)) {
            startGame();

        } else if (quitButtonBounds.contains(x, y)) {
            quitGame();
        }
    }

    /**
     * Navigue vers les paramètres
     */
    private void navigateToSettings() {
        game.setScreen(new SettingsScreen(game));
        dispose();
    }

    /**
     * Navigue vers l'écran des niveaux
     */
    private void navigateToLevels() {
        game.setScreen(new DevScreen(game));
        dispose();
    }

    /**
     * Navigue vers l'inventaire
     */
    private void navigateToInventory() {
        game.setScreen(new DevScreen(game));
        dispose();
    }

    /**
     * Navigue vers l'équipement
     */
    private void navigateToStuff() {
        game.setScreen(new DevScreen(game));
        dispose();
    }

    /**
     * Navigue vers la boutique
     */
    private void navigateToShop() {
        game.setScreen(new DevScreen(game));
        dispose();
    }

    /**
     * Démarre le jeu
     */
    private void startGame() {
        game.setScreen(new GameScreen(game));
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

        // Dispose de toutes les textures
        backgroundBlur.dispose();
        abyssLogo.dispose();
        abyssZomb1.dispose();
        abyssChelou.dispose();
        abyssSos.dispose();
        abyssZomb.dispose();
        levelsTexture.dispose();
        inventoryTexture.dispose();
        stuffTexture.dispose();
        shopTexture.dispose();
        playTexture.dispose();
        quitTexture.dispose();
        settingsIcon.dispose();
    }

    /**
     * Classe interne pour stocker le layout du menu
     */
    private static class MenuLayout {
        float drawWidth;
        float drawHeight;
        float buttonX;
        float buttonWidth;
        float buttonHeight;
        float buttonSpacing;
        float startY;
    }
}