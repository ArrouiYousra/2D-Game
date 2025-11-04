package com.tlse1.twodgame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tlse1.twodgame.utils.Direction;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstraite pour les personnages du jeu.
 * Gère les animations Idle et Run pour toutes les directions,
 * ainsi que la santé du personnage.
 */
public abstract class Character extends AnimatedEntity {
    
    // Santé du personnage
    protected int health;
    protected int maxHealth;
    
    // Liste des textures chargées pour pouvoir les libérer
    protected List<Texture> textures;
    
    // Préfixe pour les chemins des assets (ex: "character_sprites/Idle/")
    protected String idlePathPrefix;
    protected String runPathPrefix;
    
    // Nom de base pour les fichiers (ex: "Character_down_idle_no-hands-Sheet6.png")
    protected String spriteName;
    
    /**
     * Constructeur par défaut
     */
    public Character() {
        super();
        this.health = 100;
        this.maxHealth = 100;
        this.textures = new ArrayList<>();
        this.idlePathPrefix = "character_sprites/Idle/";
        this.runPathPrefix = "character_sprites/Run/";
        this.spriteName = "Character";
    }
    
    /**
     * Constructeur avec position
     */
    public Character(float x, float y) {
        super(x, y);
        this.health = 100;
        this.maxHealth = 100;
        this.textures = new ArrayList<>();
        this.idlePathPrefix = "character_sprites/Idle/";
        this.runPathPrefix = "character_sprites/Run/";
        this.spriteName = "Character";
    }
    
    /**
     * Constructeur complet
     */
    public Character(float x, float y, float speed, int maxHealth) {
        super(x, y, speed, 0, 0);
        this.health = maxHealth;
        this.maxHealth = maxHealth;
        this.textures = new ArrayList<>();
        this.idlePathPrefix = "character_sprites/Idle/";
        this.runPathPrefix = "character_sprites/Run/";
        this.spriteName = "Character";
    }
    
    /**
     * Initialise toutes les animations du personnage.
     * Cette méthode doit être appelée après la création du personnage.
     */
    protected void initializeAnimations() {
        // Charger les animations Idle
        loadIdleAnimation(Direction.DOWN);
        loadIdleAnimation(Direction.UP);
        loadIdleAnimation(Direction.SIDE);
        loadIdleAnimation(Direction.SIDE_LEFT);
        
        // Charger les animations Run
        loadRunAnimation(Direction.DOWN);
        loadRunAnimation(Direction.UP);
        loadRunAnimation(Direction.SIDE);
        loadRunAnimation(Direction.SIDE_LEFT);
        
        // Définir l'animation par défaut
        if (!idleAnimations.isEmpty()) {
            currentAnimation = idleAnimations.get(Direction.DOWN);
        }
    }
    
    /**
     * Charge une animation idle pour une direction donnée
     */
    protected void loadIdleAnimation(Direction direction) {
        String path = buildAnimationPath(idlePathPrefix, direction, "idle");
        Animation<TextureRegion> animation = loadAnimationWithTextureTracking(path);
        addIdleAnimation(direction, animation);
    }
    
    /**
     * Charge une animation run pour une direction donnée
     */
    protected void loadRunAnimation(Direction direction) {
        String path = buildAnimationPath(runPathPrefix, direction, "run");
        Animation<TextureRegion> animation = loadAnimationWithTextureTracking(path);
        addRunAnimation(direction, animation);
    }
    
    /**
     * Construit le chemin vers l'animation selon la direction
     */
    private String buildAnimationPath(String prefix, Direction direction, String action) {
        String directionName = getDirectionName(direction);
        return prefix + spriteName + "_" + directionName + "_" + action + "_no-hands-Sheet6.png";
    }
    
    /**
     * Retourne le nom de direction pour les fichiers d'assets
     */
    private String getDirectionName(Direction direction) {
        switch (direction) {
            case DOWN:
                return "down";
            case UP:
                return "up";
            case SIDE:
                return "side";
            case SIDE_LEFT:
                return "side-left";
            default:
                return "down";
        }
    }
    
    /**
     * Charge une animation et stocke la texture pour pouvoir la libérer
     */
    private Animation<TextureRegion> loadAnimationWithTextureTracking(String path) {
        Animation<TextureRegion> animation = loadAnimation(path);
        
        // Extraire la texture de l'animation pour la stocker
        // Note: Les TextureRegion contiennent une référence à la texture
        TextureRegion firstFrame = animation.getKeyFrame(0);
        if (firstFrame != null && firstFrame.getTexture() != null) {
            textures.add(firstFrame.getTexture());
        }
        
        return animation;
    }
    
    /**
     * Inflige des dégâts au personnage
     */
    public void takeDamage(int damage) {
        health = Math.max(0, health - damage);
        if (health <= 0) {
            onDeath();
        }
    }
    
    /**
     * Soigne le personnage
     */
    public void heal(int amount) {
        health = Math.min(maxHealth, health + amount);
    }
    
    /**
     * Méthode appelée lorsque le personnage meurt
     * Peut être surchargée dans les classes filles
     */
    protected void onDeath() {
        isActive = false;
    }
    
    /**
     * Vérifie si le personnage est vivant
     */
    public boolean isAlive() {
        return health > 0 && isActive;
    }
    
    /**
     * Libère les ressources du personnage
     */
    @Override
    public void dispose() {
        super.dispose();
        
        // Libérer toutes les textures
        for (Texture texture : textures) {
            if (texture != null) {
                texture.dispose();
            }
        }
        textures.clear();
    }
    
    // Getters et Setters
    public int getHealth() {
        return health;
    }
    
    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(maxHealth, health));
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }
    
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        if (health > maxHealth) {
            health = maxHealth;
        }
    }
    
    /**
     * Retourne le pourcentage de santé (0.0 à 1.0)
     */
    public float getHealthPercentage() {
        return maxHealth > 0 ? (float) health / maxHealth : 0f;
    }
    
    public String getSpriteName() {
        return spriteName;
    }
    
    public void setSpriteName(String spriteName) {
        this.spriteName = spriteName;
    }
    
    public String getIdlePathPrefix() {
        return idlePathPrefix;
    }
    
    public void setIdlePathPrefix(String idlePathPrefix) {
        this.idlePathPrefix = idlePathPrefix;
    }
    
    public String getRunPathPrefix() {
        return runPathPrefix;
    }
    
    public void setRunPathPrefix(String runPathPrefix) {
        this.runPathPrefix = runPathPrefix;
    }
}
