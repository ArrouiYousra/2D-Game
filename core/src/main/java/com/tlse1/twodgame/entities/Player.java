package com.tlse1.twodgame.entities;

import com.tlse1.twodgame.entities.handlers.AnimationLoader;

/**
 * Classe représentant le joueur.
 * Hérite de Character et charge les animations du swordsman.
 */
public class Player extends Character {
    
    // Inventaire du joueur
    private Inventory inventory;
    
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
        
        // Configurer la santé du joueur (5 HP)
        combatHandler.setMaxHealth(5);
        combatHandler.setHealth(5);
        
        // Configurer le shield du joueur (3 shield max pour tester)
        combatHandler.setMaxShield(3);
        combatHandler.setShield(3);
        
        // Initialiser l'inventaire
        inventory = new Inventory();
        
        // Définir le scale pour que le joueur fasse environ 16x16 pixels
        // Les sprites originaux font environ 17-20 pixels de large et 27-28 pixels de haut
        // Pour obtenir 16x16 max, on calcule le scale basé sur la dimension la plus grande (hauteur)
        float targetSize = 16f;
        float averageSpriteWidth = 18f; // Largeur moyenne des sprites
        float averageSpriteHeight = 27f; // Hauteur moyenne des sprites
        
        // Calculer le scale pour chaque dimension
        float scaleX = targetSize / averageSpriteWidth;
        float scaleY = targetSize / averageSpriteHeight;
        
        // Utiliser le plus petit scale pour garantir que le joueur ne dépasse pas 16 pixels
        float calculatedScale = Math.min(scaleX, scaleY);
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
     */
    @Override
    protected void loadAnimations() {
        // Idle: y=20 (DOWN), y=83 (SIDE_LEFT), y=147 (SIDE), y=212 (UP)
        AnimationLoader.loadAnimation(animationHandler,
            "swordsman1-3/swordsman_lvl1_idle_sprites.json",
            "swordsman1-3/PNG/Swordsman_lvl1/With_shadow/Swordsman_lvl1_Idle_with_shadow.png",
            "idle", 0.15f, new int[]{20, 20, 83, 83, 147, 147, 212, 212}, true);
        
        // Walk: y=19-21 (DOWN), y=81-83 (SIDE_LEFT), y=145-147 (SIDE), y=210-212 (UP)
        AnimationLoader.loadAnimation(animationHandler,
            "swordsman1-3/swordsman_lvl1_walk_sprites.json",
            "swordsman1-3/PNG/Swordsman_lvl1/With_shadow/Swordsman_lvl1_Walk_with_shadow.png",
            "walk", 0.12f, new int[]{19, 21, 81, 83, 145, 147, 210, 212}, true);
        
        // Run: y=17-19 (DOWN), y=81-84 (SIDE_LEFT), y=145-148 (SIDE), y=210-211 (UP)
        AnimationLoader.loadAnimation(animationHandler,
            "swordsman1-3/swordsman_lvl1_run_sprites.json",
            "swordsman1-3/PNG/Swordsman_lvl1/With_shadow/Swordsman_lvl1_Run_with_shadow.png",
            "run", 0.10f, new int[]{17, 19, 81, 84, 145, 148, 210, 211}, true);
        
        // Attack: y=19-24 (DOWN), y=80-83 (SIDE_LEFT), y=144-147 (SIDE), y=205-213 (UP)
        AnimationLoader.loadAnimation(animationHandler,
            "swordsman1-3/swordsman_lvl1_attack_sprites.json",
            "swordsman1-3/PNG/Swordsman_lvl1/With_shadow/Swordsman_lvl1_attack_with_shadow.png",
            "attack", 0.08f, new int[]{19, 24, 80, 83, 144, 147, 205, 213}, false);
        
        // Walk Attack: y=19-23 (DOWN), y=80-84 (SIDE_LEFT), y=144-148 (SIDE), y=205-213 (UP)
        AnimationLoader.loadAnimation(animationHandler,
            "swordsman1-3/swordsman_lvl1_walk_attack_sprites.json",
            "swordsman1-3/PNG/Swordsman_lvl1/With_shadow/Swordsman_lvl1_Walk_Attack_with_shadow.png",
            "walk_attack", 0.12f, new int[]{19, 23, 80, 84, 144, 148, 205, 213}, false);
        
        // Run Attack: y=17-19 (DOWN), y=81-84 (SIDE_LEFT), y=145-148 (SIDE), y=210-211 (UP)
        AnimationLoader.loadAnimation(animationHandler,
            "swordsman1-3/swordsman_lvl1_run_attack_sprites.json",
            "swordsman1-3/PNG/Swordsman_lvl1/With_shadow/Swordsman_lvl1_Run_Attack_with_shadow.png",
            "run_attack", 0.10f, new int[]{17, 19, 81, 84, 145, 148, 210, 211}, false);
        
        // Hurt: y=20-23 (DOWN), y=83-86 (SIDE_LEFT), y=147-150 (SIDE), y=212-214 (UP)
        AnimationLoader.loadAnimation(animationHandler,
            "swordsman1-3/swordsman_lvl1_hurt_sprites.json",
            "swordsman1-3/PNG/Swordsman_lvl1/With_shadow/Swordsman_lvl1_Hurt_with_shadow.png",
            "hurt", 0.1f, new int[]{20, 23, 83, 86, 147, 150, 212, 214}, false);
        
        // Death: y=20-31 (DOWN), y=83-92 (SIDE_LEFT), y=147-156 (SIDE), y=212-222 (UP)
        AnimationLoader.loadAnimation(animationHandler,
            "swordsman1-3/swordsman_lvl1_death_sprites.json",
            "swordsman1-3/PNG/Swordsman_lvl1/With_shadow/Swordsman_lvl1_Death_with_shadow.png",
            "death", 0.15f, new int[]{20, 31, 83, 92, 147, 156, 212, 222}, false);
    }
}
