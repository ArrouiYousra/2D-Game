package com.tlse1.twodgame.entities.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tlse1.twodgame.utils.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handler pour gérer toutes les animations d'un personnage.
 * Responsable du chargement, de la mise à jour et du rendu des animations.
 */
public class AnimationHandler {
    
    // Animations par direction
    private Map<Direction, Animation<TextureRegion>> idleAnimations;
    private Map<Direction, Animation<TextureRegion>> walkAnimations;
    private Map<Direction, Animation<TextureRegion>> runAnimations;
    private Map<Direction, Animation<TextureRegion>> attackAnimations;
    private Map<Direction, Animation<TextureRegion>> walkAttackAnimations;
    private Map<Direction, Animation<TextureRegion>> runAttackAnimations;
    private Map<Direction, Animation<TextureRegion>> hurtAnimations;
    private Map<Direction, Animation<TextureRegion>> deathAnimations;
    
    // Animation actuelle
    private Animation<TextureRegion> currentAnimation;
    private Direction currentDirection;
    
    // État du personnage
    private boolean isMoving;
    private boolean isRunning;
    private boolean isAttacking;
    private boolean isHurt;
    private boolean isDead;
    
    // Temps d'animation
    private float stateTime;
    private float attackStateTime;
    private float hurtStateTime;
    
    // Textures chargées (pour dispose)
    private List<Texture> textures;
    
    // Échelle de rendu
    private float scale;
    
    public AnimationHandler() {
        this.scale = 4f;
        this.stateTime = 0f;
        this.attackStateTime = 0f;
        this.hurtStateTime = 0f;
        this.currentDirection = Direction.DOWN;
        this.isMoving = false;
        this.isRunning = false;
        this.isAttacking = false;
        this.isHurt = false;
        this.isDead = false;
        this.idleAnimations = new HashMap<>();
        this.walkAnimations = new HashMap<>();
        this.runAnimations = new HashMap<>();
        this.attackAnimations = new HashMap<>();
        this.walkAttackAnimations = new HashMap<>();
        this.runAttackAnimations = new HashMap<>();
        this.hurtAnimations = new HashMap<>();
        this.deathAnimations = new HashMap<>();
        this.textures = new ArrayList<>();
    }
    
    /**
     * Met à jour les animations.
     * 
     * @param deltaTime Temps écoulé depuis la dernière frame
     */
    public void update(float deltaTime) {
        // Si le personnage est mort, continuer à mettre à jour stateTime pour l'animation de mort
        if (isDead) {
            stateTime += deltaTime;
            updateCurrentAnimation();
            return;
        }
        
        stateTime += deltaTime;
        
        // Gérer l'animation de blessure
        if (isHurt) {
            hurtStateTime += deltaTime;
            if (currentAnimation != null && hurtStateTime >= currentAnimation.getAnimationDuration()) {
                isHurt = false;
                hurtStateTime = 0f;
            }
        }
        
        // Gérer l'animation d'attaque
        if (isAttacking) {
            attackStateTime += deltaTime;
            if (currentAnimation != null) {
                float animationDuration = currentAnimation.getAnimationDuration();
                if (attackStateTime >= animationDuration) {
                    isAttacking = false;
                    attackStateTime = 0f;
                }
            }
        }
        
        updateCurrentAnimation();
    }
    
