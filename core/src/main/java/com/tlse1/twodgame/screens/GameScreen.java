package com.tlse1.twodgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tlse1.twodgame.TwoDGame;
import com.tlse1.twodgame.entities.Collectible;
import com.tlse1.twodgame.entities.Enemy;
import com.tlse1.twodgame.entities.Inventory;
import com.tlse1.twodgame.entities.Player;
import com.tlse1.twodgame.entities.Slime;
import com.tlse1.twodgame.entities.Vampire;
import java.util.ArrayList;
import com.tlse1.twodgame.entities.handlers.CollisionHandler;
import com.tlse1.twodgame.managers.JsonMapLoader;
import com.tlse1.twodgame.ui.HealthBar;
import com.tlse1.twodgame.ui.ShieldBar;
import com.tlse1.twodgame.utils.ActionPanelMapping;
import com.tlse1.twodgame.utils.CharacterPanelMapping;
import com.tlse1.twodgame.utils.Direction;

/**
 * Écran de jeu principal.
 * Affiche le personnage au centre de l'écran.
 */
public class GameScreen implements Screen {
    
    private TwoDGame game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private OrthographicCamera uiCamera;
    private Viewport viewport;
    private Player player;
    private Enemy enemy;
    private ArrayList<Enemy> enemies;
    private ArrayList<Collectible> collectibles;
    
    private ArrayList<PendingSlimeRespawn> pendingSlimeRespawns;
    
    private JsonMapLoader mapLoader;
    private CharacterPanelMapping characterPanelMapping;
    private ActionPanelMapping actionPanelMapping;
    private HealthBar healthBar;
    private ShieldBar shieldBar;
    private BitmapFont font;
    
    private float playerAttackRange = 100f;
    private float playerAttackCooldown = 0f;
    private float playerAttackCooldownTime = 0.5f;
    
    private boolean playerDeathLogged = false;
    private boolean enemyDeathLooted = false;
    private boolean collisionsInitialized = false;
    private boolean cameraInitialized = false;
    private boolean isPaused = false;
    private boolean gameOver = false;
    
    private float lastPlayerX = -1f;
    private float lastPlayerY = -1f;
    private float gameTime = 0f;
    
    private int totalKills = 0;
    private boolean level3VampireKilled = false;
    
    private boolean isInitialized = false;
    
    private static class PendingSlimeRespawn {
        float deathTime;
        int zoneId;
        int level;
        float initialX;
        float initialY;
        int respawnCount;
        
        PendingSlimeRespawn(float deathTime, int zoneId, int level, float initialX, float initialY, int respawnCount) {
            this.deathTime = deathTime;
            this.zoneId = zoneId;
            this.level = level;
            this.initialX = initialX;
            this.initialY = initialY;
            this.respawnCount = respawnCount;
        }
    }
    
    public GameScreen(TwoDGame game) {
        this.game = game;
    }
    
