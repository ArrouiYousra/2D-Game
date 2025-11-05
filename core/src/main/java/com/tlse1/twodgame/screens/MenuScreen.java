package com.tlse1.twodgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.tlse1.twodgame.TwoDGame;

/**
 * Écran de menu principal.
 * Affiche un titre et un bouton "Jouer" pour démarrer le jeu.
 */
public class MenuScreen implements Screen {
    
    private TwoDGame game;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont titleFont;
    private BitmapFont buttonFont;
    private BitmapFont instructionFont;
    
    // Textes
    private String titleText = "2D GAME";
    private String buttonText = "JOUER";
    private String instructionText = "Appuyez sur ESPACE ou ENTER pour jouer";
    
    // Layouts pour calculer les tailles
    private GlyphLayout titleLayout;
    private GlyphLayout buttonLayout;
    private GlyphLayout instructionLayout;
    
    // Dimensions du bouton
    private float buttonWidth = 250f;
    private float buttonHeight = 60f;
    
    // Positions (calculées dynamiquement)
    private float screenWidth;
    private float screenHeight;
    private float titleX, titleY;
    private float buttonX, buttonY;
    private float instructionX, instructionY;
    
    public MenuScreen(TwoDGame game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        
        // Créer les polices
        createFonts();
        
        // Initialiser les layouts
        titleLayout = new GlyphLayout();
        buttonLayout = new GlyphLayout();
        instructionLayout = new GlyphLayout();
        
        // Calculer les positions initiales
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        calculatePositions();
    }
    
    /**
     * Crée les polices pour le menu
     */
    private void createFonts() {
        titleFont = new BitmapFont();
        titleFont.getData().setScale(3f); // Taille 3x pour le titre
        
        buttonFont = new BitmapFont();
        buttonFont.getData().setScale(2f); // Taille 2x pour le bouton
        
        instructionFont = new BitmapFont();
        instructionFont.getData().setScale(1f); // Taille normale pour les instructions
    }
    
    /**
     * Calcule les positions de tous les éléments pour les centrer
     */
    private void calculatePositions() {
        // Mettre à jour les layouts avec les nouvelles dimensions
        titleLayout.setText(titleFont, titleText);
        buttonLayout.setText(buttonFont, buttonText);
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
        // Nettoyer l'écran
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1); // fond bleu foncé
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Dessiner le bouton avec ShapeRenderer
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Fond du bouton (gris foncé)
        shapeRenderer.setColor(0.3f, 0.3f, 0.4f, 1f);
        shapeRenderer.rect(buttonX, buttonY, buttonWidth, buttonHeight);
        
        shapeRenderer.end();
        
        // Bordure du bouton (dessinée avec des rectangles fins)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        
        // Bordure supérieure
        shapeRenderer.rect(buttonX - 2, buttonY + buttonHeight, buttonWidth + 4, 2);
        // Bordure inférieure
        shapeRenderer.rect(buttonX - 2, buttonY - 2, buttonWidth + 4, 2);
        // Bordure gauche
        shapeRenderer.rect(buttonX - 2, buttonY - 2, 2, buttonHeight + 4);
        // Bordure droite
        shapeRenderer.rect(buttonX + buttonWidth, buttonY - 2, 2, buttonHeight + 4);
        
        shapeRenderer.end();
        
        // Dessiner les textes
        batch.begin();
        
        // Titre
        titleFont.draw(batch, titleLayout, titleX, titleY);
        
        // Texte du bouton (centré dans le bouton)
        float buttonTextX = buttonX + (buttonWidth - buttonLayout.width) / 2f;
        float buttonTextY = buttonY + (buttonHeight + buttonLayout.height) / 2f;
        buttonFont.draw(batch, buttonLayout, buttonTextX, buttonTextY);
        
        // Instructions
        instructionFont.draw(batch, instructionLayout, instructionX, instructionY);
        
        batch.end();
        
        // Détecter le clic ou la touche pour lancer le jeu
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || 
            Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new GameScreen(game));
        }
        
        // Détecter le clic sur le bouton
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Inverser Y car LibGDX utilise le bas comme origine
            
            if (touchX >= buttonX && touchX <= buttonX + buttonWidth &&
                touchY >= buttonY && touchY <= buttonY + buttonHeight) {
                game.setScreen(new GameScreen(game));
            }
        }
    }
    
    @Override
    public void resize(int width, int height) {
        // Recalculer les positions si la fenêtre change de taille
        screenWidth = width;
        screenHeight = height;
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
        batch.dispose();
        shapeRenderer.dispose();
        if (titleFont != null) {
            titleFont.dispose();
        }
        if (buttonFont != null) {
            buttonFont.dispose();
        }
        if (instructionFont != null) {
            instructionFont.dispose();
        }
    }
}
