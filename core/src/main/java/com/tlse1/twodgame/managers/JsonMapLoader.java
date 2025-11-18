package com.tlse1.twodgame.managers;

import com.badlogic.gdx.Gdx;
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

public class JsonMapLoader {
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMap tiledMap;
    private int tileWidth;
    private int tileHeight;
    private int mapWidth;
    private int mapHeight;
    
    /**
     * Charge une map depuis un fichier JSON.
     * 
     * @param jsonPath Chemin vers le fichier JSON (relatif à assets/)
     */
    public JsonMapLoader(String jsonPath) {
        Gdx.app.log("JsonMapLoader", "=== DEMARRAGE JsonMapLoader ===");
        
        try {            
            // Vérification du JSON
            FileHandle jsonFile = Gdx.files.internal(jsonPath);
            if (!jsonFile.exists()) {
                Gdx.app.error("JsonMapLoader", "ERREUR: " + jsonPath + " introuvable!");
                return;
            }
            
            Gdx.app.log("JsonMapLoader", "Lecture du JSON...");
            JsonReader jsonReader = new JsonReader();
            JsonValue root = jsonReader.parse(jsonFile);
            
            // Lecture tileset
            JsonValue tileset = root.get("tileset");
            String tilesetPath = tileset.getString("path");
            tileWidth = tileset.getInt("tileWidth");
            tileHeight = tileset.getInt("tileHeight");
            int cols = tileset.getInt("columns");
            int rows = tileset.getInt("rows");
            
            Gdx.app.log("JsonMapLoader", "Tileset: " + tilesetPath + " (" + cols + "x" + rows + " tuiles de " + tileWidth + "x" + tileHeight + ")");
            
            // Chargement tileset
            FileHandle tilesetFile = Gdx.files.internal(tilesetPath);
            if (!tilesetFile.exists()) {
                Gdx.app.error("JsonMapLoader", "ERREUR: Tileset introuvable: " + tilesetPath);
                return;
            }
            
            Texture tilesetTexture = new Texture(tilesetFile);
            tilesetTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            TextureRegion[][] allTiles = TextureRegion.split(tilesetTexture, tileWidth, tileHeight);
            
            // Lecture carte
            JsonValue mapInfo = root.get("map");
            mapWidth = mapInfo.getInt("width");
            mapHeight = mapInfo.getInt("height");
            
            Gdx.app.log("JsonMapLoader", "Carte: " + mapWidth + "x" + mapHeight);
            
            // Création TiledMap
            tiledMap = new TiledMap();
            
            // Lecture layers
            JsonValue layers = mapInfo.get("layers");
            for (JsonValue layerJson : layers) {
                String layerName = layerJson.getString("name");
                Gdx.app.log("JsonMapLoader", "Chargement layer: " + layerName);
                
                TiledMapTileLayer layer = new TiledMapTileLayer(mapWidth, mapHeight, tileWidth, tileHeight);
                layer.setName(layerName); // Définir le nom du layer pour pouvoir le récupérer par nom
                
                JsonValue dataArray = layerJson.get("data");
                int y = mapHeight - 1;
                
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
                            Gdx.app.error("JsonMapLoader", "Tuile " + (tileId + 1) + " hors limites à (" + x + "," + y + ")");
                        }
                        
                        x++;
                    }
                    y--;
                }
                
                tiledMap.getLayers().add(layer);
                Gdx.app.log("JsonMapLoader", "Layer '" + layerName + "' chargé");
            }
            
            mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
            Gdx.app.log("JsonMapLoader", "=== CARTE CHARGEE ===");
            
        } catch (Exception e) {
            Gdx.app.error("JsonMapLoader", "ERREUR FATALE:", e);
            e.printStackTrace();
        }
    }
    
    /**
     * Rend la map.
     * 
     * @param camera La caméra pour le rendu
     */
    public void render(OrthographicCamera camera) {
        if (mapRenderer != null && camera != null) {
            mapRenderer.setView(camera);
            mapRenderer.render();
        }
    }
    
    /**
     * Vérifie si une position est en collision avec la map.
     * Le joueur peut aller partout sauf sur le layer "collisions" où il y a une tile non-nulle.
     * 
     * @param x Position X en pixels
     * @param y Position Y en pixels
     * @param width Largeur de l'entité
     * @param height Hauteur de l'entité
     * @return true si collision (position invalide)
     */
    public boolean isColliding(float x, float y, float width, float height) {
        if (tiledMap == null) {
            return false;
        }
        
        // Convertir les coordonnées pixels en coordonnées tiles
        // Dans LibGDX TiledMap, Y=0 est en bas, comme notre système de coordonnées
        int startTileX = (int) (x / tileWidth);
        int startTileY = (int) (y / tileHeight);
        int endTileX = (int) ((x + width) / tileWidth);
        int endTileY = (int) ((y + height) / tileHeight);
        
        // Récupérer le layer "collisions"
        TiledMapTileLayer collisionsLayer = (TiledMapTileLayer) tiledMap.getLayers().get("collisions");
        
        // Vérifier toutes les tiles dans la zone
        for (int tileY = startTileY; tileY <= endTileY; tileY++) {
            for (int tileX = startTileX; tileX <= endTileX; tileX++) {
                if (tileX >= 0 && tileX < mapWidth && tileY >= 0 && tileY < mapHeight) {
                    // Vérifier si c'est une zone de collision
                    if (collisionsLayer != null) {
                        TiledMapTileLayer.Cell collisionCell = collisionsLayer.getCell(tileX, tileY);
                        // Si la tile existe et n'est pas vide, c'est une collision
                        if (collisionCell != null && collisionCell.getTile() != null) {
                            return true; // Collision avec une zone bloquante
                        }
                    }
                } else {
                    // Hors limites de la map = collision
                    return true;
                }
            }
        }
        
        return false; // Pas de collision, position valide
    }
    
    /**
     * Libère les ressources.
     */
    public void dispose() {
        if (tiledMap != null) {
            tiledMap.dispose();
        }
        if (mapRenderer != null) {
            mapRenderer.dispose();
        }
    }
    
    // Getters
    public TiledMap getTiledMap() {
        return tiledMap;
    }
    
    public int getTileWidth() {
        return tileWidth;
    }
    
    public int getTileHeight() {
        return tileHeight;
    }
    
    public int getMapWidth() {
        return mapWidth;
    }
    
    public int getMapHeight() {
        return mapHeight;
    }
}
