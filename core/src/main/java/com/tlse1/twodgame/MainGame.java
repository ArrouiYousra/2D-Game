package com.tlse1.twodgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.tlse1.twodgame.entities.Enemy;
import com.tlse1.twodgame.entities.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe principale du jeu.
 * Utilise la nouvelle architecture avec Entity, AnimatedEntity, Character et Player.
 * Version avec caméra qui suit le joueur.
 */
public class MainGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private Player player;
    private List<Enemy> enemies;
    
    // Caméra
    private OrthographicCamera camera;
    private float cameraSmoothness = 5.0f; // Plus la valeur est élevée, plus le suivi est fluide
    
    // Limites de la carte (optionnel)
    private float mapWidth = 2000f;
    private float mapHeight = 2000f;
    private boolean useMapLimits = true; // Activer/désactiver les limites
    
    @Override
    public void create() {
        batch = new SpriteBatch();
        
        // Créer et configurer la caméra
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        // Créer le joueur au centre de l'écran
        // Position initiale : centre de l'écran (640x480 / 2 = 320x240)
        float startX = 320f;
        float startY = 240f;
        float speed = 150f; // pixels par seconde
        int maxHealth = 100;
        
        player = new Player(startX, startY, speed, maxHealth);
        
        // Centrer immédiatement la caméra sur le joueur
        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();
        
        // Créer des ennemis
        enemies = new ArrayList<>();
        createEnemies();
    }
    
    /**
     * Crée des ennemis à différentes positions sur l'écran
     */
    private void createEnemies() {
        float enemySpeed = 80f;
        int enemyHealth = 50;
        
        // Créer 3 ennemis à différentes positions
        enemies.add(new Enemy(100f, 100f, enemySpeed, enemyHealth, player));
        enemies.add(new Enemy(540f, 100f, enemySpeed, enemyHealth, player));
        enemies.add(new Enemy(320f, 380f, enemySpeed, enemyHealth, player));
    }
    
    /**
     * Met à jour la position de la caméra pour suivre le joueur de manière fluide
     */
    private void updateCamera(float deltaTime) {
        // Position cible : le joueur
        float targetX = player.getX();
        float targetY = player.getY();
        
        // Interpolation linéaire pour un mouvement fluide
        float lerpFactor = MathUtils.clamp(cameraSmoothness * deltaTime, 0, 1);
        
        Vector3 cameraPosition = camera.position;
        cameraPosition.x = MathUtils.lerp(cameraPosition.x, targetX, lerpFactor);
        cameraPosition.y = MathUtils.lerp(cameraPosition.y, targetY, lerpFactor);
        
        // Appliquer les limites de la carte si activées
        if (useMapLimits) {
            applyMapLimits();
        }
        
        // Mettre à jour la caméra
        camera.update();
    }
    
    /**
     * Empêche la caméra de sortir des limites de la carte
     */
    private void applyMapLimits() {
        float halfWidth = camera.viewportWidth * 0.5f * camera.zoom;
        float halfHeight = camera.viewportHeight * 0.5f * camera.zoom;
        
        // Limiter X
        camera.position.x = MathUtils.clamp(
            camera.position.x,
            halfWidth,
            mapWidth - halfWidth
        );
        
        // Limiter Y
        camera.position.y = MathUtils.clamp(
            camera.position.y,
            halfHeight,
            mapHeight - halfHeight
        );
    }
    
    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        
        // Mettre à jour le joueur (gère l'input, le mouvement et les animations)
        player.update(deltaTime);
        
        // Mettre à jour tous les ennemis
        for (Enemy enemy : enemies) {
            if (enemy.isActive() && enemy.isAlive()) {
                enemy.update(deltaTime);
            }
        }
        
        // Mettre à jour la caméra pour suivre le joueur
        updateCamera(deltaTime);
        
        // Nettoyer l'écran
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1); // fond gris foncé
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Appliquer la matrice de projection de la caméra au SpriteBatch
        batch.setProjectionMatrix(camera.combined);
        
        // Dessiner le joueur et les ennemis
        batch.begin();
        player.render(batch);
        
        for (Enemy enemy : enemies) {
            if (enemy.isActive() && enemy.isAlive()) {
                enemy.render(batch);
            }
        }
        
        batch.end();
    }
    
    @Override
    public void resize(int width, int height) {
        // Adapter la caméra lors du redimensionnement de la fenêtre
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }
    
    @Override
    public void dispose() {
        // Libérer les ressources du joueur
        if (player != null) {
            player.dispose();
        }
        
        // Libérer les ressources des ennemis
        if (enemies != null) {
            for (Enemy enemy : enemies) {
                if (enemy != null) {
                    enemy.dispose();
                }
            }
            enemies.clear();
        }
        
        // Libérer le SpriteBatch
        if (batch != null) {
            batch.dispose();
        }
    }
    
    // ========== Méthodes utilitaires pour configurer la caméra ==========
    
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
     * Récupérer la caméra (utile pour d'autres classes)
     * @return La caméra orthographique
     */
    public OrthographicCamera getCamera() {
        return camera;
    }
}