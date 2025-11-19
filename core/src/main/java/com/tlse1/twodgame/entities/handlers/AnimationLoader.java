package com.tlse1.twodgame.entities.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.tlse1.twodgame.utils.Direction;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilitaire pour charger les animations depuis des fichiers JSON.
 */
public class AnimationLoader {
    
    /**
     * Charge une animation depuis un fichier JSON et l'ajoute au handler.
     * 
     * @param handler Le handler d'animation
     * @param jsonPath Chemin vers le fichier JSON
     * @param pngPath Chemin vers le fichier PNG
     * @param animationType Type d'animation (idle, walk, run, attack, etc.)
     * @param frameDuration Durée de chaque frame
     * @param yRanges Plages de Y pour chaque direction [DOWN_MIN, DOWN_MAX, SIDE_LEFT_MIN, SIDE_LEFT_MAX, SIDE_MIN, SIDE_MAX, UP_MIN, UP_MAX]
     * @param looping Si l'animation doit boucler
     */
    public static void loadAnimation(AnimationHandler handler, String jsonPath, String pngPath,
                                    String animationType, float frameDuration, int[] yRanges, boolean looping) {
        try {
            JsonReader jsonReader = new JsonReader();
            JsonValue spritesData = jsonReader.parse(Gdx.files.internal(jsonPath));
            
            Texture texture = new Texture(Gdx.files.internal(pngPath));
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            handler.addTexture(texture);
            
            List<JsonValue> downSprites = new ArrayList<>();
            List<JsonValue> sideLeftSprites = new ArrayList<>();
            List<JsonValue> sideSprites = new ArrayList<>();
            List<JsonValue> upSprites = new ArrayList<>();
            
            for (int i = 0; i < spritesData.size; i++) {
                JsonValue sprite = spritesData.get(i);
                int y = sprite.getInt("y");
                
                if (y >= yRanges[0] && y <= yRanges[1]) {
                    downSprites.add(sprite);
                } else if (y >= yRanges[2] && y <= yRanges[3]) {
                    sideLeftSprites.add(sprite);
                } else if (y >= yRanges[4] && y <= yRanges[5]) {
                    sideSprites.add(sprite);
                } else if (y >= yRanges[6] && y <= yRanges[7]) {
                    upSprites.add(sprite);
                }
            }
            
            // Trier les sprites par X (ordre horizontal)
            downSprites.sort((a, b) -> a.getInt("x") - b.getInt("x"));
            sideLeftSprites.sort((a, b) -> a.getInt("x") - b.getInt("x"));
            sideSprites.sort((a, b) -> a.getInt("x") - b.getInt("x"));
            upSprites.sort((a, b) -> a.getInt("x") - b.getInt("x"));
            
            // Créer les animations
            createAndAddAnimation(handler, downSprites, Direction.DOWN, texture, frameDuration, animationType, looping);
            createAndAddAnimation(handler, sideLeftSprites, Direction.SIDE_LEFT, texture, frameDuration, animationType, looping);
            createAndAddAnimation(handler, sideSprites, Direction.SIDE, texture, frameDuration, animationType, looping);
            createAndAddAnimation(handler, upSprites, Direction.UP, texture, frameDuration, animationType, looping);
            
            // Compter le nombre de directions avec des sprites
            int directionsCount = 0;
            if (!downSprites.isEmpty()) directionsCount++;
            if (!sideLeftSprites.isEmpty()) directionsCount++;
            if (!sideSprites.isEmpty()) directionsCount++;
            if (!upSprites.isEmpty()) directionsCount++;
            
            // Compter le nombre total de sprites
            int totalSprites = downSprites.size() + sideLeftSprites.size() + sideSprites.size() + upSprites.size();
            
            Gdx.app.log("AnimationLoader", String.format(
                "Animation %s chargée: %d directions, %d sprites au total", animationType, directionsCount, totalSprites));
            
        } catch (Exception e) {
            Gdx.app.error("AnimationLoader", "Erreur lors du chargement de l'animation " + animationType, e);
            e.printStackTrace();
        }
    }
    
    private static void createAndAddAnimation(AnimationHandler handler, List<JsonValue> sprites,
                                             Direction direction, Texture texture, float frameDuration,
                                             String animationType, boolean looping) {
        if (sprites.isEmpty()) {
            return;
        }
        
        // Pour les animations de mort des vampires 2 et 3, ajouter un 12e sprite vide
        // pour que l'animation se termine correctement et que le vampire disparaisse
        int expectedSpritesPerDirection = 12;
        int actualSprites = sprites.size();
        boolean needsEmptySprite = animationType.equals("death") && actualSprites < expectedSpritesPerDirection;
        
        int totalFrames = needsEmptySprite ? expectedSpritesPerDirection : actualSprites;
        TextureRegion[] frames = new TextureRegion[totalFrames];
        
        // Charger tous les sprites existants
        for (int i = 0; i < sprites.size(); i++) {
            JsonValue sprite = sprites.get(i);
            int x = sprite.getInt("x");
            int y = sprite.getInt("y");
            int width = sprite.getInt("width");
            int height = sprite.getInt("height");
            
            frames[i] = new TextureRegion(texture, x, y, width, height);
        }
        
        // Ajouter un sprite vide (12e frame) pour les animations de mort avec 11 sprites
        // Cela permet à l'animation de se terminer correctement et au vampire de disparaître
        if (needsEmptySprite) {
            // Dupliquer le dernier sprite comme 12e frame
            // Quand l'animation atteint cette frame, elle se termine et le vampire disparaît
            frames[actualSprites] = new TextureRegion(frames[actualSprites - 1]);
            
            Gdx.app.log("AnimationLoader", String.format(
                "Animation %s (%s): Ajout d'un 12e sprite (dupliqué) pour compléter l'animation (avait %d sprites)",
                animationType, direction, actualSprites));
        }
        
        Animation<TextureRegion> animation = new Animation<>(frameDuration, frames);
        animation.setPlayMode(looping ? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL);
        
        // Ajouter l'animation au handler selon le type
        switch (animationType) {
            case "idle":
                handler.addIdleAnimation(direction, animation);
                break;
            case "walk":
                handler.addWalkAnimation(direction, animation);
                break;
            case "run":
                handler.addRunAnimation(direction, animation);
                break;
            case "attack":
                handler.addAttackAnimation(direction, animation);
                break;
            case "walk_attack":
                handler.addWalkAttackAnimation(direction, animation);
                break;
            case "run_attack":
                handler.addRunAttackAnimation(direction, animation);
                break;
            case "hurt":
                handler.addHurtAnimation(direction, animation);
                break;
            case "death":
                handler.addDeathAnimation(direction, animation);
                break;
        }
    }
}

