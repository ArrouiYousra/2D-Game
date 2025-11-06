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

/**
 * Écran des paramètres du jeu.
 * Affiche les options de configuration (à implémenter).
 */
public class SettingsScreen implements Screen {
    
    private TwoDGame game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private BitmapFont titleFont;
    private BitmapFont instructionFont;
    
    // Textures du bouton retour
    private Texture buttonBackNotPressed;
    private Texture buttonBackPressed;
    private boolean isBackButtonPressed = false;
    
    // Textes
    private String titleText = "PARAMETRES";
    private String instructionText = "Appuyez sur ESC ou cliquez sur Retour pour revenir au menu";
    
    // Layouts pour calculer les tailles
    private GlyphLayout titleLayout;
    private GlyphLayout instructionLayout;
    
    // Dimensions du bouton
    private float buttonWidth;
    private float buttonHeight;
    
    // Positions (calculées dynamiquement)
    private float screenWidth;
    private float screenHeight;
    private float titleX, titleY;
    private float buttonBackX, buttonBackY;
    private float instructionX, instructionY;
    
    public SettingsScreen(TwoDGame game) {
        this.game = game;
        
        // Initialiser la caméra avec les dimensions initiales
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(screenWidth, screenHeight);
        camera.setToOrtho(false, screenWidth, screenHeight);
        camera.update();
        
        // Initialiser le SpriteBatch avec la caméra
        batch = new SpriteBatch();
        
        // Charger les textures du bouton (utiliser Blank comme bouton retour)
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
     * Charge les textures du bouton retour
     */
    private void loadButtonTextures() {
        // Utiliser Blank comme bouton retour (ou créer un bouton personnalisé plus tard)
        buttonBackNotPressed = new Texture(Gdx.files.internal("PostApocalypse_AssetPack_v1.1.2/UI/Menu/Main Menu/Blank_Not-Pressed.png"));
        buttonBackPressed = new Texture(Gdx.files.internal("PostApocalypse_AssetPack_v1.1.2/UI/Menu/Main Menu/Blank_Pressed.png"));
        
        // Définir le filtre pour éviter le flou lors du redimensionnement
        buttonBackNotPressed.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        buttonBackPressed.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        
        // Utiliser les dimensions réelles de la texture
        buttonWidth = buttonBackNotPressed.getWidth();
        buttonHeight = buttonBackNotPressed.getHeight();
    }
    
    /**
     * Crée les polices pour l'écran
     */
    private void createFonts() {
        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.5f); // Taille 2.5x pour le titre
        
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
        
        // Positionner le bouton retour en bas
        buttonBackX = (screenWidth - buttonWidth) / 2f;
        buttonBackY = screenHeight * 0.2f;
        
        // Centrer les instructions sous le bouton
        instructionX = (screenWidth - instructionLayout.width) / 2f;
        instructionY = buttonBackY - buttonHeight / 2f - 30f;
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
        
        // Dessiner le bouton retour (utiliser la texture pressée ou non pressée)
        Texture currentBackTexture = isBackButtonPressed ? buttonBackPressed : buttonBackNotPressed;
        batch.draw(currentBackTexture, buttonBackX, buttonBackY, buttonWidth, buttonHeight);
        
        // Instructions
        instructionFont.draw(batch, instructionLayout, instructionX, instructionY);
        
        batch.end();
        
        // Détecter la touche ESC pour revenir au menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
        }
        
        // Détecter le survol et le clic sur le bouton retour
        float touchX = Gdx.input.getX();
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
        
        boolean isMouseOverBack = (touchX >= buttonBackX && touchX <= buttonBackX + buttonWidth &&
                                  touchY >= buttonBackY && touchY <= buttonBackY + buttonHeight);
        
        // Mettre à jour l'état du bouton (pressé si survolé)
        isBackButtonPressed = isMouseOverBack;
        
        // Détecter le clic sur le bouton retour
        if (Gdx.input.justTouched() && isMouseOverBack) {
            game.setScreen(new MenuScreen(game));
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
        if (buttonBackNotPressed != null) {
            buttonBackNotPressed.dispose();
        }
        if (buttonBackPressed != null) {
            buttonBackPressed.dispose();
        }
        if (titleFont != null) {
            titleFont.dispose();
        }
        if (instructionFont != null) {
            instructionFont.dispose();
        }
    }
}
