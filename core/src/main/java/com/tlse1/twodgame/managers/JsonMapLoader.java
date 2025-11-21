package com.tlse1.twodgame.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
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
        try {            
            // Vérification du JSON
            FileHandle jsonFile = Gdx.files.internal(jsonPath);
            if (!jsonFile.exists()) {
                Gdx.app.error("JsonMapLoader", "ERREUR: " + jsonPath + " introuvable!");
                return;
            }
            JsonReader jsonReader = new JsonReader();
            JsonValue root = jsonReader.parse(jsonFile);
            
            // Lecture tileset
            JsonValue tileset = root.get("tileset");
            String tilesetPath = tileset.getString("path");
            tileWidth = tileset.getInt("tileWidth");
            tileHeight = tileset.getInt("tileHeight");
            int cols = tileset.getInt("columns");
            int rows = tileset.getInt("rows");
            
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
            
            // Création TiledMap
            tiledMap = new TiledMap();
            
            // Lecture layers
            JsonValue layers = mapInfo.get("layers");
            for (JsonValue layerJson : layers) {
                String layerName = layerJson.getString("name");
                
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
            }
            
            mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
            
        } catch (Exception e) {
            Gdx.app.error("JsonMapLoader", "ERREUR FATALE:", e);
            e.printStackTrace();
        }
    }
    
    /**
     * Rend la map (tous les layers).
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
     * Rend les layers qui doivent être affichés AVANT le joueur.
     * Ordre : ground, shadow, relief
     * 
     * @param camera La caméra pour le rendu
     */
    public void renderBeforePlayer(OrthographicCamera camera) {
        if (mapRenderer == null || camera == null || tiledMap == null) {
            return;
        }
        
        mapRenderer.setView(camera);
        Batch batch = mapRenderer.getBatch();
        batch.begin();
        
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
        
        // Rendre les layers dans l'ordre : ground, shadow, relief
        // On parcourt tous les layers et on rend seulement ceux qui doivent être avant le joueur
        for (int i = 0; i < tiledMap.getLayers().size(); i++) {
            TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(i);
            if (layer == null) continue;
            
            String layerName = layer.getName();
            
            // Rendre seulement les layers qui doivent être avant le joueur
            // (collisions n'est pas rendu, c'est juste pour la détection)
            if ("ground".equals(layerName) || 
                "shadow".equals(layerName) || 
                "relief".equals(layerName)) {
                // Rendre seulement les tiles visibles à l'écran
                for (int y = startY; y <= endY; y++) {
                    for (int x = startX; x <= endX; x++) {
                        TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                        if (cell != null && cell.getTile() != null) {
                            float tileX = x * tileWidth;
                            float tileY = y * tileHeight;
                            TextureRegion region = cell.getTile().getTextureRegion();
                            if (region != null) {
                                batch.draw(region, tileX, tileY, tileWidth, tileHeight);
                            }
                        }
                    }
                }
            }
        }
        
        batch.end();
    }
    
    /**
     * Rend les layers qui doivent être affichés APRÈS le joueur.
     * Ordre : structures, over_struct
     * 
     * @param camera La caméra pour le rendu
     */
    public void renderAfterPlayer(OrthographicCamera camera) {
        if (mapRenderer == null || camera == null || tiledMap == null) {
            return;
        }
        
        mapRenderer.setView(camera);
        Batch batch = mapRenderer.getBatch();
        batch.begin();
        
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
        
        // Rendre les layers dans l'ordre : structures, over_struct
        for (int i = 0; i < tiledMap.getLayers().size(); i++) {
            TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(i);
            if (layer == null) continue;
            
            String layerName = layer.getName();
            
            // Rendre seulement les layers qui doivent être après le joueur
            if ("structures".equals(layerName) || 
                "over_struct".equals(layerName)) {
                // Rendre seulement les tiles visibles à l'écran
                for (int y = startY; y <= endY; y++) {
                    for (int x = startX; x <= endX; x++) {
                        TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                        if (cell != null && cell.getTile() != null) {
                            float tileX = x * tileWidth;
                            float tileY = y * tileHeight;
                            TextureRegion region = cell.getTile().getTextureRegion();
                            if (region != null) {
                                batch.draw(region, tileX, tileY, tileWidth, tileHeight);
                            }
                        }
                    }
                }
            }
        }
        
        batch.end();
    }
    
    /**
     * Vérifie si une position est dans une zone donnée.
     * Les zones sont définies dans les layers "zone1", "zone2", etc.
     * Une position est dans une zone si elle se trouve sur une tile non-nulle du layer correspondant.
     * 
     * @param x Position X en pixels (coin bas-gauche)
     * @param y Position Y en pixels (coin bas-gauche)
     * @param zoneId ID de la zone (1-6)
     * @return true si la position est dans la zone
     */
    public boolean isInZone(float x, float y, int zoneId) {
        if (tiledMap == null || zoneId < 1 || zoneId > 6) {
            return false;
        }
        
        // Convertir les coordonnées pixels en coordonnées tiles
        int tileX = (int) (x / tileWidth);
        int tileY = (int) (y / tileHeight);
        
        // Vérifier les limites
        if (tileX < 0 || tileX >= mapWidth || tileY < 0 || tileY >= mapHeight) {
            return false;
        }
        
        // Récupérer le layer de la zone (zone1, zone2, etc.)
        String zoneLayerName = "zone" + zoneId;
        TiledMapTileLayer zoneLayer = (TiledMapTileLayer) tiledMap.getLayers().get(zoneLayerName);
        
        if (zoneLayer == null) {
            return false;
        }
        
        // Vérifier si la tile existe et n'est pas vide (non-nulle)
        TiledMapTileLayer.Cell cell = zoneLayer.getCell(tileX, tileY);
        return cell != null && cell.getTile() != null;
    }
    
    /**
     * Trouve le centre d'une zone en pixels.
     * Parcourt toutes les tiles de la zone et calcule le centre.
     * 
     * @param zoneId ID de la zone (1-6)
     * @return Tableau [x, y] en pixels, ou null si la zone est vide
     */
    public float[] getZoneCenter(int zoneId) {
        if (tiledMap == null || zoneId < 1 || zoneId > 6) {
            return null;
        }
        
        String zoneLayerName = "zone" + zoneId;
        TiledMapTileLayer zoneLayer = (TiledMapTileLayer) tiledMap.getLayers().get(zoneLayerName);
        
        if (zoneLayer == null) {
            return null;
        }
        
        float sumX = 0f;
        float sumY = 0f;
        int count = 0;
        
        // Parcourir toutes les tiles de la zone
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                TiledMapTileLayer.Cell cell = zoneLayer.getCell(x, y);
                if (cell != null && cell.getTile() != null) {
                    // Convertir en pixels (centre de la tile)
                    float pixelX = (x + 0.5f) * tileWidth;
                    float pixelY = (y + 0.5f) * tileHeight;
                    sumX += pixelX;
                    sumY += pixelY;
                    count++;
                }
            }
        }
        
        if (count == 0) {
            return null;
        }
        
        return new float[]{sumX / count, sumY / count};
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