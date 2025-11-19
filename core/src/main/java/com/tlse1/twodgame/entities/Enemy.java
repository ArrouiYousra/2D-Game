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
    
    // Portée de détection du joueur (distance à laquelle l'ennemi commence à poursuivre)
    private float detectionRange = 300f;
    
    // Hitbox fixe pour les collisions et attaques (indépendante des sprites visuels)
    private float hitboxWidth = 16f;
    private float hitboxHeight = 16f;
    
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
     * Les sprites sont organisés en grille : 4 lignes (directions) x N colonnes
     * Ligne 1 (y=0-63): DOWN, Ligne 2 (y=64-127): UP, Ligne 3 (y=128-191): LEFT, Ligne 4 (y=192-255): RIGHT
     * yRanges format: [DOWN_MIN, DOWN_MAX, SIDE_LEFT_MIN, SIDE_LEFT_MAX, SIDE_MIN, SIDE_MAX, UP_MIN, UP_MAX]
     */
    @Override
    protected void loadAnimations() {
        // Idle: 4 sprites par direction
        // yRanges: [DOWN: 0-63, SIDE_LEFT: 128-191, SIDE: 192-255, UP: 64-127]
        AnimationLoader.loadAnimation(animationHandler,
            "vampire_sprite_sheets/vampires1_idle_sprites.json",
            "vampire_sprite_sheets/PNG/Vampires1/With_shadow/Vampires1_Idle_with_shadow.png",
            "idle", 0.15f, new int[]{0, 63, 128, 191, 192, 255, 64, 127}, true);
        
        // Walk: 6 sprites par direction
        AnimationLoader.loadAnimation(animationHandler,
            "vampire_sprite_sheets/vampires1_walk_sprites.json",
            "vampire_sprite_sheets/PNG/Vampires1/With_shadow/Vampires1_Walk_with_shadow.png",
            "walk", 0.12f, new int[]{0, 63, 128, 191, 192, 255, 64, 127}, true);
        
        // Run: 8 sprites par direction
        AnimationLoader.loadAnimation(animationHandler,
            "vampire_sprite_sheets/vampires1_run_sprites.json",
            "vampire_sprite_sheets/PNG/Vampires1/With_shadow/Vampires1_Run_with_shadow.png",
            "run", 0.10f, new int[]{0, 63, 128, 191, 192, 255, 64, 127}, true);
        
        // Attack: 12 sprites par direction
        AnimationLoader.loadAnimation(animationHandler,
            "vampire_sprite_sheets/vampires1_attack_sprites.json",
            "vampire_sprite_sheets/PNG/Vampires1/With_shadow/Vampires1_Attack_with_shadow.png",
            "attack", 0.08f, new int[]{0, 63, 128, 191, 192, 255, 64, 127}, false);
        
        // Walk Attack: Les vampires n'ont pas d'animation walk attack spécifique
        // (laissé vide, utilisera l'animation attack normale)
        
        // Run Attack: Les vampires n'ont pas d'animation run attack spécifique
        // (laissé vide, utilisera l'animation attack normale)
        
        // Hurt: 4 sprites par direction
        AnimationLoader.loadAnimation(animationHandler,
            "vampire_sprite_sheets/vampires1_hurt_sprites.json",
            "vampire_sprite_sheets/PNG/Vampires1/With_shadow/Vampires1_Hurt_with_shadow.png",
            "hurt", 0.1f, new int[]{0, 63, 128, 191, 192, 255, 64, 127}, false);
        
        // Death: À venir (l'utilisateur doit redécouper)
        // AnimationLoader.loadAnimation(animationHandler,
        //     "vampire_sprite_sheets/vampires1_death_sprites.json",
        //     "vampire_sprite_sheets/PNG/Vampires1/With_shadow/Vampires1_Death_with_shadow.png",
        //     "death", 0.15f, new int[]{0, 63, 128, 191, 192, 255, 64, 127}, false);
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
        if (target == null || !target.isAlive()) {
            // Pas de cible ou cible morte, rester en idle DOWN
            animationHandler.setCurrentDirection(Direction.DOWN);
            animationHandler.setMoving(false);
            animationHandler.setRunning(false);
            return;
        }
        
        // Mettre à jour le cooldown d'attaque
        if (attackCooldown > 0) {
            attackCooldown -= deltaTime;
        }
        
        // Vérifier que les dimensions sont initialisées
        if (getWidth() <= 0 || getHeight() <= 0 || target.getWidth() <= 0 || target.getHeight() <= 0) {
            // Dimensions pas encore initialisées, rester en idle DOWN
            animationHandler.setCurrentDirection(Direction.DOWN);
            animationHandler.setMoving(false);
            animationHandler.setRunning(false);
            return;
        }
        
        // Calculer la distance au joueur (centre à centre)
        // Le centre de la hitbox est aligné avec le centre du sprite visuel
        float enemySpriteCenterX = getX() + getWidth() / 2f;
        float enemySpriteCenterY = getY() + getHeight() / 2f;
        float enemyCenterX = enemySpriteCenterX; // Hitbox centrée sur le sprite
        float enemyCenterY = enemySpriteCenterY; // Hitbox centrée sur le sprite
        
        float targetCenterX = target.getX() + target.getWidth() / 2f;
        float targetCenterY = target.getY() + target.getHeight() / 2f;
        
        float dx = targetCenterX - enemyCenterX;
        float dy = targetCenterY - enemyCenterY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        // Si le joueur est hors de portée de détection, rester en idle DOWN
        if (distance > detectionRange) {
            animationHandler.setCurrentDirection(Direction.DOWN);
            animationHandler.setMoving(false);
            animationHandler.setRunning(false);
            return;
        }
        
        // Calculer la portée d'attaque basée sur les hitboxes
        // Utiliser la hitbox dynamique si c'est un Slime en train d'attaquer
        float currentHitboxWidth = hitboxWidth;
        float currentHitboxHeight = hitboxHeight;
        
        if (this instanceof Slime) {
            Slime slime = (Slime) this;
            float[] attackHitbox = slime.getCurrentAttackHitbox();
            if (attackHitbox != null) {
                // Utiliser la hitbox dynamique de l'animation d'attaque
                currentHitboxWidth = attackHitbox[0];
                currentHitboxHeight = attackHitbox[1];
            }
        }
        
        // Calculer la direction vers le joueur (peut être surchargée dans les sous-classes)
        Direction directionToTarget = calculateDirectionToTarget(dx, dy);
        
        // Vérifier la collision rectangle-rectangle entre les hitboxes
        // Position de la hitbox de l'ennemi (centrée sur le sprite)
        float enemyHitboxX = enemySpriteCenterX - currentHitboxWidth / 2f;
        float enemyHitboxY = enemySpriteCenterY - currentHitboxHeight / 2f;
        
        // Position de la hitbox du joueur (centrée sur le sprite)
        // Pour les collisions avec l'ennemi, on utilise toujours les dimensions normales du joueur
        // Les hitboxes d'attaque du joueur sont uniquement utilisées quand le joueur attaque
        float playerHitboxWidth = target.getWidth();
        float playerHitboxHeight = target.getHeight();
        
        float playerHitboxX = targetCenterX - playerHitboxWidth / 2f;
        float playerHitboxY = targetCenterY - playerHitboxHeight / 2f;
        
        // Vérifier si les rectangles se chevauchent (collision AABB)
        boolean hitboxesCollide = (enemyHitboxX < playerHitboxX + playerHitboxWidth &&
                                   enemyHitboxX + currentHitboxWidth > playerHitboxX &&
                                   enemyHitboxY < playerHitboxY + playerHitboxHeight &&
                                   enemyHitboxY + currentHitboxHeight > playerHitboxY);
        
        // Si les hitboxes se touchent et que le cooldown est prêt
        if (hitboxesCollide && attackCooldown <= 0) {
            // Se tourner vers le joueur avant d'attaquer
            setCurrentDirection(directionToTarget);
            attack();
            attackCooldown = attackCooldownTime;
            animationHandler.setMoving(false);
            animationHandler.setRunning(false);
            
            // Infliger des dégâts au joueur si c'est un Player
            if (target instanceof Player) {
                Player player = (Player) target;
                player.takeDamage(1); // 1 dégât par attaque
            }
        } else if (!hitboxesCollide) {
            // Les hitboxes ne se touchent pas, se déplacer vers le joueur en courant
            movementHandler.move(directionToTarget, deltaTime, true); // true = run
            // S'assurer que l'animation est bien en mode "running"
            animationHandler.setRunning(true);
        } else {
            // Les hitboxes se touchent mais en cooldown, rester immobile mais regarder vers le joueur
            setCurrentDirection(directionToTarget);
            animationHandler.setMoving(false);
            animationHandler.setRunning(false);
        }
    }
    
    /**
     * Calcule la direction vers la cible. Peut être surchargée dans les sous-classes
     * pour gérer les différences d'organisation des sprites.
     * 
     * @param dx Différence X (cible - ennemi)
     * @param dy Différence Y (cible - ennemi)
     * @return Direction vers la cible
     */
    protected Direction calculateDirectionToTarget(float dx, float dy) {
        // Par défaut, les sprites du vampire ont SIDE et SIDE_LEFT inversés
        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) {
                return Direction.SIDE_LEFT; // Joueur à droite -> utiliser SIDE_LEFT (inversé)
            } else {
                return Direction.SIDE; // Joueur à gauche -> utiliser SIDE (inversé)
            }
        } else {
            if (dy > 0) {
                return Direction.UP;
            } else {
                return Direction.DOWN;
            }
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
    
    public float getHitboxWidth() {
        return hitboxWidth;
    }
    
    public void setHitboxWidth(float hitboxWidth) {
        this.hitboxWidth = hitboxWidth;
    }
    
    public float getHitboxHeight() {
        return hitboxHeight;
    }
    
    public void setHitboxHeight(float hitboxHeight) {
        this.hitboxHeight = hitboxHeight;
    }
}
