package com.tlse1.twodgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tlse1.twodgame.utils.Direction;
import com.tlse1.twodgame.weapons.Weapon;

import java.util.Map;

/**
 * Classe représentant le joueur contrôlé par le clavier.
 * Gère l'input et le mouvement du personnage joueur.
 */
public class Player extends Character {
    
    // Arme du joueur
    private Weapon weapon;
    
    // Caméra pour calculer la direction de tir (optionnel, peut être null)
    private OrthographicCamera camera;
    
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
        
        // Utiliser les nouveaux assets avec épée
        weapon = new Weapon(Weapon.WeaponType.GUN); // On garde WeaponType pour l'instant, mais c'est une épée
        hasWeapon = true;
        
        // Configurer les chemins pour les animations avec épée
        // Les assets sont dans freebase4directionmalecharacter/PNG/Sword/Without_shadow/
        idlePathPrefix = "freebase4directionmalecharacter/PNG/Sword/Without_shadow/";
        runPathPrefix = "freebase4directionmalecharacter/PNG/Sword/Without_shadow/";
        shootPathPrefix = "freebase4directionmalecharacter/PNG/Sword/Without_shadow/";
        spriteName = "Sword";
        
        // Initialiser toutes les animations
        initializeSwordAnimations();
        
        // Direction par défaut
        currentDirection = Direction.DOWN;
    }
    
    /**
     * Initialise les animations avec épée depuis les sprite sheets 4 directions
     */
    private void initializeSwordAnimations() {
        // Les sprite sheets contiennent 4 directions (lignes) et plusieurs frames (colonnes)
        // On va charger chaque animation et extraire les bonnes frames par direction
        // Les dimensions seront détectées automatiquement ou ajustées selon les assets
        
        // Idle : Sword_Idle_without_shadow.png
        // Essayons avec détection automatique (largeur estimée ~64-128 pixels par frame)
        loadSwordAnimation("Sword_Idle_without_shadow.png", Direction.DOWN, 64, 0.15f, idleAnimations);
        loadSwordAnimation("Sword_Idle_without_shadow.png", Direction.SIDE_LEFT, 64, 0.15f, idleAnimations);
        loadSwordAnimation("Sword_Idle_without_shadow.png", Direction.SIDE, 64, 0.15f, idleAnimations);
        loadSwordAnimation("Sword_Idle_without_shadow.png", Direction.UP, 64, 0.15f, idleAnimations);
        
        // Walk : Sword_Walk_without_shadow.png (utilisé pour le mouvement)
        loadSwordAnimation("Sword_Walk_without_shadow.png", Direction.DOWN, 64, 0.12f, runAnimations);
        loadSwordAnimation("Sword_Walk_without_shadow.png", Direction.SIDE_LEFT, 64, 0.12f, runAnimations);
        loadSwordAnimation("Sword_Walk_without_shadow.png", Direction.SIDE, 64, 0.12f, runAnimations);
        loadSwordAnimation("Sword_Walk_without_shadow.png", Direction.UP, 64, 0.12f, runAnimations);
        
        // Run : Sword_Run_without_shadow.png (si disponible)
        // Pour l'instant, on utilise Walk pour Run aussi
        
        // Attack : Sword_attack_without_shadow.png (utilisé pour l'attaque/tir)
        loadSwordAnimation("Sword_attack_without_shadow.png", Direction.DOWN, 64, 0.1f, shootAnimations);
        loadSwordAnimation("Sword_attack_without_shadow.png", Direction.SIDE_LEFT, 64, 0.1f, shootAnimations);
        loadSwordAnimation("Sword_attack_without_shadow.png", Direction.SIDE, 64, 0.1f, shootAnimations);
        loadSwordAnimation("Sword_attack_without_shadow.png", Direction.UP, 64, 0.1f, shootAnimations);
        
        // Définir l'animation par défaut
        if (!idleAnimations.isEmpty()) {
            currentAnimation = idleAnimations.get(Direction.DOWN);
        }
    }
    
    /**
     * Charge une animation sword depuis une sprite sheet 4 directions avec détection automatique
     */
    private void loadSwordAnimation(String filename, Direction direction, int estimatedFrameWidth, float frameDuration, Map<Direction, Animation<TextureRegion>> targetMap) {
        String path = idlePathPrefix + filename;
        Animation<TextureRegion> animation = loadAnimationFrom4DirectionSheetAuto(path, direction, estimatedFrameWidth, frameDuration);
        if (animation != null && animation.getKeyFrames().length > 0) {
            targetMap.put(direction, animation);
        }
    }
    
    /**
     * Charge une animation sword avec un nombre de frames spécifique (si la détection auto ne fonctionne pas)
     */
    private void loadSwordAnimationFixed(String filename, Direction direction, int framesPerDirection, float frameDuration, Map<Direction, Animation<TextureRegion>> targetMap) {
        String path = idlePathPrefix + filename;
        Animation<TextureRegion> animation = loadAnimationFrom4DirectionSheet(path, direction, framesPerDirection, frameDuration);
        if (animation != null && animation.getKeyFrames().length > 0) {
            targetMap.put(direction, animation);
        }
    }
    
    /**
     * Met à jour le joueur : gère l'input et le mouvement.
     */
    @Override
    public void update(float deltaTime) {
        // Gérer l'input (définit aussi la direction si on bouge avec le clavier)
        boolean movedWithKeyboard = handleInput(deltaTime);
        
        // Mettre à jour la direction selon la position de la souris seulement si on ne bouge pas avec le clavier
        if (!movedWithKeyboard) {
            updateDirectionFromMouse();
        }
        
        // Appeler la méthode update de la classe mère pour gérer les animations
        super.update(deltaTime);
        
        // Limiter le joueur dans les bounds de l'écran
        clampToBounds(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    
    /**
     * Gère l'input clavier et met à jour la position et la direction du joueur.
     * @return true si le joueur a bougé avec le clavier
     */
    private boolean handleInput(float deltaTime) {
        isMoving = false;
        boolean movedWithKeyboard = false;
        
        // Gérer les touches directionnelles (flèches ou WASD)
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= speed * deltaTime;
            currentDirection = Direction.SIDE_LEFT; // Définir la direction à gauche
            isMoving = true;
            movedWithKeyboard = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += speed * deltaTime;
            currentDirection = Direction.SIDE; // Définir la direction à droite
            isMoving = true;
            movedWithKeyboard = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            y -= speed * deltaTime;
            currentDirection = Direction.DOWN; // Définir la direction vers le bas
            isMoving = true;
            movedWithKeyboard = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            y += speed * deltaTime;
            currentDirection = Direction.UP; // Définir la direction vers le haut
            isMoving = true;
            movedWithKeyboard = true;
        }
        
        // Gérer le tir (clic souris ou touche E)
        if (hasWeapon && !isShooting) {
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) || 
                Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                shoot();
            }
        }
        
        return movedWithKeyboard;
    }
    
    /**
     * Met à jour la direction du joueur selon la position de la souris
     * (utilisé seulement quand on ne bouge pas avec le clavier)
     */
    private void updateDirectionFromMouse() {
        // Position de la souris à l'écran
        float mouseScreenX = Gdx.input.getX();
        float mouseScreenY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Inverser Y
        
        // Position du joueur (centre) à l'écran
        float playerCenterX = x + width / 2;
        float playerCenterY = y + height / 2;
        
        // Calculer la direction
        float dx = mouseScreenX - playerCenterX;
        float dy = mouseScreenY - playerCenterY;
        
        // Déterminer la direction principale
        if (Math.abs(dy) > Math.abs(dx)) {
            // Vertical
            if (dy > 0) {
                currentDirection = Direction.UP;
            } else {
                currentDirection = Direction.DOWN;
            }
        } else {
            // Horizontal
            if (dx > 0) {
                currentDirection = Direction.SIDE;
            } else {
                currentDirection = Direction.SIDE_LEFT;
            }
        }
    }
    
    /**
     * Fait tirer le joueur
     */
    public void shoot() {
        if (hasWeapon && !isShooting) {
            isShooting = true;
            shootStateTime = 0f;
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
    
    /**
     * Retourne l'arme du joueur
     */
    public Weapon getWeapon() {
        return weapon;
    }
    
    /**
     * Définit l'arme du joueur
     */
    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
        // TODO: Recharger les animations si nécessaire
    }
    
    /**
     * Définit la caméra pour le calcul de la direction de tir
     */
    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }
}
