package com.tlse1.twodgame.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe pour charger et gérer le mapping JSON des éléments de Main_menu.png.
 * Permet de découper automatiquement les sprites selon les coordonnées définies dans le JSON.
 */
public class MenuMapping {
    
    private Texture menuTexture;
    
    // Tous les sprites chargés (par nom de sprite)
    private Map<String, TextureRegion> allSprites;
    
    /**
     * Charge le mapping depuis le fichier JSON
     */
    public MenuMapping() {
        allSprites = new HashMap<>();
        loadMapping();
    }
    
    /**
     * Charge le fichier JSON de mapping
     */
    private void loadMapping() {
        try {
            JsonReader jsonReader = new JsonReader();
            
            // Charger tous les sprites depuis main_menu_sprites.json
            JsonValue spritesData = jsonReader.parse(Gdx.files.internal("gui/main_menu_sprites.json"));
            loadAllSprites(spritesData);
        } catch (Exception e) {
            Gdx.app.error("MenuMapping", "Erreur lors du chargement du mapping menu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Charge tous les sprites depuis le fichier JSON
     */
    private void loadAllSprites(JsonValue spritesData) {
        // Charger la texture (on suppose qu'elle est dans gui/PNG/Main_menu.png)
        menuTexture = new Texture(Gdx.files.internal("gui/PNG/Main_menu.png"));
        menuTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        
        // Parcourir tous les sprites dans le tableau JSON
        for (int i = 0; i < spritesData.size; i++) {
            JsonValue sprite = spritesData.get(i);
            String name = sprite.getString("name");
            int x = sprite.getInt("x");
            int y = sprite.getInt("y");
            int width = sprite.getInt("width");
            int height = sprite.getInt("height");
            
            // Créer la TextureRegion
            TextureRegion region = new TextureRegion(menuTexture, x, y, width, height);
            allSprites.put(name, region);
        }
    }
    
    /**
     * Retourne un sprite par son nom (sprite1, sprite2, etc.)
     */
    public TextureRegion getSprite(String spriteName) {
        return allSprites.get(spriteName);
    }
    
    /**
     * Retourne tous les sprites
     */
    public Map<String, TextureRegion> getAllSprites() {
        return allSprites;
    }
    
    /**
     * Retourne la texture complète
     */
    public Texture getTexture() {
        return menuTexture;
    }
    
    /**
     * Libère les ressources
     */
    public void dispose() {
        if (menuTexture != null) {
            menuTexture.dispose();
        }
    }
}

