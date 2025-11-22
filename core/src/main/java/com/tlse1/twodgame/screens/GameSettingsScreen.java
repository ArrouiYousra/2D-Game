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
 * Écran des paramètres du jeu en cours.
 * Permet de gérer les contrôles, l'affichage plein écran/fenêtré, et les actions de jeu.
 */
public class GameSettingsScreen implements Screen {

    // Constantes
    private static final float BUTTON_START_Y_PERCENT = 0.7f;
    private static final float BUTTON_SPACING_SCALE = 8f;
    private static final float BACKGROUND_COLOR_R = 0.05f;
    private static final float BACKGROUND_COLOR_G = 0.05f;
    private static final float BACKGROUND_COLOR_B = 0.15f;
    private static final int WINDOWED_WIDTH = 1280;
    private static final int WINDOWED_HEIGHT = 720;
    
    // Référence au GameScreen pour reprendre le jeu (peut être null si appelé depuis le menu)
    private final GameScreen gameScreen;
    
    // Core
    private final TwoDGame game;
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final MenuMapping menuMapping;

    // Textures - Background et UI
    private final Texture backgroundBlur;
    private final Texture backgroundPanel;
    private final Texture settingsTitle;
    
    // Textures - Boutons principaux
    private final Texture resumeTexture;
    private final Texture quitTexture;
    private final Texture crossTexture;
    private final Texture commandeTexture;
    
    // Textures - Toggle buttons
    private final Texture clickTexture;
    private final Texture nonClickTexture;
    private final Texture fullScreenTexture;
    private final Texture windowTexture;
    
    // Textures - Touches de contrôle
    private final Texture keyZ;
    private final Texture keyQ;
    private final Texture keyS;
    private final Texture keyD;

    // Sprite regions (compatibilité)
    private final TextureRegion menuBackground;
    private final TextureRegion resumeButton;
    private final TextureRegion restartButton;
    private final TextureRegion settingsButton;
    private final TextureRegion quitButton;

    // États
    private boolean isFullscreen;
    
    // Zones cliquables
    private final Rectangle clickButtonBounds = new Rectangle();
    private final Rectangle nonClickButtonBounds = new Rectangle();
    private final Rectangle fullscreenButtonBounds = new Rectangle();
    private final Rectangle windowButtonBounds = new Rectangle();
    private final Rectangle crossButtonBounds = new Rectangle();
    private final Rectangle resumeButtonBounds = new Rectangle();
    private final Rectangle quitButtonBounds = new Rectangle();

    // Dimensions
    private float screenWidth;
    private float screenHeight;
    private float menuX;
    private float menuY;
    private float menuScale;

