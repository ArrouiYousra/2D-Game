package com.tlse1.twodgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tlse1.twodgame.TwoDGame;
import com.tlse1.twodgame.entities.Player;
import com.tlse1.twodgame.managers.RoomManager;
import com.tlse1.twodgame.rooms.Room;
import com.tlse1.twodgame.ui.HUD;
import com.tlse1.twodgame.utils.Difficulty;
import com.tlse1.twodgame.utils.RoomTransition;

/**
 * Écran de jeu principal.
 * Contient la logique du jeu avec le joueur, les salles et les ennemis.
 */
public class GameScreen implements Screen {
    
    private TwoDGame game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Player player;
    private RoomManager roomManager;
    private RoomTransition transition;
    private HUD hud;

    private static final int WINDOWED_WIDTH = 1280;
    private static final int WINDOWED_HEIGHT = 720;
    
    // Difficulté (par défaut MEDIUM, pourra être sélectionnée plus tard)
    private Difficulty difficulty = Difficulty.MEDIUM;
    
    // État de transition
    private boolean isTransitioning;
    
    public GameScreen(TwoDGame game) {
        this.game = game;
    }
    
    @Override
    public void show() {
        batch = new SpriteBatch();
        
        // Initialiser la caméra
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        camera.update();
        
        // Créer le joueur
        float startX = 320f;
        float startY = 240f;
        float speed = 150f;
        int maxHealth = 100;
        
        player = new Player(startX, startY, speed, maxHealth);
        player.setCamera(camera); // Passer la caméra au joueur pour le calcul de direction
        
        // Créer le gestionnaire de salles
        roomManager = new RoomManager(difficulty, player);
        roomManager.initialize();
        
        // Créer le système de transition
        transition = new RoomTransition();
        isTransitioning = false;
        
        // Créer le HUD
        hud = new HUD(player);
    }
    
    @Override
    public void render(float delta) {
        // Gérer retour au menu (touche Échap)
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            return;
        }
        
        // Si on est en transition, gérer la transition
        if (isTransitioning) {
            boolean transitionFinished = transition.update(delta);
            if (transitionFinished) {
                // Transition terminée, changer de salle
                changeRoom();
                isTransitioning = false;
                transition.reset();
            }
        } else {
            // Mettre à jour le jeu normalement
            updateGame(delta);
        }
        
        // Mettre à jour le HUD
        if (hud != null) {
            hud.update();
        }
        
        // Rendu
        renderGame();
        
        // Dessiner le HUD par-dessus le jeu
        if (hud != null && !isTransitioning) {
            batch.begin();
            hud.render(batch);
            batch.end();
        }
        
        // Dessiner la transition par-dessus si nécessaire
        if (isTransitioning || transition.isTransitioning()) {
            transition.render();
        }
    }
    
    /**
     * Met à jour la logique du jeu
     */
    private void updateGame(float delta) {
        // Mettre à jour la salle actuelle (pour les animations)
        roomManager.update(delta);
        
        // Ne pas mettre à jour le joueur pendant la transition
        if (!isTransitioning) {
            // Mettre à jour le joueur
            player.update(delta);
            
            // Vérifier les collisions avec les portes
            Room currentRoom = roomManager.getCurrentRoom();
            if (currentRoom != null) {
                // Vérifier collision avec la porte de sortie
                if (currentRoom.playerCollidesWithExitDoor(player)) {
                    if (!isTransitioning) {
                        startRoomTransition();
                    }
                }
                
                // Limiter le joueur dans les bounds de la salle
                player.clampToBounds(currentRoom.getWidth(), currentRoom.getHeight());
            } else {
                player.clampToBounds(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            }
        }
    }
    
    /**
     * Démarre une transition entre salles
     */
    private void startRoomTransition() {
        isTransitioning = true;
        transition.startTransition();
    }
    
    /**
     * Change de salle après la transition
     */
    private void changeRoom() {
        if (roomManager.isLastRoom()) {
            // Dernière salle - aller vers la salle de victoire (à implémenter plus tard)
            // Pour l'instant, retourner au menu
            game.setScreen(new MenuScreen(game));
            return;
        }
        
        // Passer à la salle suivante
        boolean hasNextRoom = roomManager.nextRoom();
        if (!hasNextRoom) {
            // Plus de salles, retourner au menu
            game.setScreen(new MenuScreen(game));
            return;
        }
        
        // Téléporter le joueur à la position de spawn de la nouvelle salle
        Room newRoom = roomManager.getCurrentRoom();
        if (newRoom != null) {
            player.setX(newRoom.getPlayerSpawnX());
            player.setY(newRoom.getPlayerSpawnY());
        }
    }
    
    /**
     * Dessine tous les éléments du jeu
     */
    private void renderGame() {
        // Nettoyer l'écran
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Mettre à jour la caméra
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        // Dessiner la salle (portes, ennemis, obstacles)
        batch.begin();
        roomManager.render(batch);
        
        // Dessiner le joueur
        player.render(batch);
        
        batch.end();
    }
    
    @Override
    public void resize(int width, int height) {
        // Mettre à jour la caméra
        camera.setToOrtho(false, WINDOWED_WIDTH, WINDOWED_HEIGHT);
        camera.update();
        
        // Mettre à jour le HUD
        if (hud != null) {
            hud.resize(width, height);
        }
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
        
        if (roomManager != null) {
            roomManager.dispose();
        }
        
        if (transition != null) {
            transition.dispose();
        }
        
        if (hud != null) {
            hud.dispose();
        }
        
        if (batch != null) {
            batch.dispose();
        }
    }
    
    /**
     * Définit la difficulté du jeu
     */
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}
