package com.tlse1.twodgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.tlse1.twodgame.TwoDGame;
import com.tlse1.twodgame.utils.MenuMapping;

public class GameSettingsScreen implements Screen {

    private static final int WINDOWED_WIDTH = 1280;
    private static final int WINDOWED_HEIGHT = 720;
    
    private final GameScreen gameScreen;
    private final TwoDGame game;
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final MenuMapping menuMapping;

    private final Texture backgroundBlur;
    private final Texture backgroundPanel;
    private final Texture settingsTexture;
    private final Texture textFont;
    private final Texture resumeTexture;
    private final Texture quitTexture;
    private final Texture crossTexture;
    private final Texture commandeTexture;
    private final Texture clickTexture;
    private final Texture nonClickTexture;
    private final Texture fullScreenTexture;
    private final Texture windowTexture;
    private final Texture keyZ;
    private final Texture keyQ;
    private final Texture keyS;
    private final Texture keyD;
    
    private BitmapFont instructionFont;

    private final TextureRegion menuBackground;
    private final TextureRegion resumeButton;
    private final TextureRegion restartButton;
    private final TextureRegion settingsButton;
    private final TextureRegion quitButton;

    private boolean isFullscreen;
    
    private final Rectangle clickButtonBounds = new Rectangle();
    private final Rectangle nonClickButtonBounds = new Rectangle();
    private final Rectangle fullscreenButtonBounds = new Rectangle();
    private final Rectangle windowButtonBounds = new Rectangle();
    private final Rectangle crossButtonBounds = new Rectangle();
    private final Rectangle resumeButtonBounds = new Rectangle();
    private final Rectangle quitButtonBounds = new Rectangle();

    private float screenWidth;
    private float screenHeight;
    private float menuX;
    private float menuY;
    private float menuScale;

