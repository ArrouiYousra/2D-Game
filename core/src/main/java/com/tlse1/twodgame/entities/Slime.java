package com.tlse1.twodgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.tlse1.twodgame.entities.handlers.AnimationLoader;
import com.tlse1.twodgame.utils.Direction;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe représentant un slime ennemi dans le jeu.
 * Hérite de Enemy et charge les animations selon le niveau (1, 2 ou 3).
 */
public class Slime extends Enemy {
    
    // Niveau du slime (1, 2 ou 3)
    private int level;
    
    // Hitboxes dynamiques pour l'animation d'attaque
    // Mapping : direction + frameIndex -> hitbox {width, height}
    // Tous les slimes utilisent le même fichier slims_hitbox.json
    private Map<String, HitboxData> attackHitboxes;
    
    /**
     * Structure pour stocker les données de hitbox
     */
    private static class HitboxData {
        float width;
        float height;
        
        HitboxData(float width, float height) {
            this.width = width;
            this.height = height;
        }
    }
    
    /**
     * Constructeur par défaut (niveau 1).
     */
    public Slime() {
        this(0, 0, 1);
    }
    
    /**
     * Constructeur avec position (niveau 1 par défaut).
     * 
     * @param x Position X initiale
     * @param y Position Y initiale
     */
    public Slime(float x, float y) {
        this(x, y, 1);
    }
    
    /**
     * Constructeur avec position et niveau.
     * 
     * @param x Position X initiale
     * @param y Position Y initiale
     * @param level Niveau du slime (1, 2 ou 3)
     */
    public Slime(float x, float y, int level) {
        super(x, y);
        this.level = level;
        
        // Configurer la vitesse du slime (1/4 de la vitesse du joueur)
        // Le joueur a une vitesse de 150 pixels/seconde, donc 150 / 4 = 37.5
        float playerSpeed = 150f;
        setSpeed(playerSpeed / 4f); // 37.5 pixels/seconde
        
        // Configurer la santé du slime (moins que le vampire)
        combatHandler.setMaxHealth(50);
        combatHandler.setHealth(50);
        
        // Définir la hitbox fixe du slime par défaut (centrée sur chaque sprite)
        // La hitbox est utilisée pour les collisions et les attaques
        // Dimensions réelles de la hitbox dans les sprites : 17x16 pixels
        setHitboxWidth(17f);  // 17 pixels de largeur
        setHitboxHeight(16f); // 16 pixels de hauteur
        
        // Charger les hitboxes dynamiques pour l'animation d'attaque
        loadAttackHitboxes();
        
        // Charger toutes les animations
        loadAnimations();
        
        // Définir le scale pour que le slime ait la même taille visuelle que le joueur
        // Le joueur utilise un scale de ~0.59 (basé sur sa hauteur de 27 pixels pour obtenir 16 pixels)
        // On utilise le même scale pour le slime pour avoir une taille similaire
        float targetSize = 16f;
        float averageSpriteHeight = 27f; // Même hauteur cible que le joueur
        
        // Utiliser le même calcul que le joueur : scale basé sur la hauteur cible
        float calculatedScale = targetSize / averageSpriteHeight; // ~0.59, comme le joueur
        animationHandler.setScale(calculatedScale);
        
        // Définir l'animation par défaut (idle)
        animationHandler.update(0f);
        animationHandler.setCurrentDirection(Direction.DOWN);
        animationHandler.setMoving(false);
    }
    
    /**
     * Charge toutes les animations du slime depuis les fichiers JSON selon le niveau.
     * Les sprites sont organisés en grille : 4 lignes (directions) x N colonnes
     * Ligne 1 (y=0-63): DOWN, Ligne 2 (y=64-127): UP, Ligne 3 (y=128-191): LEFT, Ligne 4 (y=192-255): RIGHT
     * yRanges format: [DOWN_MIN, DOWN_MAX, SIDE_LEFT_MIN, SIDE_LEFT_MAX, SIDE_MIN, SIDE_MAX, UP_MIN, UP_MAX]
     */
    @Override
    protected void loadAnimations() {
        String slimePrefix = "slims/PNG/Slime" + level;
        String jsonPrefix = "slims/PNG/Slime" + level;
        
        // Note: Le slime 1 utilise "slim1_" tandis que les slimes 2 et 3 utilisent "slime2_" et "slime3_"
        String jsonNamePrefix = (level == 1) ? "slim" : "slime";
        
        // yRanges pour les slimes : 
        // Ligne 1 (y=0-63): DOWN
        // Ligne 2 (y=64-127): UP
        // Ligne 3 (y=128-191): GAUCHE (SIDE_LEFT)
        // Ligne 4 (y=192-255): DROITE (SIDE)
        int[] yRanges = new int[]{0, 63, 128, 191, 192, 255, 64, 127};
        
        // Idle
        AnimationLoader.loadAnimation(animationHandler,
            jsonPrefix + "/" + jsonNamePrefix + level + "_idle.json",
            slimePrefix + "/With_shadow/Slime" + level + "_Idle_with_shadow.png",
            "idle", 0.15f, yRanges, true);
        
        // Walk
        AnimationLoader.loadAnimation(animationHandler,
            jsonPrefix + "/" + jsonNamePrefix + level + "_walk.json",
            slimePrefix + "/With_shadow/Slime" + level + "_Walk_with_shadow.png",
            "walk", 0.12f, yRanges, true);
        
        // Run
        AnimationLoader.loadAnimation(animationHandler,
            jsonPrefix + "/" + jsonNamePrefix + level + "_run.json",
            slimePrefix + "/With_shadow/Slime" + level + "_Run_with_shadow.png",
            "run", 0.10f, yRanges, true);
        
        // Attack
        AnimationLoader.loadAnimation(animationHandler,
            jsonPrefix + "/" + jsonNamePrefix + level + "_attack.json",
            slimePrefix + "/With_shadow/Slime" + level + "_Attack_with_shadow.png",
            "attack", 0.08f, yRanges, false);
        
        // Hurt
        AnimationLoader.loadAnimation(animationHandler,
            jsonPrefix + "/" + jsonNamePrefix + level + "_hurt.json",
            slimePrefix + "/With_shadow/Slime" + level + "_Hurt_with_shadow.png",
            "hurt", 0.1f, yRanges, false);
        
        // Death
        AnimationLoader.loadAnimation(animationHandler,
            jsonPrefix + "/" + jsonNamePrefix + level + "_death.json",
            slimePrefix + "/With_shadow/Slime" + level + "_Death_with_shadow.png",
            "death", 0.15f, yRanges, false);
        
        Gdx.app.log("Slime", String.format("Slime niveau %d: animations chargées", level));
    }
    
