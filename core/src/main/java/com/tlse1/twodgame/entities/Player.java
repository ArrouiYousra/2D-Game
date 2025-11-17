package com.tlse1.twodgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.tlse1.twodgame.Attacks.Attack;
import com.tlse1.twodgame.utils.Direction;
import com.tlse1.twodgame.Attacks.MeleeAttack;
import com.tlse1.twodgame.Attacks.RangedAttack;
import com.tlse1.twodgame.Attacks.MagicAttack;

/**
 * Classe représentant le joueur contrôlé par le clavier.
 * Gère l'input et le mouvement du personnage joueur.
 */
public class Player extends Character{
    
    /**
     * Constructeur par défaut
     */
    public Player() {
        super();
        initializePlayer();
    }
    
    /**
     * Constructeur avec position initiale
     */
    public Player(float x, float y) {
        super(x, y);
        initializePlayer();
    }
    
    /**
     * Constructeur complet
     */
    public Player(float x, float y, float speed, int maxHealth) {
        super(x, y, speed, maxHealth);
        initializePlayer();
    }
    
    /**
     * Initialise le joueur (appelé dans les constructeurs)
     */
    private void initializePlayer() {
        // Vitesse par défaut du joueur
        if (speed == 0) {
            speed = 150f; // pixels par seconde
        }
        
        // Initialiser toutes les animations
        initializeAnimations();
        
        // Direction par défaut
        currentDirection = Direction.DOWN;
    }
    
    /**
     * Met à jour le joueur : gère l'input et le mouvement.
     */
    @Override
    public void update(float deltaTime) {
        // Gérer l'input
        handleInput(deltaTime);
        
        // Appeler la méthode update de la classe mère pour gérer les animations
        super.update(deltaTime);
        
        // Limiter le joueur dans les bounds de l'écran
        clampToBounds(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    
    /**
     * Gère l'input clavier et met à jour la position et la direction du joueur.
     */
    private void handleInput(float deltaTime) {
        isMoving = false;
        
        // Gérer les touches directionnelles (flèches ou WASD)
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= speed * deltaTime;
            currentDirection = Direction.SIDE_LEFT;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += speed * deltaTime;
            currentDirection = Direction.SIDE;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            y -= speed * deltaTime;
            currentDirection = Direction.DOWN;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            y += speed * deltaTime;
            currentDirection = Direction.UP;
            isMoving = true;
        }
        /* if (Gdx.input.isKeyPressed(Input.Keys.Q)) {

            Attack melee = new MeleeAttack(50, 2, 1.0f);
            Attack ranged = new RangedAttack(40, 20, 1.5f);
            Attack magic = new MagicAttack(60, 15, 2.0f);
            // Attaque (à implémenter)
            System.out.println("\n=== Test d'exécution ===");
            melee.execute("Guerrier", "Ennemi");
            ranged.execute("Archer", "Ennemi");
            magic.execute("Mage", "Ennemi");
        } */
    }

    @Override
    public void takeDamage(int damage) {
        health = Math.max(0, health - damage);
        if (health <= 0) {
            onDeath();
        }
    }
    
    /**
     * Retourne la vitesse actuelle du joueur
     */
    public float getSpeed() {
        return speed;
    }
    
    /**
     * Définit la vitesse du joueur
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
