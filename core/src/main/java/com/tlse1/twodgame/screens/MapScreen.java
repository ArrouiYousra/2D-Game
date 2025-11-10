package com.tlse1.twodgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.tlse1.twodgame.TwoDGame;

/**
 * Écran affichant la TileMap chargée depuis un JSON
 */
public class MapScreen implements Screen {
    
    private TwoDGame game;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMap tiledMap;
    
    private static final String JSON_PATH = "map.json";
    
    public MapScreen(TwoDGame game) {
        this.game = game;
    }
    
    @Override
    public void show() {
        System.out.println("=== CHARGEMENT DE LA CARTE ===");
        
        try {
            
            // Vérification du JSON
            FileHandle jsonFile = Gdx.files.local(JSON_PATH);
            if (!jsonFile.exists()) {
                System.err.println("ERREUR: " + JSON_PATH + " introuvable!");
                game.setScreen(new MenuScreen(game));
                return;
            }
            
            System.out.println("Lecture du JSON...");
            JsonReader jsonReader = new JsonReader();
            JsonValue root = jsonReader.parse(jsonFile);
            
            // Lecture tileset
            JsonValue tileset = root.get("tileset");
            String tilesetPath = tileset.getString("path");
            int tileW = tileset.getInt("tileWidth");
            int tileH = tileset.getInt("tileHeight");
            int cols = tileset.getInt("columns");
            int rows = tileset.getInt("rows");
            
            System.out.println("Tileset: " + tilesetPath + " (" + cols + "x" + rows + " tuiles)");
            
            // Chargement tileset
            FileHandle tilesetFile = Gdx.files.internal(tilesetPath);
            if (!tilesetFile.exists()) {
                System.err.println("ERREUR: Tileset introuvable: " + tilesetPath);
                game.setScreen(new MenuScreen(game));
                return;
            }
            
            Texture tilesetTexture = new Texture(tilesetFile);
            TextureRegion[][] allTiles = TextureRegion.split(tilesetTexture, tileW, tileH);
            
            // Lecture carte
            JsonValue mapInfo = root.get("map");
            int mapW = mapInfo.getInt("width");
            int mapH = mapInfo.getInt("height");
            
            System.out.println("Carte: " + mapW + "x" + mapH);
            
            // Ajuster la caméra à la taille de la carte
            float viewWidth = Math.min(mapW * tileW, 800);
            float viewHeight = Math.min(mapH * tileH, 600);
            camera.viewportWidth = viewWidth;
            camera.viewportHeight = viewHeight;
            camera.position.set(viewWidth / 2f, viewHeight / 2f, 0);
            camera.update();
            
            // Création TiledMap
            tiledMap = new TiledMap();
            
            // Lecture layers
            JsonValue layers = mapInfo.get("layers");
            for (JsonValue layerJson : layers) {
                String layerName = layerJson.getString("name");
                System.out.println("Chargement layer: " + layerName);
                
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
            }
            
            mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
            System.out.println("=== CARTE CHARGÉE ===");
            
        } catch (Exception e) {
            System.err.println("ERREUR lors du chargement:");
            e.printStackTrace();
            game.setScreen(new MenuScreen(game));
        }
    }
    
    @Override
    public void render(float delta) {
        // Retour au menu avec Échap
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            return;
        }
        
        // Nettoyage écran
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Rendu de la carte
        if (mapRenderer != null) {
            camera.update();
            mapRenderer.setView(camera);
            mapRenderer.render();
        }
    }
    
    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }
    
    @Override
    public void pause() {}
    
    @Override
    public void resume() {}
    
    @Override
    public void hide() {
        dispose();
    }
    
    @Override
    public void dispose() {
        if (tiledMap != null) tiledMap.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
    }
}