    /**
     * Charge les hitboxes dynamiques depuis slims_hitbox.json.
     * Le mapping est : sprite185-194 (DOWN), sprite195-204 (UP), sprite205-214 (LEFT), sprite215-224 (RIGHT)
     * correspondant à sprite1-10, sprite11-20, sprite21-30, sprite31-40 dans le fichier hitbox
     */
    private void loadAttackHitboxes() {
        attackHitboxes = new HashMap<>();
        
        try {
            JsonReader jsonReader = new JsonReader();
            JsonValue hitboxData = jsonReader.parse(Gdx.files.internal("slims/slims_hitbox.json"));
            
            // Mapping des sprites d'attaque vers les hitboxes
            // slim1_attack.json : sprite185-194 (DOWN, frames 0-9) -> sprite1-10
            //                    sprite195-204 (UP, frames 0-9) -> sprite11-20
            //                    sprite205-214 (LEFT, frames 0-9) -> sprite21-30
            //                    sprite215-224 (RIGHT, frames 0-9) -> sprite31-40
            
            for (int i = 0; i < hitboxData.size; i++) {
                JsonValue hitbox = hitboxData.get(i);
                String spriteName = hitbox.getString("name");
                float width = hitbox.getFloat("width");
                float height = hitbox.getFloat("height");
                
                // Extraire le numéro du sprite (sprite1 -> 1, sprite11 -> 11, etc.)
                int spriteNum = Integer.parseInt(spriteName.replace("sprite", ""));
                
                // Déterminer la direction et le frame index
                Direction direction;
                int frameIndex;
                
                if (spriteNum >= 1 && spriteNum <= 10) {
                    // DOWN : sprite1-10 -> frames 0-9
                    direction = Direction.DOWN;
                    frameIndex = spriteNum - 1;
                } else if (spriteNum >= 11 && spriteNum <= 20) {
                    // UP : sprite11-20 -> frames 0-9
                    direction = Direction.UP;
                    frameIndex = spriteNum - 11;
                } else if (spriteNum >= 21 && spriteNum <= 30) {
                    // LEFT : sprite21-30 -> frames 0-9
                    direction = Direction.SIDE_LEFT;
                    frameIndex = spriteNum - 21;
                } else if (spriteNum >= 31 && spriteNum <= 40) {
                    // RIGHT : sprite31-40 -> frames 0-9
                    direction = Direction.SIDE;
                    frameIndex = spriteNum - 31;
                } else {
                    continue; // Ignorer les sprites hors range
                }
                
                // Créer la clé pour le mapping
                String key = direction.name() + "_" + frameIndex;
                attackHitboxes.put(key, new HitboxData(width, height));
            }
            
            Gdx.app.log("Slime", "Hitboxes d'attaque chargées : " + attackHitboxes.size() + " entrées");
        } catch (Exception e) {
            Gdx.app.error("Slime", "Erreur lors du chargement des hitboxes d'attaque", e);
        }
    }
    
    /**
     * Obtient la hitbox actuelle selon l'animation d'attaque en cours.
     * 
     * @return Tableau [width, height] de la hitbox, ou null si pas en attaque
     */
    public float[] getCurrentAttackHitbox() {
        if (!animationHandler.isAttackAnimation()) {
            return null;
        }
        
        Direction direction = animationHandler.getCurrentDirection();
        int frameIndex = animationHandler.getCurrentFrameIndex();
        
        if (frameIndex < 0 || frameIndex >= 10) {
            return null;
        }
        
        String key = direction.name() + "_" + frameIndex;
        HitboxData hitbox = attackHitboxes.get(key);
        
        if (hitbox != null) {
            return new float[]{hitbox.width, hitbox.height};
        }
        
        return null;
    }
    
    /**
     * Calcule la direction vers la cible pour les slimes.
     * Pour les slimes : Ligne 3 = LEFT (SIDE_LEFT), Ligne 4 = RIGHT (SIDE)
     * Contrairement aux vampires, les directions ne sont pas inversées.
     * 
     * @param dx Différence X (cible - ennemi)
     * @param dy Différence Y (cible - ennemi)
     * @return Direction vers la cible
     */
    @Override
    protected Direction calculateDirectionToTarget(float dx, float dy) {
        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) {
                return Direction.SIDE; // Joueur à droite -> utiliser SIDE (ligne 4)
            } else {
                return Direction.SIDE_LEFT; // Joueur à gauche -> utiliser SIDE_LEFT (ligne 3)
            }
        } else {
            if (dy > 0) {
                return Direction.UP;
            } else {
                return Direction.DOWN;
            }
        }
    }
}

