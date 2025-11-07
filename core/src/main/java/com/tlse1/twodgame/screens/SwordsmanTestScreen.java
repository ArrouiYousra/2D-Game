package com.tlse1.twodgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.tlse1.twodgame.TwoDGame;
import com.tlse1.twodgame.utils.SwordsmanMapping;

/**
 * Écran de test pour afficher les sprites du Swordsman.
 * Temporaire pour tester le chargement et le mouvement.
 */
public class SwordsmanTestScreen implements Screen {
    
    private TwoDGame game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private SwordsmanMapping swordsmanMapping;
    
    // Animations pour chaque direction
    private Animation<TextureRegion>[] animations; // 4 directions
    private int currentDirection; // 0=bas, 1=gauche, 2=droite, 3=haut
    private float stateTime;
    
    // Position du personnage
    private float playerX;
    private float playerY;
    private float speed = 200f; // pixels par seconde
    
    // Dimensions de l'écran
    private float screenWidth;
    private float screenHeight;
    
    // Scale pour agrandir le personnage
    private float scale = 8f;
    
    public SwordsmanTestScreen(TwoDGame game) {
        this.game = game;
        
        // Initialiser la caméra
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(screenWidth, screenHeight);
        camera.setToOrtho(false, screenWidth, screenHeight);
        camera.update();
        
        // Initialiser le SpriteBatch
        batch = new SpriteBatch();
        
        // Charger le mapping
        swordsmanMapping = new SwordsmanMapping();
        
        // Créer les animations pour les 4 directions
        animations = new Animation[4];
        for (int dir = 0; dir < 4; dir++) {
            TextureRegion[] frames = swordsmanMapping.getIdleFramesForDirection(dir);
            if (frames != null && frames.length > 0) {
                // Filtrer les frames null
                Array<TextureRegion> validFrames = new Array<>();
                for (TextureRegion frame : frames) {
                    if (frame != null) {
                        validFrames.add(frame);
                    }
                }
                
                if (validFrames.size > 0) {
                    animations[dir] = new Animation<>(0.12f, validFrames);
                    Gdx.app.log("SwordsmanTestScreen", "Animation direction " + dir + " créée avec " + validFrames.size + " frames");
                }
            }
        }
        
        // Position initiale au centre
        playerX = screenWidth / 2f;
        playerY = screenHeight / 2f;
        
        // Direction initiale (bas)
        currentDirection = 0;
        stateTime = 0f;
    }
    
    @Override
    public void show() {
        Gdx.app.log("SwordsmanTestScreen", "Écran de test affiché");
    }
    
    @Override
    public void render(float delta) {
        // Gérer l'input pour le mouvement
        handleInput(delta);
        
        // Mettre à jour le temps d'animation
        stateTime += delta;
        
        // Nettoyer l'écran
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Mettre à jour la caméra
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        // Dessiner
        batch.begin();
        
        if (animations[currentDirection] != null) {
            TextureRegion currentFrame = animations[currentDirection].getKeyFrame(stateTime, true);
            if (currentFrame != null) {
                // Calculer les dimensions
                float frameWidth = currentFrame.getRegionWidth() * scale;
                float frameHeight = currentFrame.getRegionHeight() * scale;
                
                // Centrer le personnage sur sa position (le point de référence est en bas au centre)
                float x = playerX - frameWidth / 2f;
                float y = playerY;
                
                batch.draw(currentFrame, x, y, frameWidth, frameHeight);
            }
        }
        
        batch.end();
    }
    
    /**
     * Gère l'input pour déplacer le personnage
     */
    private void handleInput(float delta) {
        int newDirection = currentDirection;
        
        // Détecter les touches pressées
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            playerY += speed * delta;
            newDirection = 3; // Haut
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            playerY -= speed * delta;
            newDirection = 0; // Bas
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerX -= speed * delta;
            newDirection = 1; // Gauche
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerX += speed * delta;
            newDirection = 2; // Droite
        }
        
        // Si la direction a changé, réinitialiser le temps d'animation
        if (newDirection != currentDirection) {
            currentDirection = newDirection;
            stateTime = 0f; // Redémarrer l'animation
        }
        
        // Limiter le personnage dans l'écran
        float frameWidth = 20 * scale; // Approximation
        float frameHeight = 27 * scale; // Approximation
        
        if (playerX < frameWidth / 2f) {
            playerX = frameWidth / 2f;
        }
        if (playerX > screenWidth - frameWidth / 2f) {
            playerX = screenWidth - frameWidth / 2f;
        }
        if (playerY < 0) {
            playerY = 0;
        }
        if (playerY > screenHeight - frameHeight) {
            playerY = screenHeight - frameHeight;
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
        if (swordsmanMapping != null) {
            swordsmanMapping.dispose();
        }
    }
}

