package com.tlse1.twodgame.entities;

import com.badlogic.gdx.Gdx;
import com.tlse1.twodgame.entities.handlers.AnimationLoader;
import com.tlse1.twodgame.managers.JsonMapLoader;
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
    
    // Référence à la map pour créer des projectiles (peut être null)
    protected JsonMapLoader mapLoader;
    
    
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
        
        // Ne pas charger les animations ici - les sous-classes (Vampire, Slime) le feront
        // après avoir initialisé leurs propriétés spécifiques (level, etc.)
        // loadAnimations();
        
        // Définir l'animation par défaut (sera fait après le chargement des animations dans les sous-classes)
        // animationHandler.update(0f);
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
        // Si l'ennemi est mort, ne pas mettre à jour l'IA
        if (isDead()) {
            return;
        }
        
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
        // Si les dimensions ne sont pas encore initialisées, attendre
        if (getWidth() <= 0 || getHeight() <= 0) {
            // L'ennemi n'a pas encore de dimensions, attendre
            // Log pour déboguer
            if (this instanceof Vampire) {
                Vampire v = (Vampire) this;
                Gdx.app.log("Enemy", String.format("Vampire niveau %d: dimensions pas encore initialisées (%.1fx%.1f)", 
                    v.getLevel(), getWidth(), getHeight()));
            }
            animationHandler.setCurrentDirection(Direction.DOWN);
            animationHandler.setMoving(false);
            animationHandler.setRunning(false);
            return;
        }
        
        // Si le joueur n'a pas encore de dimensions, attendre aussi
        if (target.getWidth() <= 0 || target.getHeight() <= 0) {
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
        
        // Utiliser la hitbox fixe pour tous les ennemis (slimes et vampires)
        // Les slimes ont une hitbox fixe de 17x16, les vampires de 30x30
        // Utiliser les getters pour s'assurer que les hitboxes sont correctement initialisées
        float currentHitboxWidth = getHitboxWidth();
        float currentHitboxHeight = getHitboxHeight();
        
        // Calculer la direction vers le joueur (peut être surchargée dans les sous-classes)
        Direction directionToTarget = calculateDirectionToTarget(dx, dy);
        
        // Vérifier la collision rectangle-rectangle entre les hitboxes
        // Position de la hitbox de l'ennemi (centrée sur le sprite)
        float enemyHitboxX = enemySpriteCenterX - currentHitboxWidth / 2f;
        float enemyHitboxY = enemySpriteCenterY - currentHitboxHeight / 2f;
        
        // Position de la hitbox du joueur (centrée sur le sprite)
        // Utiliser la hitbox fixe du joueur (20x27) au lieu des dimensions visuelles
        float playerHitboxWidth = target.getHitboxWidth();
        float playerHitboxHeight = target.getHitboxHeight();
        
        float playerHitboxX = targetCenterX - playerHitboxWidth / 2f;
        float playerHitboxY = targetCenterY - playerHitboxHeight / 2f;
        
        // Vérifier si les rectangles se touchent (avec une tolérance de 1 pixel pour permettre un chevauchement)
        // Cela permet aux hitboxes de se chevaucher de 1 pixel dans toutes les directions pour que les ennemis puissent attaquer
        float touchTolerance = 1.0f; // Tolérance de 1 pixel pour permettre un chevauchement de 1 pixel dans toutes les directions
        boolean hitboxesTouching = (enemyHitboxX < playerHitboxX + playerHitboxWidth + touchTolerance &&
                                   enemyHitboxX + currentHitboxWidth + touchTolerance > playerHitboxX &&
                                   enemyHitboxY < playerHitboxY + playerHitboxHeight + touchTolerance &&
                                   enemyHitboxY + currentHitboxHeight + touchTolerance > playerHitboxY);
        
        // Pour la détection d'attaque, utiliser la même condition que pour le blocage de mouvement
        // Les ennemis attaquent dès que leurs hitboxes touchent celle du joueur (avec la tolérance)
        boolean hitboxesCollide = hitboxesTouching;
        
        // Tous les ennemis (vampires et slimes) attaquent au corps à corps
        // Ils attaquent dès que leur hitbox touche celle du joueur
        if (hitboxesCollide && attackCooldown <= 0) {
            // Se tourner vers le joueur avant d'attaquer
            setCurrentDirection(directionToTarget);
            attack();
            attackCooldown = attackCooldownTime;
            animationHandler.setMoving(false);
            animationHandler.setRunning(false);
            
            // Infliger des dégâts au joueur
            if (target instanceof Player) {
                Player player = (Player) target;
                player.takeDamage(1); // 1 dégât par attaque
            }
        } else if (hitboxesTouching) {
            // Les hitboxes se touchent vraiment mais en cooldown : s'arrêter et regarder vers le joueur
            setCurrentDirection(directionToTarget);
            animationHandler.setMoving(false);
            animationHandler.setRunning(false);
        } else {
            // Les hitboxes ne se touchent pas : vérifier si on peut se déplacer sans causer de collision
            // Calculer la nouvelle position de l'ennemi
            float currentSpeed = getSpeed() * 1.5f; // Vitesse de course
            float moveDistance = currentSpeed * deltaTime;
            
            float currentX = getX();
            float currentY = getY();
            float newX = currentX;
            float newY = currentY;
            
            switch (directionToTarget) {
                case UP:
                    newY += moveDistance;
                    break;
                case DOWN:
                    newY -= moveDistance;
                    break;
                case SIDE:
                    newX += moveDistance;
                    break;
                case SIDE_LEFT:
                    newX -= moveDistance;
                    break;
            }
            
            // Calculer la hitbox de l'ennemi à sa nouvelle position
            float newEnemySpriteCenterX = newX + getWidth() / 2f;
            float newEnemySpriteCenterY = newY + getHeight() / 2f;
            float newEnemyHitboxX = newEnemySpriteCenterX - currentHitboxWidth / 2f;
            float newEnemyHitboxY = newEnemySpriteCenterY - currentHitboxHeight / 2f;
            
            // Vérifier si le mouvement causerait une collision avec le joueur (sans tolérance, collision stricte)
            boolean wouldCollide = (newEnemyHitboxX < playerHitboxX + playerHitboxWidth &&
                                   newEnemyHitboxX + currentHitboxWidth > playerHitboxX &&
                                   newEnemyHitboxY < playerHitboxY + playerHitboxHeight &&
                                   newEnemyHitboxY + currentHitboxHeight > playerHitboxY);
            
            if (wouldCollide) {
                // Le mouvement causerait une collision : s'arrêter et regarder vers le joueur
                setCurrentDirection(directionToTarget);
                animationHandler.setMoving(false);
                animationHandler.setRunning(false);
            } else {
                // Pas de collision : se déplacer vers le joueur en courant
                movementHandler.move(directionToTarget, deltaTime, true); // true = run
                animationHandler.setRunning(true);
            }
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
    
    public float getDetectionRange() {
        return detectionRange;
    }
    
    public void setDetectionRange(float detectionRange) {
        this.detectionRange = detectionRange;
    }
    
    public float getAttackCooldownTime() {
        return attackCooldownTime;
    }
    
    public void setAttackCooldownTime(float attackCooldownTime) {
        this.attackCooldownTime = attackCooldownTime;
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
    
    /**
     * Définit la référence à la map pour créer des projectiles.
     * 
     * @param mapLoader Référence à la map
     */
    public void setMapLoader(JsonMapLoader mapLoader) {
        this.mapLoader = mapLoader;
    }
    
    /**
     * Crée un projectile lors de l'attaque. Par défaut, retourne null.
     * Peut être surchargée dans les sous-classes (ex: Vampire) pour créer des projectiles.
     * 
     * @return Le projectile créé, ou null si cet ennemi n'utilise pas de projectiles
     */
    public Projectile createProjectileOnAttack() {
        // Par défaut, pas de projectile (pour les slimes par exemple)
        return null;
    }
}
