package com.tlse1.twodgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.tlse1.twodgame.TwoDGame;
import com.tlse1.twodgame.entities.Enemy;
import com.tlse1.twodgame.entities.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Écran de jeu principal avec TileMap en arrière-plan.
 * Contient la logique du jeu avec le joueur et les ennemis.
 */
public class GameScreen implements Screen {
    
    private TwoDGame game;
    private SpriteBatch batch;
    private Player player;
    private List<Enemy> enemies;
    
    // TileMap
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMap tiledMap;
    
    private static final String JSON_PATH = "map.json";
    
    public GameScreen(TwoDGame game) {
        this.game = game;
    }
    
    @Override
    public void show() {
        batch = new SpriteBatch();
        
        // Charger la TileMap
        loadTileMap();
        
        // Créer le joueur au centre de l'écran (90, 70 pour une caméra 180x140)
        float startX = 90f - 8f;  // Centré horizontalement (90 - moitié de 16)
        float startY = 70f - 8f;  // Centré verticalement (70 - moitié de 16)
        float speed = 150f;
        int maxHealth = 100;
        
        player = new Player(startX, startY, speed, maxHealth);
        
        // Ajuster la taille du personnage à 16x16
        player.setScale(1f);  // Échelle 1:1 au lieu de 4x
        
        // Créer des ennemis
        enemies = new ArrayList<>();
        createEnemies();
    }
    
    /**
     * Charge la TileMap depuis le JSON
     */
    private void loadTileMap() {
        System.out.println("=== CHARGEMENT DE LA CARTE DANS GAMESCREEN ===");
        
        try {
            // Caméra pour la carte - 180x140
            camera = new OrthographicCamera();
            camera.setToOrtho(false, 180, 140);
            camera.update();
            
            // Vérification du JSON
            FileHandle jsonFile = Gdx.files.local(JSON_PATH);
            if (!jsonFile.exists()) {
                System.err.println("ATTENTION: " + JSON_PATH + " introuvable. Pas de carte affichée.");
                return;
            }
            
            JsonReader jsonReader = new JsonReader();
            JsonValue root = jsonReader.parse(jsonFile);
            
            // Lecture tileset
            JsonValue tileset = root.get("tileset");
            String tilesetPath = tileset.getString("path");
            int tileW = tileset.getInt("tileWidth");
            int tileH = tileset.getInt("tileHeight");
            int cols = tileset.getInt("columns");
            int rows = tileset.getInt("rows");
            
            // Chargement tileset
            FileHandle tilesetFile = Gdx.files.internal(tilesetPath);
            if (!tilesetFile.exists()) {
                System.err.println("ATTENTION: Tileset introuvable: " + tilesetPath);
                return;
            }
            
            Texture tilesetTexture = new Texture(tilesetFile);
            TextureRegion[][] allTiles = TextureRegion.split(tilesetTexture, tileW, tileH);
            
            // Lecture carte
            JsonValue mapInfo = root.get("map");
            int mapW = mapInfo.getInt("width");
            int mapH = mapInfo.getInt("height");
            
            System.out.println("Carte chargée: " + mapW + "x" + mapH);
            
            // Création TiledMap
            tiledMap = new TiledMap();
            
            // Lecture layers
            JsonValue layers = mapInfo.get("layers");
            for (JsonValue layerJson : layers) {
                String layerName = layerJson.getString("name");
                
                TiledMapTileLayer layer = new TiledMapTileLayer(mapW, mapH, tileW, tileH);
                JsonValue dataArray = layerJson.get("data");
                int y = mapH - 1;
                
                for (JsonValue rowJson : dataArray) {
                    int x = 0;
                    for (JsonValue cellJson : rowJson) {
                        int tileId = cellJson.asInt() - 1; // Décrémentation
                        
                        if (tileId < 0) {
                            x++;
                            continue;
                        }
                        
                        int tileRow = tileId / cols;
                        int tileCol = tileId % cols;
                        
                        if (tileRow >= 0 && tileRow < rows && tileCol >= 0 && tileCol < cols) {
                            TextureRegion region = allTiles[tileRow][tileCol];
                            StaticTiledMapTile tile = new StaticTiledMapTile(region);
                            
                            TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                            cell.setTile(tile);
                            layer.setCell(x, y, cell);
                        }
                        
                        x++;
                    }
                    y--;
                }
                
                tiledMap.getLayers().add(layer);
                System.out.println("Layer '" + layerName + "' chargé");
            }
            
            mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
            System.out.println("=== CARTE CHARGÉE AVEC SUCCÈS ===");
            
        } catch (Exception e) {
            System.err.println("ERREUR lors du chargement de la carte:");
            e.printStackTrace();
        }
    }
    
    /**
     * Crée des ennemis à différentes positions sur l'écran
     */
    private void createEnemies() {
        float enemySpeed = 50f;  // Réduit pour s'adapter à la nouvelle échelle
        int enemyHealth = 50;
        
        // Positionner les ennemis dans la vue 180x140
        Enemy enemy1 = new Enemy(30f, 30f, enemySpeed, enemyHealth, player);
        enemy1.setScale(1f);  // Échelle 1:1
        enemies.add(enemy1);
        
        Enemy enemy2 = new Enemy(150f, 30f, enemySpeed, enemyHealth, player);
        enemy2.setScale(1f);
        enemies.add(enemy2);
        
        Enemy enemy3 = new Enemy(90f, 110f, enemySpeed, enemyHealth, player);
        enemy3.setScale(1f);
        enemies.add(enemy3);
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
        
        // 1. Dessiner la TileMap EN PREMIER (arrière-plan)
        if (mapRenderer != null && camera != null) {
            camera.update();
            mapRenderer.setView(camera);
            mapRenderer.render();
        }
        
        // 2. Dessiner le joueur et les ennemis PAR-DESSUS
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
        if (camera != null) {
            camera.viewportWidth = width;
            camera.viewportHeight = height;
            camera.update();
        }
    }
    
    @Override
    public void pause() {
    }
    
    @Override
    public void resume() {
    }
    
    @Override
    public void hide() {
        dispose();
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
        
        // Libérer la TileMap
        if (tiledMap != null) {
            tiledMap.dispose();
        }
        if (mapRenderer != null) {
            mapRenderer.dispose();
        }
        
        // Libérer le SpriteBatch
        if (batch != null) {
            batch.dispose();
        }
    }
}