    @Override
    public void show() {
        // Ne s'initialiser qu'une seule fois
        if (isInitialized) {
            return;
        }
        
        batch = new SpriteBatch();
        
        float mapViewWidth = 180f;
        float mapViewHeight = 140f;
        
        camera = new OrthographicCamera();
        viewport = new StretchViewport(mapViewWidth, mapViewHeight, camera);
        viewport.apply();
        camera.update();
        
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiCamera.update();
        
        mapLoader = new JsonMapLoader("map/map.json");
        
        player = new Player(0, 0);
        
        float playerStartX = 32f;
        float playerStartY = 50f;
        player.setX(playerStartX);
        player.setY(playerStartY);
        
        enemies = new ArrayList<>();
        pendingSlimeRespawns = new ArrayList<>();
        collectibles = new ArrayList<>();
        
        // Spawn des slimes
        float[] zone1Center = mapLoader.getZoneCenter(1);
        if (zone1Center != null) {
            Slime slime1 = new Slime(zone1Center[0], zone1Center[1], 1);
            slime1.setTarget(player);
            slime1.setMapLoader(mapLoader);
            slime1.setZoneId(1);
            slime1.setInitialPosition(zone1Center[0], zone1Center[1]);
            enemies.add(slime1);
        }
        
        float[] zone2Center = mapLoader.getZoneCenter(2);
        if (zone2Center != null) {
            Slime slime2 = new Slime(zone2Center[0], zone2Center[1], 2);
            slime2.setTarget(player);
            slime2.setMapLoader(mapLoader);
            slime2.setZoneId(2);
            slime2.setInitialPosition(zone2Center[0], zone2Center[1]);
            enemies.add(slime2);
        }
        
        float[] zone3Center = mapLoader.getZoneCenter(3);
        if (zone3Center != null) {
            Slime slime3 = new Slime(zone3Center[0], zone3Center[1], 3);
            slime3.setTarget(player);
            slime3.setMapLoader(mapLoader);
            slime3.setZoneId(3);
            slime3.setInitialPosition(zone3Center[0], zone3Center[1]);
            enemies.add(slime3);
        }
        
        float[] zone4Center = mapLoader.getZoneCenter(4);
        if (zone4Center != null) {
            Vampire vampire1 = new Vampire(zone4Center[0], zone4Center[1], 1);
            vampire1.setTarget(player);
            vampire1.setMapLoader(mapLoader);
            vampire1.setZoneId(4);
            vampire1.setInitialPosition(zone4Center[0], zone4Center[1]);
            enemies.add(vampire1);
        }
        
        float[] zone5Center = mapLoader.getZoneCenter(5);
        if (zone5Center != null) {
            Vampire vampire2 = new Vampire(zone5Center[0], zone5Center[1], 2);
            vampire2.setTarget(player);
            vampire2.setMapLoader(mapLoader);
            vampire2.setZoneId(5);
            vampire2.setInitialPosition(zone5Center[0], zone5Center[1]);
            enemies.add(vampire2);
        }
        
        float[] zone6Center = mapLoader.getZoneCenter(6);
        if (zone6Center != null) {
            Vampire vampire3 = new Vampire(zone6Center[0], zone6Center[1], 3);
            vampire3.setTarget(player);
            vampire3.setMapLoader(mapLoader);
            vampire3.setZoneId(6);
            vampire3.setInitialPosition(zone6Center[0], zone6Center[1]);
            enemies.add(vampire3);
        }
        
        characterPanelMapping = new CharacterPanelMapping();
        actionPanelMapping = new ActionPanelMapping();
        
        float healthBarScale = 4f;
        float healthBarX = 10f;
        float healthBarY = Gdx.graphics.getHeight() - (30f * healthBarScale) - 10f;
        healthBar = new HealthBar(healthBarX, healthBarY, healthBarScale, characterPanelMapping);
        shieldBar = new ShieldBar(healthBarX, healthBarY, healthBarScale, characterPanelMapping);
        
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        
        isInitialized = true;
    }
    
    public void pause() {
        isPaused = true;
    }
    
    public void resume() {
        isPaused = false;
        // Forcer la mise à jour de la caméra lors de la reprise
        if (player != null && player.getWidth() > 0 && player.getHeight() > 0) {
            float playerCenterX = player.getX() + player.getWidth() / 2f;
            float playerCenterY = player.getY() + player.getHeight() / 2f;
            
            float cameraMinX = 90f;
            float cameraMaxX = 710f;
            float cameraMinY = 70f;
            float cameraMaxY = 570f;
            
            float clampedCameraX = Math.max(cameraMinX, Math.min(cameraMaxX, playerCenterX));
            float clampedCameraY = Math.max(cameraMinY, Math.min(cameraMaxY, playerCenterY));
            
            camera.position.set(clampedCameraX, clampedCameraY, 0);
            camera.update();
            
            lastPlayerX = player.getX();
            lastPlayerY = player.getY();
        }
    }
    
    public boolean isPaused() {
        return isPaused;
    }
    
    @Override
    public void render(float delta) {
        // Gérer la touche ÉCHAP pour ouvrir les paramètres
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !isPaused && !gameOver) {
            pause();
            game.setScreen(new GameSettingsScreen(game, this));
            return;
        }
        
