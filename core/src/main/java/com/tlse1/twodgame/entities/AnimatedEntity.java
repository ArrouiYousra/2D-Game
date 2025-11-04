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
    
    // Animation actuellement affichée
    protected Animation<TextureRegion> currentAnimation;
    
    // Temps écoulé pour l'animation
    protected float stateTime;
    
    // Direction actuelle de l'entité
    protected Direction currentDirection;
    
    // État de mouvement
    protected boolean isMoving;
    
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
        this.currentDirection = Direction.DOWN;
        this.isMoving = false;
        this.stateTime = 0f;
        this.scale = 4f;
    }
    
    /**
     * Constructeur avec position
     */
    public AnimatedEntity(float x, float y) {
        super(x, y);
        this.idleAnimations = new HashMap<>();
        this.runAnimations = new HashMap<>();
        this.currentDirection = Direction.DOWN;
        this.isMoving = false;
        this.stateTime = 0f;
        this.scale = 4f;
    }
    
    /**
     * Constructeur complet
     */
    public AnimatedEntity(float x, float y, float speed, float width, float height) {
        super(x, y, speed, width, height);
        this.idleAnimations = new HashMap<>();
        this.runAnimations = new HashMap<>();
        this.currentDirection = Direction.DOWN;
        this.isMoving = false;
        this.stateTime = 0f;
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
     * Met à jour l'animation actuelle selon l'état (idle/run) et la direction.
     * Cette méthode doit être appelée dans update() pour changer l'animation si nécessaire.
     */
    protected void updateAnimation() {
        Map<Direction, Animation<TextureRegion>> animations = isMoving ? runAnimations : idleAnimations;
        Animation<TextureRegion> newAnimation = animations.get(currentDirection);
        
        // Changer d'animation seulement si nécessaire
        if (newAnimation != null && newAnimation != currentAnimation) {
            currentAnimation = newAnimation;
            // Optionnel : réinitialiser le stateTime pour recommencer l'animation
            // stateTime = 0f;
        }
        
        // Si aucune animation n'est définie, essayer de trouver une animation par défaut
        if (currentAnimation == null && !animations.isEmpty()) {
            currentAnimation = animations.values().iterator().next();
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
        
        // Mettre à jour l'animation selon l'état
        updateAnimation();
    }
    
    /**
     * Dessine l'entité animée.
     * Implémentation de base qui dessine l'animation courante.
     */
    @Override
    public void render(SpriteBatch batch) {
        if (currentAnimation == null || !isActive) {
            return;
        }
        
        TextureRegion frame = currentAnimation.getKeyFrame(stateTime, true); // true = boucle
        
        if (frame != null) {
            float renderWidth = frame.getRegionWidth() * scale;
            float renderHeight = frame.getRegionHeight() * scale;
            
            // Mettre à jour les dimensions si elles ne sont pas définies
            if (width == 0 || height == 0) {
                width = renderWidth;
                height = renderHeight;
            }
            
            batch.draw(frame, x, y, renderWidth, renderHeight);
        }
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
