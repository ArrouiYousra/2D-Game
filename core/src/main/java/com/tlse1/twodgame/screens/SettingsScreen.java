package com.tlse1.twodgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tlse1.twodgame.TwoDGame;
import com.tlse1.twodgame.screens.GameScreen;
import com.tlse1.twodgame.screens.SettingsScreen;
import com.tlse1.twodgame.utils.MenuMapping;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

public class SettingsScreen implements Screen {

    private TwoDGame game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private MenuMapping menuMapping;

    private TextureRegion menuBackground;
    private Texture font = new Texture("gui/PNG/font_flou.png");
    private Texture restart = new Texture("gui/PNG/Restart.png");
    private Texture resume = new Texture("gui/PNG/Resume.png");
    private Texture quit_long = new Texture("gui/PNG/Quit_long.png");
    private Texture quit = new Texture("gui/PNG/Quit.png");
    private Texture inventory = new Texture("gui/PNG/Inventory_button.png");
    private Texture equipement = new Texture("gui/PNG/Equipement.png");
    private Texture setting = new Texture("gui/PNG/Setting.png");
    private Texture cross = new Texture("gui/PNG/Cross_button.png");
    private Texture home = new Texture("gui/PNG/Home.png");
    private Texture click = new Texture("gui/PNG/Click.png");
    private Texture non_click = new Texture("gui/PNG/Non_click.png");
    private Texture settings_font = new Texture("gui/PNG/Settings_font.png");
    private Texture text_font = new Texture("gui/PNG/Text_font.png");
    private Texture full_screen = new Texture("gui/PNG/Full_screen.png");
    private Texture window = new Texture("gui/PNG/Window.png");
    private Texture commande = new Texture("gui/PNG/Commande.png");
    private Texture Z = new Texture("gui/PNG/Z.png");
    private Texture Q = new Texture("gui/PNG/Q.png");
    private Texture S = new Texture("gui/PNG/S.png");
    private Texture D = new Texture("gui/PNG/D.png");
    private Texture background = new Texture("gui/PNG/Background.png");

    private TextureRegion resumeButton;
    private TextureRegion restartButton;
    private TextureRegion settingsButton;
    private TextureRegion quitButton;

    // État des boutons
    private boolean isFullscreen = false; // État actuel de l'affichage

    // Zones cliquables
    private Rectangle clickButtonBounds = new Rectangle();
    private Rectangle non_clickButtonBounds = new Rectangle();
    private Rectangle fullscreenButtonBounds = new Rectangle();
    private Rectangle windowButtonBounds = new Rectangle();
    private Rectangle crossButtonBounds = new Rectangle();
    private Rectangle restartButtonBounds = new Rectangle();
    private Rectangle settingsButtonBounds = new Rectangle();
    private Rectangle quitButtonBounds = new Rectangle();

    private boolean isCrossButtonHovered = false;
    private boolean isRestartButtonHovered = false;
    private boolean isSettingsButtonHovered = false;
    private boolean isQuitButtonHovered = false;

    private float screenWidth;
    private float screenHeight;
    private float menuX, menuY, menuScale;

    // Dimensions de la fenêtre en mode fenêtré
    private static final int WINDOWED_WIDTH = 1280;
    private static final int WINDOWED_HEIGHT = 720;

    public SettingsScreen(TwoDGame game) {
        this.game = game;

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(screenWidth, screenHeight);
        camera.setToOrtho(false, screenWidth, screenHeight);
        camera.update();

        // Vérifier l'état initial du plein écran
        isFullscreen = Gdx.graphics.isFullscreen();

        batch = new SpriteBatch();
        menuMapping = new MenuMapping();

        menuBackground = menuMapping.getSprite("sprite6");
        resumeButton = menuMapping.getSprite("sprite7");
        restartButton = menuMapping.getSprite("sprite11");
        settingsButton = menuMapping.getSprite("sprite15");
        quitButton = menuMapping.getSprite("sprite39");

        if (menuBackground == null || resume == null || restart == null
                || setting == null || quit == null) {
            Gdx.app.error("MenuScreen", "Impossible de charger les sprites du menu");
        }

        menuX = 0;
        menuY = 0;
        menuScale = 1f;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        camera.update();

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        if (menuBackground != null) {
            float scaleX = screenWidth / menuBackground.getRegionWidth();
            float scaleY = screenHeight / menuBackground.getRegionHeight();
            menuScale = Math.min(scaleX, scaleY);

            float drawWidth = menuBackground.getRegionWidth() * menuScale;
            float drawHeight = menuBackground.getRegionHeight() * menuScale;
            menuX = (screenWidth - drawWidth) / 2f;
            menuY = (screenHeight - drawHeight) / 2f;

            batch.draw(font, 0, 0, screenWidth, screenHeight);
            drawButtons(drawWidth, drawHeight);
        }

        batch.end();

        handleInput();
    }

