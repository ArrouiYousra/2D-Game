package com.tlse1.twodgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tlse1.twodgame.utils.Direction;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe abstraite pour les entités animées.
 * Gère les animations et le rendu basé sur la direction et l'état de mouvement.
 */
public abstract class AnimatedEntity extends Entity {
    
    // Animations par direction (pour chaque type d'animation)
    protected Map<Direction, Animation<TextureRegion>> idleAnimations;
    protected Map<Direction, Animation<TextureRegion>> runAnimations;
    protected Map<Direction, Animation<TextureRegion>> shootAnimations; // Animations de tir
    
    // Animation actuellement affichée
    protected Animation<TextureRegion> currentAnimation;
    
    // Temps écoulé pour l'animation
    protected float stateTime;
    
    // Direction actuelle de l'entité
    protected Direction currentDirection;
    
    // État de mouvement
    protected boolean isMoving;
    
    // État de tir
    protected boolean isShooting;
    protected float shootStateTime; // Temps pour l'animation de tir
    
    // Échelle pour le rendu
    protected float scale;
    
    // Configuration des sprite sheets
    protected static final int DEFAULT_COLS = 6;
    protected static final int DEFAULT_ROWS = 1;
    protected static final float DEFAULT_FRAME_DURATION = 0.12f; // ~8 fps
    
    /**
     * Constructeur par défaut
     */
    public AnimatedEntity() {
        super();
        this.idleAnimations = new HashMap<>();
        this.runAnimations = new HashMap<>();
        this.shootAnimations = new HashMap<>();
        this.currentDirection = Direction.DOWN;
        this.isMoving = false;
        this.isShooting = false;
        this.stateTime = 0f;
        this.shootStateTime = 0f;
        this.scale = 4f;
    }
    
    /**
     * Constructeur avec position
     */
    public AnimatedEntity(float x, float y) {
        super(x, y);
        this.idleAnimations = new HashMap<>();
        this.runAnimations = new HashMap<>();
        this.shootAnimations = new HashMap<>();
        this.currentDirection = Direction.DOWN;
        this.isMoving = false;
        this.isShooting = false;
        this.stateTime = 0f;
        this.shootStateTime = 0f;
        this.scale = 4f;
    }
    
    /**
     * Constructeur complet
     */
    public AnimatedEntity(float x, float y, float speed, float width, float height) {
        super(x, y, speed, width, height);
        this.idleAnimations = new HashMap<>();
        this.runAnimations = new HashMap<>();
        this.shootAnimations = new HashMap<>();
        this.currentDirection = Direction.DOWN;
        this.isMoving = false;
        this.isShooting = false;
        this.stateTime = 0f;
        this.shootStateTime = 0f;
        this.scale = 4f;
    }
    
    /**
     * Charge une animation depuis une sprite sheet.
     * Cette méthode peut être utilisée par les classes filles pour charger leurs animations.
     * 
     * @param path Chemin vers la texture
     * @param cols Nombre de colonnes dans la sprite sheet
     * @param rows Nombre de lignes dans la sprite sheet
     * @param frameDuration Durée de chaque frame en secondes
     * @return L'animation créée
     */
    protected Animation<TextureRegion> loadAnimation(String path, int cols, int rows, float frameDuration) {
        try {
            Texture texture = new Texture(Gdx.files.internal(path));
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            
            int frameW = texture.getWidth() / cols;
            int frameH = texture.getHeight() / rows;
            
            TextureRegion[][] grid = TextureRegion.split(texture, frameW, frameH);
            
            TextureRegion[] frames = new TextureRegion[cols * rows];
            int i = 0;
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    frames[i++] = grid[r][c];
                }
            }
            
