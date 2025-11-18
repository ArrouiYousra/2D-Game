package com.tlse1.twodgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tlse1.twodgame.TwoDGame; // ✅ Correction ici

public class AnimatedSplashScreen implements Screen {
    
    private final TwoDGame game; // ✅ Correction ici
    private SpriteBatch batch;
    private Texture splashTexture;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    
    private float time = 0f;
    private static final float SPLASH_DURATION = 5f;
    
    private float alpha = 0f;
    private static final float FADE_IN_TIME = 1f;
    private static final float FADE_OUT_TIME = 1f;
    
    // Particules
    private Array<Particle> particles;
    private static final int PARTICLE_COUNT = 50;
    
    public AnimatedSplashScreen(TwoDGame game) { // ✅ Correction ici
        this.game = game;
    }
    
    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        splashTexture = new Texture(Gdx.files.internal("abyssborn_splash.png"));
        
        viewport = new FitViewport(1920, 1080);
        viewport.apply();
        
        // Initialiser les particules
        particles = new Array<>();
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            particles.add(new Particle(viewport.getWorldWidth(), viewport.getWorldHeight()));
        }
    }
    
    @Override
    public void render(float delta) {
        time += delta;
        
        // Gérer le fade in/out
        if (time < FADE_IN_TIME) {
            alpha = time / FADE_IN_TIME;
        } else if (time > SPLASH_DURATION - FADE_OUT_TIME) {
            alpha = (SPLASH_DURATION - time) / FADE_OUT_TIME;
        } else {
            alpha = 1f;
        }
        
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        viewport.apply();
        
        // Dessiner l'image de fond
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.setColor(1, 1, 1, alpha);
        batch.draw(splashTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();
        
        // Dessiner les particules par-dessus
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        for (Particle p : particles) {
            p.update(delta, viewport.getWorldHeight());
            shapeRenderer.setColor(0.6f, 0.4f, 0.8f, p.alpha * alpha);
            shapeRenderer.circle(p.x, p.y, p.size);
        }
        
        shapeRenderer.end();
        
        // Ajouter un effet de glow pulsant au texte
        float glowIntensity = (float)(Math.sin(time * 2) * 0.3 + 0.7);
        batch.begin();
        batch.setColor(0.6f, 0.4f, 1f, glowIntensity * alpha * 0.3f);
        batch.draw(splashTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();
        
        // Passer à l'écran suivant - UTILISEZ VOTRE ÉCRAN DE MENU EXISTANT
        if (time >= SPLASH_DURATION) {
            // ✅ Remplacez par le nom de votre écran de menu existant
            // Par exemple: game.setScreen(new MenuScreen(game));
            // Ou: game.setScreen(new GameScreen(game));
            // Pour l'instant, on ne fait rien pour éviter l'erreur
            // game.setScreen(new YourMenuScreen(game));
            dispose();
        }
    }
    
    // Classe interne pour les particules
    private static class Particle {
        float x, y;
        float speed;
        float size;
        float alpha;
        float worldWidth;
        
        Particle(float worldWidth, float worldHeight) {
            this.worldWidth = worldWidth;
            reset(worldHeight);
        }
        
        void reset(float worldHeight) {
            x = MathUtils.random(0, worldWidth);
            y = MathUtils.random(-50, 0);
            speed = MathUtils.random(30, 80);
            size = MathUtils.random(2, 6);
            alpha = MathUtils.random(0.3f, 0.7f);
        }
        
        void update(float delta, float worldHeight) {
            y += speed * delta;
            
            if (y > worldHeight + 50) {
                reset(worldHeight);
            }
        }
    }
    
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
    
    @Override
    public void pause() {}
    
    @Override
    public void resume() {}
    
    @Override
    public void hide() {}
    
    @Override
    public void dispose() {
        batch.dispose();
        splashTexture.dispose();
        shapeRenderer.dispose();
    }
}