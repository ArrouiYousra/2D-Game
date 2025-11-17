package com.tlse1.twodgame.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe pour charger et gérer les maps depuis un fichier JSON.
 */
public class MapLoader {
    
    private int tileWidth;
    private int tileHeight;
    private int mapWidth;
    private int mapHeight;
    
    // Scale de la map (1.0 = taille normale, 2.0 = 2x plus grand, etc.)
    private float mapScale = 1.0f;
    
    // Matrice de collision (true = solide)
    private boolean[][] collisionMap;
    
    // Layers pour le rendu (optionnel pour l'instant)
    private List<LayerData> layers;
    
    // Texture du tileset
    private Texture tilesetTexture;
    private int tilesPerRow;
    private int tilesPerColumn;
    
    /**
     * Données d'un layer.
     */
    private static class LayerData {
        String name;
        int[][] tileData;
        
        LayerData(String name, int[][] tileData) {
            this.name = name;
            this.tileData = tileData;
        }
    }
    
    /**
     * Charge une map depuis un fichier JSON.
     * 
     * @param mapPath Chemin vers le fichier JSON (relatif à assets/)
     */
    public MapLoader(String mapPath) {
        try {
            FileHandle jsonFile = Gdx.files.internal(mapPath);
            if (!jsonFile.exists()) {
                Gdx.app.error("MapLoader", "Fichier map non trouvé: " + mapPath);
                return;
            }
            
            JsonReader jsonReader = new JsonReader();
            JsonValue root = jsonReader.parse(jsonFile);
            
            // Lire les informations du tileset
            JsonValue tilesetJson = root.get("tileset");
            String tilesetPath = tilesetJson.getString("path");
            tileWidth = tilesetJson.getInt("tileWidth");
            tileHeight = tilesetJson.getInt("tileHeight");
            int tilesetColumns = tilesetJson.getInt("columns");
            int tilesetRows = tilesetJson.getInt("rows");
            
            // Lire les informations de la map
            JsonValue mapJson = root.get("map");
            mapWidth = mapJson.getInt("width");
            mapHeight = mapJson.getInt("height");
            
            // Charger le tileset
            loadTileset(tilesetPath, tilesetColumns, tilesetRows);
            
            // Parser les layers
            parseLayersFromJson(mapJson);
            
            // Initialiser la matrice de collision depuis le layer "structures"
            initializeCollisionMap();
            
            Gdx.app.log("MapLoader", String.format(
                "Map chargée: %s (%dx%d tuiles, %dx%d pixels par tuile)",
                mapPath, mapWidth, mapHeight, tileWidth, tileHeight));
        } catch (Exception e) {
            Gdx.app.error("MapLoader", "Erreur lors du chargement de la map: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Charge le tileset.
     */
    private void loadTileset(String tilesetPath, int columns, int rows) {
        try {
            FileHandle tilesetFile = Gdx.files.internal(tilesetPath);
            if (!tilesetFile.exists()) {
                Gdx.app.error("MapLoader", "Fichier tileset non trouvé: " + tilesetPath);
                return;
            }
            
            tilesetTexture = new Texture(tilesetFile);
            tilesetTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            
            // Utiliser les valeurs du JSON
            tilesPerRow = columns;
            tilesPerColumn = rows;
            
            Gdx.app.log("MapLoader", String.format(
                "Tileset chargé: %s (%dx%d pixels, %dx%d tuiles, %d par ligne, %d par colonne)",
                tilesetPath, tilesetTexture.getWidth(), tilesetTexture.getHeight(), 
                tileWidth, tileHeight, tilesPerRow, tilesPerColumn));
        } catch (Exception e) {
            Gdx.app.error("MapLoader", "Erreur lors du chargement du tileset: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Récupère une TextureRegion pour un ID de tuile donné.
     * Les IDs dans le JSON commencent à 1 (1-based), on les convertit en 0-based.
     */
    private TextureRegion getTileRegion(int tileId) {
        if (tilesetTexture == null || tileId == 0) {
            return null;
        }
        
        // Convertir de 1-based à 0-based (comme dans le code de l'utilisateur)
        int localTileId = tileId - 1;
        
        if (localTileId < 0 || localTileId >= tilesPerRow * tilesPerColumn) {
            // ID hors limites
            return null;
        }
        
        // Calculer la position dans le tileset (comme dans le code de l'utilisateur)
        int tileRow = localTileId / tilesPerRow;
        int tileCol = localTileId % tilesPerRow;
        
        // Vérifier les bounds
        if (tileRow < 0 || tileRow >= tilesPerColumn || tileCol < 0 || tileCol >= tilesPerRow) {
            return null;
        }
        
        // Calculer les coordonnées en pixels dans la texture
        int pixelX = tileCol * tileWidth;
        int pixelY = tileRow * tileHeight;
        
        // Vérifier que les coordonnées sont dans la texture
        if (pixelX + tileWidth > tilesetTexture.getWidth() || 
            pixelY + tileHeight > tilesetTexture.getHeight()) {
            return null;
        }
        
        return new TextureRegion(tilesetTexture, pixelX, pixelY, tileWidth, tileHeight);
    }
    
    /**
     * Rend la map en coordonnées du monde (la caméra s'occupe du suivi).
     *
     * @param batch SpriteBatch pour le rendu
     */
    public void render(SpriteBatch batch) {
        if (tilesetTexture == null) {
            Gdx.app.error("MapLoader", "Tileset non chargé !");
            return;
        }

        if (layers == null || layers.isEmpty()) {
            Gdx.app.error("MapLoader", "Aucun layer chargé !");
            return;
        }

        // Rendre la map en coordonnées du monde (sans offset)
        // La caméra s'occupe de suivre le joueur
        // La map commence à (0, 0) en bas à gauche
        float offsetX = 0f;
        float offsetY = 0f;
        
        // Rendre les layers dans l'ordre : ground, shadow, relief, structures, over_struct
        String[] layerOrder = {"ground", "shadow", "relief", "structures", "over_struct"};

        int renderedLayers = 0;
        for (String layerName : layerOrder) {
            LayerData layer = null;
            for (LayerData l : layers) {
                if (layerName.equals(l.name)) {
                    layer = l;
                    break;
                }
            }

            if (layer != null) {
                renderLayer(batch, layer, offsetX, offsetY);
                renderedLayers++;
            }
        }

        // Log de débogage
        if (renderedLayers == 0) {
            StringBuilder layerNames = new StringBuilder();
            for (int i = 0; i < layers.size(); i++) {
                if (i > 0) layerNames.append(", ");
                layerNames.append(layers.get(i).name);
            }
            Gdx.app.error("MapLoader", "Aucun layer rendu ! Layers disponibles: " + layerNames.toString());
        }
    }
    
    /**
     * Rend un layer spécifique.
     */
    private void renderLayer(SpriteBatch batch, LayerData layer, float offsetX, float offsetY) {
        int tilesRendered = 0;
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                int tileId = layer.tileData[y][x];
                if (tileId != 0) {
                    TextureRegion region = getTileRegion(tileId);
                    if (region != null) {
                        // Convertir les coordonnées de tuile en pixels
                        // Dans le JSON, y=0 est la première ligne (en bas de la map)
                        // Dans libGDX, Y=0 est en bas, donc on rend y=0 à offsetY
                        // et y=mapHeight-1 à offsetY + (mapHeight-1)*tileHeight*mapScale (en haut)
                        float pixelX = offsetX + x * tileWidth * mapScale;
                        float pixelY = offsetY + y * tileHeight * mapScale;
                        
                        // Dessiner avec le scale
                        batch.draw(region, pixelX, pixelY, tileWidth * mapScale, tileHeight * mapScale);
                        tilesRendered++;
                    } else {
                        // Log seulement pour les premiers tiles manquants pour éviter le spam
                        if (tilesRendered < 5) {
                            Gdx.app.debug("MapLoader", String.format(
                                "Tile ID %d non trouvé pour layer %s à (%d, %d)", 
                                tileId, layer.name, x, y));
                        }
                    }
                }
            }
        }
        
        if (tilesRendered > 0) {
            Gdx.app.debug("MapLoader", String.format(
                "Layer '%s' rendu: %d tuiles", layer.name, tilesRendered));
        }
    }
    
    
    /**
     * Parse tous les layers de la map depuis le JSON.
     */
    private void parseLayersFromJson(JsonValue mapJson) {
        layers = new ArrayList<>();
        
        JsonValue layersJson = mapJson.get("layers");
        if (layersJson == null) {
            Gdx.app.error("MapLoader", "Aucun layer trouvé dans le JSON");
            return;
        }
        
        for (JsonValue layerJson : layersJson) {
            String layerName = layerJson.getString("name");
            JsonValue dataArray = layerJson.get("data");
            
            if (dataArray == null) {
                Gdx.app.log("MapLoader", "Layer '" + layerName + "' n'a pas de données");
                continue;
            }
            
            // Parser les données du layer (array 2D)
            int[][] tileData = new int[mapHeight][mapWidth];
            
            int row = 0;
            for (JsonValue rowJson : dataArray) {
                if (row >= mapHeight) break;
                
                int col = 0;
                for (JsonValue cellJson : rowJson) {
                    if (col >= mapWidth) break;
                    tileData[row][col] = cellJson.asInt();
                    col++;
                }
                row++;
            }
            
            layers.add(new LayerData(layerName, tileData));
            Gdx.app.log("MapLoader", "Layer '" + layerName + "' chargé: " + row + " lignes");
        }
    }
    
    
    /**
     * Initialise la matrice de collision depuis le layer "structures".
     */
    private void initializeCollisionMap() {
        collisionMap = new boolean[mapHeight][mapWidth];
        
        // Chercher le layer "structures"
        LayerData structLayer = null;
        for (LayerData layer : layers) {
            if ("structures".equals(layer.name)) {
                structLayer = layer;
                break;
            }
        }
        
        if (structLayer != null) {
            // Une tuile est solide si sa valeur n'est pas 0
            for (int y = 0; y < mapHeight; y++) {
                for (int x = 0; x < mapWidth; x++) {
                    collisionMap[y][x] = (structLayer.tileData[y][x] != 0);
                }
            }
            Gdx.app.log("MapLoader", "Matrice de collision initialisée depuis le layer 'structures'");
        } else {
            Gdx.app.log("MapLoader", "Aucun layer 'structures' trouvé, pas de collisions");
        }
    }
    
    /**
     * Vérifie si une position (en pixels) est dans une tuile solide.
     * 
     * @param x Position X en pixels
     * @param y Position Y en pixels
     * @return true si la position est dans une tuile solide
     */
    public boolean isSolid(float x, float y) {
        if (collisionMap == null) {
            return false;
        }
        
        // Convertir les coordonnées pixels en coordonnées tuiles (en tenant compte du scale)
        int tileX = (int) (x / (tileWidth * mapScale));
        int tileY = (int) (y / (tileHeight * mapScale));
        
        // Les maps TMX utilisent Y croissant vers le bas (Y=0 en haut)
        // Notre système utilise Y croissant vers le haut (Y=0 en bas)
        // Donc on inverse Y
        tileY = mapHeight - 1 - tileY;
        
        // Vérifier les bounds
        if (tileX < 0 || tileX >= mapWidth || tileY < 0 || tileY >= mapHeight) {
            return true; // Hors de la map = solide
        }
        
        return collisionMap[tileY][tileX];
    }
    
    /**
     * Vérifie si un rectangle (en pixels) entre en collision avec une tuile solide.
     * 
     * @param x Position X du rectangle en pixels
     * @param y Position Y du rectangle en pixels
     * @param width Largeur du rectangle en pixels
     * @param height Hauteur du rectangle en pixels
     * @return true si le rectangle entre en collision avec une tuile solide
     */
    public boolean isColliding(float x, float y, float width, float height) {
        // Vérifier les 4 coins du rectangle
        if (isSolid(x, y) || 
            isSolid(x + width, y) || 
            isSolid(x, y + height) || 
            isSolid(x + width, y + height)) {
            return true;
        }
        
        // Vérifier aussi le centre
        if (isSolid(x + width / 2, y + height / 2)) {
            return true;
        }
        
        // Vérifier quelques points intermédiaires pour éviter de passer entre les tuiles
        if (isSolid(x + width / 4, y + height / 2) ||
            isSolid(x + 3 * width / 4, y + height / 2) ||
            isSolid(x + width / 2, y + height / 4) ||
            isSolid(x + width / 2, y + 3 * height / 4)) {
            return true;
        }
        
        return false;
    }
    
    // Getters
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
    
    public int getMapWidthPixels() {
        return (int) (mapWidth * tileWidth * mapScale);
    }
    
    public int getMapHeightPixels() {
        return (int) (mapHeight * tileHeight * mapScale);
    }
    
    public float getMapScale() {
        return mapScale;
    }
    
    public void setMapScale(float scale) {
        this.mapScale = scale;
    }
    
    /**
     * Calcule automatiquement le scale pour que la map remplisse la fenêtre.
     * 
     * @param screenWidth Largeur de l'écran
     * @param screenHeight Hauteur de l'écran
     * @param fitToScreen Si true, la map remplit l'écran (peut couper). Si false, la map s'adapte entièrement (peut avoir des bordures).
     */
    public void calculateScaleToFitScreen(float screenWidth, float screenHeight, boolean fitToScreen) {
        float mapWidthPixels = mapWidth * tileWidth;
        float mapHeightPixels = mapHeight * tileHeight;
        
        float scaleX = screenWidth / mapWidthPixels;
        float scaleY = screenHeight / mapHeightPixels;
        
        if (fitToScreen) {
            // Remplir l'écran (peut couper)
            mapScale = Math.max(scaleX, scaleY);
        } else {
            // S'adapter entièrement (peut avoir des bordures)
            mapScale = Math.min(scaleX, scaleY);
        }
        
        Gdx.app.log("MapLoader", String.format(
            "Scale calculé: %.2f (écran: %.0fx%.0f, map: %.0fx%.0f)",
            mapScale, screenWidth, screenHeight, mapWidthPixels, mapHeightPixels));
    }
    
    /**
     * Libère les ressources.
     */
    public void dispose() {
        if (tilesetTexture != null) {
            tilesetTexture.dispose();
            tilesetTexture = null;
        }
        collisionMap = null;
        layers = null;
    }
}
