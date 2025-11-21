package com.tlse1.twodgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private OrthographicCamera uiCamera;
    private Viewport viewport;
    private Player player;
    private Enemy enemy; // Ancien ennemi (vampire) - gardé pour compatibilité
    private ArrayList<Enemy> enemies; // Liste des ennemis (slimes, vampires, etc.)
    private ArrayList<Collectible> collectibles; // Liste des collectibles droppés
    
    // Map
    private JsonMapLoader mapLoader;
    
    // Character panel
    private CharacterPanelMapping characterPanelMapping;
    
    // Action panel
    private ActionPanelMapping actionPanelMapping;
    
    // Health bar
    private HealthBar healthBar;
    
    // Shield bar
    private ShieldBar shieldBar;
    
    // Font pour afficher les compteurs
    private BitmapFont font;
    
    // Portée d'attaque du joueur
    private float playerAttackRange = 100f;
    
    // Cooldown d'attaque du joueur
    private float playerAttackCooldown = 0f;
    private float playerAttackCooldownTime = 0.5f;
    
    // Flag pour savoir si on a déjà loggé la mort du joueur
    private boolean playerDeathLogged = false;
    
    // Flag pour savoir si on a déjà donné les items de l'ennemi mort
    private boolean enemyDeathLooted = false;
    
    // Flag pour savoir si les collisions ont été initialisées
    private boolean collisionsInitialized = false;
    
    // Flag pour savoir si la caméra a été initialisée
    private boolean cameraInitialized = false;
    
    // Position précédente du joueur pour détecter les changements
    private float lastPlayerX = -1f;
    private float lastPlayerY = -1f;
    
    public GameScreen(TwoDGame game) {
        this.game = game;
    }
    
    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        // Initialiser la caméra et le viewport
        // Définir la zone de la map à afficher (180x140 pixels)
        float mapViewWidth = 180f;
        float mapViewHeight = 140f;
        
        camera = new OrthographicCamera();
        // Utiliser StretchViewport pour étirer les 180x140 pixels pour remplir l'écran
        viewport = new StretchViewport(mapViewWidth, mapViewHeight, camera);
        viewport.apply();
        camera.update();
        
        // Initialiser la caméra UI pour les éléments d'interface (barre de santé)
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiCamera.update();
        
        Gdx.app.log("GameScreen", String.format("Caméra configurée: vue de %.0fx%.0f pixels de la map (étirée pour remplir l'écran)", 
            mapViewWidth, mapViewHeight));
        
        // Charger la map
        mapLoader = new JsonMapLoader("map/map.json");
        
        // Créer le joueur et le positionner en bas à gauche de la map
        player = new Player(0, 0);
        
        // Positionner le joueur sur la map
        float playerStartX = 32f; // Position X initiale
        float playerStartY = 50f; // Position Y initiale
        player.setX(playerStartX);
        player.setY(playerStartY);
        
        // Initialiser la caméra sur le joueur (sera ajusté après le premier rendu quand on connaît sa taille)
        // La caméra sera mise à jour dans updateCamera() après le premier rendu
        
        // Créer l'ennemi (vampire) - DÉSACTIVÉ TEMPORAIREMENT
        // enemy = new Enemy(screenWidth * 0.2f, screenHeight * 0.2f);
        // enemy.setTarget(player); // L'ennemi cible le joueur
        
        // Initialiser la liste des ennemis
        enemies = new ArrayList<>();
        
        // Initialiser la liste des collectibles
        collectibles = new ArrayList<>();
        
        // Spawn des slimes aux positions stratégiques
        // Slime niveau 1
        Slime slime1 = new Slime(64f, 448f, 1);
        slime1.setTarget(player);
        enemies.add(slime1);
        Gdx.app.log("GameScreen", "Slime niveau 1 créé à (64, 448)");
        
        // Slime niveau 2
        Slime slime2 = new Slime(160f, 192f, 2);
        slime2.setTarget(player);
        enemies.add(slime2);
        Gdx.app.log("GameScreen", "Slime niveau 2 créé à (160, 192)");
        
        // Slime niveau 3
        Slime slime3 = new Slime(64f, 352f, 3);
        slime3.setTarget(player);
        enemies.add(slime3);
        Gdx.app.log("GameScreen", "Slime niveau 3 créé à (64, 352)");
        
        // Spawn des vampires aux positions stratégiques
        // Vampire niveau 1
        Vampire vampire1 = new Vampire(192f, 320f, 1);
        vampire1.setTarget(player);
        enemies.add(vampire1);
        Gdx.app.log("GameScreen", "Vampire niveau 1 créé à (192, 320)");
        
        // Vampire niveau 2
        Vampire vampire2 = new Vampire(640f, 416f, 2);
        vampire2.setTarget(player);
        enemies.add(vampire2);
        Gdx.app.log("GameScreen", "Vampire niveau 2 créé à (640, 416)");
        
        // Vampire niveau 3
        Vampire vampire3 = new Vampire(672f, 192f, 3);
        vampire3.setTarget(player);
        enemies.add(vampire3);
        Gdx.app.log("GameScreen", "Vampire niveau 3 créé à (672, 192)");
        
        // Les collisions seront configurées après le premier rendu
        // quand on connaîtra les dimensions réelles des entités
        
        // Charger le character panel
        characterPanelMapping = new CharacterPanelMapping();
        
        // Charger l'action panel
        actionPanelMapping = new ActionPanelMapping();
        
        // Initialiser la barre de santé (après avoir chargé characterPanelMapping)
        // Positionnée en haut à gauche de l'écran
        float healthBarScale = 4f; // Échelle pour agrandir la barre (augmenté de 2 à 4)
        float healthBarX = 10f;
        float healthBarY = Gdx.graphics.getHeight() - (30f * healthBarScale) - 10f; // 30 = hauteur du panel
        healthBar = new HealthBar(healthBarX, healthBarY, healthBarScale, characterPanelMapping);
        
        // Initialiser la barre de shield (même position et scale que healthBar)
        shieldBar = new ShieldBar(healthBarX, healthBarY, healthBarScale, characterPanelMapping);
        
        // Initialiser la font pour les compteurs
        font = new BitmapFont();
        font.getData().setScale(1.5f); // Agrandir un peu la police
    }
    
    @Override
    public void render(float delta) {
        // Gérer l'input et le mouvement
        handleInput(delta);
        
        // Mettre à jour le cooldown d'attaque du joueur
        if (playerAttackCooldown > 0) {
            playerAttackCooldown -= delta;
        }
        
        // Vérifier si le joueur est mort (ne logger qu'une seule fois)
        if (!player.isAlive() && !playerDeathLogged) {
            Gdx.app.log("GameScreen", "Le joueur est mort !");
            playerDeathLogged = true;
            // Ici on pourrait afficher un écran de game over ou redémarrer
        }
        
        // Mettre à jour le joueur (même s'il est mort, pour l'animation de mort)
        player.update(delta);
        
        // Mettre à jour les ennemis (même s'ils sont morts, pour l'animation de mort)
        if (enemies != null) {
            for (Enemy enemy : enemies) {
                if (enemy != null) {
                    // Toujours mettre à jour l'animation, même si l'ennemi est mort
                    enemy.update(delta);
                    // Mettre à jour l'IA seulement si l'ennemi et le joueur sont vivants
                    if (enemy.isAlive() && player.isAlive()) {
                        enemy.updateAI(delta); // Activer l'IA de poursuite
                    }
                }
            }
        }
        
        // Vérifier et résoudre les collisions entre entités (joueur et ennemis)
        if (player.isAlive() && enemies != null) {
            resolveEntityCollisions();
        }
        
        // Initialiser la caméra sur le joueur après le premier rendu (quand on connaît sa taille)
        if (!cameraInitialized && player.getWidth() > 0 && player.getHeight() > 0) {
            updateCamera();
            cameraInitialized = true;
        }
        
        // Initialiser les collisions après le premier rendu (quand on connaît les dimensions)
        if (!collisionsInitialized && mapLoader != null && player.getWidth() > 0 && player.getHeight() > 0) {
            initializeCollisions();
            collisionsInitialized = true;
        }
        
        handlePlayerAttack();
        
        // Vérifier si l'ennemi est mort et ajouter des items à l'inventaire - DÉSACTIVÉ TEMPORAIREMENT
        // checkEnemyDeath();
        
        // Vérifier si des ennemis sont morts et drop des collectibles
        checkEnemyDeathsAndDropCollectibles();
        
        // Nettoyer les collectibles collectés
        cleanupCollectedCollectibles();
        
        // Mettre à jour l'IA de l'ennemi seulement si le joueur est vivant et l'ennemi est vivant - DÉSACTIVÉ TEMPORAIREMENT
        // if (enemy != null && player.isAlive() && enemy.isAlive()) {
        //     enemy.update(delta);
        //     enemy.updateAI(delta);
        // }
        
        // Limiter le joueur dans les bounds de la map (pas de l'écran)
        clampToMapBounds();
        
        // Limiter l'ennemi dans les bounds de l'écran - DÉSACTIVÉ TEMPORAIREMENT
        // clampEnemyToScreenBounds();
        
        // Faire suivre la caméra au joueur
        updateCamera();
        
        // Nettoyer l'écran
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Mettre à jour la caméra et le viewport
        viewport.update((int)Gdx.graphics.getWidth(), (int)Gdx.graphics.getHeight());
        camera.update();
        
        // Rendre la map en premier (en arrière-plan) - OrthogonalTiledMapRenderer gère son propre batch
        if (mapLoader != null) {
            mapLoader.render(camera);
        }
        
        // Dessiner le joueur et l'ennemi
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        // Afficher le joueur même s'il est mort (pour voir l'animation de mort)
        player.render(batch);
        
        // Afficher les ennemis
        if (enemies != null) {
            for (Enemy enemy : enemies) {
                if (enemy != null) {
                    enemy.render(batch);
                }
            }
        }
        
        // Afficher l'ennemi (vampire) - DÉSACTIVÉ TEMPORAIREMENT
        // if (enemy != null) {
        //     enemy.render(batch);
        // }
        
        // Afficher les collectibles (dans le monde du jeu, avec la caméra du jeu)
        if (collectibles != null) {
            for (Collectible collectible : collectibles) {
                if (collectible != null && !collectible.isCollected()) {
                    collectible.render(batch);
                }
            }
        }
        
        // Dessiner le character panel
        renderCharacterPanel();
        
        // Action panel
        renderActionPanel();
        
        batch.end();
        
        // Dessiner les hitboxes pour le débogage (rectangles rouges)
        drawHitboxes();
        
        // Dessiner les zones de collision de la map (rectangles rouges)
        drawCollisionDebug();
        
        // Dessiner les structures de la map (rectangles verts)
        drawStructuresDebug();
        
        // Dessiner les reliefs de la map (rectangles bleus)
        drawReliefDebug();
        
        // Dessiner les over_struct de la map (rectangles jaunes)
        drawOverStructDebug();
        
        // Dessiner les barres de santé et shield (en coordonnées écran)
        if (player != null) {
            float screenHeight = Gdx.graphics.getHeight();
            
            // Mettre à jour et dessiner la barre de santé
            if (healthBar != null) {
                healthBar.update(player.getHealth(), player.getMaxHealth());
                healthBar.setPosition(10f, screenHeight - healthBar.getHeight() - 10f);
            }
            
            // Mettre à jour et dessiner la barre de shield
            if (shieldBar != null) {
                shieldBar.update(player.getShield(), player.getMaxShield());
                shieldBar.setPosition(10f, screenHeight - shieldBar.getHeight() - 10f);
            }
            
            // Dessiner les barres avec SpriteBatch en coordonnées écran
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
    
    /**
     * Dessine les hitboxes du joueur et des ennemis pour le débogage.
     */
    private void drawHitboxes() {
        if (shapeRenderer == null) {
            return;
        }
        
        // Utiliser la projection de la caméra pour dessiner dans le monde du jeu
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        
        // Dessiner les hitboxes de collision en rouge
        shapeRenderer.setColor(1f, 0f, 0f, 1f); // Rouge
        
        // Dessiner la hitbox du joueur (20x27, centrée)
        if (player != null && player.getWidth() > 0 && player.getHeight() > 0) {
            float playerHitboxX = player.getHitboxX();
            float playerHitboxY = player.getHitboxY();
            float playerHitboxWidth = player.getHitboxWidth();
            float playerHitboxHeight = player.getHitboxHeight();
            
            // Dessiner un rectangle rouge pour la hitbox de collision
            shapeRenderer.rect(playerHitboxX, playerHitboxY, playerHitboxWidth, playerHitboxHeight);
        }
        
        // Dessiner les hitboxes des ennemis (17x16 pour slimes, 30x30 pour vampires, centrées)
        if (enemies != null) {
            for (Enemy enemy : enemies) {
                if (enemy != null && enemy.getWidth() > 0 && enemy.getHeight() > 0) {
                    float enemyHitboxX = enemy.getHitboxX();
                    float enemyHitboxY = enemy.getHitboxY();
                    float enemyHitboxWidth = enemy.getHitboxWidth();
                    float enemyHitboxHeight = enemy.getHitboxHeight();
                    
                    // Dessiner un rectangle rouge pour la hitbox de collision
                    shapeRenderer.rect(enemyHitboxX, enemyHitboxY, enemyHitboxWidth, enemyHitboxHeight);
                }
            }
        }
        
        // Dessiner la range d'attaque du joueur en vert (25x10 pixels, collée à l'extrémité de la hitbox rouge)
        shapeRenderer.setColor(0f, 1f, 0f, 1f); // Vert
        
        if (player != null && player.isAttacking()) {
            Direction attackDirection = player.getCurrentDirection();
            
            // Utiliser la hitbox de collision du joueur (rouge) pour positionner la range
            float playerHitboxX = player.getHitboxX();
            float playerHitboxY = player.getHitboxY();
            float playerHitboxWidth = player.getHitboxWidth();
            float playerHitboxHeight = player.getHitboxHeight();
            float playerHitboxCenterX = playerHitboxX + playerHitboxWidth / 2f;
            float playerHitboxCenterY = playerHitboxY + playerHitboxHeight / 2f;
            
            // Range d'attaque : 25x10 pixels
            float attackRangeWidth = 25f;
            float attackRangeHeight = 10f;
            
            float attackRangeX, attackRangeY;
            float finalWidth, finalHeight;
            
            // Positionner la range collée à l'extrémité de la hitbox selon la direction
            switch (attackDirection) {
                case DOWN:
                    // Range en bas de la hitbox (25x10, horizontale)
                    attackRangeX = playerHitboxCenterX - attackRangeWidth / 2f;
                    attackRangeY = playerHitboxY - attackRangeHeight; // Collée en bas
                    finalWidth = attackRangeWidth;
                    finalHeight = attackRangeHeight;
                    break;
                case UP:
                    // Range en haut de la hitbox (25x10, horizontale)
                    attackRangeX = playerHitboxCenterX - attackRangeWidth / 2f;
                    attackRangeY = playerHitboxY + playerHitboxHeight; // Collée en haut
                    finalWidth = attackRangeWidth;
                    finalHeight = attackRangeHeight;
                    break;
                case SIDE_LEFT:
                    // Range à gauche de la hitbox (10x25, verticale)
                    attackRangeX = playerHitboxX - attackRangeHeight; // Collée à gauche
                    attackRangeY = playerHitboxCenterY - attackRangeWidth / 2f;
                    finalWidth = attackRangeHeight; // 10
                    finalHeight = attackRangeWidth; // 25
                    break;
                case SIDE:
                    // Range à droite de la hitbox (10x25, verticale)
                    attackRangeX = playerHitboxX + playerHitboxWidth; // Collée à droite
                    attackRangeY = playerHitboxCenterY - attackRangeWidth / 2f;
                    finalWidth = attackRangeHeight; // 10
                    finalHeight = attackRangeWidth; // 25
                    break;
                default:
                    attackRangeX = playerHitboxCenterX - attackRangeWidth / 2f;
                    attackRangeY = playerHitboxCenterY - attackRangeHeight / 2f;
                    finalWidth = attackRangeWidth;
                    finalHeight = attackRangeHeight;
                    break;
            }
            
            // Dessiner un rectangle vert pour la range d'attaque
            shapeRenderer.rect(attackRangeX, attackRangeY, finalWidth, finalHeight);
        }
        
        shapeRenderer.end();
    }
    
    /**
     * Dessine les zones de collision de la map avec des rectangles rouges pour le débogage.
     */
    private void drawCollisionDebug() {
        if (shapeRenderer == null || mapLoader == null) {
            return;
        }
        
        // Récupérer le TiledMap depuis le mapLoader
        com.badlogic.gdx.maps.tiled.TiledMap tiledMap = mapLoader.getTiledMap();
        if (tiledMap == null) {
            return;
        }
        
        // Récupérer le layer "collisions"
        com.badlogic.gdx.maps.tiled.TiledMapTileLayer collisionsLayer = 
            (com.badlogic.gdx.maps.tiled.TiledMapTileLayer) tiledMap.getLayers().get("collisions");
        
        if (collisionsLayer == null) {
            return;
        }
        
        // Utiliser la projection de la caméra pour dessiner dans le monde du jeu
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 0f, 0f, 1f); // Rouge
        
        // Récupérer les dimensions des tiles
        int tileWidth = mapLoader.getTileWidth();
        int tileHeight = mapLoader.getTileHeight();
        int mapWidth = mapLoader.getMapWidth();
        int mapHeight = mapLoader.getMapHeight();
        
        // Calculer les bounds visibles de la caméra pour optimiser le rendu
        float camLeft = camera.position.x - camera.viewportWidth / 2f;
        float camRight = camera.position.x + camera.viewportWidth / 2f;
        float camBottom = camera.position.y - camera.viewportHeight / 2f;
        float camTop = camera.position.y + camera.viewportHeight / 2f;
        
        // Calculer les indices de tiles visibles (avec une marge pour éviter les coupures)
        int startX = Math.max(0, (int)(camLeft / tileWidth) - 1);
        int endX = Math.min(mapWidth - 1, (int)(camRight / tileWidth) + 1);
        int startY = Math.max(0, (int)(camBottom / tileHeight) - 1);
        int endY = Math.min(mapHeight - 1, (int)(camTop / tileHeight) + 1);
        
        // Parcourir uniquement les tiles visibles à l'écran
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell cell = collisionsLayer.getCell(x, y);
                
                // Si la tile existe et n'est pas vide, c'est une zone de collision
                if (cell != null && cell.getTile() != null) {
                    // Calculer la position en pixels (coin bas-gauche de la tile)
                    float pixelX = x * tileWidth;
                    float pixelY = y * tileHeight;
                    
                    // Dessiner un rectangle rouge autour de la tile de collision
                    shapeRenderer.rect(pixelX, pixelY, tileWidth, tileHeight);
                }
            }
        }
        
        shapeRenderer.end();
    }
    
    /**
     * Dessine les structures de la map avec des rectangles verts pour le débogage.
     */
    private void drawStructuresDebug() {
        if (shapeRenderer == null || mapLoader == null) {
            return;
        }
        
        // Récupérer le TiledMap depuis le mapLoader
        com.badlogic.gdx.maps.tiled.TiledMap tiledMap = mapLoader.getTiledMap();
        if (tiledMap == null) {
            return;
        }
        
        // Récupérer le layer "structures"
        com.badlogic.gdx.maps.tiled.TiledMapTileLayer structuresLayer = 
            (com.badlogic.gdx.maps.tiled.TiledMapTileLayer) tiledMap.getLayers().get("structures");
        
        if (structuresLayer == null) {
            return;
        }
        
        // Utiliser la projection de la caméra pour dessiner dans le monde du jeu
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0f, 1f, 0f, 1f); // Vert
        
        // Récupérer les dimensions des tiles
        int tileWidth = mapLoader.getTileWidth();
        int tileHeight = mapLoader.getTileHeight();
        int mapWidth = mapLoader.getMapWidth();
        int mapHeight = mapLoader.getMapHeight();
        
        // Calculer les bounds visibles de la caméra pour optimiser le rendu
        float camLeft = camera.position.x - camera.viewportWidth / 2f;
        float camRight = camera.position.x + camera.viewportWidth / 2f;
        float camBottom = camera.position.y - camera.viewportHeight / 2f;
        float camTop = camera.position.y + camera.viewportHeight / 2f;
        
        // Calculer les indices de tiles visibles (avec une marge pour éviter les coupures)
        int startX = Math.max(0, (int)(camLeft / tileWidth) - 1);
        int endX = Math.min(mapWidth - 1, (int)(camRight / tileWidth) + 1);
        int startY = Math.max(0, (int)(camBottom / tileHeight) - 1);
        int endY = Math.min(mapHeight - 1, (int)(camTop / tileHeight) + 1);
        
        // Parcourir uniquement les tiles visibles à l'écran
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell cell = structuresLayer.getCell(x, y);
                
                // Si la tile existe et n'est pas vide, c'est une structure
                if (cell != null && cell.getTile() != null) {
                    // Calculer la position en pixels (coin bas-gauche de la tile)
                    float pixelX = x * tileWidth;
                    float pixelY = y * tileHeight;
                    
                    // Dessiner un rectangle vert autour de la tile de structure
                    shapeRenderer.rect(pixelX, pixelY, tileWidth, tileHeight);
                }
            }
        }
        
        shapeRenderer.end();
    }
    
    /**
     * Dessine les reliefs de la map avec des rectangles bleus pour le débogage.
     */
    private void drawReliefDebug() {
        if (shapeRenderer == null || mapLoader == null) {
            return;
        }
        
        // Récupérer le TiledMap depuis le mapLoader
        com.badlogic.gdx.maps.tiled.TiledMap tiledMap = mapLoader.getTiledMap();
        if (tiledMap == null) {
            return;
        }
        
        // Récupérer le layer "relief"
        com.badlogic.gdx.maps.tiled.TiledMapTileLayer reliefLayer = 
            (com.badlogic.gdx.maps.tiled.TiledMapTileLayer) tiledMap.getLayers().get("relief");
        
        if (reliefLayer == null) {
            return;
        }
        
        // Utiliser la projection de la caméra pour dessiner dans le monde du jeu
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0f, 0f, 1f, 1f); // Bleu
        
        // Récupérer les dimensions des tiles
        int tileWidth = mapLoader.getTileWidth();
        int tileHeight = mapLoader.getTileHeight();
        int mapWidth = mapLoader.getMapWidth();
        int mapHeight = mapLoader.getMapHeight();
        
        // Calculer les bounds visibles de la caméra pour optimiser le rendu
        float camLeft = camera.position.x - camera.viewportWidth / 2f;
        float camRight = camera.position.x + camera.viewportWidth / 2f;
        float camBottom = camera.position.y - camera.viewportHeight / 2f;
        float camTop = camera.position.y + camera.viewportHeight / 2f;
        
        // Calculer les indices de tiles visibles (avec une marge pour éviter les coupures)
        int startX = Math.max(0, (int)(camLeft / tileWidth) - 1);
        int endX = Math.min(mapWidth - 1, (int)(camRight / tileWidth) + 1);
        int startY = Math.max(0, (int)(camBottom / tileHeight) - 1);
        int endY = Math.min(mapHeight - 1, (int)(camTop / tileHeight) + 1);
        
        // Parcourir uniquement les tiles visibles à l'écran
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell cell = reliefLayer.getCell(x, y);
                
                // Si la tile existe et n'est pas vide, c'est un relief
                if (cell != null && cell.getTile() != null) {
                    // Calculer la position en pixels (coin bas-gauche de la tile)
                    float pixelX = x * tileWidth;
                    float pixelY = y * tileHeight;
                    
                    // Dessiner un rectangle bleu autour de la tile de relief
                    shapeRenderer.rect(pixelX, pixelY, tileWidth, tileHeight);
                }
            }
        }
        
        shapeRenderer.end();
    }
    
    /**
     * Dessine les over_struct de la map avec des rectangles jaunes pour le débogage.
     */
    private void drawOverStructDebug() {
        if (shapeRenderer == null || mapLoader == null) {
            return;
        }
        
        // Récupérer le TiledMap depuis le mapLoader
        com.badlogic.gdx.maps.tiled.TiledMap tiledMap = mapLoader.getTiledMap();
        if (tiledMap == null) {
            return;
        }
        
        // Récupérer le layer "over_struct"
        com.badlogic.gdx.maps.tiled.TiledMapTileLayer overStructLayer = 
            (com.badlogic.gdx.maps.tiled.TiledMapTileLayer) tiledMap.getLayers().get("over_struct");
        
        if (overStructLayer == null) {
            return;
        }
        
        // Utiliser la projection de la caméra pour dessiner dans le monde du jeu
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 0f, 1f); // Jaune
        
        // Récupérer les dimensions des tiles
        int tileWidth = mapLoader.getTileWidth();
        int tileHeight = mapLoader.getTileHeight();
        int mapWidth = mapLoader.getMapWidth();
        int mapHeight = mapLoader.getMapHeight();
        
        // Calculer les bounds visibles de la caméra pour optimiser le rendu
        float camLeft = camera.position.x - camera.viewportWidth / 2f;
        float camRight = camera.position.x + camera.viewportWidth / 2f;
        float camBottom = camera.position.y - camera.viewportHeight / 2f;
        float camTop = camera.position.y + camera.viewportHeight / 2f;
        
        // Calculer les indices de tiles visibles (avec une marge pour éviter les coupures)
        int startX = Math.max(0, (int)(camLeft / tileWidth) - 1);
        int endX = Math.min(mapWidth - 1, (int)(camRight / tileWidth) + 1);
        int startY = Math.max(0, (int)(camBottom / tileHeight) - 1);
        int endY = Math.min(mapHeight - 1, (int)(camTop / tileHeight) + 1);
        
        // Parcourir uniquement les tiles visibles à l'écran
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell cell = overStructLayer.getCell(x, y);
                
                // Si la tile existe et n'est pas vide, c'est une over_struct
                if (cell != null && cell.getTile() != null) {
                    // Calculer la position en pixels (coin bas-gauche de la tile)
                    float pixelX = x * tileWidth;
                    float pixelY = y * tileHeight;
                    
                    // Dessiner un rectangle jaune autour de la tile d'over_struct
                    shapeRenderer.rect(pixelX, pixelY, tileWidth, tileHeight);
                }
            }
        }
        
        shapeRenderer.end();
    }
    
    /**
     * Gère l'input clavier et met à jour la position et la direction du personnage.
     */
    private void handleInput(float deltaTime) {
        // Si le joueur est mort, ne pas gérer l'input
        if (!player.isAlive()) {
            player.getMovementHandler().stop();
            return;
        }
        
        Direction moveDirection = null;
        
        // Vérifier si Shift est pressé pour courir
        boolean isRunning = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
        
        // Gérer les touches directionnelles : Z/W/Flèche haut, S/Flèche bas, Q/A/Flèche gauche, D/Flèche droite
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.Z)) {
            moveDirection = Direction.UP;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            moveDirection = Direction.DOWN;
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.Q)) {
            moveDirection = Direction.SIDE_LEFT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            moveDirection = Direction.SIDE;
        }
        
        // Déplacer le joueur si une direction est pressée
        if (moveDirection != null) {
            // Vérifier si le mouvement causerait une collision avec un ennemi
            if (canPlayerMove(moveDirection, deltaTime, isRunning)) {
                player.getMovementHandler().move(moveDirection, deltaTime, isRunning);
            } else {
                // Collision avec un ennemi, ne pas bouger
                player.getMovementHandler().stop();
            }
        } else {
            player.getMovementHandler().stop();
        }
        
        // Gérer l'attaque (touche E)
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            player.attack();
        }
        
        // Touche T pour ramasser les collectibles proches
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            pickupCollectibles();
        }
        
        // Touches pour consommer les collectibles
        // Touche 1 pour damage boost
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1)) {
            // Vérifier que Shift n'est pas pressé pour éviter les conflits
            if (!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && !Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                player.useDamageBoost();
            }
        }
        
        // é (e accentué) pour speed boost - Sur AZERTY, c'est la touche 2
        // Note: On utilise NUM_2 mais il faut faire attention aux conflits
        // Alternative: utiliser une autre touche comme V ou E
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2)) {
            // Vérifier que Shift n'est pas pressé pour éviter le conflit avec @
            if (!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && !Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                player.useSpeedBoost();
            }
        }
        
        // " (guillemets) pour shield potion - Sur AZERTY, c'est Shift+3 ou la touche 3
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3)) {
            // Vérifier que Shift n'est pas pressé pour éviter le conflit avec #
            if (!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && !Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                player.useShieldPotion();
            }
        }
        
        // ' (apostrophe) pour heal potion - Sur AZERTY, c'est la touche 4
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_4)) {
            player.useHealPotion();
        }
    }
    
    /**
     * Gère l'attaque du joueur sur les ennemis.
     * Utilise les hitboxes d'attaque dynamiques du joueur pour détecter les collisions.
     */
    private void handlePlayerAttack() {
        if (!player.isAlive() || enemies == null || enemies.isEmpty()) {
            return;
        }
        
        // Vérifier si le joueur est en train d'attaquer
        if (!player.isAttacking() || playerAttackCooldown > 0) {
            return;
        }
        
        // Range d'attaque du joueur : 25x10 pixels, collée à l'extrémité de la hitbox de collision
        Direction attackDirection = player.getCurrentDirection();
        
        // Utiliser la hitbox de collision du joueur (rouge) pour positionner la range
        float playerHitboxX = player.getHitboxX();
        float playerHitboxY = player.getHitboxY();
        float playerHitboxWidth = player.getHitboxWidth();
        float playerHitboxHeight = player.getHitboxHeight();
        float playerHitboxCenterX = playerHitboxX + playerHitboxWidth / 2f;
        float playerHitboxCenterY = playerHitboxY + playerHitboxHeight / 2f;
        
        // Dimensions de la range d'attaque
        float attackRangeWidth = 25f;
        float attackRangeHeight = 10f;
        
        float playerAttackX, playerAttackY;
        float playerAttackWidth, playerAttackHeight;
        
        // Positionner la range collée à l'extrémité de la hitbox selon la direction
        switch (attackDirection) {
            case DOWN:
                // Range en bas de la hitbox (25x10, horizontale)
                playerAttackX = playerHitboxCenterX - attackRangeWidth / 2f;
                playerAttackY = playerHitboxY - attackRangeHeight; // Collée en bas
                playerAttackWidth = attackRangeWidth;
                playerAttackHeight = attackRangeHeight;
                break;
            case UP:
                // Range en haut de la hitbox (25x10, horizontale)
                playerAttackX = playerHitboxCenterX - attackRangeWidth / 2f;
                playerAttackY = playerHitboxY + playerHitboxHeight; // Collée en haut
                playerAttackWidth = attackRangeWidth;
                playerAttackHeight = attackRangeHeight;
                break;
            case SIDE_LEFT:
                // Range à gauche de la hitbox (10x25, verticale)
                playerAttackX = playerHitboxX - attackRangeHeight; // Collée à gauche
                playerAttackY = playerHitboxCenterY - attackRangeWidth / 2f;
                playerAttackWidth = attackRangeHeight; // 10
                playerAttackHeight = attackRangeWidth; // 25
                break;
            case SIDE:
                // Range à droite de la hitbox (10x25, verticale)
                playerAttackX = playerHitboxX + playerHitboxWidth; // Collée à droite
                playerAttackY = playerHitboxCenterY - attackRangeWidth / 2f;
                playerAttackWidth = attackRangeHeight; // 10
                playerAttackHeight = attackRangeWidth; // 25
                break;
            default:
                playerAttackX = playerHitboxCenterX - attackRangeWidth / 2f;
                playerAttackY = playerHitboxCenterY - attackRangeHeight / 2f;
                playerAttackWidth = attackRangeWidth;
                playerAttackHeight = attackRangeHeight;
                break;
        }
        
        // Vérifier les collisions avec tous les ennemis
        for (Enemy enemy : enemies) {
            if (enemy == null || !enemy.isAlive()) {
                continue;
            }
            
            // Position du centre du sprite de l'ennemi
            float enemyCenterX = enemy.getX() + enemy.getWidth() / 2f;
            float enemyCenterY = enemy.getY() + enemy.getHeight() / 2f;
            
            // Utiliser la hitbox fixe de l'ennemi (17x16 pour slimes, 30x30 pour vampires)
            float enemyHitboxWidth = enemy.getHitboxWidth();
            float enemyHitboxHeight = enemy.getHitboxHeight();
            
            // Position de la hitbox de l'ennemi (centrée sur le sprite)
            float enemyHitboxX = enemyCenterX - enemyHitboxWidth / 2f;
            float enemyHitboxY = enemyCenterY - enemyHitboxHeight / 2f;
            
            // Vérifier si les rectangles se chevauchent (collision AABB)
            boolean hitboxesCollide = (playerAttackX < enemyHitboxX + enemyHitboxWidth &&
                                       playerAttackX + playerAttackWidth > enemyHitboxX &&
                                       playerAttackY < enemyHitboxY + enemyHitboxHeight &&
                                       playerAttackY + playerAttackHeight > enemyHitboxY);
            
            // Si les hitboxes se touchent, infliger des dégâts à l'ennemi
            if (hitboxesCollide) {
                int baseDamage = 10;
                int totalDamage = baseDamage + player.getDamageBoost();
                enemy.takeDamage(totalDamage);
                playerAttackCooldown = playerAttackCooldownTime;
                // Ne pas infliger de dégâts à plusieurs ennemis en une seule frame
                break;
            }
        }
    }
    
    /**
     * Vérifie si l'ennemi est mort et ajoute des items à l'inventaire.
     */
    private void checkEnemyDeath() {
        if (enemy == null || enemy.isAlive()) {
            // Réinitialiser le flag si l'ennemi est vivant
            enemyDeathLooted = false;
            return;
        }
        
        // Si l'ennemi est mort et qu'on n'a pas encore donné les items
        if (!enemyDeathLooted) {
            // L'ennemi est mort, ajouter des items à l'inventaire
            // Ajouter aléatoirement des items (shield ou heal)
            if (Math.random() < 0.5) {
                player.getInventory().addItem(Inventory.ItemType.HEAL);
                Gdx.app.log("GameScreen", "Item HEAL ajouté à l'inventaire !");
            } else {
                player.getInventory().addItem(Inventory.ItemType.SHIELD);
                Gdx.app.log("GameScreen", "Item SHIELD ajouté à l'inventaire !");
            }
            
            enemyDeathLooted = true;
        }
    }
    
    /**
     * Fait suivre la caméra au joueur.
     * La caméra ne bouge que si la position du joueur a réellement changé.
     */
    private void updateCamera() {
        if (player == null) {
            return;
        }
        
        // Récupérer la position actuelle du joueur
        float currentPlayerX = player.getX();
        float currentPlayerY = player.getY();
        
        // Vérifier si la position a changé (avec une tolérance pour éviter les micro-mouvements)
        float tolerance = 0.5f;
        boolean positionChanged = Math.abs(currentPlayerX - lastPlayerX) > tolerance || 
                                   Math.abs(currentPlayerY - lastPlayerY) > tolerance;
        
        // Mettre à jour la caméra seulement si la position a changé
        if (positionChanged || lastPlayerX < 0 || lastPlayerY < 0) {
            // Centrer la caméra sur le joueur
            float playerCenterX = currentPlayerX + player.getWidth() / 2f;
            float playerCenterY = currentPlayerY + player.getHeight() / 2f;
            
            // Limites de la caméra pour qu'elle ne sorte pas de la map (800x640 px)
            // La caméra fait 180x140 px, donc :
            // - Largeur : 90px < camera.x < 710px (pour ne pas voir les bords)
            // - Hauteur : 70px < camera.y < 570px (pour ne pas voir les bords)
            float cameraMinX = 90f;
            float cameraMaxX = 710f;
            float cameraMinY = 70f;
            float cameraMaxY = 570f;
            
            // Clamper la position de la caméra dans les limites
            float clampedCameraX = Math.max(cameraMinX, Math.min(cameraMaxX, playerCenterX));
            float clampedCameraY = Math.max(cameraMinY, Math.min(cameraMaxY, playerCenterY));
            
            // Positionner la caméra (clampée si nécessaire)
            camera.position.set(clampedCameraX, clampedCameraY, 0);
            
            // Mettre à jour la position précédente
            lastPlayerX = currentPlayerX;
            lastPlayerY = currentPlayerY;
        }
    }
    
    /**
     * Limite le joueur dans les bounds de la map.
     */
    private void clampToMapBounds() {
        if (mapLoader == null || player == null) {
            return;
        }
        
        // Récupérer les dimensions de la map en pixels
        int mapWidthPixels = mapLoader.getMapWidth() * mapLoader.getTileWidth();
        int mapHeightPixels = mapLoader.getMapHeight() * mapLoader.getTileHeight();
        
        float playerWidth = player.getWidth();
        float playerHeight = player.getHeight();
        
        // Limiter le joueur dans les bounds de la map
        float minX = 0;
        float minY = 0;
        float maxX = mapWidthPixels - playerWidth;
        float maxY = mapHeightPixels - playerHeight;
        
        float x = Math.max(minX, Math.min(maxX, player.getX()));
        float y = Math.max(minY, Math.min(maxY, player.getY()));
        
        player.setX(x);
        player.setY(y);
    }
    
    /**
     * Vérifie si le joueur peut se déplacer dans une direction sans entrer en collision avec un ennemi.
     * 
     * @param direction Direction du mouvement
     * @param deltaTime Temps écoulé
     * @param isRunning Si le joueur court
     * @return true si le mouvement est possible (pas de collision avec un ennemi)
     */
    private boolean canPlayerMove(Direction direction, float deltaTime, boolean isRunning) {
        if (player == null || !player.isAlive()) {
            return true;
        }
        
        if (enemies == null) {
            return true;
        }
        
        // Calculer la nouvelle position du joueur
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
        
        // Calculer la hitbox du joueur à sa nouvelle position
        float playerHitboxWidth = player.getHitboxWidth();
        float playerHitboxHeight = player.getHitboxHeight();
        float playerSpriteCenterX = newX + player.getWidth() / 2f;
        float playerSpriteCenterY = newY + player.getHeight() / 2f;
        float newPlayerHitboxX = playerSpriteCenterX - playerHitboxWidth / 2f;
        float newPlayerHitboxY = playerSpriteCenterY - playerHitboxHeight / 2f;
        
        // Vérifier les collisions avec chaque ennemi
        for (Enemy enemy : enemies) {
            if (enemy == null || !enemy.isAlive()) {
                continue;
            }
            
            // Hitbox de l'ennemi
            float enemyHitboxX = enemy.getHitboxX();
            float enemyHitboxY = enemy.getHitboxY();
            float enemyHitboxWidth = enemy.getHitboxWidth();
            float enemyHitboxHeight = enemy.getHitboxHeight();
            
            // Vérifier si les hitboxes se chevauchent (collision AABB)
            boolean wouldCollide = (newPlayerHitboxX < enemyHitboxX + enemyHitboxWidth &&
                                   newPlayerHitboxX + playerHitboxWidth > enemyHitboxX &&
                                   newPlayerHitboxY < enemyHitboxY + enemyHitboxHeight &&
                                   newPlayerHitboxY + playerHitboxHeight > enemyHitboxY);
            
            if (wouldCollide) {
                // Collision détectée, le mouvement n'est pas possible
                return false;
            }
        }
        
        return true; // Pas de collision, le mouvement est possible
    }
    
    /**
     * Résout les collisions entre le joueur et les ennemis pour empêcher le chevauchement.
     * Si les hitboxes se chevauchent, remet le joueur à sa position précédente.
     * Ne pousse PAS l'ennemi.
     */
    private void resolveEntityCollisions() {
        if (player == null || enemies == null || !player.isAlive()) {
            return;
        }
        
        // Hitbox du joueur
        float playerHitboxX = player.getHitboxX();
        float playerHitboxY = player.getHitboxY();
        float playerHitboxWidth = player.getHitboxWidth();
        float playerHitboxHeight = player.getHitboxHeight();
        
        // Vérifier les collisions avec chaque ennemi
        for (Enemy enemy : enemies) {
            if (enemy == null || !enemy.isAlive()) {
                continue;
            }
            
            // Hitbox de l'ennemi
            float enemyHitboxX = enemy.getHitboxX();
            float enemyHitboxY = enemy.getHitboxY();
            float enemyHitboxWidth = enemy.getHitboxWidth();
            float enemyHitboxHeight = enemy.getHitboxHeight();
            
            // Vérifier si les hitboxes se chevauchent (collision AABB)
            boolean colliding = (playerHitboxX < enemyHitboxX + enemyHitboxWidth &&
                               playerHitboxX + playerHitboxWidth > enemyHitboxX &&
                               playerHitboxY < enemyHitboxY + enemyHitboxHeight &&
                               playerHitboxY + playerHitboxHeight > enemyHitboxY);
            
            if (colliding) {
                // Calculer le chevauchement
                float overlapX = Math.min(playerHitboxX + playerHitboxWidth - enemyHitboxX,
                                         enemyHitboxX + enemyHitboxWidth - playerHitboxX);
                float overlapY = Math.min(playerHitboxY + playerHitboxHeight - enemyHitboxY,
                                         enemyHitboxY + enemyHitboxHeight - playerHitboxY);
                
                // Calculer les centres pour déterminer la direction de séparation
                float playerCenterX = playerHitboxX + playerHitboxWidth / 2f;
                float playerCenterY = playerHitboxY + playerHitboxHeight / 2f;
                float enemyCenterX = enemyHitboxX + enemyHitboxWidth / 2f;
                float enemyCenterY = enemyHitboxY + enemyHitboxHeight / 2f;
                
                // Séparer en reculant uniquement le joueur (ne pas pousser l'ennemi)
                if (overlapX < overlapY) {
                    // Séparation horizontale : reculer le joueur
                    if (playerCenterX < enemyCenterX) {
                        // Le joueur est à gauche, le reculer vers la gauche
                        player.setX(player.getX() - overlapX);
                    } else {
                        // Le joueur est à droite, le reculer vers la droite
                        player.setX(player.getX() + overlapX);
                    }
                } else {
                    // Séparation verticale : reculer le joueur
                    if (playerCenterY < enemyCenterY) {
                        // Le joueur est en bas, le reculer vers le bas
                        player.setY(player.getY() - overlapY);
                    } else {
                        // Le joueur est en haut, le reculer vers le haut
                        player.setY(player.getY() + overlapY);
                    }
                }
            }
        }
    }
    
    /**
     * Limite le joueur dans les bounds de l'écran (ancienne méthode, remplacée par clampToMapBounds).
     */
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
    
    /**
     * Limite l'ennemi dans les bounds de l'écran.
     */
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
        // Mettre à jour le viewport pour le nouveau format d'écran
        // Le StretchViewport étirera automatiquement les 180x140 pixels pour remplir l'écran
        viewport.update(width, height);
        camera.update();
    }
    
    @Override
    public void pause() {
        // Mettre en pause si nécessaire
    }
    
    @Override
    public void resume() {
        // Reprendre si nécessaire
    }
    
    @Override
    public void hide() {
        // Appelé quand l'écran devient invisible
    }
    
    /**
     * Dessine l'action panel avec 4 slots en bas centré.
     * sprite3 et sprite4: carrés (slots) pour placer les collectibles
     * sprite1: damage boost collectible
     * sprite2: speed boost collectible
     * sprite5: shield potion
     * sprite6: heal potion
     */
    private void renderActionPanel() {
        if (actionPanelMapping == null || player == null) {
            return;
        }
        
        // Utiliser la caméra UI pour le rendu en coordonnées écran
        batch.setProjectionMatrix(uiCamera.combined);
        
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        
        // Scale pour agrandir les slots
        float slotScale = 4f;
        
        // Récupérer les sprites des slots (sprite3 et sprite4 sont les carrés)
        com.badlogic.gdx.graphics.g2d.TextureRegion slotSprite = actionPanelMapping.getSprite("sprite3");
        if (slotSprite == null) {
            slotSprite = actionPanelMapping.getSprite("sprite4");
        }
        
        if (slotSprite == null) {
            return;
        }
        
        float slotWidth = slotSprite.getRegionWidth() * slotScale;
        float slotHeight = slotSprite.getRegionHeight() * slotScale;
        float spacing = 10f; // Espacement entre les slots
        
        // Calculer la position pour centrer les 4 slots en bas
        float totalWidth = slotWidth * 4 + spacing * 3;
        float startX = (screenWidth - totalWidth) / 2f;
        float slotY = 20f; // Position en bas de l'écran
        
        // Afficher les 4 slots
        for (int i = 0; i < 4; i++) {
            float slotX = startX + i * (slotWidth + spacing);
            batch.draw(slotSprite, slotX, slotY, slotWidth, slotHeight);
            
            // Afficher le collectible dans le slot si disponible
            Inventory.ItemType itemType = getItemTypeForSlot(i);
            int itemCount = itemType != null ? player.getInventory().getItemCount(itemType) : 0;
            if (itemType != null && itemCount > 0) {
                com.badlogic.gdx.graphics.g2d.TextureRegion itemSprite = getSpriteForItemType(itemType);
                if (itemSprite != null) {
                    float itemWidth = itemSprite.getRegionWidth() * slotScale;
                    float itemHeight = itemSprite.getRegionHeight() * slotScale;
                    // Centrer l'item dans le slot
                    float itemX = slotX + (slotWidth - itemWidth) / 2f;
                    float itemY = slotY + (slotHeight - itemHeight) / 2f;
                    batch.draw(itemSprite, itemX, itemY, itemWidth, itemHeight);
                    
                    // Afficher le compteur en haut à droite du slot
                    if (itemCount > 1) {
                        String countText = String.valueOf(itemCount);
                        float textX = slotX + slotWidth - 5f; // Position à droite
                        float textY = slotY + slotHeight - 5f; // Position en haut
                        font.setColor(1f, 1f, 1f, 1f); // Blanc
                        font.draw(batch, countText, textX, textY);
                    }
                }
            }
        }
    }
    
    /**
     * Retourne le type d'item pour un slot donné.
     * Slot 0: DAMAGE_BOOST, Slot 1: SPEED_BOOST, Slot 2: SHIELD_POTION, Slot 3: HEAL_POTION
     */
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
    
    /**
     * Retourne le sprite correspondant au type d'item.
     */
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
    
    /**
     * Dessine le character panel selon l'état du joueur.
     * sprite2: full health et full shield
     * sprite1: mort (ni health ni shield)
     */
    private void renderCharacterPanel() {
        if (characterPanelMapping == null || player == null) {
            return;
        }
        
        int health = player.getHealth();
        int maxHealth = player.getMaxHealth();
        int shield = player.getShield();
        int maxShield = player.getMaxShield();
        
        // Déterminer quel sprite afficher
        String spriteName;
        if (!player.isAlive() || (health == 0 && shield == 0)) {
            // sprite1: mort ou ni health ni shield
            spriteName = "sprite1";
        } else if (health == maxHealth && shield == maxShield && maxShield > 0) {
            // sprite2: full health et full shield
            spriteName = "sprite2";
        } else {
            // Pour l'instant, on affiche sprite1 par défaut (l'entre-deux sera géré plus tard)
            spriteName = "sprite1";
        }
        
        // Récupérer le sprite
        com.badlogic.gdx.graphics.g2d.TextureRegion sprite = characterPanelMapping.getSprite(spriteName);
        if (sprite != null) {
            // Scale pour agrandir le character panel
            float characterPanelScale = 3f;
            float spriteWidth = sprite.getRegionWidth() * characterPanelScale;
            float spriteHeight = sprite.getRegionHeight() * characterPanelScale;
            
            // Positionner le panel en haut à gauche (ou ailleurs selon vos préférences)
            float x = 10f;
            float y = Gdx.graphics.getHeight() - spriteHeight - 10f;
            batch.draw(sprite, x, y, spriteWidth, spriteHeight);
        }
    }
    
    /**
     * Vérifie si des ennemis sont morts et drop des collectibles aléatoirement.
     */
    private void checkEnemyDeathsAndDropCollectibles() {
        if (enemies == null || actionPanelMapping == null) {
            return;
        }
        
        // Utiliser un Iterator pour éviter les problèmes de modification de liste pendant l'itération
        java.util.Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            if (enemy != null && !enemy.isAlive()) {
                // Drop aléatoire d'un collectible (25% de chance pour chaque type)
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
                
                // Créer le collectible à la position de l'ennemi
                float enemyX = enemy.getX() + enemy.getWidth() / 2f;
                float enemyY = enemy.getY() + enemy.getHeight() / 2f;
                
                Collectible collectible = new Collectible(enemyX, enemyY, itemType, actionPanelMapping);
                collectibles.add(collectible);
                
                Gdx.app.log("GameScreen", "Collectible droppé: " + itemType + " à (" + enemyX + ", " + enemyY + ")");
                
                // Retirer l'ennemi de la liste pour éviter de drop plusieurs fois
                iterator.remove();
                break; // Ne traiter qu'un ennemi par frame
            }
        }
    }
    
    /**
     * Ramasse les collectibles proches du joueur avec la touche T.
     */
    private void pickupCollectibles() {
        if (collectibles == null || player == null) {
            return;
        }
        
        float playerX = player.getX();
        float playerY = player.getY();
        float playerWidth = player.getWidth();
        float playerHeight = player.getHeight();
        
        // Parcourir les collectibles et ramasser ceux qui sont proches
        java.util.Iterator<Collectible> iterator = collectibles.iterator();
        while (iterator.hasNext()) {
            Collectible collectible = iterator.next();
            if (collectible != null && !collectible.isCollected()) {
                if (collectible.canBePickedUp(playerX, playerY, playerWidth, playerHeight)) {
                    // Ajouter à l'inventaire
                    if (player.getInventory().addItem(collectible.getItemType())) {
                        collectible.collect();
                        Gdx.app.log("GameScreen", "Collectible ramassé: " + collectible.getItemType());
                        iterator.remove(); // Retirer de la liste
                    } else {
                        Gdx.app.log("GameScreen", "Inventaire plein, impossible de ramasser le collectible");
                    }
                }
            }
        }
    }
    
    /**
     * Nettoie les collectibles collectés de la liste.
     */
    private void cleanupCollectedCollectibles() {
        if (collectibles == null) {
            return;
        }
        
        collectibles.removeIf(Collectible::isCollected);
    }
    
    /**
     * Initialise les collisions pour le joueur et l'ennemi.
     */
    private void initializeCollisions() {
        if (mapLoader == null) {
            return;
        }
        
        // Configurer les collisions pour le joueur en utilisant sa hitbox fixe (13x15)
        // Le sprite rendu fait 32x32 pixels (64x64 avec scale 0.5)
        if (player != null && player.getHitboxWidth() > 0 && player.getHitboxHeight() > 0) {
            float spriteWidth = 32f; // Sprite rendu fait 32x32 pixels
            float spriteHeight = 32f;
            CollisionHandler playerCollision = new CollisionHandler(
                mapLoader, player.getHitboxWidth(), player.getHitboxHeight(), spriteWidth, spriteHeight);
            player.getMovementHandler().setCollisionHandler(playerCollision);
            Gdx.app.log("GameScreen", String.format("Collisions initialisées pour le joueur (hitbox: %.1fx%.1f pixels, sprite: %.1fx%.1f) à la position (%.1f, %.1f)", 
                player.getHitboxWidth(), player.getHitboxHeight(), spriteWidth, spriteHeight, player.getX(), player.getY()));
            
            // Vérifier si la position initiale est valide (en utilisant la position du sprite)
            if (playerCollision != null) {
                boolean isValid = playerCollision.isValidPosition(player.getX(), player.getY());
                Gdx.app.log("GameScreen", String.format("Position initiale du joueur valide : %s", isValid));
            }
        } else {
            Gdx.app.log("GameScreen", "Impossible d'initialiser les collisions : hitbox du joueur invalide");
        }
        
        // Configurer les collisions pour les ennemis (slimes) en utilisant leur hitbox
        if (enemies != null) {
            for (Enemy enemy : enemies) {
                if (enemy != null && enemy.getHitboxWidth() > 0 && enemy.getHitboxHeight() > 0) {
                    CollisionHandler enemyCollision = new CollisionHandler(
                        mapLoader, enemy.getHitboxWidth(), enemy.getHitboxHeight());
                    enemy.getMovementHandler().setCollisionHandler(enemyCollision);
                    Gdx.app.log("GameScreen", String.format("Collisions initialisées pour un ennemi (hitbox: %.1fx%.1f pixels)", 
                        enemy.getHitboxWidth(), enemy.getHitboxHeight()));
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
        // Map loader dispose
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
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
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

