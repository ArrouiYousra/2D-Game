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
    private Texture full_screen = new Texture("gui/PNG/Full_screen.png");
    private Texture window = new Texture("gui/PNG/Window.png");

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
    private Rectangle resumeButtonBounds = new Rectangle();
    private Rectangle restartButtonBounds = new Rectangle();
    private Rectangle settingsButtonBounds = new Rectangle();
    private Rectangle quitButtonBounds = new Rectangle();

    private boolean isResumeButtonHovered = false;
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
        } else {
            Gdx.app.log("MenuScreen", "Sprites du menu chargés");
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
    
    // Positions des boutons fullscreen/window - MODIFIÉ : plus bas
    float fullscreenX = buttonX + drawHeight * 0.2f;
    float windowX = buttonX - drawHeight * 0.2f;
    float screenButtonY = resumeY - clickHeight - 35; // En dessous des boutons click avec espacement
    float screenButtonWidth = settingskWidth * 0.6f;
    float screenButtonHeight = settingsHeight * 0.6f;
    
    // DESSINER LES BOUTONS CLICK/NON_CLICK SELON L'ÉTAT
    if (isFullscreen) {
        // En plein écran : afficher click sur fullscreen
        float clickX = fullscreenX;
        float non_clickX = windowX;
        
        batch.draw(click, clickX, resumeY, clickWidth, clickHeight);
        batch.draw(non_click, non_clickX, resumeY, clickWidth, clickHeight);
        
        clickButtonBounds.set(clickX, resumeY, clickWidth, clickHeight);
        non_clickButtonBounds.set(non_clickX, resumeY, clickWidth, clickHeight);
    } else {
        // En mode fenêtré : afficher click sur window
        float clickX = windowX;
        float non_clickX = fullscreenX;
        
        batch.draw(click, clickX, resumeY, clickWidth, clickHeight);
        batch.draw(non_click, non_clickX, resumeY, clickWidth, clickHeight);
        
        clickButtonBounds.set(clickX, resumeY, clickWidth, clickHeight);
        non_clickButtonBounds.set(non_clickX, resumeY, clickWidth, clickHeight);
    }
    
    // Dessiner les icônes fullscreen et window EN DESSOUS
    batch.draw(full_screen, fullscreenX, screenButtonY, screenButtonWidth, screenButtonHeight);
    batch.draw(window, windowX, screenButtonY, screenButtonWidth, screenButtonHeight);
    
    // Zones cliquables pour les boutons fullscreen/window
    fullscreenButtonBounds.set(fullscreenX, screenButtonY, screenButtonWidth, screenButtonHeight);
    windowButtonBounds.set(windowX, screenButtonY, screenButtonWidth, screenButtonHeight);
    
    // Texte "Settings" au centre et à droite
    batch.draw(settings_font, buttonX - drawHeight * 0.05f, resumeY + 30, settingskWidth, settingsHeight);

    // Autres boutons du menu
    batch.draw(setting, buttonX, settingsY, buttonWidth, buttonHeight);
    batch.draw(inventory, buttonX, inventoryY, buttonWidth, buttonHeight);
    batch.draw(quit_long, buttonX, quitY, buttonWidth, buttonHeight);
    
    // Mettre à jour les zones cliquables des autres boutons
    resumeButtonBounds.set(buttonX, resumeY + 50, buttonWidth, buttonHeight);
    restartButtonBounds.set(buttonX, restartY, buttonWidth, buttonHeight);
    settingsButtonBounds.set(buttonX, settingsY, buttonWidth, buttonHeight);
    quitButtonBounds.set(buttonX, quitY, buttonWidth, buttonHeight);
}
    
    private void handleInput() {
        float touchX = Gdx.input.getX();
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
        
        // Vérifier les survols
        isResumeButtonHovered = resumeButtonBounds.contains(touchX, touchY);
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
                
            } else if (isResumeButtonHovered) {
                Gdx.app.log("MenuScreen", "Bouton Resume cliqué");
                
            } else if (isRestartButtonHovered) {
                game.setScreen(new GameScreen(game));
                
            } else if (isSettingsButtonHovered) {
                game.setScreen(new SettingsScreen(game));
                
            } else if (isQuitButtonHovered) {
                Gdx.app.exit();
            }
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.log("MenuScreen", "ESC pressé");
        }
    }
    
    /**
     * Bascule entre plein écran et mode fenêtré
     * @param fullscreen true pour plein écran, false pour fenêtré
     */
    private void toggleFullscreen(boolean fullscreen) {
        if (fullscreen && !isFullscreen) {
            // Passer en plein écran
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            isFullscreen = true;
            Gdx.app.log("SettingsScreen", "Passage en plein écran");
            
        } else if (!fullscreen && isFullscreen) {
            // Passer en mode fenêtré
            Gdx.graphics.setWindowedMode(WINDOWED_WIDTH, WINDOWED_HEIGHT);
            isFullscreen = false;
            Gdx.app.log("SettingsScreen", "Passage en mode fenêtré");
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
        full_screen.dispose();
        window.dispose();
    }
}