    /**
     * Constructeur avec référence au GameScreen pour reprendre le jeu
     * @param game Le jeu principal
     * @param gameScreen Le GameScreen à reprendre (peut être null si appelé depuis le menu)
     */
    public GameSettingsScreen(TwoDGame game, GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;

        // Initialisation caméra
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(screenWidth, screenHeight);
        camera.setToOrtho(false, screenWidth, screenHeight);
        camera.update();

        // État initial
        isFullscreen = Gdx.graphics.isFullscreen();

        batch = new SpriteBatch();
        menuMapping = new MenuMapping();

        // Chargement des textures - Background et UI
        backgroundBlur = new Texture("gui/PNG/font_flou.png");
        backgroundPanel = new Texture("gui/PNG/Background.png");
        settingsTitle = new Texture("gui/PNG/Settings_font.png");
        
        // Chargement des textures - Boutons principaux
        resumeTexture = new Texture("gui/PNG/Resume.png");
        quitTexture = new Texture("gui/PNG/Quit_long.png");
        crossTexture = new Texture("gui/PNG/Cross_button.png");
        commandeTexture = new Texture("gui/PNG/Commande.png");
        
        // Chargement des textures - Toggle buttons
        clickTexture = new Texture("gui/PNG/Click.png");
        nonClickTexture = new Texture("gui/PNG/Non_click.png");
        fullScreenTexture = new Texture("gui/PNG/Full_screen.png");
        windowTexture = new Texture("gui/PNG/Window.png");
        
        // Chargement des textures - Touches
        keyZ = new Texture("gui/PNG/Z.png");
        keyQ = new Texture("gui/PNG/Q.png");
        keyS = new Texture("gui/PNG/S.png");
        keyD = new Texture("gui/PNG/D.png");

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
        if (menuBackground == null) {
            Gdx.app.error("GameSettingsScreen", "Impossible de charger le background");
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
            drawUI();
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
     * Dessine tous les éléments de l'interface
     */
    private void drawUI() {
        UILayout layout = calculateUILayout();
        
        drawBackgroundPanel(layout);
        drawTitle(layout);
        drawCloseButton(layout);
        drawWindowControls(layout);
        drawCommandsSection(layout);
        drawActionButtons(layout);
    }

    /**
     * Calcule le layout de l'interface
     */
    private UILayout calculateUILayout() {
        float drawWidth = menuBackground.getRegionWidth() * menuScale;
        float drawHeight = menuBackground.getRegionHeight() * menuScale;
        float buttonWidth = resumeButton.getRegionWidth() * menuScale;
        float buttonHeight = resumeButton.getRegionHeight() * menuScale;
        float buttonSpacing = BUTTON_SPACING_SCALE * menuScale;
        float startY = menuY + drawHeight * BUTTON_START_Y_PERCENT;
        float buttonX = menuX + (drawWidth - buttonWidth) / 2f;

        UILayout layout = new UILayout();
        layout.drawWidth = drawWidth;
        layout.drawHeight = drawHeight;
        layout.buttonX = buttonX;
        layout.buttonWidth = buttonWidth;
        layout.buttonHeight = buttonHeight;
        layout.buttonSpacing = buttonSpacing;
        layout.startY = startY;
        layout.clickWidth = (clickTexture.getWidth() * menuScale) * 0.06f;
        layout.clickHeight = (clickTexture.getHeight() * menuScale) * 0.06f;
        layout.titleWidth = (settingsTitle.getWidth() * menuScale) * 0.4f;
        layout.titleHeight = (settingsTitle.getHeight() * menuScale) * 0.4f;
        
        return layout;
    }

    /**
     * Dessine le panneau de fond
     */
    private void drawBackgroundPanel(UILayout layout) {
        float panelX = layout.drawWidth * 0.31f;
        float panelY = layout.drawHeight * 0.108f;
        float panelWidth = layout.drawWidth * 2.2f;
        float panelHeight = layout.drawHeight * 0.86f;
        
        batch.draw(backgroundPanel, panelX, panelY, panelWidth, panelHeight);
    }

    /**
     * Dessine le titre "Settings"
     */
    private void drawTitle(UILayout layout) {
        float titleX = layout.buttonX - layout.drawHeight * 0.05f;
        float titleY = layout.startY + 30;
        
        batch.draw(settingsTitle, titleX, titleY, layout.titleWidth, layout.titleHeight);
    }

    /**
     * Dessine le bouton de fermeture (croix)
     */
    private void drawCloseButton(UILayout layout) {
        float crossX = layout.buttonX + layout.drawHeight * 0.6f;
        float crossY = layout.startY + layout.drawHeight * 0.2f;
        
        batch.draw(crossTexture, crossX * 0.98f, crossY * 1.01f, 
                   layout.clickWidth * 2f, layout.clickHeight);
        
        crossButtonBounds.set(crossX, crossY, layout.clickWidth, layout.clickHeight);
    }

    /**
     * Dessine les contrôles de fenêtre (fullscreen/windowed)
     */
    private void drawWindowControls(UILayout layout) {
        float fullscreenX = layout.buttonX + layout.drawHeight * 0.2f;
        float windowX = layout.buttonX - layout.drawHeight * 0.2f;
        float screenButtonY = layout.startY - layout.clickHeight - 35;
        float iconWidth = layout.titleWidth * 0.6f;
        float iconHeight = layout.titleHeight * 0.6f;

        if (isFullscreen) {
            batch.draw(clickTexture, fullscreenX, layout.startY, layout.clickWidth, layout.clickHeight);
            batch.draw(nonClickTexture, windowX, layout.startY, layout.clickWidth, layout.clickHeight);
            clickButtonBounds.set(fullscreenX, layout.startY, layout.clickWidth, layout.clickHeight);
            nonClickButtonBounds.set(windowX, layout.startY, layout.clickWidth, layout.clickHeight);
        } else {
            batch.draw(clickTexture, windowX, layout.startY, layout.clickWidth, layout.clickHeight);
            batch.draw(nonClickTexture, fullscreenX, layout.startY, layout.clickWidth, layout.clickHeight);
            clickButtonBounds.set(windowX, layout.startY, layout.clickWidth, layout.clickHeight);
            nonClickButtonBounds.set(fullscreenX, layout.startY, layout.clickWidth, layout.clickHeight);
        }

        batch.draw(fullScreenTexture, fullscreenX, screenButtonY, iconWidth, iconHeight);
        batch.draw(windowTexture, windowX, screenButtonY, iconWidth, iconHeight);

        fullscreenButtonBounds.set(fullscreenX, screenButtonY, iconWidth, iconHeight);
        windowButtonBounds.set(windowX, screenButtonY, iconWidth, iconHeight);
    }

    /**
     * Dessine la section des commandes (touches ZQSD)
     */
    private void drawCommandsSection(UILayout layout) {
        float commandY = layout.startY - (layout.buttonHeight + layout.buttonSpacing) * 2;
        float commandX = layout.buttonX - layout.drawHeight * 0.3f;
        
        batch.draw(commandeTexture, commandX, commandY, layout.buttonWidth, layout.buttonHeight);

        float keyBaseX = layout.buttonX + layout.drawHeight * 0.2f;
        float keyY = commandY;
        float keyOffsetX = layout.drawHeight * 0.1f;
        float keyWidth = layout.clickWidth * 1.12f;
        float keyHeight = layout.clickHeight * 1.12f;
        float keyIconWidth = layout.clickWidth * 0.7f;
        float keyIconHeight = layout.clickHeight * 0.7f;

        // Touche Z (haut)
        batch.draw(clickTexture, keyBaseX + keyOffsetX, keyY + layout.drawHeight * 0.05f, keyWidth, keyHeight);
        batch.draw(keyZ, keyBaseX + keyOffsetX + keyWidth * 0.21f, keyY + layout.drawHeight * 0.055f, keyIconWidth, keyIconHeight);

        // Touche Q (gauche)
        batch.draw(clickTexture, keyBaseX, keyY, keyWidth, keyHeight);
        batch.draw(keyQ, keyBaseX + keyWidth * 0.21f, keyY + keyHeight * 0.08f, keyIconWidth, keyIconHeight);

        // Touche S (bas)
        batch.draw(clickTexture, keyBaseX + keyOffsetX, keyY, keyWidth, keyHeight);
        batch.draw(keyS, keyBaseX + keyOffsetX + keyWidth * 0.21f, keyY + keyHeight * 0.08f, keyIconWidth, keyIconHeight);

        // Touche D (droite)
        batch.draw(clickTexture, keyBaseX + keyOffsetX * 2, keyY, keyWidth, keyHeight);
        batch.draw(keyD, keyBaseX + keyOffsetX * 2 + keyWidth * 0.21f, keyY + keyHeight * 0.08f, keyIconWidth, keyIconHeight);
    }

    /**
     * Dessine les boutons d'action (Resume et Quit)
     */
    private void drawActionButtons(UILayout layout) {
        float resumeY = layout.startY - (layout.buttonHeight + layout.buttonSpacing) * 3;
        float quitY = resumeY - layout.buttonHeight - layout.buttonSpacing;

        batch.draw(resumeTexture, layout.buttonX, resumeY, layout.buttonWidth, layout.buttonHeight);
        resumeButtonBounds.set(layout.buttonX, resumeY, layout.buttonWidth, layout.buttonHeight);

        batch.draw(quitTexture, layout.buttonX, quitY, layout.buttonWidth, layout.buttonHeight);
        quitButtonBounds.set(layout.buttonX, quitY, layout.buttonWidth, layout.buttonHeight);
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

        // ESC pour reprendre le jeu (pas retourner au menu)
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            resumeGame();
        }
    }

    /**
     * Gère les clics sur les boutons
     */
    private void handleButtonClick(float x, float y) {
        if (resumeButtonBounds.contains(x, y)) {
            resumeGame();
            
        } else if (quitButtonBounds.contains(x, y)) {
            quitGame();
            
        } else if (crossButtonBounds.contains(x, y)) {
            resumeGame(); // Reprendre au lieu de retourner au menu
            
        } else if (fullscreenButtonBounds.contains(x, y)) {
            toggleFullscreen(true);
            
        } else if (windowButtonBounds.contains(x, y)) {
            toggleFullscreen(false);
            
        } else if (clickButtonBounds.contains(x, y) || nonClickButtonBounds.contains(x, y)) {
            toggleFullscreen(!isFullscreen);
        }
    }

    /**
     * Reprend le jeu où il était
     */
    private void resumeGame() {
    if (gameScreen != null) {
        gameScreen.resumeGame();
        // Ne pas appeler setScreen, juste revenir
        game.setScreen(gameScreen);
    } else {
        game.setScreen(new GameScreen(game));
    }
    // Ne pas disposer pour garder les ressources
}

    /**
     * Quitte le jeu
     */
    private void quitGame() {
        Gdx.app.exit();
    }

    /**
     * Bascule entre plein écran et mode fenêtré
     */
    private void toggleFullscreen(boolean fullscreen) {
        if (fullscreen && !isFullscreen) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            isFullscreen = true;
        } else if (!fullscreen && isFullscreen) {
            Gdx.graphics.setWindowedMode(WINDOWED_WIDTH, WINDOWED_HEIGHT);
            isFullscreen = false;
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
        
        backgroundBlur.dispose();
        backgroundPanel.dispose();
        settingsTitle.dispose();
        resumeTexture.dispose();
        quitTexture.dispose();
        crossTexture.dispose();
        commandeTexture.dispose();
        clickTexture.dispose();
        nonClickTexture.dispose();
        fullScreenTexture.dispose();
        windowTexture.dispose();
        keyZ.dispose();
        keyQ.dispose();
        keyS.dispose();
        keyD.dispose();
    }

    /**
     * Classe interne pour stocker le layout de l'interface
     */
    private static class UILayout {
        float drawWidth;
        float drawHeight;
        float buttonX;
        float buttonWidth;
        float buttonHeight;
        float buttonSpacing;
        float startY;
        float clickWidth;
        float clickHeight;
        float titleWidth;
        float titleHeight;
    }
}