    public GameSettingsScreen(TwoDGame game, GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(screenWidth, screenHeight);
        camera.setToOrtho(false, screenWidth, screenHeight);
        camera.update();

        isFullscreen = Gdx.graphics.isFullscreen();

        batch = new SpriteBatch();
        menuMapping = new MenuMapping();

        backgroundBlur = new Texture("gui/PNG/font_flou.png");
        backgroundPanel = new Texture("gui/PNG/Background.png");
        settingsTexture = new Texture("gui/PNG/Setting.png");
        textFont = new Texture("gui/PNG/Text_font.png");
        
        resumeTexture = new Texture("gui/PNG/Resume.png");
        quitTexture = new Texture("gui/PNG/Quit_long.png");
        crossTexture = new Texture("gui/PNG/Cross_button.png");
        commandeTexture = new Texture("gui/PNG/Commande.png");
        
        clickTexture = new Texture("gui/PNG/Click.png");
        nonClickTexture = new Texture("gui/PNG/Non_click.png");
        fullScreenTexture = new Texture("gui/PNG/Full_screen.png");
        windowTexture = new Texture("gui/PNG/Window.png");
        
        keyZ = new Texture("gui/PNG/Z.png");
        keyQ = new Texture("gui/PNG/Q.png");
        keyS = new Texture("gui/PNG/S.png");
        keyD = new Texture("gui/PNG/D.png");
        
        instructionFont = new BitmapFont();
        instructionFont.getData().setScale(2.5f);
        instructionFont.setColor(0f, 0f, 0f, 1f); // Noir

        menuBackground = menuMapping.getSprite("sprite6");
        resumeButton = menuMapping.getSprite("sprite7");
        restartButton = menuMapping.getSprite("sprite11");
        settingsButton = menuMapping.getSprite("sprite15");
        quitButton = menuMapping.getSprite("sprite39");

        menuX = 0;
        menuY = 0;
        menuScale = 1f;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        // RENDRE LE GAMESCREEN EN ARRIÈRE-PLAN (sans mettre à jour la logique)
        if (gameScreen != null) {
            gameScreen.render(0);
        }
        
        camera.update();

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

    private void calculateMenuDimensions() {
        float scaleX = screenWidth / menuBackground.getRegionWidth();
        float scaleY = screenHeight / menuBackground.getRegionHeight();
        menuScale = Math.min(scaleX, scaleY);

        float drawWidth = menuBackground.getRegionWidth() * menuScale;
        float drawHeight = menuBackground.getRegionHeight() * menuScale;
        menuX = (screenWidth - drawWidth) / 2f;
        menuY = (screenHeight - drawHeight) / 2f;
    }

    private void drawBackground() {
        // Fond semi-transparent pour voir le jeu derrière
        batch.setColor(1f, 1f, 1f, 0.5f);
        batch.draw(backgroundBlur, 0, 0, screenWidth, screenHeight);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void drawUI() {
        UILayout layout = calculateUILayout();
        
        drawBackgroundPanel();
        drawLogos();
        drawCloseButton(layout);
        drawWindowControls();
        drawCommandsSection(layout);
        drawInstructions(layout);
        drawActionButtons(layout);
    }

    private UILayout calculateUILayout() {
        float drawWidth = menuBackground.getRegionWidth() * menuScale;
        float drawHeight = menuBackground.getRegionHeight() * menuScale;
        float buttonWidth = resumeButton.getRegionWidth() * menuScale;
        float buttonHeight = resumeButton.getRegionHeight() * menuScale;
        float buttonSpacing = 8f * menuScale;
        float startY = menuY + drawHeight * 0.7f;
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
        
        return layout;
    }

    private void drawBackgroundPanel() {
        float panelWidth = screenWidth * 0.8f;
        float panelX = screenWidth * 0.1f;
        float panelHeight = screenHeight * 0.9f;
        float panelY = (screenHeight - panelHeight) / 2f;
        
        batch.draw(backgroundPanel, panelX, panelY, panelWidth, panelHeight);
    }

    private void drawLogos() {
        float baseWidth = screenWidth * 0.25f;
        
        float textFontWidth = baseWidth;
        float textFontHeight = (textFont.getHeight() * textFontWidth / textFont.getWidth()) / 2f;
        float textFontX = (screenWidth - textFontWidth) / 2f;
        float textFontY = screenHeight * 0.9f - textFontHeight / 2f;

        float settingsFontWidth = textFontWidth * 2f / 3f;
        float settingsFontHeight = textFontHeight * 2f / 3f;
        float settingsFontX = (screenWidth - settingsFontWidth) / 2f;
        float settingsFontY = screenHeight * 0.9f - settingsFontHeight / 2f;
        
        batch.draw(settingsTexture, settingsFontX, settingsFontY, settingsFontWidth, settingsFontHeight);
    }

    private void drawCloseButton(UILayout layout) {
        float panelWidth = screenWidth * 0.8f;
        float panelX = screenWidth * 0.1f;
        float panelHeight = screenHeight * 0.9f;
        float panelY = (screenHeight - panelHeight) / 2f;
        
        float crossWidth = layout.clickWidth * 2f;
        float crossHeight = layout.clickHeight;
        float crossX = panelX + panelWidth - crossWidth - 10f - 40f;
        float crossY = panelY + panelHeight - crossHeight - 10f;
        
        batch.draw(crossTexture, crossX, crossY, crossWidth, crossHeight);
        crossButtonBounds.set(crossX, crossY, crossWidth, crossHeight);
    }

    private void drawWindowControls() {
        UILayout layout = calculateUILayout();
        
        float screenButtonHeight = screenHeight * 0.1f;
        float windowButtonWidth = screenWidth * 0.15f;
        float windowX = (screenWidth / 3f) - (windowButtonWidth / 2f);
        float fullscreenButtonWidth = screenWidth * 0.15f;
        float fullscreenX = (screenWidth * 2f / 3f) - (fullscreenButtonWidth / 2f);
        float clickY = screenHeight * 0.8f; // Remonté de 0.7f à 0.8f

        if (isFullscreen) {
            float clickX = fullscreenX + (fullscreenButtonWidth - layout.clickWidth) / 2f;
            float non_clickX = windowX + (windowButtonWidth - layout.clickWidth) / 2f;

            batch.draw(clickTexture, clickX, clickY, layout.clickWidth, layout.clickHeight);
            batch.draw(nonClickTexture, non_clickX, clickY, layout.clickWidth, layout.clickHeight);

            clickButtonBounds.set(clickX, clickY, layout.clickWidth, layout.clickHeight);
            nonClickButtonBounds.set(non_clickX, clickY, layout.clickWidth, layout.clickHeight);
        } else {
            float clickX = windowX + (windowButtonWidth - layout.clickWidth) / 2f;
            float non_clickX = fullscreenX + (fullscreenButtonWidth - layout.clickWidth) / 2f;

            batch.draw(clickTexture, clickX, clickY, layout.clickWidth, layout.clickHeight);
            batch.draw(nonClickTexture, non_clickX, clickY, layout.clickWidth, layout.clickHeight);

            clickButtonBounds.set(clickX, clickY, layout.clickWidth, layout.clickHeight);
            nonClickButtonBounds.set(non_clickX, clickY, layout.clickWidth, layout.clickHeight);
        }

        float screenButtonY = clickY - screenButtonHeight - 10f;

        batch.draw(fullScreenTexture, fullscreenX, screenButtonY, fullscreenButtonWidth, screenButtonHeight);
        batch.draw(windowTexture, windowX, screenButtonY, windowButtonWidth, screenButtonHeight);

        fullscreenButtonBounds.set(fullscreenX, screenButtonY, fullscreenButtonWidth, screenButtonHeight);
        windowButtonBounds.set(windowX, screenButtonY, windowButtonWidth, screenButtonHeight);
    }

    private void drawCommandsSection(UILayout layout) {
        float commandY = screenHeight * 0.58f; // Remonté de 0.45f à 0.58f
        float commandX = screenWidth * 0.2f;
        float commandWidth = screenWidth * 0.2f;
        float commandHeight = commandWidth * (commandeTexture.getHeight() / (float)commandeTexture.getWidth());
        
        batch.draw(commandeTexture, commandX, commandY, commandWidth, commandHeight);

        float keyBaseX = screenWidth * 0.5f;
        float keyBaseY = commandY;
        float keyOffsetX = screenWidth * 0.08f;
        float keyWidth = layout.clickWidth * 1.12f;
        float keyHeight = layout.clickHeight * 1.12f;
        float keyIconWidth = layout.clickWidth * 0.7f;
        float keyIconHeight = layout.clickHeight * 0.7f;

        float keyZY = keyBaseY + keyHeight * 0.5f;
        batch.draw(clickTexture, keyBaseX + keyOffsetX, keyZY, keyWidth, keyHeight);
        batch.draw(keyZ, keyBaseX + keyOffsetX + keyWidth * 0.21f, keyZY + keyHeight * 0.05f, keyIconWidth, keyIconHeight);

        float keyQY = keyBaseY - 20f;
        batch.draw(clickTexture, keyBaseX, keyQY, keyWidth, keyHeight);
        batch.draw(keyQ, keyBaseX + keyWidth * 0.21f, keyQY + keyHeight * 0.08f, keyIconWidth, keyIconHeight);

        float keySY = keyBaseY - 20f;
        batch.draw(clickTexture, keyBaseX + keyOffsetX, keySY, keyWidth, keyHeight);
        batch.draw(keyS, keyBaseX + keyOffsetX + keyWidth * 0.21f, keySY + keyHeight * 0.08f, keyIconWidth, keyIconHeight);

        float keyDY = keyBaseY - 20f;
        batch.draw(clickTexture, keyBaseX + keyOffsetX * 2, keyDY, keyWidth, keyHeight);
        batch.draw(keyD, keyBaseX + keyOffsetX * 2 + keyWidth * 0.21f, keyDY + keyHeight * 0.08f, keyIconWidth, keyIconHeight);
    }
    
    private void drawInstructions(UILayout layout) {
        if (instructionFont == null) {
            return;
        }
        
        float instructionY = screenHeight * 0.42f; // Positionné entre Commande (0.58f) et Resume (0.3f)
        float centerX = screenWidth / 2f;
        
        // Ligne 1 : Loot item : T
        String lootText = "Loot item : T";
        GlyphLayout lootLayout = new GlyphLayout(instructionFont, lootText);
        float lootTextX = centerX - lootLayout.width / 2f;
        instructionFont.draw(batch, lootText, lootTextX, instructionY);
        
        // Ligne 2 : Use item : 1, 2, 3, 4
        String useText = "Use item : 1, 2, 3, 4";
        GlyphLayout useLayout = new GlyphLayout(instructionFont, useText);
        float useTextX = centerX - useLayout.width / 2f;
        float useTextY = instructionY - 45f; // Espacement entre les deux lignes
        instructionFont.draw(batch, useText, useTextX, useTextY);
    }

    private void drawActionButtons(UILayout layout) {
        float resumeY = screenHeight * 0.23f; // Descendu de 0.3f à 0.23f
        float quitY = screenHeight * 0.13f;   // Descendu de 0.2f à 0.13f
        float buttonX = (screenWidth - layout.buttonWidth) / 2f;

        batch.draw(resumeTexture, buttonX, resumeY, layout.buttonWidth, layout.buttonHeight);
        resumeButtonBounds.set(buttonX, resumeY, layout.buttonWidth, layout.buttonHeight);

        batch.draw(quitTexture, buttonX, quitY, layout.buttonWidth, layout.buttonHeight);
        quitButtonBounds.set(buttonX, quitY, layout.buttonWidth, layout.buttonHeight);
    }

    private void handleInput() {
        float touchX = Gdx.input.getX();
        float touchY = screenHeight - Gdx.input.getY();

        if (Gdx.input.justTouched()) {
            handleButtonClick(touchX, touchY);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            returnToGameScreen();
        }
    }

    private void handleButtonClick(float x, float y) {
        if (crossButtonBounds.contains(x, y)) {
            returnToGameScreen();
            
        } else if (resumeButtonBounds.contains(x, y)) {
            returnToGameScreen();
            
        } else if (quitButtonBounds.contains(x, y)) {
            returnToMenuScreen();
            
        } else if (fullscreenButtonBounds.contains(x, y)) {
            toggleFullscreen(true);
            
        } else if (windowButtonBounds.contains(x, y)) {
            toggleFullscreen(false);
            
        } else if (clickButtonBounds.contains(x, y) || nonClickButtonBounds.contains(x, y)) {
            toggleFullscreen(!isFullscreen);
        }
    }

    private void returnToGameScreen() {
        if (gameScreen != null) {
            // Réactiver le jeu
            gameScreen.resume();
            game.setScreen(gameScreen);
        }
        // Disposer uniquement les ressources de GameSettingsScreen
        disposeSettingsResources();
    }

    private void returnToMenuScreen() {
        // Si on retourne au menu, on dispose tout
        if (gameScreen != null) {
            gameScreen.dispose();
        }
        game.setScreen(new MenuScreen(game));
        dispose();
    }

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
        
        // PROPAGER LE RESIZE AU GAMESCREEN
        if (gameScreen != null) {
            gameScreen.resize(width, height);
        }
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
    
    private void disposeSettingsResources() {
        // Disposer uniquement les ressources spécifiques à GameSettingsScreen
        if (batch != null) {
            batch.dispose();
        }
        if (menuMapping != null) {
            menuMapping.dispose();
        }
        if (instructionFont != null) {
            instructionFont.dispose();
        }
        
        backgroundBlur.dispose();
        backgroundPanel.dispose();
        settingsTexture.dispose();
        textFont.dispose();
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

    @Override
    public void dispose() {
        disposeSettingsResources();
    }

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
    }
}