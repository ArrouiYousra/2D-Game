package com.tlse1.twodgame.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe pour charger et gérer le mapping JSON des lettres de Text1.png.
 * Permet de découper automatiquement les sprites de texte selon les coordonnées définies dans le JSON.
 */
public class TextMapping {
    
    private Texture textTexture;
    
    // Tous les sprites de texte chargés (par nom de sprite)
    private Map<String, TextureRegion> allSprites;
    
    /**
     * Charge le mapping depuis le fichier JSON
     */
    public TextMapping() {
        allSprites = new HashMap<>();
        loadMapping();
    }
    
    /**
     * Charge le fichier JSON de mapping
     */
    private void loadMapping() {
        try {
            JsonReader jsonReader = new JsonReader();
            
            // Charger tous les sprites depuis text1_sprites.json
            JsonValue spritesData = jsonReader.parse(Gdx.files.internal("gui/text1_sprites.json"));
            loadAllSprites(spritesData);
        } catch (Exception e) {
            Gdx.app.error("TextMapping", "Erreur lors du chargement du mapping texte: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Charge tous les sprites depuis le fichier JSON
     */
    private void loadAllSprites(JsonValue spritesData) {
        // Charger la texture (on suppose qu'elle est dans gui/PNG/Text1.png)
        textTexture = new Texture(Gdx.files.internal("gui/PNG/Text1.png"));
        textTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        
        // Parcourir tous les sprites dans le tableau JSON
        for (int i = 0; i < spritesData.size; i++) {
            JsonValue sprite = spritesData.get(i);
            String name = sprite.getString("name");
            int x = sprite.getInt("x");
            int y = sprite.getInt("y");
            int width = sprite.getInt("width");
            int height = sprite.getInt("height");
            
            // Créer la TextureRegion
            TextureRegion region = new TextureRegion(textTexture, x, y, width, height);
            allSprites.put(name, region);
        }
    }
    
    /**
     * Retourne un sprite de texte par son nom (sprite1, sprite2, etc.)
     */
    public TextureRegion getSprite(String spriteName) {
        return allSprites.get(spriteName);
    }
    
    /**
     * Retourne tous les sprites de texte
     */
    public Map<String, TextureRegion> getAllSprites() {
        return allSprites;
    }
    
    /**
     * Retourne la texture complète
     */
    public Texture getTexture() {
        return textTexture;
    }
    
    /**
     * Libère les ressources
     */
    public void dispose() {
        if (textTexture != null) {
            textTexture.dispose();
        }
    }
}

