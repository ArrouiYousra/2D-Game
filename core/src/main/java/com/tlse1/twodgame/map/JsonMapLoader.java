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
        System.out.println("=== DEMARRAGE JsonMapLoader ===");
        
        try {            
            // Vérification du JSON
            FileHandle jsonFile = Gdx.files.local(JSON_PATH);
            if (!jsonFile.exists()) {
                System.err.println("ERREUR: " + JSON_PATH + " introuvable!");
                Gdx.app.exit();
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
            
            System.out.println("Tileset: " + tilesetPath + " (" + cols + "x" + rows + " tuiles de " + tileW + "x" + tileH + ")");
            
            // Chargement tileset
            FileHandle tilesetFile = Gdx.files.internal(tilesetPath);
            if (!tilesetFile.exists()) {
                System.err.println("ERREUR: Tileset introuvable: " + tilesetPath);
                Gdx.app.exit();
                return;
            }
            
            Texture tilesetTexture = new Texture(tilesetFile);
            TextureRegion[][] allTiles = TextureRegion.split(tilesetTexture, tileW, tileH);
            
            // Lecture carte
            JsonValue mapInfo = root.get("map");
            int mapW = mapInfo.getInt("width");
            int mapH = mapInfo.getInt("height");
            
            System.out.println("Carte: " + mapW + "x" + mapH);
            
            // Création TiledMap
            tiledMap = new TiledMap();
            
            // Lecture layers
            JsonValue layers = mapInfo.get("layers");
            for (JsonValue layerJson : layers) {
                if (layerName == "collision") continue;
                String layerName = layerJson.getString("name");
                System.out.println("Chargement layer: " + layerName);
                
                TiledMapTileLayer layer = new TiledMapTileLayer(mapW, mapH, tileW, tileH);
                
                JsonValue dataArray = layerJson.get("data");
                int y = mapH - 1;
                
                for (JsonValue rowJson : dataArray) {
                    int x = 0;
                    for (JsonValue cellJson : rowJson) {
                        int tileId = cellJson.asInt();
                        
                        // Décrémentation : les valeurs du JSON commencent à 1, mais les indices du tileset à 0
                        tileId = tileId - 1;
                        
                        // Si tileId < 0 (c'était 0 dans le JSON), c'est une case vide/transparente
                        if (tileId < 0) {
                            x++;
                            continue;
                        }
                        
                        // Calcul position dans tileset
                        int tileRow = tileId / cols;
                        int tileCol = tileId % cols;
                        
                        // Vérification limites
                        if (tileRow >= 0 && tileRow < rows && tileCol >= 0 && tileCol < cols) {
                            TextureRegion region = allTiles[tileRow][tileCol];
                            StaticTiledMapTile tile = new StaticTiledMapTile(region);
                            
                            TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                            cell.setTile(tile);
                            layer.setCell(x, y, cell);
                        } else {
                            System.err.println("Tuile " + (tileId + 1) + " hors limites à (" + x + "," + y + ")");
                        }
                        
                        x++;
                    }
                    y--;
                }
                
                tiledMap.getLayers().add(layer);
                System.out.println("Layer '" + layerName + "' chargé");
            }
            
            mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
            System.out.println("=== CARTE CHARGEE ===");
            
        } catch (Exception e) {
            System.err.println("ERREUR FATALE:");
            e.printStackTrace();
            Gdx.app.exit();
        }
    }
    
    @Override
    public void render() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        if (mapRenderer != null) {
            camera.update();
            mapRenderer.setView(camera);
            mapRenderer.render();
        }
    }
    
    @Override
    public void dispose() {
        if (tiledMap != null) tiledMap.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
    }
}