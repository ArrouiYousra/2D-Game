package com.tlse1.twodgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tlse1.twodgame.utils.Direction;

/**
 * Classe représentant un ennemi.
 * Implémente une IA simple qui suit le joueur.
 */
public class Enemy extends Character {
    
    // Référence au joueur à suivre
    private Player target;
    
    // Distance de détection (si le joueur est trop loin, l'ennemi ne bouge pas)
    private float detectionRange = 300f;
    
    /**
     * Constructeur par défaut
     */
    public Enemy() {
        super();
        initializeEnemy();
    }
    
    /**
     * Constructeur avec position initiale
     */
    public Enemy(float x, float y) {
        super(x, y);
        initializeEnemy();
    }
    
    /**
     * Constructeur complet
     */
    public Enemy(float x, float y, float speed, int maxHealth) {
        super(x, y, speed, maxHealth);
        initializeEnemy();
    }
    
    /**
     * Constructeur avec cible (joueur)
     */
    public Enemy(float x, float y, float speed, int maxHealth, Player target) {
        super(x, y, speed, maxHealth);
        this.target = target;
        initializeEnemy();
    }
    
    /**
     * Initialise l'ennemi (appelé dans les constructeurs)
     */
    private void initializeEnemy() {
        // Vitesse par défaut de l'ennemi
        if (speed == 0) {
            speed = 80f; // pixels par seconde (plus lent que le joueur)
        }
        
        // Configurer les chemins d'assets pour les zombies
        idlePathPrefix = "PostApocalypse_AssetPack_v1.1.2/Enemies/Zombie_Small/";
        runPathPrefix = "PostApocalypse_AssetPack_v1.1.2/Enemies/Zombie_Small/";
        spriteName = "Zombie_Small";
        
        // Initialiser toutes les animations
        initializeAnimations();
        
        // Direction par défaut
        currentDirection = Direction.DOWN;
    }
    
    /**
     * Charge une animation idle pour une direction donnée (surcharge pour utiliser les assets de zombie)
     */
    @Override
    protected void loadIdleAnimation(Direction direction) {
        String path = buildEnemyAnimationPath(idlePathPrefix, direction, "Idle");
        Animation<TextureRegion> animation = loadAnimation(path);
        
        // Stocker la texture pour pouvoir la libérer
        TextureRegion firstFrame = animation.getKeyFrame(0);
        if (firstFrame != null && firstFrame.getTexture() != null) {
            textures.add(firstFrame.getTexture());
        }
        
        addIdleAnimation(direction, animation);
    }
    
    /**
     * Charge une animation run/walk pour une direction donnée (surcharge pour utiliser les assets de zombie)
     */
    @Override
    protected void loadRunAnimation(Direction direction) {
        // Les zombies utilisent "walk" au lieu de "run"
        // Note: "Down" utilise "walk" (minuscule), les autres utilisent "Walk" (majuscule)
        String action = (direction == Direction.DOWN) ? "walk" : "Walk";
        String path = buildEnemyAnimationPath(runPathPrefix, direction, action);
        Animation<TextureRegion> animation = loadAnimation(path);
        
        // Stocker la texture pour pouvoir la libérer
        TextureRegion firstFrame = animation.getKeyFrame(0);
        if (firstFrame != null && firstFrame.getTexture() != null) {
            textures.add(firstFrame.getTexture());
        }
        
        addRunAnimation(direction, animation);
    }
    
    /**
     * Construit le chemin vers l'animation d'ennemi selon la direction
     */
    private String buildEnemyAnimationPath(String prefix, Direction direction, String action) {
        String directionName = getEnemyDirectionName(direction);
        // Format: Zombie_Small_Down_Idle-Sheet6.png ou Zombie_Small_Down_walk-Sheet6.png
        return prefix + spriteName + "_" + directionName + "_" + action + "-Sheet6.png";
    }
    
    /**
     * Retourne le nom de direction pour les fichiers d'assets d'ennemi
     */
    private String getEnemyDirectionName(Direction direction) {
        switch (direction) {
            case DOWN:
                return "Down";
            case UP:
                return "Up";
            case SIDE:
                return "Side";
            case SIDE_LEFT:
                return "Side-left";
            default:
                return "Down";
        }
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
            if (distance <= detectionRange && distance > 5f) { // 5f = distance minimale pour éviter les tremblements
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
