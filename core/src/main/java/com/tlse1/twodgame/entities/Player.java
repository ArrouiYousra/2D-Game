package com.tlse1.twodgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.tlse1.twodgame.entities.handlers.AnimationLoader;
import com.tlse1.twodgame.utils.Direction;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe représentant le joueur.
 * Hérite de Character et charge les animations du swordsman.
 */
public class Player extends Character {
    
    // Inventaire du joueur
    private Inventory inventory;
    
    // Hitboxes dynamiques pour les animations d'attaque
    // Mapping : animationType_direction_frameIndex -> hitbox {width, height}
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
     * Constructeur par défaut.
     */
    public Player() {
        this(0, 0);
    }
    
    /**
     * Constructeur avec position.
     * 
     * @param x Position X initiale
     * @param y Position Y initiale
     */
    public Player(float x, float y) {
        super(x, y);
        
        // Configurer la santé du joueur (50 HP)
        combatHandler.setMaxHealth(50);
        combatHandler.setHealth(50);
        
        // Configurer le shield du joueur (3 shield max pour tester)
        combatHandler.setMaxShield(3);
        combatHandler.setShield(3);
        
        // Initialiser l'inventaire
        inventory = new Inventory();
        
        // Charger les hitboxes dynamiques pour les animations d'attaque
        loadAttackHitboxes();
        
        // Définir le scale pour que le joueur fasse 32x32 pixels
        // Les sprites sont maintenant découpés en 64x64 pixels
        // Pour obtenir 32x32 pixels, on calcule le scale : 32 / 64 = 0.5
        float targetSize = 32f;
        float spriteSize = 64f; // Tous les sprites font maintenant 64x64 pixels
        
        // Calculer le scale pour obtenir la taille cible
        float calculatedScale = targetSize / spriteSize; // 32 / 64 = 0.5
        animationHandler.setScale(calculatedScale);
        
        // Charger toutes les animations
        loadAnimations();
        
        // Définir l'animation par défaut
        animationHandler.update(0f);
    }
    
    /**
     * Retourne l'inventaire du joueur.
     * 
     * @return L'inventaire
     */
    public Inventory getInventory() {
        return inventory;
    }
    