    /**
     * Met à jour l'animation actuelle selon l'état du personnage.
     * Priorité : death > hurt > run_attack > walk_attack > attack > run > walk > idle
     */
    private void updateCurrentAnimation() {
        Map<Direction, Animation<TextureRegion>> animations;
        
        if (isDead && !deathAnimations.isEmpty()) {
            animations = deathAnimations;
        } else if (isHurt && !hurtAnimations.isEmpty()) {
            animations = hurtAnimations;
        } else if (isAttacking && isMoving && isRunning && !runAttackAnimations.isEmpty()) {
            animations = runAttackAnimations;
        } else if (isAttacking && isMoving && !walkAttackAnimations.isEmpty()) {
            animations = walkAttackAnimations;
        } else if (isAttacking && !attackAnimations.isEmpty()) {
            animations = attackAnimations;
        } else if (isMoving && isRunning && !runAnimations.isEmpty()) {
            animations = runAnimations;
        } else if (isMoving && !walkAnimations.isEmpty()) {
            animations = walkAnimations;
        } else {
            animations = idleAnimations;
        }
        
        Animation<TextureRegion> newAnimation = animations.get(currentDirection);
        
        // Log si aucune animation trouvée (pour déboguer)
        if (newAnimation == null && !animations.isEmpty()) {
            // Essayer de trouver une animation dans une autre direction comme fallback
            for (Direction dir : Direction.values()) {
                Animation<TextureRegion> fallback = animations.get(dir);
                if (fallback != null) {
                    newAnimation = fallback;
                    Gdx.app.log("AnimationHandler", String.format("Animation non trouvée pour direction %s, utilisation de %s comme fallback", 
                        currentDirection, dir));
                    break;
                }
            }
        }
        
        // Fallbacks
        if (newAnimation == null && isAttacking && isMoving && isRunning) {
            newAnimation = walkAttackAnimations.get(currentDirection);
            if (newAnimation == null) {
                newAnimation = attackAnimations.get(currentDirection);
            }
        }
        if (newAnimation == null && isAttacking && isMoving) {
            newAnimation = attackAnimations.get(currentDirection);
        }
        if (newAnimation == null && isAttacking) {
            newAnimation = runAnimations.get(currentDirection);
            if (newAnimation == null) {
                newAnimation = walkAnimations.get(currentDirection);
            }
            if (newAnimation == null) {
                newAnimation = idleAnimations.get(currentDirection);
            }
        }
        if (newAnimation == null && isMoving && isRunning) {
            newAnimation = walkAnimations.get(currentDirection);
        }
        if (newAnimation == null && isMoving) {
            newAnimation = idleAnimations.get(currentDirection);
        }
        if (newAnimation == null && !animations.isEmpty()) {
            newAnimation = animations.values().iterator().next();
        }
        
        if (newAnimation != null && newAnimation != currentAnimation) {
            currentAnimation = newAnimation;
            if (isDead) {
                stateTime = 0f;
            } else if (isHurt) {
                hurtStateTime = 0f;
            } else if (isAttacking) {
                attackStateTime = 0f;
            } else {
                stateTime = 0f;
            }
        }
    }
    
    /**
     * Dessine l'animation actuelle.
     * 
     * @param batch SpriteBatch pour le rendu
     * @param x Position X
     * @param y Position Y
     * @return Dimensions rendues [width, height]
     */
    public float[] render(SpriteBatch batch, float x, float y) {
        if (currentAnimation == null) {
            return new float[]{0, 0};
        }
        
        float animTime;
        boolean looping;
        
        if (isDead) {
            animTime = stateTime;
            looping = false;
            
            // Si l'animation de mort est terminée, ne pas rendre le personnage (il disparaît)
            if (currentAnimation != null && stateTime >= currentAnimation.getAnimationDuration()) {
                return new float[]{0, 0};
            }
        } else if (isHurt) {
            animTime = hurtStateTime;
            looping = false;
        } else if (isAttacking) {
            animTime = attackStateTime;
            looping = false;
        } else {
            animTime = stateTime;
            looping = true;
        }
        
        TextureRegion frame = currentAnimation.getKeyFrame(animTime, looping);
        if (frame == null) {
            return new float[]{0, 0};
        }
        
        float renderWidth = frame.getRegionWidth() * scale;
        float renderHeight = frame.getRegionHeight() * scale;
        
        batch.draw(frame, x, y, renderWidth, renderHeight);
        
        return new float[]{renderWidth, renderHeight};
    }
    
    /**
     * Obtient l'index de la frame actuelle de l'animation.
     * 
     * @return Index de la frame actuelle, ou -1 si aucune animation
     */
    public int getCurrentFrameIndex() {
        if (currentAnimation == null) {
            return -1;
        }
        
        float animTime;
        boolean looping;
        
        if (isDead) {
            animTime = stateTime;
            looping = false;
        } else if (isHurt) {
            animTime = hurtStateTime;
            looping = false;
        } else if (isAttacking) {
            animTime = attackStateTime;
            looping = false;
        } else {
            animTime = stateTime;
            looping = true;
        }
        
        return currentAnimation.getKeyFrameIndex(animTime);
    }
    
    /**
     * Vérifie si l'animation actuelle est une animation d'attaque.
     * 
     * @return true si en train d'attaquer
     */
    public boolean isAttackAnimation() {
        return isAttacking && currentAnimation != null && 
               (attackAnimations.containsValue(currentAnimation) ||
                walkAttackAnimations.containsValue(currentAnimation) ||
                runAttackAnimations.containsValue(currentAnimation));
    }
    
