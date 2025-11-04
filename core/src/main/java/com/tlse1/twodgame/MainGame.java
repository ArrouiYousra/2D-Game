package com.tlse1.twodgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tlse1.twodgame.entities.Enemy;
import com.tlse1.twodgame.entities.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe principale du jeu.
 * Utilise la nouvelle architecture avec Entity, AnimatedEntity, Character et Player.
 */
public class MainGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private Player player;
    private List<Enemy> enemies;
    
    @Override
    public void create() {
        batch = new SpriteBatch();
        
        // Créer le joueur au centre de l'écran
        // Position initiale : centre de l'écran (640x480 / 2 = 320x240)
        float startX = 320f;
        float startY = 240f;
        float speed = 150f; // pixels par seconde
        int maxHealth = 100;
        
        player = new Player(startX, startY, speed, maxHealth);
        
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
        
        // Nettoyer l'écran
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1); // fond gris foncé
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
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
}