    /**
     * Utilise un item de type shield.
     * Restaure le shield du joueur.
     * 
     * @return true si un item shield a été utilisé
     */
    public boolean useShieldItem() {
        if (inventory.useItem(Inventory.ItemType.SHIELD)) {
            // Restaurer le shield à son maximum
            int maxShield = combatHandler.getMaxShield();
            if (maxShield > 0) {
                combatHandler.setShield(maxShield);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Utilise un item de type heal.
     * Restaure la santé du joueur.
     * 
     * @return true si un item heal a été utilisé
     */
    public boolean useHealItem() {
        if (inventory.useItem(Inventory.ItemType.HEAL)) {
            // Restaurer la santé à son maximum
            int maxHealth = combatHandler.getMaxHealth();
            combatHandler.setHealth(maxHealth);
            return true;
        }
        return false;
    }
    
    /**
     * Charge toutes les animations du swordsman depuis les fichiers JSON.
     * Les sprites sont organisés en grille : 4 lignes (directions) x N colonnes
     * Ligne 1 (y=0-63): DOWN, Ligne 2 (y=64-127): LEFT, Ligne 3 (y=128-191): RIGHT, Ligne 4 (y=192-255): UP
     * yRanges format: [DOWN_MIN, DOWN_MAX, SIDE_LEFT_MIN, SIDE_LEFT_MAX, SIDE_MIN, SIDE_MAX, UP_MIN, UP_MAX]
     */
    @Override
    protected void loadAnimations() {
        // Idle: 12 sprites par direction
        // yRanges: [DOWN: 0-63, SIDE_LEFT: 64-127, SIDE: 128-191, UP: 192-255]
        AnimationLoader.loadAnimation(animationHandler,
            "swordsman1-3/swordsman_lvl1_idle_sprites.json",
            "swordsman1-3/PNG/Swordsman_lvl1/With_shadow/Swordsman_lvl1_Idle_with_shadow.png",
            "idle", 0.15f, new int[]{0, 63, 64, 127, 128, 191, 192, 255}, true);
        
        // Walk: 6 sprites par direction
        AnimationLoader.loadAnimation(animationHandler,
            "swordsman1-3/swordsman_lvl1_walk_sprites.json",
            "swordsman1-3/PNG/Swordsman_lvl1/With_shadow/Swordsman_lvl1_Walk_with_shadow.png",
            "walk", 0.12f, new int[]{0, 63, 64, 127, 128, 191, 192, 255}, true);
        
        // Run: 8 sprites par direction
        AnimationLoader.loadAnimation(animationHandler,
            "swordsman1-3/swordsman_lvl1_run_sprites.json",
            "swordsman1-3/PNG/Swordsman_lvl1/With_shadow/Swordsman_lvl1_Run_with_shadow.png",
            "run", 0.10f, new int[]{0, 63, 64, 127, 128, 191, 192, 255}, true);
        
        // Attack: 8 sprites par direction
        AnimationLoader.loadAnimation(animationHandler,
            "swordsman1-3/swordsman_lvl1_attack_sprites.json",
            "swordsman1-3/PNG/Swordsman_lvl1/With_shadow/Swordsman_lvl1_attack_with_shadow.png",
            "attack", 0.08f, new int[]{0, 63, 64, 127, 128, 191, 192, 255}, false);
        
        // Walk Attack: 6 sprites par direction
        AnimationLoader.loadAnimation(animationHandler,
            "swordsman1-3/swordsman_lvl1_walk_attack_sprites.json",
            "swordsman1-3/PNG/Swordsman_lvl1/With_shadow/Swordsman_lvl1_Walk_Attack_with_shadow.png",
            "walk_attack", 0.12f, new int[]{0, 63, 64, 127, 128, 191, 192, 255}, false);
        
        // Run Attack: 8 sprites par direction
        AnimationLoader.loadAnimation(animationHandler,
            "swordsman1-3/swordsman_lvl1_run_attack_sprites.json",
            "swordsman1-3/PNG/Swordsman_lvl1/With_shadow/Swordsman_lvl1_Run_Attack_with_shadow.png",
            "run_attack", 0.10f, new int[]{0, 63, 64, 127, 128, 191, 192, 255}, false);
        
        // Hurt: 5 sprites par direction
        AnimationLoader.loadAnimation(animationHandler,
            "swordsman1-3/swordsman_lvl1_hurt_sprites.json",
            "swordsman1-3/PNG/Swordsman_lvl1/With_shadow/Swordsman_lvl1_Hurt_with_shadow.png",
            "hurt", 0.1f, new int[]{0, 63, 64, 127, 128, 191, 192, 255}, false);
        
        // Death: 7 sprites par direction
        AnimationLoader.loadAnimation(animationHandler,
            "swordsman1-3/swordsman_lvl1_death_sprites.json",
            "swordsman1-3/PNG/Swordsman_lvl1/With_shadow/Swordsman_lvl1_Death_with_shadow.png",
            "death", 0.15f, new int[]{0, 63, 64, 127, 128, 191, 192, 255}, false);
    }
    
    /**
     * Charge les hitboxes dynamiques depuis les fichiers JSON pour les animations d'attaque.
     * Les hitboxes sont organisées par type d'animation (attack, walk_attack, run_attack)
     * et mappées selon la direction et l'index de frame.
     */
    private void loadAttackHitboxes() {
        attackHitboxes = new HashMap<>();
        
        // Charger les hitboxes pour chaque type d'animation
        loadHitboxFile("swordsman1-3/swordsman_attack_hitbox.json", "attack", 8); // 8 sprites par direction
        loadHitboxFile("swordsman1-3/swordsman_walk_attack_hitbox.json", "walk_attack", 6); // 6 sprites par direction
        loadHitboxFile("swordsman1-3/swordsman_run_attack_hitbox.json", "run_attack", 8); // 8 sprites par direction
        
        Gdx.app.log("Player", "Hitboxes d'attaque chargées : " + attackHitboxes.size() + " entrées");
    }
    
    /**
     * Charge un fichier de hitboxes et les mappe selon la direction et l'index de frame.
     * 
     * @param filePath Chemin vers le fichier JSON
     * @param animationType Type d'animation ("attack", "walk_attack", "run_attack")
     * @param spritesPerDirection Nombre de sprites par direction
     */
    private void loadHitboxFile(String filePath, String animationType, int spritesPerDirection) {
        try {
            JsonReader jsonReader = new JsonReader();
            JsonValue hitboxData = jsonReader.parse(Gdx.files.internal(filePath));
            
            // Les hitboxes sont organisées : sprite1-6/8 (DOWN), sprite9-16 (LEFT), sprite17-24 (RIGHT), sprite25-32 (UP)
            // Pour le joueur : Ligne 1 = DOWN, Ligne 2 = LEFT (SIDE_LEFT), Ligne 3 = RIGHT (SIDE), Ligne 4 = UP
            // Note: Les sprites ne sont pas triés dans le JSON, on doit les trier par numéro
            
            // Créer une liste temporaire pour trier
            java.util.List<JsonValue> sortedHitboxes = new java.util.ArrayList<>();
            for (int i = 0; i < hitboxData.size; i++) {
                sortedHitboxes.add(hitboxData.get(i));
            }
            
            // Trier par numéro de sprite
            sortedHitboxes.sort((a, b) -> {
                String nameA = a.getString("name");
                String nameB = b.getString("name");
                int numA = Integer.parseInt(nameA.replace("sprite", ""));
                int numB = Integer.parseInt(nameB.replace("sprite", ""));
                return Integer.compare(numA, numB);
            });
            
            // Mapper les hitboxes triées
            for (int i = 0; i < sortedHitboxes.size(); i++) {
                JsonValue hitbox = sortedHitboxes.get(i);
                float width = hitbox.getFloat("width");
                float height = hitbox.getFloat("height");
                
                // Le sprite numéro est i+1 (car trié de 1 à N)
                int spriteNum = i + 1;
                
                // Déterminer la direction et le frame index
                Direction direction;
                int frameIndex;
                
                if (spriteNum >= 1 && spriteNum <= spritesPerDirection) {
                    // DOWN : sprite1-6/8 -> frames 0-5/7
                    direction = Direction.DOWN;
                    frameIndex = spriteNum - 1;
                } else if (spriteNum >= spritesPerDirection + 1 && spriteNum <= spritesPerDirection * 2) {
                    // LEFT (SIDE_LEFT) : sprite9-16 -> frames 0-7
                    direction = Direction.SIDE_LEFT;
                    frameIndex = spriteNum - spritesPerDirection - 1;
                } else if (spriteNum >= spritesPerDirection * 2 + 1 && spriteNum <= spritesPerDirection * 3) {
                    // RIGHT (SIDE) : sprite17-24 -> frames 0-7
                    direction = Direction.SIDE;
                    frameIndex = spriteNum - spritesPerDirection * 2 - 1;
                } else if (spriteNum >= spritesPerDirection * 3 + 1 && spriteNum <= spritesPerDirection * 4) {
                    // UP : sprite25-32 -> frames 0-7
                    direction = Direction.UP;
                    frameIndex = spriteNum - spritesPerDirection * 3 - 1;
                } else {
                    continue; // Ignorer les sprites hors range
                }
                
                // Créer la clé pour le mapping : animationType_direction_frameIndex
                String key = animationType + "_" + direction.name() + "_" + frameIndex;
                attackHitboxes.put(key, new HitboxData(width, height));
            }
        } catch (Exception e) {
            Gdx.app.error("Player", "Erreur lors du chargement des hitboxes d'attaque depuis " + filePath, e);
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
        
        String animationType = animationHandler.getAttackAnimationType();
        if (animationType == null) {
            return null;
        }
        
        Direction direction = animationHandler.getCurrentDirection();
        int frameIndex = animationHandler.getCurrentFrameIndex();
        
        if (frameIndex < 0) {
            return null;
        }
        
        // Créer la clé pour le mapping
        String key = animationType + "_" + direction.name() + "_" + frameIndex;
        HitboxData hitbox = attackHitboxes.get(key);
        
        if (hitbox != null) {
            return new float[]{hitbox.width, hitbox.height};
        }
        
        return null;
    }
}
