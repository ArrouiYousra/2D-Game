package com.tlse1.twodgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tlse1.twodgame.TwoDGame;
import com.tlse1.twodgame.screens.GameScreen;

/**
 * Écran de menu principal.
 * Affiche un titre et un bouton "Jouer" pour démarrer le jeu.
 */
public class MenuScreen implements Screen {
    
    private TwoDGame game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private BitmapFont titleFont;
    private BitmapFont instructionFont;
    
    // Textures des boutons
    private Texture buttonPlayNotPressed;
    private Texture buttonPlayPressed;
    private boolean isButtonPressed = false;
    
    // Textes
    private String titleText = "2D GAME";
    private String instructionText = "Appuyez sur ESPACE ou ENTER pour jouer";
    
    // Layouts pour calculer les tailles
    private GlyphLayout titleLayout;
    private GlyphLayout instructionLayout;
    
    // Dimensions du bouton (seront ajustées selon la texture)
    private float buttonWidth;
    private float buttonHeight;
    
    // Positions (calculées dynamiquement)
    private float screenWidth;
    private float screenHeight;
    private float titleX, titleY;
    private float buttonX, buttonY;
    private float instructionX, instructionY;
    
    public MenuScreen(TwoDGame game) {
        this.game = game;
        
        // Initialiser la caméra avec les dimensions initiales
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(screenWidth, screenHeight);
        camera.setToOrtho(false, screenWidth, screenHeight);
        camera.update();
        
        // Initialiser le SpriteBatch avec la caméra
        batch = new SpriteBatch();
        
        // Charger les textures des boutons
        loadButtonTextures();
        
        // Créer les polices
        createFonts();
        
        // Initialiser les layouts
        titleLayout = new GlyphLayout();
        instructionLayout = new GlyphLayout();
        
        // Calculer les positions initiales
        calculatePositions();
    }
    
    /**
     * Charge les textures des boutons
     */
    private void loadButtonTextures() {
        buttonPlayNotPressed = new Texture(Gdx.files.internal("PostApocalypse_AssetPack_v1.1.2/UI/Menu/Main Menu/Play_Not-Pressed.png"));
        buttonPlayPressed = new Texture(Gdx.files.internal("PostApocalypse_AssetPack_v1.1.2/UI/Menu/Main Menu/Play_Pressed.png"));
        
        // Définir le filtre pour éviter le flou lors du redimensionnement
        buttonPlayNotPressed.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        buttonPlayPressed.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        
        // Utiliser les dimensions réelles de la texture
        buttonWidth = buttonPlayNotPressed.getWidth();
        buttonHeight = buttonPlayNotPressed.getHeight();
    }
    
    /**
     * Crée les polices pour le menu
     */
    private void createFonts() {
        titleFont = new BitmapFont();
        titleFont.getData().setScale(3f); // Taille 3x pour le titre
        
        instructionFont = new BitmapFont();
        instructionFont.getData().setScale(1f); // Taille normale pour les instructions
    }
    
    /**
     * Calcule les positions de tous les éléments pour les centrer
     */
    private void calculatePositions() {
        // Mettre à jour les layouts avec les nouvelles dimensions
        titleLayout.setText(titleFont, titleText);
        instructionLayout.setText(instructionFont, instructionText);
        
        // Centrer le titre en haut
        titleX = (screenWidth - titleLayout.width) / 2f;
        titleY = screenHeight * 0.75f;
        
        // Centrer le bouton au milieu
        buttonX = (screenWidth - buttonWidth) / 2f;
        buttonY = screenHeight * 0.45f;
        
        // Centrer les instructions sous le bouton
        instructionX = (screenWidth - instructionLayout.width) / 2f;
        instructionY = buttonY - buttonHeight / 2f - 30f;
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
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1); // fond bleu foncé
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Définir les matrices de projection pour utiliser la caméra
        batch.setProjectionMatrix(camera.combined);
        
        // Dessiner les éléments
        batch.begin();
        
        // Titre
        titleFont.draw(batch, titleLayout, titleX, titleY);
        
        // Dessiner le bouton (utiliser la texture pressée ou non pressée)
        Texture currentButtonTexture = isButtonPressed ? buttonPlayPressed : buttonPlayNotPressed;
        batch.draw(currentButtonTexture, buttonX, buttonY, buttonWidth, buttonHeight);
        
        // Instructions
        instructionFont.draw(batch, instructionLayout, instructionX, instructionY);
        
        batch.end();
        
        // Détecter le clic ou la touche pour lancer le jeu
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || 
            Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new GameScreen(game));
        }
        
        // Détecter le survol et le clic sur le bouton
        // Convertir les coordonnées de l'écran vers les coordonnées de la caméra
        float touchX = Gdx.input.getX();
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Inverser Y car LibGDX utilise le bas comme origine
        
        boolean isMouseOverButton = (touchX >= buttonX && touchX <= buttonX + buttonWidth &&
                                     touchY >= buttonY && touchY <= buttonY + buttonHeight);
        
        // Mettre à jour l'état du bouton (pressé si survolé)
        isButtonPressed = isMouseOverButton;
        
        // Détecter le clic sur le bouton
        if (Gdx.input.justTouched() && isMouseOverButton) {
            game.setScreen(new GameScreen(game));
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
        
        // Recalculer les positions des éléments
        calculatePositions();
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
        if (buttonPlayNotPressed != null) {
            buttonPlayNotPressed.dispose();
        }
        if (buttonPlayPressed != null) {
            buttonPlayPressed.dispose();
        }
        if (titleFont != null) {
            titleFont.dispose();
        }
        if (instructionFont != null) {
            instructionFont.dispose();
        }
    }
}