            return new Animation<>(frameDuration, frames);
        } catch (Exception e) {
            Gdx.app.error("AnimatedEntity", "Erreur lors du chargement de l'animation: " + path, e);
            // Retourner une animation vide pour éviter un crash
            return new Animation<>(frameDuration, new TextureRegion[0]);
        }
    }
    
    /**
     * Charge une animation avec les valeurs par défaut (6 cols, 1 row, 0.12f duration)
     */
    protected Animation<TextureRegion> loadAnimation(String path) {
        return loadAnimation(path, DEFAULT_COLS, DEFAULT_ROWS, DEFAULT_FRAME_DURATION);
    }
    
    /**
     * Ajoute une animation idle pour une direction donnée
     */
    protected void addIdleAnimation(Direction direction, Animation<TextureRegion> animation) {
        idleAnimations.put(direction, animation);
    }
    
    /**
     * Ajoute une animation run pour une direction donnée
     */
    protected void addRunAnimation(Direction direction, Animation<TextureRegion> animation) {
        runAnimations.put(direction, animation);
    }
    
    /**
     * Ajoute une animation shoot pour une direction donnée
     */
    protected void addShootAnimation(Direction direction, Animation<TextureRegion> animation) {
        shootAnimations.put(direction, animation);
    }
    
    /**
     * Met à jour l'animation actuelle selon l'état (idle/run/shoot) et la direction.
     * Cette méthode doit être appelée dans update() pour changer l'animation si nécessaire.
     */
    protected void updateAnimation() {
        Map<Direction, Animation<TextureRegion>> animations;
        
        // Priorité : tir > mouvement > idle
        if (isShooting && !shootAnimations.isEmpty()) {
            animations = shootAnimations;
        } else if (isMoving) {
            animations = runAnimations;
        } else {
            animations = idleAnimations;
        }
        
        Animation<TextureRegion> newAnimation = animations.get(currentDirection);
        
        // Si l'animation pour cette direction n'existe pas, logger un avertissement
        if (newAnimation == null) {
            Gdx.app.debug("AnimatedEntity", String.format("Animation manquante pour direction: %s, isMoving: %s, isShooting: %s", 
                currentDirection, isMoving, isShooting));
            
            // Essayer de trouver une animation par défaut dans cette map
            if (!animations.isEmpty()) {
                newAnimation = animations.values().iterator().next();
                Gdx.app.debug("AnimatedEntity", "Utilisation d'une animation par défaut");
            } else {
                // Si cette map est vide, essayer les autres maps
                if (animations == shootAnimations) {
                    if (!runAnimations.isEmpty()) {
                        newAnimation = runAnimations.values().iterator().next();
                    } else if (!idleAnimations.isEmpty()) {
                        newAnimation = idleAnimations.values().iterator().next();
                    }
                } else if (animations == runAnimations) {
                    if (!idleAnimations.isEmpty()) {
                        newAnimation = idleAnimations.values().iterator().next();
                    }
                }
            }
        }
        
        // Changer d'animation seulement si nécessaire
        if (newAnimation != null && newAnimation != currentAnimation) {
            currentAnimation = newAnimation;
            // Si on commence une animation de tir, réinitialiser le temps
            if (isShooting) {
                shootStateTime = 0f;
            }
        }
        
        // Si aucune animation n'est définie, essayer de trouver une animation par défaut
        if (currentAnimation == null && !animations.isEmpty()) {
            currentAnimation = animations.values().iterator().next();
            Gdx.app.log("AnimatedEntity", "currentAnimation était null, utilisation d'une animation par défaut");
        }
        
        // Dernière vérification : si toujours null, c'est un problème grave
        if (currentAnimation == null) {
            Gdx.app.error("AnimatedEntity", "ERREUR: currentAnimation est null ! Le personnage ne sera pas rendu.");
        }
    }
    
    /**
     * Met à jour l'entité.
     * Les classes filles doivent appeler super.update() pour mettre à jour les animations.
     */
    @Override
    public void update(float deltaTime) {
        // Mettre à jour le temps d'animation
        stateTime += deltaTime;
        
        // Si on est en train de tirer, mettre à jour le temps de tir
        if (isShooting) {
            shootStateTime += deltaTime;
            Animation<TextureRegion> shootAnim = shootAnimations.get(currentDirection);
            if (shootAnim != null) {
                // Vérifier si l'animation de tir est terminée
                if (shootStateTime >= shootAnim.getAnimationDuration()) {
                    isShooting = false;
                    shootStateTime = 0f;
                }
            } else {
                // Pas d'animation de tir, arrêter immédiatement
                isShooting = false;
                shootStateTime = 0f;
            }
        }
        
        // Mettre à jour l'animation selon l'état
        updateAnimation();
    }
    
    /**
     * Dessine l'entité animée.
     * Implémentation de base qui dessine l'animation courante.
     */
    @Override
    public void render(SpriteBatch batch) {
        if (!isActive) {
            Gdx.app.debug("AnimatedEntity", "Entité non active, pas de rendu");
            return;
        }
        
        if (currentAnimation == null) {
            Gdx.app.error("AnimatedEntity", "ERREUR: Tentative de rendu avec currentAnimation null !");
            return;
        }
        
        // Utiliser le temps approprié selon l'état
        float animTime = isShooting ? shootStateTime : stateTime;
        boolean looping = !isShooting; // Les animations de tir ne bouclent pas
        
        TextureRegion frame = currentAnimation.getKeyFrame(animTime, looping);
        
        if (frame == null) {
            Gdx.app.log("AnimatedEntity", String.format("Frame null pour animation, animTime: %.2f, looping: %s, isShooting: %s", 
                animTime, looping, isShooting));
            return;
        }
        
        float renderWidth = frame.getRegionWidth() * scale;
        float renderHeight = frame.getRegionHeight() * scale;
        
        // Mettre à jour les dimensions si elles ne sont pas définies
        if (width == 0 || height == 0) {
            width = renderWidth;
            height = renderHeight;
        }
        
        batch.draw(frame, x, y, renderWidth, renderHeight);
    }
    
    /**
     * Libère les ressources.
     * Les classes filles doivent appeler super.dispose() et libérer leurs propres textures.
     */
    @Override
    public void dispose() {
        // Les animations contiennent des références aux textures,
        // mais les textures doivent être libérées séparément par les classes filles
        idleAnimations.clear();
        runAnimations.clear();
        shootAnimations.clear();
        currentAnimation = null;
    }
    
    // Getters et Setters
    public Direction getCurrentDirection() {
        return currentDirection;
    }
    
    public void setCurrentDirection(Direction direction) {
        this.currentDirection = direction;
    }
    
    public boolean isMoving() {
        return isMoving;
    }
    
    public void setMoving(boolean moving) {
        this.isMoving = moving;
    }
    
    public float getScale() {
        return scale;
    }
    
    public void setScale(float scale) {
        this.scale = scale;
    }
    
    public float getStateTime() {
        return stateTime;
    }
    
    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
    }
    
    public Animation<TextureRegion> getCurrentAnimation() {
        return currentAnimation;
    }
}
