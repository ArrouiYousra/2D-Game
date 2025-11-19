package com.tlse1.twodgame.entities;

import com.badlogic.gdx.Gdx;
import com.tlse1.twodgame.entities.handlers.AnimationLoader;
import com.tlse1.twodgame.utils.Direction;

/**
 * Classe représentant un vampire ennemi dans le jeu.
 * Hérite de Enemy et charge les animations selon le niveau (Vampires1, Vampires2, Vampires3).
 */
public class Vampire extends Enemy {
    
    // Niveau du vampire (1, 2 ou 3)
    private int level;
    
    /**
     * Constructeur par défaut (niveau 1).
     */
    public Vampire() {
        this(0, 0, 1);
    }
    
    /**
     * Constructeur avec position et niveau.
     * 
     * @param x Position X initiale
     * @param y Position Y initiale
     * @param level Niveau du vampire (1, 2 ou 3)
     */
    public Vampire(float x, float y, int level) {
        super(x, y);
        
        this.level = Math.max(1, Math.min(3, level)); // Clamp entre 1 et 3
        
        // Configurer la vitesse du vampire (1/4 de la vitesse du joueur)
        // Le joueur a une vitesse de 150 pixels/seconde, donc 150 / 4 = 37.5
        float playerSpeed = 150f;
        setSpeed(playerSpeed / 4f); // 37.5 pixels/seconde
        
        // Configurer la santé selon le niveau
        int health = 100 * level; // Niveau 1 = 100 HP, Niveau 2 = 200 HP, Niveau 3 = 300 HP
        combatHandler.setMaxHealth(health);
        combatHandler.setHealth(health);
        
        // Définir la hitbox fixe du vampire (30x30 pixels)
        setHitboxWidth(30f);
        setHitboxHeight(30f);
        
        // Définir le scale pour que le vampire fasse 32x32 pixels (comme le joueur)
        // Les sprites sont découpés en 64x64 pixels
        float targetSize = 32f;
        float spriteSize = 64f;
        float calculatedScale = targetSize / spriteSize; // 32 / 64 = 0.5
        animationHandler.setScale(calculatedScale);
        
        // Charger toutes les animations selon le niveau
        loadAnimations();
        
        // Définir l'animation par défaut
        animationHandler.update(0f);
        
        // Vérifier que les animations sont bien chargées
        if (animationHandler.getCurrentAnimation() == null) {
            Gdx.app.error("Vampire", String.format("Vampire niveau %d: Aucune animation chargée après loadAnimations()!", level));
        } else {
            Gdx.app.log("Vampire", String.format("Vampire niveau %d: Animation initialisée correctement", level));
        }
    }
    
    /**
     * Retourne le niveau du vampire.
     * 
     * @return Le niveau (1, 2 ou 3)
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * Charge toutes les animations du vampire selon son niveau.
     * Les sprites sont organisés en grille : 4 lignes (directions) x N colonnes
     * Ligne 1 (y=0-63): DOWN, Ligne 2 (y=64-127): UP, Ligne 3 (y=128-191): LEFT, Ligne 4 (y=192-255): RIGHT
     * yRanges format: [DOWN_MIN, DOWN_MAX, SIDE_LEFT_MIN, SIDE_LEFT_MAX, SIDE_MIN, SIDE_MAX, UP_MIN, UP_MAX]
     */
    @Override
    protected void loadAnimations() {
        String vampirePrefix = "vampire_sprite_sheets/PNG/Vampires" + level;
        String jsonPrefix = "vampire_sprite_sheets/PNG/Vampires" + level;
        
        // yRanges pour les vampires : 
        // Ligne 1 (y=0-63): DOWN
        // Ligne 2 (y=64-127): UP
        // Ligne 3 (y=128-191): GAUCHE (SIDE_LEFT)
        // Ligne 4 (y=192-255): DROITE (SIDE)
        // Format: [DOWN_MIN, DOWN_MAX, SIDE_LEFT_MIN, SIDE_LEFT_MAX, SIDE_MIN, SIDE_MAX, UP_MIN, UP_MAX]
        int[] yRanges = new int[]{0, 63, 128, 191, 192, 255, 64, 127};
        
        // Idle
        AnimationLoader.loadAnimation(animationHandler,
            jsonPrefix + "/vampires" + level + "_idle_sprites.json",
            vampirePrefix + "/With_shadow/Vampires" + level + "_Idle_with_shadow.png",
            "idle", 0.15f, yRanges, true);
        
        // Walk
        AnimationLoader.loadAnimation(animationHandler,
            jsonPrefix + "/vampires" + level + "_walk_sprites.json",
            vampirePrefix + "/With_shadow/Vampires" + level + "_Walk_with_shadow.png",
            "walk", 0.12f, yRanges, true);
        
        // Run
        AnimationLoader.loadAnimation(animationHandler,
            jsonPrefix + "/vampires" + level + "_run_sprites.json",
            vampirePrefix + "/With_shadow/Vampires" + level + "_Run_with_shadow.png",
            "run", 0.10f, yRanges, true);
        
        // Attack
        AnimationLoader.loadAnimation(animationHandler,
            jsonPrefix + "/vampires" + level + "_attack_sprites.json",
            vampirePrefix + "/With_shadow/Vampires" + level + "_Attack_with_shadow.png",
            "attack", 0.08f, yRanges, false);
        
        // Hurt
        AnimationLoader.loadAnimation(animationHandler,
            jsonPrefix + "/vampires" + level + "_hurt_sprites.json",
            vampirePrefix + "/With_shadow/Vampires" + level + "_Hurt_with_shadow.png",
            "hurt", 0.1f, yRanges, false);
        
        // Death
        AnimationLoader.loadAnimation(animationHandler,
            jsonPrefix + "/vampires" + level + "_death_sprites.json",
            vampirePrefix + "/With_shadow/Vampires" + level + "_Death_with_shadow.png",
            "death", 0.15f, yRanges, false);
        
        // Log pour confirmer le chargement de l'animation de mort
        Gdx.app.log("Vampire", String.format("Vampire niveau %d: animation de mort chargée", level));
    }
    
    /**
     * Surcharge de calculateDirectionToTarget pour les vampires.
     * Les vampires utilisent le même mapping de directions que les slimes.
     */
    @Override
    protected Direction calculateDirectionToTarget(float dx, float dy) {
        // Pour les vampires : Ligne 1 = DOWN, Ligne 2 = UP, Ligne 3 = LEFT, Ligne 4 = RIGHT
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

