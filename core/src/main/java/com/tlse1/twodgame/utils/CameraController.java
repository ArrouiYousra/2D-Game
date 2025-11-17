package com.tlse1.twodgame.utils;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Contrôleur de caméra pour suivre une entité (typiquement le joueur).
 * Supporte plusieurs modes de suivi avec interpolation fluide.
 */
public class CameraController {
    
    private OrthographicCamera camera;
    
    // Paramètres de suivi
    private float smoothness = 5.0f; // Fluidité du suivi (1-10)
    private Vector2 offset = new Vector2(0, 0); // Décalage par rapport à la cible
    
    // Limites de la carte (optionnel)
    private boolean useMapLimits = false;
    private float mapWidth = 0;
    private float mapHeight = 0;
    
    // Zone morte (deadzone) - optionnel
    private boolean useDeadzone = false;
    private float deadzoneWidth = 100f;
    private float deadzoneHeight = 100f;
    
    public CameraController(OrthographicCamera camera) {
        this.camera = camera;
    }
    
    /**
     * Met à jour la caméra pour suivre une position cible
     * @param targetX Position X de la cible
     * @param targetY Position Y de la cible
     * @param deltaTime Temps écoulé depuis la dernière frame
     */
    public void update(float targetX, float targetY, float deltaTime) {
        if (useDeadzone) {
            updateWithDeadzone(targetX, targetY, deltaTime);
        } else {
            updateSmooth(targetX, targetY, deltaTime);
        }
        
        if (useMapLimits) {
            applyMapLimits();
        }
        
        camera.update();
    }
    
    /**
     * Mise à jour fluide classique (lerp)
     */
    private void updateSmooth(float targetX, float targetY, float deltaTime) {
        float lerpFactor = MathUtils.clamp(smoothness * deltaTime, 0, 1);
        
        Vector3 cameraPosition = camera.position;
        cameraPosition.x = MathUtils.lerp(cameraPosition.x, targetX + offset.x, lerpFactor);
        cameraPosition.y = MathUtils.lerp(cameraPosition.y, targetY + offset.y, lerpFactor);
    }
    
    /**
     * Mise à jour avec zone morte (la caméra ne bouge que si la cible sort de la zone)
     */
    private void updateWithDeadzone(float targetX, float targetY, float deltaTime) {
        Vector3 cameraPosition = camera.position;
        
        // Calculer la distance entre la caméra et la cible
        float deltaX = (targetX + offset.x) - cameraPosition.x;
        float deltaY = (targetY + offset.y) - cameraPosition.y;
        
        // Vérifier si la cible est hors de la zone morte
        float halfDeadzoneWidth = deadzoneWidth * 0.5f;
        float halfDeadzoneHeight = deadzoneHeight * 0.5f;
        
        // Mouvement X
        if (Math.abs(deltaX) > halfDeadzoneWidth) {
            float targetCameraX = targetX + offset.x - Math.signum(deltaX) * halfDeadzoneWidth;
            float lerpFactor = MathUtils.clamp(smoothness * deltaTime, 0, 1);
            cameraPosition.x = MathUtils.lerp(cameraPosition.x, targetCameraX, lerpFactor);
        }
        
        // Mouvement Y
        if (Math.abs(deltaY) > halfDeadzoneHeight) {
            float targetCameraY = targetY + offset.y - Math.signum(deltaY) * halfDeadzoneHeight;
            float lerpFactor = MathUtils.clamp(smoothness * deltaTime, 0, 1);
            cameraPosition.y = MathUtils.lerp(cameraPosition.y, targetCameraY, lerpFactor);
        }
    }
    
    /**
     * Applique les limites de la carte pour empêcher la caméra de sortir
     */
    private void applyMapLimits() {
        float halfWidth = camera.viewportWidth * 0.5f * camera.zoom;
        float halfHeight = camera.viewportHeight * 0.5f * camera.zoom;
        
        camera.position.x = MathUtils.clamp(
            camera.position.x,
            halfWidth,
            mapWidth - halfWidth
        );
        
        camera.position.y = MathUtils.clamp(
            camera.position.y,
            halfHeight,
            mapHeight - halfHeight
        );
    }
    
    /**
     * Centre immédiatement la caméra sur une position (sans interpolation)
     */
    public void snapTo(float x, float y) {
        camera.position.set(x + offset.x, y + offset.y, 0);
        camera.update();
    }
    
    // ========== Getters et Setters ==========
    
    /**
     * Définir la fluidité du suivi
     * @param smoothness Valeur entre 1 (rapide/nerveux) et 10 (très fluide). Défaut: 5
     */
    public void setSmoothness(float smoothness) {
        this.smoothness = smoothness;
    }
    
    /**
     * Définir un décalage par rapport à la cible
     * Utile pour décaler la vue légèrement vers l'avant du mouvement
     */
    public void setOffset(float x, float y) {
        this.offset.set(x, y);
    }
    
    /**
     * Activer les limites de la carte
     */
    public void setMapLimits(float width, float height) {
        this.mapWidth = width;
        this.mapHeight = height;
        this.useMapLimits = true;
    }
    
    /**
     * Désactiver les limites de la carte
     */
    public void disableMapLimits() {
        this.useMapLimits = false;
    }
    
    /**
     * Activer la zone morte (deadzone)
     * La caméra ne bouge que si la cible sort de cette zone
     * @param width Largeur de la zone morte en pixels
     * @param height Hauteur de la zone morte en pixels
     */
    public void setDeadzone(float width, float height) {
        this.deadzoneWidth = width;
        this.deadzoneHeight = height;
        this.useDeadzone = true;
    }
    
    /**
     * Désactiver la zone morte
     */
    public void disableDeadzone() {
        this.useDeadzone = false;
    }
    
    public OrthographicCamera getCamera() {
        return camera;
    }
}
