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
    protected String shootPathPrefix; // Préfixe pour les animations de tir
    
    // Nom de base pour les fichiers (ex: "Character_down_idle_no-hands-Sheet6.png")
    protected String spriteName;
    
    // Indique si le personnage a une arme
    protected boolean hasWeapon;
    
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
        this.shootPathPrefix = "";
        this.spriteName = "Character";
        this.hasWeapon = false;
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
        this.shootPathPrefix = "";
        this.spriteName = "Character";
        this.hasWeapon = false;
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
        this.shootPathPrefix = "";
        this.spriteName = "Character";
        this.hasWeapon = false;
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
        
        // Charger les animations de tir si on a une arme
        if (hasWeapon && !shootPathPrefix.isEmpty()) {
            loadShootAnimation(Direction.DOWN);
            loadShootAnimation(Direction.UP);
            loadShootAnimation(Direction.SIDE);
            loadShootAnimation(Direction.SIDE_LEFT);
        }
        
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
     * Charge une animation shoot pour une direction donnée
     */
    protected void loadShootAnimation(Direction direction) {
        if (shootPathPrefix == null || shootPathPrefix.isEmpty()) {
            return;
        }
        String path = buildShootAnimationPath(shootPathPrefix, direction);
        Animation<TextureRegion> animation = loadAnimationWithTextureTracking(path);
        addShootAnimation(direction, animation);
    }
    
    /**
     * Charge une animation depuis une sprite sheet avec 4 directions (lignes)
     * Les directions sont organisées en lignes : DOWN, LEFT, RIGHT, UP (ou similaire)
     * 
     * @param path Chemin vers la texture
     * @param direction Direction à extraire
     * @param framesPerDirection Nombre de frames par direction (colonnes)
     * @param frameDuration Durée de chaque frame
     * @return Animation pour la direction spécifiée
     */
    protected Animation<TextureRegion> loadAnimationFrom4DirectionSheet(String path, Direction direction, int framesPerDirection, float frameDuration) {
        try {
            Texture texture = new Texture(com.badlogic.gdx.Gdx.files.internal(path));
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            
            int textureWidth = texture.getWidth();
            int textureHeight = texture.getHeight();
            
            // Les sprite sheets sont organisées en 4 lignes (directions) et plusieurs colonnes (frames)
            int rows = 4; // Toujours 4 directions
            int cols = framesPerDirection;
            
            // Calculer les dimensions d'une frame
            int frameW = textureWidth / cols;
            int frameH = textureHeight / rows;
            
            // Découper la texture en grille
            TextureRegion[][] grid = TextureRegion.split(texture, frameW, frameH);
            
            // Mapper les directions aux lignes
            int rowIndex = getDirectionRowIndex(direction, rows);
            
            // Extraire les frames pour cette direction
            TextureRegion[] frames = new TextureRegion[cols];
            for (int c = 0; c < cols; c++) {
                frames[c] = grid[rowIndex][c];
            }
            
            // Stocker la texture pour le dispose
            textures.add(texture);
            
            com.badlogic.gdx.Gdx.app.log("Character", String.format("Loaded animation: %s, direction: %s, frames: %d, frameSize: %dx%d", 
                path, direction, cols, frameW, frameH));
            
            return new Animation<>(frameDuration, frames);
        } catch (Exception e) {
            com.badlogic.gdx.Gdx.app.error("Character", "Erreur lors du chargement de l'animation 4 directions: " + path, e);
            return new Animation<>(frameDuration, new TextureRegion[0]);
        }
    }
    
    /**
     * Charge une animation depuis une sprite sheet en détectant automatiquement le nombre de frames
     * Cette méthode essaie de détecter le nombre de frames par direction en analysant la texture
     * 
     * @param path Chemin vers la texture
     * @param direction Direction à extraire
     * @param estimatedFrameWidth Largeur estimée d'une frame (en pixels)
     * @param frameDuration Durée de chaque frame
     * @return Animation pour la direction spécifiée
     */
    protected Animation<TextureRegion> loadAnimationFrom4DirectionSheetAuto(String path, Direction direction, int estimatedFrameWidth, float frameDuration) {
        try {
            Texture texture = new Texture(com.badlogic.gdx.Gdx.files.internal(path));
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            
            int textureWidth = texture.getWidth();
            int textureHeight = texture.getHeight();
            
            // Calculer le nombre de colonnes basé sur la largeur estimée
            int cols = textureWidth / estimatedFrameWidth;
            int rows = 4; // Toujours 4 directions
            
            // Ajuster pour être sûr que ça divise bien
            int frameW = textureWidth / cols;
            int frameH = textureHeight / rows;
            
            // Découper la texture en grille
            TextureRegion[][] grid = TextureRegion.split(texture, frameW, frameH);
            
            // Mapper les directions aux lignes
            int rowIndex = getDirectionRowIndex(direction, rows);
            
            // Extraire les frames pour cette direction
            TextureRegion[] frames = new TextureRegion[cols];
            for (int c = 0; c < cols; c++) {
                frames[c] = grid[rowIndex][c];
            }
            
            // Stocker la texture pour le dispose
            textures.add(texture);
            
            com.badlogic.gdx.Gdx.app.log("Character", String.format("Auto-loaded animation: %s, direction: %s, frames: %d (detected), frameSize: %dx%d", 
                path, direction, cols, frameW, frameH));
            
            return new Animation<>(frameDuration, frames);
        } catch (Exception e) {
            com.badlogic.gdx.Gdx.app.error("Character", "Erreur lors du chargement auto de l'animation: " + path, e);
            return new Animation<>(frameDuration, new TextureRegion[0]);
        }
    }
    
    /**
     * Retourne l'index de la ligne pour une direction donnée
     * Ordre typique dans les sprite sheets : DOWN (0), LEFT (1), RIGHT (2), UP (3)
     */
    private int getDirectionRowIndex(Direction direction, int totalRows) {
        switch (direction) {
            case DOWN:
                return 0;
            case SIDE_LEFT:
                return 1; // LEFT
            case SIDE:
                return 2; // RIGHT
            case UP:
                return 3;
            default:
                return 0;
        }
    }
    
    /**
     * Construit le chemin vers l'animation selon la direction
     */
    private String buildAnimationPath(String prefix, Direction direction, String action) {
        String directionName = getDirectionName(direction);
        // Si on a une arme, utiliser "idle-and-run" au lieu de "idle" ou "run"
        if (hasWeapon && (action.equals("idle") || action.equals("run"))) {
            return prefix + spriteName + "_" + directionName + "_idle-and-run-Sheet6.png";
        }
        return prefix + spriteName + "_" + directionName + "_" + action + "_no-hands-Sheet6.png";
    }
    
    /**
     * Construit le chemin vers l'animation de tir selon la direction
     */
    private String buildShootAnimationPath(String prefix, Direction direction) {
        String directionName = getDirectionName(direction);
        return prefix + spriteName + "_" + directionName + "_shoot-Sheet3.png";
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
    
    public String getShootPathPrefix() {
        return shootPathPrefix;
    }
    
    public void setShootPathPrefix(String shootPathPrefix) {
        this.shootPathPrefix = shootPathPrefix;
    }
    
    public boolean hasWeapon() {
        return hasWeapon;
    }
    
    public void setHasWeapon(boolean hasWeapon) {
        this.hasWeapon = hasWeapon;
    }
}