        // Ne pas mettre à jour la logique du jeu si en pause ou game over
        if (!isPaused && !gameOver) {
            // Mettre à jour le temps de jeu
            gameTime += delta;
            
            // Gérer l'input et le mouvement
            handleInput(delta);
            
            // Mettre à jour le cooldown d'attaque du joueur
            if (playerAttackCooldown > 0) {
                playerAttackCooldown -= delta;
            }
            
            // Vérifier si le joueur est mort
            if (!player.isAlive() && !playerDeathLogged) {
                playerDeathLogged = true;
                gameOver = true;
                // Transition vers LoseScreen après un court délai
                game.setScreen(new LoseScreen(game));
                dispose();
                return;
            }
            
            // Mettre à jour le joueur
            player.update(delta);
            
            // Mettre à jour les ennemis
            if (enemies != null) {
                for (Enemy enemy : enemies) {
                    if (enemy != null) {
                        enemy.update(delta);
                        if (enemy.isAlive() && player.isAlive()) {
                            enemy.updateAI(delta);
                        }
                    }
                }
            }
            
            // Vérifier et résoudre les collisions entre entités
            if (player.isAlive() && enemies != null) {
                resolveEntityCollisions();
            }
            
            // Initialiser la caméra sur le joueur après le premier rendu
            if (!cameraInitialized && player.getWidth() > 0 && player.getHeight() > 0) {
                updateCamera();
                cameraInitialized = true;
            }
            
            // Initialiser les collisions après le premier rendu
            if (!collisionsInitialized && mapLoader != null && player.getWidth() > 0 && player.getHeight() > 0) {
                initializeCollisions();
                collisionsInitialized = true;
            }
            
            handlePlayerAttack();
            checkEnemyDeathsAndDropCollectibles();
            processPendingSlimeRespawns(delta);
            cleanupCollectedCollectibles();
            clampToMapBounds();
            updateCamera();
            
            // Vérifier la victoire : tous les ennemis sont morts et aucun respawn en attente
            if (checkWinCondition()) {
                gameOver = true;
                // Transition vers WinScreen
                game.setScreen(new WinScreen(game));
                dispose();
                return;
            }
        }
        
