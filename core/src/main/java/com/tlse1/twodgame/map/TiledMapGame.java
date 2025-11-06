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
import java.util.Random;

public class TiledMapGame extends ApplicationAdapter {
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMap tiledMap;
    
    // Configuration - AJUSTEZ CES VALEURS SELON VOTRE TILESET
    private static final String TILESET_PATH = "tileset.png";
    private static final int TILE_WIDTH = 16;   // Largeur d'une tuile
    private static final int TILE_HEIGHT = 16;  // Hauteur d'une tuile
    private static final int MAP_WIDTH = 20;    // Largeur de la carte en tuiles
    private static final int MAP_HEIGHT = 15;   // Hauteur de la carte en tuiles
    private static final String JSON_OUTPUT = "map.json";
    
    @Override
    public void create() {
        System.out.println("=== CHARGEMENT DU TILESET ===");
        
        // Configuration de la caméra pour voir toute la carte
        float viewWidth = MAP_WIDTH * TILE_WIDTH;
        float viewHeight = MAP_HEIGHT * TILE_HEIGHT;
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, viewWidth, viewHeight);
        camera.position.set(viewWidth / 2f, viewHeight / 2f, 0);
        camera.update();
        
        // Vérification du tileset
        FileHandle tilesetFile = Gdx.files.internal(TILESET_PATH);
        if (!tilesetFile.exists()) {
            System.err.println("ERREUR: Le fichier " + TILESET_PATH + " n'existe pas!");
            System.err.println("Placez votre tileset dans le dossier 'assets/'");
            Gdx.app.exit();
            return;
        }
        
        // Chargement et découpe du tileset
        Texture tilesetTexture = new Texture(tilesetFile);
        TextureRegion[][] tiles = TextureRegion.split(tilesetTexture, TILE_WIDTH, TILE_HEIGHT);
        
        int tilesPerRow = tiles[0].length;
        int tilesPerColumn = tiles.length;
        int totalTiles = tilesPerRow * tilesPerColumn;
        
        System.out.println("Tileset: " + tilesetTexture.getWidth() + "x" + tilesetTexture.getHeight() + " pixels");
        System.out.println("Tuiles: " + tilesPerRow + " colonnes x " + tilesPerColumn + " lignes");
        System.out.println("Total: " + totalTiles + " tuiles de " + TILE_WIDTH + "x" + TILE_HEIGHT + " pixels");
        
        // Création de la TiledMap
        tiledMap = new TiledMap();
        TiledMapTileLayer layer = new TiledMapTileLayer(MAP_WIDTH, MAP_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
        
        // Remplissage de la carte (aléatoire ou pattern)
        Random random = new Random();
        int[][] mapData = new int[MAP_HEIGHT][MAP_WIDTH];
        
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                // Sélection aléatoire d'une tuile du tileset
                int tileIndex = random.nextInt(totalTiles);
                int tileRow = tileIndex / tilesPerRow;
                int tileCol = tileIndex % tilesPerRow;
                
                // Sauvegarde pour le JSON
                mapData[y][x] = tileIndex;
                
                // Création de la tuile
                TextureRegion region = tiles[tileRow][tileCol];
                StaticTiledMapTile tile = new StaticTiledMapTile(region);
                
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(tile);
                layer.setCell(x, y, cell);
            }
        }
        
        tiledMap.getLayers().add(layer);
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        
        // Export en JSON
        exportToJson(mapData, tilesPerRow, tilesPerColumn);
        
        System.out.println("=== CARTE CRÉÉE ===");
    }
    
    private void exportToJson(int[][] mapData, int tilesPerRow, int tilesPerColumn) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"tileset\": {\n");
        json.append("    \"path\": \"").append(TILESET_PATH).append("\",\n");
        json.append("    \"tileWidth\": ").append(TILE_WIDTH).append(",\n");
        json.append("    \"tileHeight\": ").append(TILE_HEIGHT).append(",\n");
        json.append("    \"columns\": ").append(tilesPerRow).append(",\n");
        json.append("    \"rows\": ").append(tilesPerColumn).append("\n");
        json.append("  },\n");
        json.append("  \"map\": {\n");
        json.append("    \"width\": ").append(MAP_WIDTH).append(",\n");
        json.append("    \"height\": ").append(MAP_HEIGHT).append(",\n");
        json.append("    \"layers\": [\n");
        json.append("      {\n");
        json.append("        \"name\": \"ground\",\n");
        json.append("        \"data\": [\n");
        
        for (int y = MAP_HEIGHT - 1; y >= 0; y--) {
            json.append("          [");
            for (int x = 0; x < MAP_WIDTH; x++) {
                json.append(mapData[y][x]);
                if (x < MAP_WIDTH - 1) json.append(", ");
            }
            json.append("]");
            if (y > 0) json.append(",");
            json.append("\n");
        }
        
        json.append("        ]\n");
        json.append("      }\n");
        json.append("    ]\n");
        json.append("  }\n");
        json.append("}\n");
        
        // Sauvegarde du fichier JSON
        FileHandle jsonFile = Gdx.files.local(JSON_OUTPUT);
        jsonFile.writeString(json.toString(), false);
        
        System.out.println("=== JSON EXPORTÉ ===");
        System.out.println("Fichier: " + jsonFile.file().getAbsolutePath());
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
        tiledMap.dispose();
        mapRenderer.dispose();
    }
}