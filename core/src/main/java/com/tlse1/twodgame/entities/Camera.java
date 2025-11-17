package com.tlse1.twodgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.tlse1.twodgame.entities.Player;

/**
 * Classe Camera qui hérite de Player pour gérer la caméra du jeu.
 * Cette classe encapsule une OrthographicCamera et gère son suivi du joueur.
 */
public class Camera extends Player {
    private OrthographicCamera orthographicCamera;
    private Player target; // Le joueur à suivre
    private float cameraSmoothness;
    
    // Limites de la carte
    private float mapWidth;
    private float mapHeight;
    private boolean useMapLimits;
    
    /**
     * Constructeur de la Camera
     * @param viewportWidth Largeur du viewport
     * @param viewportHeight Hauteur du viewport
     */
    public Camera(float viewportWidth, float viewportHeight) {
        super(0, 0, 0, 100); // Appel du constructeur parent avec des valeurs par défaut
        
        // Créer et configurer la caméra orthographique
        orthographicCamera = new OrthographicCamera();
        orthographicCamera.setToOrtho(false, viewportWidth, viewportHeight);
        
        // Configuration par défaut
        this.cameraSmoothness = 5.0f;
        this.mapWidth = 2000f;
        this.mapHeight = 2000f;
        this.useMapLimits = true;
    }
    
    /**
     * Définir la cible que la caméra doit suivre
     * @param target Le joueur à suivre
     */
    public void setTarget(Player target) {
        this.target = target;
        
        // Centrer immédiatement la caméra sur la cible
        if (target != null) {
            orthographicCamera.position.set(target.getX(), target.getY(), 0);
            orthographicCamera.update();
        }
    }
    
    /**
     * Met à jour la position de la caméra pour suivre le joueur
     * @param deltaTime Le temps écoulé depuis la dernière frame
     */
    @Override
    public void update(float deltaTime) {
        // Gestion des inputs ZQSD pour déplacer manuellement la caméra (optionnel)
        // Décommentez la ligne suivante si vous voulez contrôler la caméra avec ZQSD
        // handleCameraInput(deltaTime);
        
        // Suivre la cible si elle existe
        if (target != null) {
            updateCameraPosition(deltaTime);
        }
        
        // Appliquer les limites de la carte si activées
        if (useMapLimits) {
            applyMapLimits();
        }
        
        // Mettre à jour la caméra
        orthographicCamera.update();
    }
    
    /**
     * Gestion des inputs ZQSD pour déplacer manuellement la caméra
     * Cette méthode est optionnelle et peut être activée en décommentant l'appel dans update()
     */
    private void handleCameraInput(float deltaTime) {
        float cameraSpeed = 200f * deltaTime;
        
        // Z - Haut
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            orthographicCamera.position.y += cameraSpeed;
        }
        // S - Bas
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            orthographicCamera.position.y -= cameraSpeed;
        }
        // Q - Gauche
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            orthographicCamera.position.x -= cameraSpeed;
        }
        // D - Droite
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            orthographicCamera.position.x += cameraSpeed;
        }
    }
    
    /**
     * Met à jour la position de la caméra pour suivre la cible de manière fluide
     */
    private void updateCameraPosition(float deltaTime) {
        // Position cible : le joueur
        float targetX = target.getX();
        float targetY = target.getY();
        
        // Interpolation linéaire pour un mouvement fluide
        float lerpFactor = MathUtils.clamp(cameraSmoothness * deltaTime, 0, 1);
        
        Vector3 cameraPosition = orthographicCamera.position;
        cameraPosition.x = MathUtils.lerp(cameraPosition.x, targetX, lerpFactor);
        cameraPosition.y = MathUtils.lerp(cameraPosition.y, targetY, lerpFactor);
    }
    
    /**
     * Empêche la caméra de sortir des limites de la carte
     */
    private void applyMapLimits() {
        float halfWidth = orthographicCamera.viewportWidth * 0.5f * orthographicCamera.zoom;
        float halfHeight = orthographicCamera.viewportHeight * 0.5f * orthographicCamera.zoom;
        
        // Limiter X
        orthographicCamera.position.x = MathUtils.clamp(
            orthographicCamera.position.x,
            halfWidth,
            mapWidth - halfWidth
        );
        
        // Limiter Y
        orthographicCamera.position.y = MathUtils.clamp(
            orthographicCamera.position.y,
            halfHeight,
            mapHeight - halfHeight
        );
    }
    
    /**
     * Ajuster le viewport lors du redimensionnement de la fenêtre
     */
    public void resize(int width, int height) {
        orthographicCamera.viewportWidth = width;
        orthographicCamera.viewportHeight = height;
        orthographicCamera.update();
    }
    
    // ========== Getters et Setters ==========
    
    /**
     * Récupérer la caméra orthographique interne
     */
    public OrthographicCamera getOrthographicCamera() {
        return orthographicCamera;
    }
    
    /**
     * Définir la fluidité du suivi de la caméra
     * @param smoothness Valeur entre 1 (rapide) et 10 (très fluide). Par défaut: 5
     */
    public void setCameraSmoothness(float smoothness) {
        this.cameraSmoothness = smoothness;
    }
    
    /**
     * Définir les limites de la carte
     * @param width Largeur de la carte
     * @param height Hauteur de la carte
     */
    public void setMapLimits(float width, float height) {
        this.mapWidth = width;
        this.mapHeight = height;
    }
    
    /**
     * Activer ou désactiver les limites de la carte
     * @param enabled true pour activer, false pour désactiver
     */
    public void setMapLimitsEnabled(boolean enabled) {
        this.useMapLimits = enabled;
    }
    
    /**
     * Obtenir la fluidité actuelle de la caméra
     */
    public float getCameraSmoothness() {
        return cameraSmoothness;
    }
    
    /**
     * Vérifier si les limites de la carte sont activées
     */
    public boolean isMapLimitsEnabled() {
        return useMapLimits;
    }
    
    @Override
    public void dispose() {
        // Pas besoin de dispose pour OrthographicCamera
        super.dispose();
    }
}