        // Toujours effectuer le rendu, même en pause
        renderGame();
    }
    
    /**
     * Vérifie si le joueur a gagné :
     * - Tuer un vampire de niveau 3
     * OU
     * - Tuer 20 ennemis au total
     */
    private boolean checkWinCondition() {
        // Victoire si on a tué un vampire de niveau 3
        if (level3VampireKilled) {
            return true;
        }
        
        // Victoire si on a tué 20 ennemis au total
        if (totalKills >= 20) {
            return true;
        }
        
        return false;
    }
    
    private void renderGame() {
        // Nettoyer l'écran
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Mettre à jour la caméra et le viewport
        viewport.update((int)Gdx.graphics.getWidth(), (int)Gdx.graphics.getHeight());
        camera.update();
        
        // Rendre les layers de la map AVANT le joueur
        if (mapLoader != null) {
            mapLoader.renderBeforePlayer(camera);
        }
        
        // Dessiner le joueur et les ennemis
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        player.render(batch);
        
        if (enemies != null) {
            for (Enemy enemy : enemies) {
                if (enemy != null) {
                    enemy.render(batch);
                }
            }
        }
        
        if (collectibles != null) {
            for (Collectible collectible : collectibles) {
                if (collectible != null && !collectible.isCollected()) {
                    collectible.render(batch);
                }
            }
        }
        
        batch.end();
        
        // Rendre les layers de la map APRÈS le joueur
        if (mapLoader != null) {
            mapLoader.renderAfterPlayer(camera);
        }
        
        // Dessiner l'inventaire
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();
        renderActionPanel();
        batch.end();
        
        // Dessiner les barres de santé et shield
        if (player != null) {
            float screenHeight = Gdx.graphics.getHeight();
            
            if (healthBar != null) {
                healthBar.update(player.getHealth(), player.getMaxHealth());
                healthBar.setPosition(10f, screenHeight - healthBar.getHeight() - 10f);
            }
            
            if (shieldBar != null) {
                shieldBar.update(player.getShield(), player.getMaxShield());
                shieldBar.setPosition(10f, screenHeight - shieldBar.getHeight() - 10f);
            }
            
            batch.setProjectionMatrix(uiCamera.combined);
            batch.begin();
            if (healthBar != null) {
                healthBar.render(batch);
            }
            if (shieldBar != null) {
                shieldBar.render(batch);
            }
            batch.end();
        }
    }
    
    private void handleInput(float deltaTime) {
        if (!player.isAlive()) {
            player.getMovementHandler().stop();
            return;
        }
        
        Direction moveDirection = null;
        boolean isRunning = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
        
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.Z)) {
            moveDirection = Direction.UP;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            moveDirection = Direction.DOWN;
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.Q)) {
            moveDirection = Direction.SIDE_LEFT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            moveDirection = Direction.SIDE;
        }
        
        if (moveDirection != null) {
            if (canPlayerMove(moveDirection, deltaTime, isRunning)) {
                player.getMovementHandler().move(moveDirection, deltaTime, isRunning);
            } else {
                player.getMovementHandler().move(moveDirection, deltaTime, isRunning);
            }
        } else {
            player.getMovementHandler().stop();
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            player.attack();
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            pickupCollectibles();
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1)) {
            if (!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && !Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                player.useDamageBoost();
            }
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2)) {
            if (!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && !Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                player.useSpeedBoost();
            }
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3)) {
            if (!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && !Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                player.useShieldPotion();
            }
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_4)) {
            player.useHealPotion();
        }
    }
    
    private void handlePlayerAttack() {
        if (!player.isAlive() || enemies == null || enemies.isEmpty()) {
            return;
        }
        
        if (!player.isAttacking() || playerAttackCooldown > 0) {
            return;
        }
        
        Direction attackDirection = player.getCurrentDirection();
        
        float playerHitboxX = player.getHitboxX();
        float playerHitboxY = player.getHitboxY();
        float playerHitboxWidth = player.getHitboxWidth();
        float playerHitboxHeight = player.getHitboxHeight();
        float playerHitboxCenterX = playerHitboxX + playerHitboxWidth / 2f;
        float playerHitboxCenterY = playerHitboxY + playerHitboxHeight / 2f;
        
        float attackRangeWidth = 25f;
        float attackRangeHeight = 10f;
        
        float playerAttackX, playerAttackY;
        float playerAttackWidth, playerAttackHeight;
        
        switch (attackDirection) {
            case DOWN:
                playerAttackX = playerHitboxCenterX - attackRangeWidth / 2f;
                playerAttackY = playerHitboxY - attackRangeHeight;
                playerAttackWidth = attackRangeWidth;
                playerAttackHeight = attackRangeHeight;
                break;
            case UP:
                playerAttackX = playerHitboxCenterX - attackRangeWidth / 2f;
                playerAttackY = playerHitboxY + playerHitboxHeight;
                playerAttackWidth = attackRangeWidth;
                playerAttackHeight = attackRangeHeight;
                break;
            case SIDE_LEFT:
                playerAttackX = playerHitboxX - attackRangeHeight;
                playerAttackY = playerHitboxCenterY - attackRangeWidth / 2f;
                playerAttackWidth = attackRangeHeight;
                playerAttackHeight = attackRangeWidth;
                break;
            case SIDE:
                playerAttackX = playerHitboxX + playerHitboxWidth;
                playerAttackY = playerHitboxCenterY - attackRangeWidth / 2f;
                playerAttackWidth = attackRangeHeight;
                playerAttackHeight = attackRangeWidth;
                break;
            default:
                playerAttackX = playerHitboxCenterX - attackRangeWidth / 2f;
                playerAttackY = playerHitboxCenterY - attackRangeHeight / 2f;
                playerAttackWidth = attackRangeWidth;
                playerAttackHeight = attackRangeHeight;
                break;
        }
        
        for (Enemy enemy : enemies) {
            if (enemy == null || !enemy.isAlive()) {
                continue;
            }
            
            float enemyCenterX = enemy.getX() + enemy.getWidth() / 2f;
            float enemyCenterY = enemy.getY() + enemy.getHeight() / 2f;
            
            float enemyHitboxWidth = enemy.getHitboxWidth();
            float enemyHitboxHeight = enemy.getHitboxHeight();
            
            float enemyHitboxX = enemyCenterX - enemyHitboxWidth / 2f;
            float enemyHitboxY = enemyCenterY - enemyHitboxHeight / 2f;
            
            boolean hitboxesCollide = (playerAttackX < enemyHitboxX + enemyHitboxWidth &&
                                       playerAttackX + playerAttackWidth > enemyHitboxX &&
                                       playerAttackY < enemyHitboxY + enemyHitboxHeight &&
                                       playerAttackY + playerAttackHeight > enemyHitboxY);
            
            if (hitboxesCollide) {
                int baseDamage = 10;
                int totalDamage = baseDamage + player.getDamageBoost();
                enemy.takeDamage(totalDamage);
                playerAttackCooldown = playerAttackCooldownTime;
                break;
            }
        }
    }
    
    private void checkEnemyDeath() {
        if (enemy == null || enemy.isAlive()) {
            enemyDeathLooted = false;
            return;
        }
        
        if (!enemyDeathLooted) {
            if (Math.random() < 0.5) {
                player.getInventory().addItem(Inventory.ItemType.HEAL_POTION);
            } else {
                player.getInventory().addItem(Inventory.ItemType.SHIELD_POTION);
            }
            
            enemyDeathLooted = true;
        }
    }
    
    private void updateCamera() {
        if (player == null) {
            return;
        }
        
        float currentPlayerX = player.getX();
        float currentPlayerY = player.getY();
        
        float tolerance = 0.5f;
        boolean positionChanged = Math.abs(currentPlayerX - lastPlayerX) > tolerance || 
                                   Math.abs(currentPlayerY - lastPlayerY) > tolerance;
        
        if (positionChanged || lastPlayerX < 0 || lastPlayerY < 0) {
            float playerCenterX = currentPlayerX + player.getWidth() / 2f;
            float playerCenterY = currentPlayerY + player.getHeight() / 2f;
            
            float cameraMinX = 90f;
            float cameraMaxX = 710f;
            float cameraMinY = 70f;
            float cameraMaxY = 570f;
            
            float clampedCameraX = Math.max(cameraMinX, Math.min(cameraMaxX, playerCenterX));
            float clampedCameraY = Math.max(cameraMinY, Math.min(cameraMaxY, playerCenterY));
            
            camera.position.set(clampedCameraX, clampedCameraY, 0);
            
            lastPlayerX = currentPlayerX;
            lastPlayerY = currentPlayerY;
        }
    }
    
    private void clampToMapBounds() {
        if (mapLoader == null || player == null) {
            return;
        }
        
        int mapWidthPixels = mapLoader.getMapWidth() * mapLoader.getTileWidth();
        int mapHeightPixels = mapLoader.getMapHeight() * mapLoader.getTileHeight();
        
        float playerWidth = player.getWidth();
        float playerHeight = player.getHeight();
        
        float minX = 0;
        float minY = 0;
        float maxX = mapWidthPixels - playerWidth;
        float maxY = mapHeightPixels - playerHeight;
        
        float x = Math.max(minX, Math.min(maxX, player.getX()));
        float y = Math.max(minY, Math.min(maxY, player.getY()));
        
        player.setX(x);
        player.setY(y);
    }
    
    private boolean canPlayerMove(Direction direction, float deltaTime, boolean isRunning) {
        if (player == null || !player.isAlive()) {
            return true;
        }
        
        if (enemies == null) {
            return true;
        }
        
        float playerSpeed = player.getMovementHandler().getSpeed();
        float currentSpeed = isRunning ? playerSpeed * 1.5f : playerSpeed;
        float moveDistance = currentSpeed * deltaTime;
        
        float currentX = player.getX();
        float currentY = player.getY();
        float newX = currentX;
        float newY = currentY;
        
        switch (direction) {
            case UP:
                newY += moveDistance;
                break;
            case DOWN:
                newY -= moveDistance;
                break;
            case SIDE:
                newX += moveDistance;
                break;
            case SIDE_LEFT:
                newX -= moveDistance;
                break;
        }
        
        float playerHitboxWidth = player.getHitboxWidth();
        float playerHitboxHeight = player.getHitboxHeight();
        float playerSpriteCenterX = newX + player.getWidth() / 2f;
        float playerSpriteCenterY = newY + player.getHeight() / 2f;
        float newPlayerHitboxX = playerSpriteCenterX - playerHitboxWidth / 2f;
        float newPlayerHitboxY = playerSpriteCenterY - playerHitboxHeight / 2f;
        
        for (Enemy enemy : enemies) {
            if (enemy == null || !enemy.isAlive()) {
                continue;
            }
            
            float enemyHitboxX = enemy.getHitboxX();
            float enemyHitboxY = enemy.getHitboxY();
            float enemyHitboxWidth = enemy.getHitboxWidth();
            float enemyHitboxHeight = enemy.getHitboxHeight();
            
            boolean wouldCollide = (newPlayerHitboxX < enemyHitboxX + enemyHitboxWidth &&
                                   newPlayerHitboxX + playerHitboxWidth > enemyHitboxX &&
                                   newPlayerHitboxY < enemyHitboxY + enemyHitboxHeight &&
                                   newPlayerHitboxY + playerHitboxHeight > enemyHitboxY);
            
            if (wouldCollide) {
                return false;
            }
        }
        
        return true;
    }
    
    private void resolveEntityCollisions() {
        if (player == null || enemies == null || !player.isAlive()) {
            return;
        }
        
        float playerHitboxX = player.getHitboxX();
        float playerHitboxY = player.getHitboxY();
        float playerHitboxWidth = player.getHitboxWidth();
        float playerHitboxHeight = player.getHitboxHeight();
        
        for (Enemy enemy : enemies) {
            if (enemy == null || !enemy.isAlive()) {
                continue;
            }
            
            float enemyHitboxX = enemy.getHitboxX();
            float enemyHitboxY = enemy.getHitboxY();
            float enemyHitboxWidth = enemy.getHitboxWidth();
            float enemyHitboxHeight = enemy.getHitboxHeight();
            
            boolean colliding = (playerHitboxX < enemyHitboxX + enemyHitboxWidth &&
                               playerHitboxX + playerHitboxWidth > enemyHitboxX &&
                               playerHitboxY < enemyHitboxY + enemyHitboxHeight &&
                               playerHitboxY + playerHitboxHeight > enemyHitboxY);
            
            if (colliding) {
                float overlapX = Math.min(playerHitboxX + playerHitboxWidth - enemyHitboxX,
                                         enemyHitboxX + enemyHitboxWidth - playerHitboxX);
                float overlapY = Math.min(playerHitboxY + playerHitboxHeight - enemyHitboxY,
                                         enemyHitboxY + enemyHitboxHeight - playerHitboxY);
                
                float playerCenterX = playerHitboxX + playerHitboxWidth / 2f;
                float playerCenterY = playerHitboxY + playerHitboxHeight / 2f;
                float enemyCenterX = enemyHitboxX + enemyHitboxWidth / 2f;
                float enemyCenterY = enemyHitboxY + enemyHitboxHeight / 2f;
                
                if (overlapX < overlapY) {
                    if (playerCenterX < enemyCenterX) {
                        player.setX(player.getX() - overlapX);
                    } else {
                        player.setX(player.getX() + overlapX);
                    }
                } else {
                    if (playerCenterY < enemyCenterY) {
                        player.setY(player.getY() - overlapY);
                    } else {
                        player.setY(player.getY() + overlapY);
                    }
                }
            }
        }
    }
    
    private void clampToScreenBounds() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float playerWidth = player.getWidth();
        float playerHeight = player.getHeight();
        
        float maxX = screenWidth - playerWidth;
        float maxY = screenHeight - playerHeight;
        
        float x = Math.max(0, Math.min(maxX, player.getX()));
        float y = Math.max(0, Math.min(maxY, player.getY()));
        
        player.setX(x);
        player.setY(y);
    }
    
    private void clampEnemyToScreenBounds() {
        if (enemy == null) {
            return;
        }
        
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float enemyWidth = enemy.getWidth();
        float enemyHeight = enemy.getHeight();
        
        float maxX = screenWidth - enemyWidth;
        float maxY = screenHeight - enemyHeight;
        
        float x = Math.max(0, Math.min(maxX, enemy.getX()));
        float y = Math.max(0, Math.min(maxY, enemy.getY()));
        
        enemy.setX(x);
        enemy.setY(y);
    }
    
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.update();
        uiCamera.setToOrtho(false, width, height);
        uiCamera.update();
    }
    
    @Override
    public void hide() {
    }
    
    private void renderActionPanel() {
        if (actionPanelMapping == null || player == null) {
            return;
        }
        
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        
        float slotScale = 4f;
        
        com.badlogic.gdx.graphics.g2d.TextureRegion slotSprite = actionPanelMapping.getSprite("sprite3");
        if (slotSprite == null) {
            slotSprite = actionPanelMapping.getSprite("sprite4");
        }
        
        if (slotSprite == null) {
            return;
        }
        
        float slotWidth = slotSprite.getRegionWidth() * slotScale;
        float slotHeight = slotSprite.getRegionHeight() * slotScale;
        float spacing = 10f;
        
        float totalWidth = slotWidth * 4 + spacing * 3;
        float startX = (screenWidth - totalWidth) / 2f;
        float slotY = 20f;
        
        for (int i = 0; i < 4; i++) {
            float slotX = startX + i * (slotWidth + spacing);
            batch.draw(slotSprite, slotX, slotY, slotWidth, slotHeight);
            
            Inventory.ItemType itemType = getItemTypeForSlot(i);
            int itemCount = itemType != null ? player.getInventory().getItemCount(itemType) : 0;
            if (itemType != null && itemCount > 0) {
                com.badlogic.gdx.graphics.g2d.TextureRegion itemSprite = getSpriteForItemType(itemType);
                if (itemSprite != null) {
                    float itemWidth = itemSprite.getRegionWidth() * slotScale;
                    float itemHeight = itemSprite.getRegionHeight() * slotScale;
                    float itemX = slotX + (slotWidth - itemWidth) / 2f;
                    float itemY = slotY + (slotHeight - itemHeight) / 2f;
                    batch.draw(itemSprite, itemX, itemY, itemWidth, itemHeight);
                    
                    if (itemCount > 1) {
                        String countText = String.valueOf(itemCount);
                        float textX = slotX + slotWidth - 5f;
                        float textY = slotY + slotHeight - 5f;
                        font.setColor(1f, 1f, 1f, 1f);
                        font.draw(batch, countText, textX, textY);
                    }
                }
            }
        }
    }
    
    private Inventory.ItemType getItemTypeForSlot(int slotIndex) {
        switch (slotIndex) {
            case 0:
                return Inventory.ItemType.DAMAGE_BOOST;
            case 1:
                return Inventory.ItemType.SPEED_BOOST;
            case 2:
                return Inventory.ItemType.SHIELD_POTION;
            case 3:
                return Inventory.ItemType.HEAL_POTION;
            default:
                return null;
        }
    }
    
    private com.badlogic.gdx.graphics.g2d.TextureRegion getSpriteForItemType(Inventory.ItemType itemType) {
        if (actionPanelMapping == null) {
            return null;
        }
        
        switch (itemType) {
            case DAMAGE_BOOST:
                return actionPanelMapping.getSprite("sprite1");
            case SPEED_BOOST:
                return actionPanelMapping.getSprite("sprite2");
            case SHIELD_POTION:
                return actionPanelMapping.getSprite("sprite5");
            case HEAL_POTION:
                return actionPanelMapping.getSprite("sprite6");
            default:
                return null;
        }
    }
    
    private void renderCharacterPanel() {
        if (characterPanelMapping == null || player == null) {
            return;
        }
        
        int health = player.getHealth();
        int maxHealth = player.getMaxHealth();
        int shield = player.getShield();
        int maxShield = player.getMaxShield();
        
        String spriteName;
        if (!player.isAlive() || (health == 0 && shield == 0)) {
            spriteName = "sprite1";
        } else if (health == maxHealth && shield == maxShield && maxShield > 0) {
            spriteName = "sprite2";
        } else {
            spriteName = "sprite1";
        }
        
        com.badlogic.gdx.graphics.g2d.TextureRegion sprite = characterPanelMapping.getSprite(spriteName);
        if (sprite != null) {
            float characterPanelScale = 3f;
            float spriteWidth = sprite.getRegionWidth() * characterPanelScale;
            float spriteHeight = sprite.getRegionHeight() * characterPanelScale;
            
            float x = 10f;
            float y = Gdx.graphics.getHeight() - spriteHeight - 10f;
            batch.draw(sprite, x, y, spriteWidth, spriteHeight);
        }
    }
    
    private void checkEnemyDeathsAndDropCollectibles() {
        if (enemies == null || actionPanelMapping == null) {
            return;
        }
        
        java.util.Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            if (enemy != null && !enemy.isAlive()) {
                // Incrémenter le compteur de kills
                totalKills++;
                
                // Vérifier si c'est un vampire de niveau 3
                if (enemy instanceof Vampire) {
                    Vampire vampire = (Vampire) enemy;
                    if (vampire.getLevel() == 3) {
                        level3VampireKilled = true;
                    }
                }
                
                float rand = (float) Math.random();
                Inventory.ItemType itemType = null;
                
                if (rand < 0.25f) {
                    itemType = Inventory.ItemType.DAMAGE_BOOST;
                } else if (rand < 0.5f) {
                    itemType = Inventory.ItemType.SPEED_BOOST;
                } else if (rand < 0.75f) {
                    itemType = Inventory.ItemType.SHIELD_POTION;
                } else {
                    itemType = Inventory.ItemType.HEAL_POTION;
                }
                
                float enemyX = enemy.getX() + enemy.getWidth() / 2f;
                float enemyY = enemy.getY() + enemy.getHeight() / 2f;
                
                Collectible collectible = new Collectible(enemyX, enemyY, itemType, actionPanelMapping);
                collectibles.add(collectible);
                
                if (enemy instanceof Slime && enemy.getRespawnCount() < 2) {
                    Slime deadSlime = (Slime) enemy;
                    int zoneId = deadSlime.getZoneId();
                    int level = deadSlime.getLevel();
                    float initialX = deadSlime.getInitialX();
                    float initialY = deadSlime.getInitialY();
                    
                    PendingSlimeRespawn pendingRespawn = new PendingSlimeRespawn(
                        gameTime, zoneId, level, initialX, initialY, deadSlime.getRespawnCount() + 1);
                    pendingSlimeRespawns.add(pendingRespawn);
                }
                
                iterator.remove();
                break;
            }
        }
    }
    
    private void processPendingSlimeRespawns(float delta) {
        if (pendingSlimeRespawns == null || pendingSlimeRespawns.isEmpty()) {
            return;
        }
        
        float respawnDelay = 10f;
        
        java.util.Iterator<PendingSlimeRespawn> iterator = pendingSlimeRespawns.iterator();
        while (iterator.hasNext()) {
            PendingSlimeRespawn pending = iterator.next();
            float elapsedTime = gameTime - pending.deathTime;
            
            if (elapsedTime >= respawnDelay) {
                Slime newSlime = new Slime(pending.initialX, pending.initialY, pending.level);
                newSlime.setTarget(player);
                newSlime.setMapLoader(mapLoader);
                newSlime.setZoneId(pending.zoneId);
                newSlime.setInitialPosition(pending.initialX, pending.initialY);
                newSlime.setRespawnCount(pending.respawnCount);
                
                enemies.add(newSlime);
                
                if (mapLoader != null && newSlime.getHitboxWidth() > 0 && newSlime.getHitboxHeight() > 0) {
                    float spriteWidth = 16f;
                    float spriteHeight = 16f;
                    CollisionHandler enemyCollision = new CollisionHandler(
                        mapLoader, newSlime.getHitboxWidth(), newSlime.getHitboxHeight(), spriteWidth, spriteHeight);
                    newSlime.getMovementHandler().setCollisionHandler(enemyCollision);
                }
                
                iterator.remove();
            }
        }
    }
    
    private void pickupCollectibles() {
        if (collectibles == null || player == null) {
            return;
        }
        
        float playerX = player.getX();
        float playerY = player.getY();
        float playerWidth = player.getWidth();
        float playerHeight = player.getHeight();
        
        java.util.Iterator<Collectible> iterator = collectibles.iterator();
        while (iterator.hasNext()) {
            Collectible collectible = iterator.next();
            if (collectible != null && !collectible.isCollected()) {
                if (collectible.canBePickedUp(playerX, playerY, playerWidth, playerHeight)) {
                    if (player.getInventory().addItem(collectible.getItemType())) {
                        collectible.collect();
                        iterator.remove();
                    }
                }
            }
        }
    }
    
    private void cleanupCollectedCollectibles() {
        if (collectibles == null) {
            return;
        }
        
        collectibles.removeIf(Collectible::isCollected);
    }
    
    private void initializeCollisions() {
        if (mapLoader == null) {
            return;
        }
        
        if (player != null && player.getHitboxWidth() > 0 && player.getHitboxHeight() > 0) {
            float spriteWidth = 32f;
            float spriteHeight = 32f;
            CollisionHandler playerCollision = new CollisionHandler(
                mapLoader, player.getHitboxWidth(), player.getHitboxHeight(), spriteWidth, spriteHeight);
            player.getMovementHandler().setCollisionHandler(playerCollision);
        } else {
            Gdx.app.error("GameScreen", "Impossible d'initialiser les collisions : hitbox du joueur invalide");
        }
        
        if (enemies != null) {
            for (Enemy enemy : enemies) {
                if (enemy != null && enemy.getHitboxWidth() > 0 && enemy.getHitboxHeight() > 0) {
                    float spriteWidth, spriteHeight;
                    if (enemy instanceof Slime) {
                        spriteWidth = 16f;
                        spriteHeight = 16f;
                    } else if (enemy instanceof Vampire) {
                        spriteWidth = 32f;
                        spriteHeight = 32f;
                    } else {
                        spriteWidth = enemy.getWidth();
                        spriteHeight = enemy.getHeight();
                    }
                    
                    CollisionHandler enemyCollision = new CollisionHandler(
                        mapLoader, enemy.getHitboxWidth(), enemy.getHitboxHeight(), spriteWidth, spriteHeight);
                    enemy.getMovementHandler().setCollisionHandler(enemyCollision);
                }
            }
        }
    }
    
    @Override
    public void dispose() {
        if (player != null) {
            player.dispose();
        }
        if (enemy != null) {
            enemy.dispose();
        }
        if (mapLoader != null) {
            mapLoader.dispose();
        }
        if (characterPanelMapping != null) {
            characterPanelMapping.dispose();
        }
        if (actionPanelMapping != null) {
            actionPanelMapping.dispose();
        }
        if (batch != null) {
            batch.dispose();
        }
        if (healthBar != null) {
            healthBar.dispose();
        }
        if (shieldBar != null) {
            shieldBar.dispose();
        }
        if (font != null) {
            font.dispose();
        }
    }
}