package com.tlse1.twodgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.tlse1.twodgame.TwoDGame;

public class WinScreen implements Screen {
    
    private SpriteBatch batch;
    private Texture background;
    private Texture textFont;
    private Texture abyssBorn;
    private BitmapFont font;
    private BitmapFont titleFont;
    private GlyphLayout layout;
    
    private float blinkTimer;
    private float blinkSpeed = 1.5f; // Vitesse du clignotement (en secondes)
    
    private TwoDGame game; // Référence vers votre classe principale du jeu
    
    public WinScreen(TwoDGame game) {
        this.game = game;
    }
    
    @Override
    public void show() {
        batch = new SpriteBatch();
        background = new Texture("gui/PNG/font_flou.png");
        textFont = new Texture("gui/PNG/Text_font.png");
        abyssBorn = new Texture("gui/PNG/AbyssBorn.png");
        font = new BitmapFont(); // Ou chargez votre propre font
        font.getData().setScale(3.0f); // 2 fois plus gros (1.5f * 2 = 3.0f)
        
        // Font pour le titre "You Win !!!"
        titleFont = new BitmapFont();
        titleFont.getData().setScale(5.0f); // Grand et gras
        titleFont.getData().markupEnabled = true;
        
        layout = new GlyphLayout();
        blinkTimer = 0;
    }
    
    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Update blink timer
        blinkTimer += delta;
        
        batch.begin();
        
        // Dessiner le fond (toute la fenêtre)
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        // Réduire Text_font de 1/3 (donc afficher à 2/3 de sa taille originale)
        float textFontWidth = textFont.getWidth() * 2f / 3f;
        float textFontHeight = textFont.getHeight() * 2f / 3f;
        float textFontX = (Gdx.graphics.getWidth() - textFontWidth) / 2f;
        float textFontY = Gdx.graphics.getHeight() * 0.7f - textFontHeight / 2f;
        batch.draw(textFont, textFontX, textFontY, textFontWidth, textFontHeight);
        
        // AbyssBorn à 2/3 de la taille de Text_font
        float abyssBornWidth = textFontWidth * 2f / 3f;
        float abyssBornHeight = textFontHeight * 2f / 3f;
        
        // Centrer AbyssBorn sur Text_font horizontalement
        float abyssBornX = textFontX + (textFontWidth - abyssBornWidth) / 2f;
        
        // Centrer AbyssBorn sur Text_font verticalement et ajouter 10px vers le haut
        float abyssBornY = textFontY + (textFontHeight - abyssBornHeight) / 2f + 10;
        
        batch.draw(abyssBorn, abyssBornX, abyssBornY, abyssBornWidth, abyssBornHeight);
        
        // Dessiner "You Win !!!" juste en dessous du titre
        titleFont.setColor(0, 1, 0, 1); // Vert pour la victoire
        String winText = "You Win !!!";
        layout.setText(titleFont, winText);
        float winTextX = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float winTextY = textFontY - 30; // Juste en dessous du titre
        titleFont.draw(batch, winText, winTextX, winTextY);
        
        // Calculer l'alpha pour le clignotement (oscillation sinusoïdale lente)
        float alpha = (float) Math.abs(Math.sin(blinkTimer * Math.PI / blinkSpeed));
        font.setColor(1, 1, 1, alpha);
        
        // Dessiner le texte "Click anywhere to go to the Menu !" à 30% de la hauteur
        String clickText = "Click anywhere to go to the Menu !";
        layout.setText(font, clickText);
        float textX = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float textY = Gdx.graphics.getHeight() * 0.3f;
        font.draw(batch, clickText, textX, textY);
        
        batch.end();
        
        // Détecter le clic sur toute la fenêtre
        if (Gdx.input.justTouched()) {
            // Passer au MenuScreen
            game.setScreen(new MenuScreen(game));
            dispose();
        }
    }
    
    @Override
    public void resize(int width, int height) {
        // Gérer le redimensionnement si nécessaire
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
        batch.dispose();
        background.dispose();
        textFont.dispose();
        abyssBorn.dispose();
        font.dispose();
        titleFont.dispose();
    }
}