    private void drawButtons(float drawWidth, float drawHeight) {
        if (resume == null || restart == null || setting == null || quit == null) {
            return;
        }

        float clickWidth = (click.getWidth() * menuScale) * 0.06f;
        float clickHeight = (click.getHeight() * menuScale) * 0.06f;

        float settingskWidth = (settings_font.getWidth() * menuScale) * 0.4f;
        float settingsHeight = (settings_font.getHeight() * menuScale) * 0.4f;

        float buttonWidth = resumeButton.getRegionWidth() * menuScale;
        float buttonHeight = resumeButton.getRegionHeight() * menuScale;
        float buttonSpacing = 8f * menuScale;
        float startY = menuY + drawHeight * 0.7f;
        float buttonX = menuX + (drawWidth - buttonWidth) / 2f;

        // Positions des boutons
        float resumeY = startY;
        float restartY = resumeY - (buttonHeight + buttonSpacing);
        float settingsY = restartY - (buttonHeight + buttonSpacing);
        float inventoryY = settingsY - (buttonHeight + buttonSpacing);
        float quitY = inventoryY - (buttonHeight + buttonSpacing);

        // === NOUVELLES POSITIONS - Settings et Text_font scalables comme MenuScreen ===
        
        // Calculer les dimensions basées sur la largeur de l'écran pour être scalable
        float baseWidth = screenWidth * 0.25f; // 25% de la largeur de l'écran
        
        // Text_font - calculer la hauteur proportionnellement
        float textFontWidth = baseWidth;
        float textFontHeight = (text_font.getHeight() * textFontWidth / text_font.getWidth()) / 2f; // 2x moins haut
        float textFontX = (screenWidth - textFontWidth) / 2f;
        float textFontY = screenHeight * 0.9f - textFontHeight / 2f;
        
        // Settings_font à 2/3 de la taille de Text_font
        float settingsFontWidth = textFontWidth * 2f / 3f;
        float settingsFontHeight = textFontHeight * 2f / 3f;

        // Centrer Settings_font sur Text_font horizontalement
        float settingsFontX = textFontX + (textFontWidth - settingsFontWidth) / 2f;
        
        // Centrer Settings_font sur Text_font verticalement et ajouter 10px vers le haut
        float settingsFontY = textFontY + (textFontHeight - settingsFontHeight) / 2f + 10;
        
        // Dessiner Text_font et Settings_font
        batch.draw(text_font, textFontX, textFontY, textFontWidth, textFontHeight);
        batch.draw(settings_font, settingsFontX, settingsFontY, settingsFontWidth, settingsFontHeight);

        // === NOUVELLES POSITIONS - Window et Fullscreen (à 1/3 et 2/3) ===
        
        // Calculer la hauteur du bouton
        float screenButtonHeight = screenHeight * 0.1f; // 10% de la hauteur de l'écran
        
        // Bouton Window à 1/3 de la largeur de la fenêtre
        float windowButtonWidth = screenWidth * 0.15f; // 15% de la largeur
        float windowX = (screenWidth / 3f) - (windowButtonWidth / 2f); // Centré à 1/3
        
        // Bouton Fullscreen à 2/3 de la largeur de la fenêtre
        float fullscreenButtonWidth = screenWidth * 0.15f; // 15% de la largeur
        float fullscreenX = (screenWidth * 2f / 3f) - (fullscreenButtonWidth / 2f); // Centré à 2/3

        // Position du bouton cross
        float crossX = buttonX + drawHeight * 0.6f;
        float crossY = resumeY + drawHeight * 0.2f;
        
        // DESSINER LES BOUTONS CLICK/NON_CLICK SELON L'ÉTAT (aux positions 1/3 et 2/3)
        if (isFullscreen) {
            // En plein écran : afficher click sur fullscreen
            float clickX = fullscreenX + (fullscreenButtonWidth - clickWidth) / 2f; // Centré sur fullscreen
            float non_clickX = windowX + (windowButtonWidth - clickWidth) / 2f; // Centré sur window

            batch.draw(click, clickX, resumeY, clickWidth, clickHeight);
            batch.draw(non_click, non_clickX, resumeY, clickWidth, clickHeight);

            clickButtonBounds.set(clickX, resumeY, clickWidth, clickHeight);
            non_clickButtonBounds.set(non_clickX, resumeY, clickWidth, clickHeight);
        } else {
            // En mode fenêtré : afficher click sur window
            float clickX = windowX + (windowButtonWidth - clickWidth) / 2f; // Centré sur window
            float non_clickX = fullscreenX + (fullscreenButtonWidth - clickWidth) / 2f; // Centré sur fullscreen

            batch.draw(click, clickX, resumeY, clickWidth, clickHeight);
            batch.draw(non_click, non_clickX, resumeY, clickWidth, clickHeight);

            clickButtonBounds.set(clickX, resumeY, clickWidth, clickHeight);
            non_clickButtonBounds.set(non_clickX, resumeY, clickWidth, clickHeight);
        }

        // Position Y pour window et fullscreen : juste en dessous des clicks
        float screenButtonY = resumeY - screenButtonHeight - 10f; // 10px en dessous des clicks

        // Dessiner le bouton cross
        batch.draw(cross, crossX * 0.98f, crossY * 1.01f, clickWidth * 2f, clickHeight);

        // Dessiner les icônes fullscreen et window juste en dessous des clicks
        batch.draw(full_screen, fullscreenX, screenButtonY, fullscreenButtonWidth, screenButtonHeight);
        batch.draw(window, windowX, screenButtonY, windowButtonWidth, screenButtonHeight);

        // Zones cliquables pour les boutons fullscreen/window
        fullscreenButtonBounds.set(fullscreenX, screenButtonY, fullscreenButtonWidth, screenButtonHeight);
        windowButtonBounds.set(windowX, screenButtonY, windowButtonWidth, screenButtonHeight);

        // Commande
        batch.draw(commande, buttonX - drawHeight * 0.3f, settingsY, buttonWidth, buttonHeight);

        batch.draw(click, buttonX + drawHeight * 0.3f, settingsY + drawHeight * 0.05f, clickWidth * 1.12f, clickHeight * 1.12f);
        batch.draw(click, buttonX + drawHeight * 0.2f, settingsY, clickWidth * 1.12f, clickHeight * 1.12f);
        batch.draw(click, buttonX + drawHeight * 0.3f, settingsY, clickWidth * 1.12f, clickHeight * 1.12f);
        batch.draw(click, buttonX + drawHeight * 0.4f, settingsY, clickWidth * 1.12f, clickHeight * 1.12f);
        batch.draw(Z, buttonX + drawHeight * 0.307f, settingsY + drawHeight * 0.055f + 5, clickWidth * 0.7f, clickHeight * 0.7f);
        batch.draw(Q, buttonX + drawHeight * 0.207f, settingsY + drawHeight * 0.009f, clickWidth * 0.7f, clickHeight * 0.7f);
        batch.draw(S, buttonX + drawHeight * 0.307f, settingsY + drawHeight * 0.009f, clickWidth * 0.7f, clickHeight * 0.7f);
        batch.draw(D, buttonX + drawHeight * 0.407f, settingsY + drawHeight * 0.009f, clickWidth * 0.7f, clickHeight * 0.7f);

        // Mettre à jour les zones cliquables des autres boutons
        crossButtonBounds.set(crossX, crossY, clickWidth, clickHeight);
        restartButtonBounds.set(buttonX, restartY, buttonWidth, buttonHeight);
        settingsButtonBounds.set(buttonX, settingsY, buttonWidth, buttonHeight);
        quitButtonBounds.set(buttonX, quitY, buttonWidth, buttonHeight);
    }