    /**
     * Obtient le type d'animation d'attaque actuelle.
     * 
     * @return "attack", "walk_attack", "run_attack", ou null si pas en attaque
     */
    public String getAttackAnimationType() {
        if (!isAttacking || currentAnimation == null) {
            return null;
        }
        
        // Vérifier dans l'ordre de priorité : run_attack > walk_attack > attack
        for (Animation<TextureRegion> anim : runAttackAnimations.values()) {
            if (anim == currentAnimation) {
                return "run_attack";
            }
        }
        for (Animation<TextureRegion> anim : walkAttackAnimations.values()) {
            if (anim == currentAnimation) {
                return "walk_attack";
            }
        }
        for (Animation<TextureRegion> anim : attackAnimations.values()) {
            if (anim == currentAnimation) {
                return "attack";
            }
        }
        
        return null;
    }
    
    /**
     * Libère les ressources.
     */
    public void dispose() {
        for (Texture texture : textures) {
            if (texture != null) {
                texture.dispose();
            }
        }
        textures.clear();
        idleAnimations.clear();
        walkAnimations.clear();
        runAnimations.clear();
        attackAnimations.clear();
        walkAttackAnimations.clear();
        runAttackAnimations.clear();
        hurtAnimations.clear();
        deathAnimations.clear();
    }
    
    // Getters et Setters
    public Direction getCurrentDirection() {
        return currentDirection;
    }
    
    public void setCurrentDirection(Direction direction) {
        this.currentDirection = direction;
        updateCurrentAnimation();
    }
    
    public boolean isMoving() {
        return isMoving;
    }
    
    public void setMoving(boolean moving) {
        if (this.isMoving != moving) {
            this.isMoving = moving;
            updateCurrentAnimation();
        }
    }
    
    public boolean isRunning() {
        return isRunning;
    }
    
    public void setRunning(boolean running) {
        if (this.isRunning != running) {
            this.isRunning = running;
            updateCurrentAnimation();
        }
    }
    
    public boolean isAttacking() {
        return isAttacking;
    }
    
    public void attack() {
        if (!isAttacking) {
            isAttacking = true;
            attackStateTime = 0f;
            updateCurrentAnimation();
        }
    }
    
    public boolean isHurt() {
        return isHurt;
    }
    
    public void setHurt(boolean hurt) {
        if (this.isHurt != hurt) {
            this.isHurt = hurt;
            if (hurt) {
                hurtStateTime = 0f;
            }
            updateCurrentAnimation();
        }
    }
    
    public boolean isDead() {
        return isDead;
    }
    
    /**
     * Retourne l'animation actuelle (pour déboguer).
     * 
     * @return L'animation actuelle, ou null si aucune
     */
    public Animation<TextureRegion> getCurrentAnimation() {
        return currentAnimation;
    }
    
    public void setDead(boolean dead) {
        if (this.isDead != dead) {
            this.isDead = dead;
            if (dead) {
                stateTime = 0f;
                isMoving = false;
                isRunning = false;
                isAttacking = false;
                isHurt = false;
            }
            updateCurrentAnimation();
        }
    }
    
    public float getScale() {
        return scale;
    }
    
    public void setScale(float scale) {
        this.scale = scale;
    }
    
    // Méthodes pour ajouter des animations (utilisées par les classes filles)
    public void addIdleAnimation(Direction direction, Animation<TextureRegion> animation) {
        idleAnimations.put(direction, animation);
    }
    
    public void addWalkAnimation(Direction direction, Animation<TextureRegion> animation) {
        walkAnimations.put(direction, animation);
    }
    
    public void addRunAnimation(Direction direction, Animation<TextureRegion> animation) {
        runAnimations.put(direction, animation);
    }
    
    public void addAttackAnimation(Direction direction, Animation<TextureRegion> animation) {
        attackAnimations.put(direction, animation);
    }
    
    public void addWalkAttackAnimation(Direction direction, Animation<TextureRegion> animation) {
        walkAttackAnimations.put(direction, animation);
    }
    
    public void addRunAttackAnimation(Direction direction, Animation<TextureRegion> animation) {
        runAttackAnimations.put(direction, animation);
    }
    
    public void addHurtAnimation(Direction direction, Animation<TextureRegion> animation) {
        hurtAnimations.put(direction, animation);
    }
    
    public void addDeathAnimation(Direction direction, Animation<TextureRegion> animation) {
        deathAnimations.put(direction, animation);
    }
    
    public void addTexture(Texture texture) {
        textures.add(texture);
    }
}

