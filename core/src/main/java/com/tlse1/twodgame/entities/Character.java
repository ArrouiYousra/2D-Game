package com.tlse1.twodgame.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tlse1.twodgame.entities.handlers.AnimationHandler;
import com.tlse1.twodgame.entities.handlers.CombatHandler;
import com.tlse1.twodgame.entities.handlers.MovementHandler;
import com.tlse1.twodgame.utils.Direction;

/**
 * Classe de base abstraite pour tous les personnages du jeu.
 * Utilise un système de handlers pour séparer les responsabilités.
 * Les classes filles (Player, Enemy) doivent charger leurs animations spécifiques.
 */
public abstract class Character {
    
    // Handlers
    protected AnimationHandler animationHandler;
    protected CombatHandler combatHandler;
    protected MovementHandler movementHandler;
    
    // Dimensions (mises à jour par le rendu)
    private float width;
    private float height;
    
    // Hitbox fixe pour les collisions et les dégâts (indépendante des sprites visuels)
    // Par défaut, utilise les dimensions visuelles, mais peut être surchargée dans les sous-classes
    protected float hitboxWidth = 0f; // 0 = utilise width
    protected float hitboxHeight = 0f; // 0 = utilise height
    
    /**
     * Constructeur par défaut.
     * Initialise le personnage à la position (0, 0).
     */
    public Character() {
        this(0, 0);
    }
    
    /**
     * Constructeur avec position.
     * 
     * @param x Position X initiale
     * @param y Position Y initiale
     */
    public Character(float x, float y) {
        // Initialiser les handlers
        this.animationHandler = new AnimationHandler();
        this.combatHandler = new CombatHandler(100, animationHandler); // 100 HP par défaut
        this.movementHandler = new MovementHandler(x, y, 150f, animationHandler); // 150 pixels/seconde par défaut
        
        // Les classes filles doivent charger leurs animations dans leur constructeur
    }
    
    /**
     * Charge toutes les animations du personnage.
     * Doit être implémentée par les classes filles.
     */
    protected abstract void loadAnimations();
    
    /**
     * Met à jour le personnage.
     * 
     * @param deltaTime Temps écoulé depuis la dernière frame
     */
    public void update(float deltaTime) {
        animationHandler.update(deltaTime);
    }
    
    /**
     * Dessine le personnage.
     * 
     * @param batch SpriteBatch pour le rendu
     */
    public void render(SpriteBatch batch) {
        float[] dimensions = animationHandler.render(batch, movementHandler.getX(), movementHandler.getY());
        width = dimensions[0];
        height = dimensions[1];
    }
    
    /**
     * Libère les ressources.
     */
    public void dispose() {
        if (animationHandler != null) {
            animationHandler.dispose();
        }
    }
    
    // Déléguer les méthodes aux handlers
    
    // Position
    public float getX() {
        return movementHandler.getX();
    }
    
    public void setX(float x) {
        movementHandler.setX(x);
    }
    
    public float getY() {
        return movementHandler.getY();
    }
    
    public void setY(float y) {
        movementHandler.setY(y);
    }
    
    // Dimensions
    public float getWidth() {
        return width;
    }
    
    public float getHeight() {
        return height;
    }
    
    // Hitbox (retourne la hitbox fixe si définie, sinon les dimensions visuelles)
    public float getHitboxWidth() {
        return hitboxWidth > 0 ? hitboxWidth : width;
    }
    
    public float getHitboxHeight() {
        return hitboxHeight > 0 ? hitboxHeight : height;
    }
    
    public void setHitboxWidth(float hitboxWidth) {
        this.hitboxWidth = hitboxWidth;
    }
    
    public void setHitboxHeight(float hitboxHeight) {
        this.hitboxHeight = hitboxHeight;
    }
    
    /**
     * Obtient la position X de la hitbox (centrée sur le sprite visuel).
     * 
     * @return Position X du coin bas-gauche de la hitbox
     */
    public float getHitboxX() {
        float spriteCenterX = getX() + width / 2f;
        return spriteCenterX - getHitboxWidth() / 2f;
    }
    
    /**
     * Obtient la position Y de la hitbox (centrée sur le sprite visuel).
     * 
     * @return Position Y du coin bas-gauche de la hitbox
     */
    public float getHitboxY() {
        float spriteCenterY = getY() + height / 2f;
        return spriteCenterY - getHitboxHeight() / 2f;
    }
    
    // Direction
    public Direction getCurrentDirection() {
        return animationHandler.getCurrentDirection();
    }
    
    public void setCurrentDirection(Direction direction) {
        animationHandler.setCurrentDirection(direction);
    }
    
    // Mouvement
    public boolean isMoving() {
        return animationHandler.isMoving();
    }
    
    public void setMoving(boolean moving) {
        animationHandler.setMoving(moving);
    }
    
    public boolean isRunning() {
        return animationHandler.isRunning();
    }
    
    public void setRunning(boolean running) {
        animationHandler.setRunning(running);
    }
    
    // Combat
    public void attack() {
        animationHandler.attack();
    }
    
    public boolean isAttacking() {
        return animationHandler.isAttacking();
    }
    
    public void takeDamage(int damage) {
        combatHandler.takeDamage(damage);
    }
    
    public int getHealth() {
        return combatHandler.getHealth();
    }
    
    public int getMaxHealth() {
        return combatHandler.getMaxHealth();
    }
    
    public boolean isAlive() {
        return combatHandler.isAlive();
    }
    
    public boolean isHurt() {
        return animationHandler.isHurt();
    }
    
    public boolean isDead() {
        return animationHandler.isDead();
    }
    
    public int getShield() {
        return combatHandler.getShield();
    }
    
    public int getMaxShield() {
        return combatHandler.getMaxShield();
    }
    
    // Handlers (pour accès direct si nécessaire)
    public AnimationHandler getAnimationHandler() {
        return animationHandler;
    }
    
    public CombatHandler getCombatHandler() {
        return combatHandler;
    }
    
    public MovementHandler getMovementHandler() {
        return movementHandler;
    }
}