    private void handleInput() {
        float touchX = Gdx.input.getX();
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

        // Vérifier les survols
        isCrossButtonHovered = crossButtonBounds.contains(touchX, touchY);
        isRestartButtonHovered = restartButtonBounds.contains(touchX, touchY);
        isSettingsButtonHovered = settingsButtonBounds.contains(touchX, touchY);
        isQuitButtonHovered = quitButtonBounds.contains(touchX, touchY);

        // Détecter les clics
        if (Gdx.input.justTouched()) {
            if (fullscreenButtonBounds.contains(touchX, touchY)) {
                // Passer en plein écran
                toggleFullscreen(true);

            } else if (windowButtonBounds.contains(touchX, touchY)) {
                // Passer en mode fenêtré
                toggleFullscreen(false);

            } else if (clickButtonBounds.contains(touchX, touchY) ||
                    non_clickButtonBounds.contains(touchX, touchY)) {
                // Inverser l'état du bouton
                toggleFullscreen(!isFullscreen);

            } else if (isCrossButtonHovered) {
                game.setScreen(new MenuScreen(game));

            } else if (isRestartButtonHovered) {
                game.setScreen(new GameScreen(game));

            } else if (isSettingsButtonHovered) {
                game.setScreen(new SettingsScreen(game));

            } else if (isQuitButtonHovered) {
                Gdx.app.exit();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            // ESC pressé
        }
    }

    /**
     * Bascule entre plein écran et mode fenêtré
     * 
     * @param fullscreen true pour plein écran, false pour fenêtré
     */
    private void toggleFullscreen(boolean fullscreen) {
        if (fullscreen && !isFullscreen) {
            // Passer en plein écran
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            isFullscreen = true;
        } else if (!fullscreen && isFullscreen) {
            // Passer en mode fenêtré
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
        font.dispose();
        restart.dispose();
        resume.dispose();
        quit_long.dispose();
        quit.dispose();
        inventory.dispose();
        equipement.dispose();
        setting.dispose();
        cross.dispose();
        home.dispose();
        click.dispose();
        non_click.dispose();
        settings_font.dispose();
        text_font.dispose();
        full_screen.dispose();
        window.dispose();
        commande.dispose();
        Z.dispose();
        Q.dispose();
        S.dispose();
        D.dispose();
        background.dispose();
    }
}