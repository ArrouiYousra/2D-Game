package com.tlse1.twodgame.entities;

import com.badlogic.gdx.Gdx;
import com.tlse1.twodgame.entities.handlers.AnimationLoader;
import com.tlse1.twodgame.utils.Direction;

/**
 * Classe représentant un ennemi dans le jeu.
 * Hérite de Character et charge les animations du vampire.
 */
public class Enemy extends Character {
    
    // Vitesse de l'ennemi
    private float speed;
    
    // IA : cible (joueur)
    private Character target;
    
    // Distance d'attaque
    private float attackRange = 80f;
    
    // Cooldown entre les attaques
    private float attackCooldown = 0f;
    private float attackCooldownTime = 2.0f;
    
    /**
     * Constructeur par défaut.
     */
    public Enemy() {
        this(0, 0);
    }
    
    /**
     * Constructeur avec position.
     * 
     * @param x Position X initiale
     * @param y Position Y initiale
     */
    public Enemy(float x, float y) {
        super(x, y);
        this.speed = 100f;
        this.target = null;
        this.attackCooldown = 0f;
        
        // Configurer la santé de l'ennemi (100 HP)
        combatHandler.setMaxHealth(100);
        combatHandler.setHealth(100);
        
        // Configurer la vitesse
        movementHandler.setSpeed(speed);
        
        // Charger toutes les animations
        loadAnimations();
        
        // Définir l'animation par défaut
        animationHandler.update(0f);
    }
    
    /**
     * Charge toutes les animations du vampire depuis les fichiers JSON.
     */
    @Override
    protected void loadAnimations() {
        // Idle: y=17-19 (DOWN), y=81-83 (SIDE_LEFT), y=145-147 (SIDE), y=209-211 (UP)
        AnimationLoader.loadAnimation(animationHandler,
            "vampire_sprite_sheets/vampires1_idle_sprites.json",
            "vampire_sprite_sheets/PNG/Vampires1/With_shadow/Vampires1_Idle_with_shadow.png",
            "idle", 0.15f, new int[]{17, 19, 81, 83, 145, 147, 209, 211}, true);
        
        // Walk: y=17-19 (DOWN), y=81-83 (SIDE_LEFT), y=145-147 (SIDE), y=209-211 (UP)
        AnimationLoader.loadAnimation(animationHandler,
            "vampire_sprite_sheets/vampires1_walk_sprites.json",
            "vampire_sprite_sheets/PNG/Vampires1/With_shadow/Vampires1_Walk_with_shadow.png",
            "walk", 0.12f, new int[]{17, 19, 81, 83, 145, 147, 209, 211}, true);
        
        // Run: y=7-15 (DOWN), y=71-79 (SIDE_LEFT), y=135-143 (SIDE), y=199-207 (UP)
        AnimationLoader.loadAnimation(animationHandler,
            "vampire_sprite_sheets/vampires1_run_sprites.json",
            "vampire_sprite_sheets/PNG/Vampires1/With_shadow/Vampires1_Run_with_shadow.png",
            "run", 0.10f, new int[]{7, 15, 71, 79, 135, 143, 199, 207}, true);
        
        // Attack: y=5-24 (DOWN), y=69-83 (SIDE_LEFT), y=133-147 (SIDE), y=197-213 (UP)
        AnimationLoader.loadAnimation(animationHandler,
            "vampire_sprite_sheets/vampires1_attack_sprites.json",
            "vampire_sprite_sheets/PNG/Vampires1/With_shadow/Vampires1_Attack_with_shadow.png",
            "attack", 0.08f, new int[]{5, 24, 69, 83, 133, 147, 197, 213}, false);
        
        // Walk Attack: Les vampires n'ont pas d'animation walk attack spécifique
        // (laissé vide, utilisera l'animation attack normale)
        
        // Run Attack: Les vampires n'ont pas d'animation run attack spécifique
        // (laissé vide, utilisera l'animation attack normale)
        
        // Hurt: y=18-21 (DOWN), y=82-85 (SIDE_LEFT), y=146-149 (SIDE), y=210-213 (UP)
        AnimationLoader.loadAnimation(animationHandler,
            "vampire_sprite_sheets/vampires1_hurt_sprites.json",
            "vampire_sprite_sheets/PNG/Vampires1/With_shadow/Vampires1_Hurt_with_shadow.png",
            "hurt", 0.1f, new int[]{18, 21, 82, 85, 146, 149, 210, 213}, false);
        
        // Death: y=11-21 (DOWN), y=75-85 (SIDE_LEFT), y=139-149 (SIDE), y=203-213 (UP)
        AnimationLoader.loadAnimation(animationHandler,
            "vampire_sprite_sheets/vampires1_death_sprites.json",
            "vampire_sprite_sheets/PNG/Vampires1/With_shadow/Vampires1_Death_with_shadow.png",
            "death", 0.15f, new int[]{11, 21, 75, 85, 139, 149, 203, 213}, false);
    }
    
    /**
     * Définit la cible (joueur) pour l'IA.
     * 
     * @param target Le personnage à cibler
     */
    public void setTarget(Character target) {
        this.target = target;
    }
    
    /**
     * Met à jour l'IA de l'ennemi.
     * 
     * @param deltaTime Temps écoulé depuis la dernière frame
     */
    public void updateAI(float deltaTime) {
        if (target == null) {
            return;
        }
        
        // Mettre à jour le cooldown d'attaque
        if (attackCooldown > 0) {
            attackCooldown -= deltaTime;
        }
        
        // Calculer la distance au joueur
        float dx = target.getX() - getX();
        float dy = target.getY() - getY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        // Calculer la direction vers le joueur
        // ATTENTION: Les sprites du vampire ont SIDE et SIDE_LEFT inversés
        Direction directionToTarget;
        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) {
                directionToTarget = Direction.SIDE_LEFT; // Joueur à droite -> utiliser SIDE_LEFT (inversé)
            } else {
                directionToTarget = Direction.SIDE; // Joueur à gauche -> utiliser SIDE (inversé)
            }
        } else {
            if (dy > 0) {
                directionToTarget = Direction.UP;
            } else {
                directionToTarget = Direction.DOWN;
            }
        }
        
        // Si l'ennemi est dans la portée d'attaque
        if (distance <= attackRange && attackCooldown <= 0) {
            // Se tourner vers le joueur avant d'attaquer
            setCurrentDirection(directionToTarget);
            attack();
            attackCooldown = attackCooldownTime;
            animationHandler.setMoving(false);
            
            // Infliger des dégâts au joueur si c'est un Player
            if (target instanceof Player) {
                Player player = (Player) target;
                player.takeDamage(1); // 1 dégât par attaque
            }
        } else if (distance > attackRange) {
            // Se déplacer vers le joueur
            movementHandler.move(directionToTarget, deltaTime, false);
        } else {
            // Trop proche mais en cooldown, rester immobile
            animationHandler.setMoving(false);
        }
    }
    
    // Getters et Setters
    public float getSpeed() {
        return speed;
    }
    
    public void setSpeed(float speed) {
        this.speed = speed;
        movementHandler.setSpeed(speed);
    }
    
    public float getAttackRange() {
        return attackRange;
    }
    
    public void setAttackRange(float attackRange) {
        this.attackRange = attackRange;
    }
}
