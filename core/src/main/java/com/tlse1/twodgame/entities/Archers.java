package com.tlse1.twodgame.entities;

import com.badlogic.gdx.Gdx;
import com.tlse1.twodgame.utils.Direction;

/**
 * Classe représentant un ennemi.
 * Implémente une IA simple qui suit le joueur.
 */
public class Archers extends Character {
    
    // Référence au joueur à suivre
    private Player target;
    
    // Distance de détection (si le joueur est trop loin, l'ennemi ne bouge pas)
    private float detectionRange = 120f;
    private float securityRange = 60f;
    
    /**
     * Constructeur par défaut
     */
    public Archers() {
        super();
        initializeArchers();
    }
    
    /**
     * Constructeur avec position initiale
     */
    public Archers(float x, float y) {
        super(x, y);
        initializeArchers();
    }
    
    /**
     * Constructeur complet
     */
    public Archers(float x, float y, float speed, int maxHealth) {
        super(x, y, speed, maxHealth);
        initializeArchers();
    }
    
    /**
     * Constructeur avec cible (joueur)
     */
    public Archers(float x, float y, float speed, int maxHealth, Player target) {
        super(x, y, speed, maxHealth);
        this.target = target;
        initializeArchers();
    }
    
    /**
     * Initialise l'ennemi (appelé dans les constructeurs)
     */
    private void initializeArchers() {
        // Vitesse par défaut de l'ennemi
        if (speed == 0) {
            speed = 80f; // pixels par seconde (plus lent que le joueur)
        }
        
        // Initialiser toutes les animations
        initializeAnimations();
        
        // Direction par défaut
        currentDirection = Direction.DOWN;
    }
    
    /**
     * Met à jour l'ennemi : gère l'IA et le mouvement.
     * L'ennemi suit le joueur s'il est à portée.
     */
    @Override
    public void update(float deltaTime) {
        if (target != null && target.isAlive()) {
            // Calculer la distance au joueur
            float dx = target.getX() - x;
            float dy = target.getY() - y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            
            // Si le joueur est à portée, le suivre
            if (distance < securityRange && distance > 10f) { // 5f = distance minimale pour éviter les tremblements
                isMoving = true;
                
                // Normaliser la direction pour un mouvement uniforme
                dx /= distance;
                dy /= distance;
                
                // Déplacer l'ennemi vers le joueur
                x -= (dx * speed * deltaTime);
                y -= (dy * speed * deltaTime);

                // Le joueur est très proche, rester immobile
                isMoving = false;
            } if (distance <= detectionRange && distance > 5f) { // 5f = distance minimale pour éviter les tremblements
                isMoving = true;
                
                // Normaliser la direction pour un mouvement uniforme
                dx /= distance;
                dy /= distance;
                
                // Déplacer l'ennemi vers le joueur
                x += dx * speed * deltaTime;
                y += dy * speed * deltaTime;
                
                // Déterminer la direction selon le mouvement
                updateDirection(dx, dy);
            } else {
                // Le joueur est trop loin ou trop proche, rester immobile
                isMoving = false;
            }
        } else {
            // Pas de cible, rester immobile
            isMoving = false;
        }
        
        // Appeler la méthode update de la classe mère pour gérer les animations
        super.update(deltaTime);
        
        // Limiter l'ennemi dans les bounds de l'écran
        clampToBounds(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    
    /**
     * Met à jour la direction de l'ennemi selon le vecteur de mouvement
     */
    private void updateDirection(float dx, float dy) {
        // Déterminer la direction principale selon le plus grand déplacement
        if (Math.abs(dx) > Math.abs(dy)) {
            // Mouvement horizontal
            if (dx > 0) {
                currentDirection = Direction.SIDE; // Droite
            } else {
                currentDirection = Direction.SIDE_LEFT; // Gauche
            }
        } else {
            // Mouvement vertical
            if (dy > 0) {
                currentDirection = Direction.UP; // Haut
            } else {
                currentDirection = Direction.DOWN; // Bas
            }
        }
    }
    
    // Getters et Setters
    public Player getTarget() {
        return target;
    }
    
    public void setTarget(Player target) {
        this.target = target;
    }
    
    public float getDetectionRange() {
        return detectionRange;
    }
    
    public void setDetectionRange(float detectionRange) {
        this.detectionRange = detectionRange;
    }
}
