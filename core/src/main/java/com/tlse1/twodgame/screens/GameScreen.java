package com.tlse1.twodgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tlse1.twodgame.TwoDGame;
import com.tlse1.twodgame.entities.Enemy;
import com.tlse1.twodgame.entities.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Écran de jeu principal.
 * Contient la logique du jeu avec le joueur et les ennemis.
 */
public class GameScreen implements Screen {
    
    private TwoDGame game;
    private SpriteBatch batch;
    private Player player;
    private List<Enemy> enemies;
    
    public GameScreen(TwoDGame game) {
        this.game = game;
    }
    
    @Override
    public void show() {
        batch = new SpriteBatch();
        
        // Créer le joueur au centre de l'écran
        float startX = 320f;
        float startY = 240f;
        float speed = 150f;
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
        
        enemies.add(new Enemy(100f, 100f, enemySpeed, enemyHealth, player));
        enemies.add(new Enemy(540f, 100f, enemySpeed, enemyHealth, player));
        enemies.add(new Enemy(320f, 380f, enemySpeed, enemyHealth, player));
    }
    
    @Override
    public void render(float delta) {
        // Gérer retour au menu (touche Échap)
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            return;
        }
        
        // Mettre à jour le joueur
        player.update(delta);
        
        // Mettre à jour tous les ennemis
        for (Enemy enemy : enemies) {
            if (enemy.isActive() && enemy.isAlive()) {
                enemy.update(delta);
            }
        }
        
        // Nettoyer l'écran
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
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
    public void resize(int width, int height) {
        // Gérer le redimensionnement si nécessaire
    }
    
    @Override
    public void pause() {
        // Mettre en pause le jeu si nécessaire
    }
    
    @Override
    public void resume() {
        // Reprendre le jeu si nécessaire
    }
    
    @Override
    public void hide() {
        // Libérer les ressources quand on quitte l'écran
        dispose();
    }
    
    @Override
    public void dispose() {
        // Libérer les ressources
        if (player != null) {
            player.dispose();
        }
        
        if (enemies != null) {
            for (Enemy enemy : enemies) {
                if (enemy != null) {
                    enemy.dispose();
                }
            }
            enemies.clear();
        }
        
        if (batch != null) {
            batch.dispose();
        }
    }
}

