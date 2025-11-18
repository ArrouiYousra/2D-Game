package com.tlse1.twodgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tlse1.twodgame.TwoDGame;
import com.tlse1.twodgame.entities.Enemy;
import com.tlse1.twodgame.entities.Inventory;
import com.tlse1.twodgame.entities.Player;
import com.tlse1.twodgame.entities.handlers.CollisionHandler;
import com.tlse1.twodgame.managers.JsonMapLoader;
import com.tlse1.twodgame.ui.HealthBar;
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
    private Enemy enemy;
    
    // Map
    private JsonMapLoader mapLoader;
    
    // Character panel
    private CharacterPanelMapping characterPanelMapping;
    
    // Action panel
    private ActionPanelMapping actionPanelMapping;
    
    // Health bar
    private HealthBar healthBar;
    
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
        
        // Positionner le joueur en bas à gauche de la map (offset pour qu'il ne soit pas collé au bord)
        float playerStartX = 50f; // Offset depuis le bord gauche
        float playerStartY = 50f; // Offset depuis le bas
        player.setX(playerStartX);
        player.setY(playerStartY);
        
        // Initialiser la caméra sur le joueur (sera ajusté après le premier rendu quand on connaît sa taille)
        // La caméra sera mise à jour dans updateCamera() après le premier rendu
        
        // Créer l'ennemi (vampire) - DÉSACTIVÉ TEMPORAIREMENT
        // enemy = new Enemy(screenWidth * 0.2f, screenHeight * 0.2f);
        // enemy.setTarget(player); // L'ennemi cible le joueur
        
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
        
        // Gérer l'attaque du joueur sur l'ennemi - DÉSACTIVÉ TEMPORAIREMENT
        // handlePlayerAttack();
        
        // Vérifier si l'ennemi est mort et ajouter des items à l'inventaire - DÉSACTIVÉ TEMPORAIREMENT
        // checkEnemyDeath();
        
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
        // Afficher l'ennemi - DÉSACTIVÉ TEMPORAIREMENT
        // if (enemy != null) {
        //     enemy.render(batch);
        // }
        
        // Dessiner le character panel
        renderCharacterPanel();
        
        // Action panel désactivé
        // renderActionPanel();
        
        batch.end();
        
        // Dessiner la barre de santé (en coordonnées écran)
        if (player != null && healthBar != null) {
            // Mettre à jour la barre avec la santé actuelle du joueur
            healthBar.update(player.getHealth(), player.getMaxHealth());
            
            // Mettre à jour la position Y de la barre en cas de redimensionnement
            float screenHeight = Gdx.graphics.getHeight();
            healthBar.setPosition(10f, screenHeight - healthBar.getHeight() - 10f);
            
            // Dessiner la barre avec SpriteBatch en coordonnées écran
            batch.setProjectionMatrix(uiCamera.combined);
            batch.begin();
            healthBar.render(batch);
            batch.end();
        }
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
            player.getMovementHandler().move(moveDirection, deltaTime, isRunning);
        } else {
            player.getMovementHandler().stop();
        }
        
        // Gérer l'attaque (touche E)
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            player.attack();
        }
        
        // Gérer l'utilisation des items
        // Touche 1 pour utiliser un heal
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1)) {
            player.useHealItem();
        }
        
        // Touche 2 pour utiliser un shield
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2)) {
            player.useShieldItem();
        }
    }
    
    /**
     * Gère l'attaque du joueur sur l'ennemi.
     * Inflige des dégâts si le joueur attaque et est proche de l'ennemi.
     */
    private void handlePlayerAttack() {
        if (enemy == null || !enemy.isAlive() || !player.isAlive()) {
            return;
        }
        
        // Vérifier si le joueur est en train d'attaquer
        if (player.isAttacking() && playerAttackCooldown <= 0) {
            // Calculer la distance à l'ennemi
            float dx = enemy.getX() - player.getX();
            float dy = enemy.getY() - player.getY();
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            
            // Si l'ennemi est dans la portée d'attaque
            if (distance <= playerAttackRange) {
                // Infliger des dégâts à l'ennemi
                enemy.takeDamage(10); // 10 dégâts par attaque
                playerAttackCooldown = playerAttackCooldownTime;
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
            
            // Positionner la caméra pour centrer le joueur
            camera.position.set(playerCenterX, playerCenterY, 0);
            
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
     * Dessine l'action panel avec l'inventaire et les items utilisables.
     * sprite1: inventaire (vide de base, se remplit avec les items collectés)
     * sprite2: shield item (affiché dans les carrés de l'inventaire si disponible)
     * sprite3: heal item (affiché dans les carrés de l'inventaire si disponible)
     */
    private void renderActionPanel() {
        if (actionPanelMapping == null || player == null) {
            return;
        }
        
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        
        // Scale pour agrandir l'action panel
        float panelScale = 5f; // Augmenté de 3f à 5f
        
        // Afficher sprite1 (inventaire) - positionné à gauche, juste en dessous du CharacterPanel
        com.badlogic.gdx.graphics.g2d.TextureRegion inventorySprite = actionPanelMapping.getSprite("sprite1");
        if (inventorySprite != null) {
            float inventoryWidth = inventorySprite.getRegionWidth() * panelScale;
            float inventoryHeight = inventorySprite.getRegionHeight() * panelScale;
            
            // Calculer la position du CharacterPanel pour placer l'inventaire juste en dessous
            float characterPanelX = 10f;
            float characterPanelScale = 3f;
            // Récupérer le sprite actuel du CharacterPanel (même logique que renderCharacterPanel)
            String characterSpriteName = "sprite1"; // Par défaut
            if (characterPanelMapping != null && player != null) {
                int health = player.getHealth();
                int maxHealth = player.getMaxHealth();
                int shield = player.getShield();
                int maxShield = player.getMaxShield();
                if (!player.isAlive() || (health == 0 && shield == 0)) {
                    characterSpriteName = "sprite1";
                } else if (health == maxHealth && shield == maxShield && maxShield > 0) {
                    characterSpriteName = "sprite2";
                }
            }
            com.badlogic.gdx.graphics.g2d.TextureRegion characterSprite = characterPanelMapping != null ? 
                characterPanelMapping.getSprite(characterSpriteName) : null;
            float characterPanelHeight = characterSprite != null ? 
                characterSprite.getRegionHeight() * characterPanelScale : 0f;
            float characterPanelY = screenHeight - characterPanelHeight - 10f;
            
            // Positionner l'inventaire vertical à gauche, collé au bord, juste en dessous du CharacterPanel
            // Après rotation de -90°, inventoryWidth devient la hauteur et inventoryHeight devient la largeur
            // Pour coller au bord gauche, le point de rotation doit être à inventoryHeight / 2f (largeur après rotation / 2)
            float inventoryX = 0f; // Collé au bord gauche de l'écran
            float inventoryY = characterPanelY - inventoryWidth - 5f; // Juste en dessous du CharacterPanel avec espacement
            
            // Dessiner l'inventaire agrandi avec rotation de -90° (vers la gauche, vertical)
            // Le point de rotation est au centre du sprite
            float originX = inventoryWidth / 2f;
            float originY = inventoryHeight / 2f;
            // Position du point de rotation : x doit être à inventoryHeight / 2f pour que le bord gauche soit à 0
            // y doit être à inventoryY + inventoryWidth / 2f pour centrer verticalement
            batch.draw(inventorySprite, 
                inventoryHeight / 2f, // Position X du point de rotation (pour que le bord gauche soit à 0)
                inventoryY + inventoryWidth / 2f, // Position Y du point de rotation
                originX, originY, 
                inventoryWidth, inventoryHeight, 
                1f, 1f, 
                -90f); // Rotation de -90 degrés (vers la gauche, vertical)
            
            // Calculer les positions des items en utilisant les coordonnées absolues de l'image
            // sprite1: x=12, y=44, width=168, height=20
            // sprite2 (shield): x=163, y=82, width=9, height=12
            // sprite3 (heal): x=179, y=82, width=9, height=12
            
            // Position relative des items par rapport à sprite1
            // sprite2 est à x=163 dans l'image, sprite1 commence à x=12
            // Donc sprite2 est à (163-12) = 151 pixels depuis le début de sprite1
            float sprite1StartX = 12f; // Position X de sprite1 dans l'image originale
            float sprite2AbsX = 163f; // Position X absolue de sprite2 dans l'image
            float sprite3AbsX = 179f; // Position X absolue de sprite3 dans l'image
            float sprite2RelX = sprite2AbsX - sprite1StartX; // Position relative de sprite2 (151px)
            float sprite3RelX = sprite3AbsX - sprite1StartX; // Position relative de sprite3 (167px)
            
            // Position Y relative : sprite1 commence à y=44, sprite2/sprite3 à y=82
            // Donc sprite2/sprite3 sont à (82-44) = 38 pixels depuis le haut de sprite1
            // Mais sprite1 fait 20px de haut, donc les items sont en dehors de sprite1 verticalement
            // On va les centrer verticalement dans sprite1 quand même
            float sprite1StartY = 44f;
            float sprite2AbsY = 82f;
            float sprite2RelY = sprite2AbsY - sprite1StartY; // 38px, mais sprite1 fait 20px de haut
            
            // Calculer les positions des items dans l'inventaire agrandi
            // Les items doivent être centrés dans leurs carrés respectifs
            // sprite1 fait 168px de large, on suppose 8 carrés avec des bordures
            // sprite2 (shield) est à x=151 (relatif), sprite3 (heal) à x=167 (relatif)
            
            // Analysons la structure : sprite1 commence à x=12, les items sont à x=163 et x=179
            // Cela suggère que les carrés sont probablement de ~20-21px avec des bordures
            // Calculons plus précisément en utilisant les positions exactes
            
            // Si on regarde les positions : sprite2 à 151px, sprite3 à 167px depuis le début de sprite1
            // La différence entre les deux est 16px, ce qui suggère qu'ils sont dans des carrés adjacents
            // Supposons que chaque carré fait environ 20-21px avec un petit espacement
            
            // Calculer la taille d'un carré en analysant la structure
            // sprite1 fait 168px de large, on suppose 8 carrés
            // En regardant l'image de référence, les carrés semblent bien espacés
            float sprite1Width = 168f;
            float numSlots = 8f; // 8 carrés dans l'inventaire
            float squareSize = sprite1Width / numSlots; // 21px par carré exactement
            
            // Les items doivent être centrés dans leurs carrés respectifs
            // Dans l'image de référence : heal (rouge) est dans le premier slot, shield (bleu) dans le deuxième
            int healSlotIndex = 0; // Premier slot pour heal (rouge)
            int shieldSlotIndex = 1; // Deuxième slot pour shield (bleu)
            
            // Calculer le centre de chaque carré (en tenant compte de la rotation de -90°, vertical)
            float squareSizeScaled = squareSize * panelScale;
            
            // Position du centre de rotation de l'inventaire (même position que dans batch.draw)
            float inventoryCenterX = inventoryHeight / 2f; // Position X du point de rotation
            float inventoryCenterY = inventoryY + inventoryWidth / 2f; // Position Y du point de rotation
            
            // Après rotation de -90°, les slots sont maintenant verticaux
            // Le premier slot est en bas, le dernier en haut
            // Calculer les positions des items en tenant compte de la rotation
            float firstSquareCenterY = inventoryCenterY - (inventoryWidth / 2f) + squareSizeScaled / 2f;
            
            // Calculer les centres des carrés pour heal et shield (après rotation, vertical)
            float healSquareCenterY = firstSquareCenterY + healSlotIndex * squareSizeScaled;
            float shieldSquareCenterY = firstSquareCenterY + shieldSlotIndex * squareSizeScaled;
            
            // Afficher sprite3 (heal) dans le premier carré si disponible
            int healCount = player.getInventory().getItemCount(Inventory.ItemType.HEAL);
            if (healCount > 0) {
                com.badlogic.gdx.graphics.g2d.TextureRegion healSprite = actionPanelMapping.getSprite("sprite3");
                if (healSprite != null) {
                    float healWidth = healSprite.getRegionWidth() * panelScale;
                    float healHeight = healSprite.getRegionHeight() * panelScale;
                    // Centrer parfaitement l'item dans son carré (après rotation, vertical)
                    float healX = inventoryCenterX - healWidth / 2f;
                    float healY = healSquareCenterY - healHeight / 2f;
                    batch.draw(healSprite, healX, healY, healWidth, healHeight);
                }
            }
            
            // Afficher sprite2 (shield) dans le deuxième carré si disponible
            int shieldCount = player.getInventory().getItemCount(Inventory.ItemType.SHIELD);
            if (shieldCount > 0) {
                com.badlogic.gdx.graphics.g2d.TextureRegion shieldSprite = actionPanelMapping.getSprite("sprite2");
                if (shieldSprite != null) {
                    float shieldWidth = shieldSprite.getRegionWidth() * panelScale;
                    float shieldHeight = shieldSprite.getRegionHeight() * panelScale;
                    // Centrer parfaitement l'item dans son carré (après rotation, vertical)
                    float shieldX = inventoryCenterX - shieldWidth / 2f;
                    float shieldY = shieldSquareCenterY - shieldHeight / 2f;
                    batch.draw(shieldSprite, shieldX, shieldY, shieldWidth, shieldHeight);
                }
            }
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
     * Initialise les collisions pour le joueur et l'ennemi.
     */
    private void initializeCollisions() {
        if (mapLoader == null) {
            return;
        }
        
        // Configurer les collisions pour le joueur
        if (player != null && player.getWidth() > 0 && player.getHeight() > 0) {
            CollisionHandler playerCollision = new CollisionHandler(
                mapLoader, player.getWidth(), player.getHeight());
            player.getMovementHandler().setCollisionHandler(playerCollision);
            Gdx.app.log("GameScreen", String.format("Collisions initialisées pour le joueur (%.1fx%.1f pixels)", 
                player.getWidth(), player.getHeight()));
        } else {
            Gdx.app.log("GameScreen", "Impossible d'initialiser les collisions : dimensions du joueur invalides");
        }
        
        // Configurer les collisions pour l'ennemi - DÉSACTIVÉ TEMPORAIREMENT
        // if (enemy != null && enemy.getWidth() > 0 && enemy.getHeight() > 0) {
        //     CollisionHandler enemyCollision = new CollisionHandler(
        //         mapLoader, enemy.getWidth(), enemy.getHeight());
        //     enemy.getMovementHandler().setCollisionHandler(enemyCollision);
        //     Gdx.app.log("GameScreen", "Collisions initialisées pour l'ennemi");
        // }
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
    }
}

