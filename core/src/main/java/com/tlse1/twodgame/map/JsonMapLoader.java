package com.tlse1.twodgame.map;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
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

public class JsonMapLoader extends ApplicationAdapter {
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMap tiledMap;
    
    private static final String JSON_PATH = "map.json";
    
    @Override
    public void create() {
        System.out.println("=== CHARGEMENT DE LA CARTE DEPUIS JSON ===");
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        camera.update();
        
        // Vérification du fichier JSON
        FileHandle jsonFile = Gdx.files.local(JSON_PATH);
        if (!jsonFile.exists()) {
            System.err.println("ERREUR: Le fichier " + JSON_PATH + " n'existe pas!");
            System.err.println("Générez d'abord une carte avec TiledMapGame");
            Gdx.app.exit();
            return;
        }
        
        // Lecture du JSON
        JsonReader jsonReader = new JsonReader();
        JsonValue root = jsonReader.parse(jsonFile);
        
        // Lecture des informations du tileset
        JsonValue tilesetInfo = root.get("tileset");
        String tilesetPath = tilesetInfo.getString("path");
        int tileWidth = tilesetInfo.getInt("tileWidth");
        int tileHeight = tilesetInfo.getInt("tileHeight");
        int columns = tilesetInfo.getInt("columns");
        int rows = tilesetInfo.getInt("rows");
        
        System.out.println("Tileset: " + tilesetPath);
        System.out.println("Tuiles: " + columns + "x" + rows + " (" + tileWidth + "x" + tileHeight + " pixels)");
        
        // Chargement du tileset
        FileHandle tilesetFile = Gdx.files.internal(tilesetPath);
        if (!tilesetFile.exists()) {
            System.err.println("ERREUR: Le tileset " + tilesetPath + " n'existe pas!");
            Gdx.app.exit();
            return;
        }
        
        Texture tilesetTexture = new Texture(tilesetFile);
        TextureRegion[][] tiles = TextureRegion.split(tilesetTexture, tileWidth, tileHeight);
        
        // Lecture des informations de la carte
        JsonValue mapInfo = root.get("map");
        int mapWidth = mapInfo.getInt("width");
        int mapHeight = mapInfo.getInt("height");
        
        System.out.println("Carte: " + mapWidth + "x" + mapHeight);
        
        // Création de la TiledMap
        tiledMap = new TiledMap();
        
        // Lecture des layers
        JsonValue layers = mapInfo.get("layers");
        for (JsonValue layerJson : layers) {
            String layerName = layerJson.getString("name");
            JsonValue data = layerJson.get("data");
            
            System.out.println("Chargement du layer: " + layerName);
            
            TiledMapTileLayer layer = new TiledMapTileLayer(mapWidth, mapHeight, tileWidth, tileHeight);
            
            // Lecture des données de la carte
            int y = mapHeight - 1;
            for (JsonValue row : data) {
                int x = 0;
                for (JsonValue tileIndex : row) {
                    int index = tileIndex.asInt();
                    
                    // Calcul de la position dans le tileset
                    int tileRow = index / columns;
                    int tileCol = index % columns;
                    
                    // Vérification des limites
                    if (tileRow < rows && tileCol < columns) {
                        TextureRegion region = tiles[tileRow][tileCol];
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
        
        System.out.println("=== CARTE CHARGÉE AVEC SUCCÈS ===");
    }
    
    @Override
    public void render() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();
    }
    
    @Override
    public void dispose() {
        if (tiledMap != null) tiledMap.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
    }
}