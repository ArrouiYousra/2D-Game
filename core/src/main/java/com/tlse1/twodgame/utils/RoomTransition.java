package com.tlse1.twodgame.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Gère les transitions entre les salles (écran noir).
 */
public class RoomTransition {
    
    private boolean isTransitioning;
    private float transitionTimer;
    private static final float TRANSITION_DURATION = 1.0f; // 1 seconde
    private ShapeRenderer shapeRenderer;
    
    public RoomTransition() {
        this.isTransitioning = false;
        this.transitionTimer = 0f;
        this.shapeRenderer = new ShapeRenderer();
    }
    
    /**
     * Démarre une transition
     */
    public void startTransition() {
        isTransitioning = true;
        transitionTimer = 0f;
    }
    
    /**
     * Met à jour la transition
     * @param delta Temps écoulé depuis la dernière frame
     * @return true si la transition est terminée
     */
    public boolean update(float delta) {
        if (!isTransitioning) {
            return false;
        }
        
        transitionTimer += delta;
        
        if (transitionTimer >= TRANSITION_DURATION) {
            isTransitioning = false;
            transitionTimer = TRANSITION_DURATION;
            return true; // Transition terminée
        }
        
        return false;
    }
    
    /**
     * Dessine l'écran noir de transition
     */
    public void render() {
        if (!isTransitioning && transitionTimer <= 0f) {
            return; // Pas de transition en cours
        }
        
        // Calculer l'opacité (0 à 1)
        float alpha = 1.0f;
        if (transitionTimer < TRANSITION_DURATION / 3) {
            // Fade in (0 à 1) - premier tiers
            alpha = transitionTimer / (TRANSITION_DURATION / 3);
        } else if (transitionTimer < TRANSITION_DURATION * 2 / 3) {
            // Écran complètement noir - deuxième tiers
            alpha = 1.0f;
        } else {
            // Fade out (1 à 0) - dernier tiers
            float fadeOutTime = transitionTimer - (TRANSITION_DURATION * 2 / 3);
            float fadeOutDuration = TRANSITION_DURATION / 3;
            alpha = 1.0f - (fadeOutTime / fadeOutDuration);
        }
        
        // Dessiner un rectangle noir qui couvre tout l'écran
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, Math.max(0f, Math.min(1f, alpha)));
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
    
    /**
     * Réinitialise la transition
     */
    public void reset() {
        isTransitioning = false;
        transitionTimer = 0f;
    }
    
    /**
     * Vérifie si une transition est en cours
     */
    public boolean isTransitioning() {
        return isTransitioning || transitionTimer > 0f;
    }
    
    /**
     * Libère les ressources
     */
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
