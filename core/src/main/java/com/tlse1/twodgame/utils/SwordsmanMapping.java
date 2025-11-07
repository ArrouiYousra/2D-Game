package com.tlse1.twodgame.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe pour charger et gérer le mapping JSON des sprites du Swordsman.
 * Permet de découper automatiquement les sprites selon les coordonnées définies dans le JSON.
 */
public class SwordsmanMapping {
    
    private Texture idleTexture;
    
    // Tous les sprites chargés (par nom de sprite)
    private Map<String, TextureRegion> idleSprites;
    
    /**
     * Charge le mapping depuis le fichier JSON pour l'animation Idle niveau 1
     */
    public SwordsmanMapping() {
        idleSprites = new HashMap<>();
        loadIdleMapping();
    }
    
    /**
     * Charge le fichier JSON de mapping pour Idle
     */
    private void loadIdleMapping() {
        try {
            JsonReader jsonReader = new JsonReader();
            
            // Charger tous les sprites depuis swordsman_lvl1_idle_sprites.json
            JsonValue spritesData = jsonReader.parse(Gdx.files.internal("swordsman1-3/swordsman_lvl1_idle_sprites.json"));
            
            // Charger la texture
            idleTexture = new Texture(Gdx.files.internal("swordsman1-3/PNG/Swordsman_lvl1/With_shadow/Swordsman_lvl1_Idle_with_shadow.png"));
            idleTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            
            // Parcourir tous les sprites dans le tableau JSON
            for (int i = 0; i < spritesData.size; i++) {
                JsonValue sprite = spritesData.get(i);
                String name = sprite.getString("name");
                int x = sprite.getInt("x");
                int y = sprite.getInt("y");
                int width = sprite.getInt("width");
                int height = sprite.getInt("height");
                
                // Créer la TextureRegion
                TextureRegion region = new TextureRegion(idleTexture, x, y, width, height);
                idleSprites.put(name, region);
                
                Gdx.app.debug("SwordsmanMapping", String.format(
                    "Sprite '%s' chargé: %dx%d à (%d,%d)", name, width, height, x, y));
            }
            
            Gdx.app.log("SwordsmanMapping", String.format(
                "Mapping Idle chargé: %d sprites", idleSprites.size()));
        } catch (Exception e) {
            Gdx.app.error("SwordsmanMapping", "Erreur lors du chargement du mapping Idle: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Retourne un sprite Idle par son nom (sprite1, sprite2, etc.)
     */
    public TextureRegion getIdleSprite(String spriteName) {
        return idleSprites.get(spriteName);
    }
    
    /**
     * Retourne tous les sprites Idle
     */
    public Map<String, TextureRegion> getIdleSprites() {
        return idleSprites;
    }
    
    /**
     * Retourne un tableau de sprites pour une direction donnée
     * On suppose que les sprites sont organisés en 4 directions (lignes)
     * Direction 0: sprite1-12 (12 frames)
     * Direction 1: sprite13-24 (12 frames)
     * Direction 2: sprite25-36 (12 frames)
     * Direction 3: sprite37-40 (4 frames) - peut-être incomplet
     */
    public TextureRegion[] getIdleFramesForDirection(int direction) {
        TextureRegion[] frames;
        
        switch (direction) {
            case 0: // Première direction (sprite1-12)
                frames = new TextureRegion[12];
                for (int i = 0; i < 12; i++) {
                    frames[i] = idleSprites.get("sprite" + (i + 1));
                }
                break;
            case 1: // Deuxième direction (sprite13-24)
                frames = new TextureRegion[12];
                for (int i = 0; i < 12; i++) {
                    frames[i] = idleSprites.get("sprite" + (i + 13));
                }
                break;
            case 2: // Troisième direction (sprite25-36)
                frames = new TextureRegion[12];
                for (int i = 0; i < 12; i++) {
                    frames[i] = idleSprites.get("sprite" + (i + 25));
                }
                break;
            case 3: // Quatrième direction (sprite37-40)
                frames = new TextureRegion[4];
                for (int i = 0; i < 4; i++) {
                    frames[i] = idleSprites.get("sprite" + (i + 37));
                }
                break;
            default:
                frames = new TextureRegion[0];
        }
        
        return frames;
    }
    
    /**
     * Libère les ressources
     */
    public void dispose() {
        if (idleTexture != null) {
            idleTexture.dispose();
        }
    }
}

