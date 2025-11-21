package com.tlse1.twodgame.entities.handlers;

import com.badlogic.gdx.Gdx;

/**
 * Handler pour gérer le combat, la santé et les dégâts d'un personnage.
 */
public class CombatHandler {
    
    private int health;
    private int maxHealth;
    private int shield;
    private int maxShield;
    private AnimationHandler animationHandler;
    
    public CombatHandler(int maxHealth, AnimationHandler animationHandler) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.maxShield = 0;
        this.shield = 0;
        this.animationHandler = animationHandler;
    }
    
    /**
     * Inflige des dégâts au personnage.
     * Les dégâts sont d'abord absorbés par le shield, puis par les HP.
     * 
     * @param damage Montant des dégâts
     */
    public void takeDamage(int damage) {
        if (damage <= 0) {
            return;
        }
        
        // D'abord, les dégâts sont absorbés par le shield
        if (shield > 0) {
            if (shield >= damage) {
                // Le shield absorbe tous les dégâts
                shield -= damage;
                damage = 0;
            } else {
                // Le shield est détruit, les dégâts restants passent aux HP
                damage -= shield;
                shield = 0;
            }
        }
        
        // Les dégâts restants sont infligés aux HP
        if (damage > 0) {
            health = Math.max(0, health - damage);
        }
        
        // Déclencher l'animation de blessure
        if (health > 0 && animationHandler != null) {
            animationHandler.setHurt(true);
        }
        
        // Si le personnage est mort, déclencher l'animation de mort
        if (health <= 0 && animationHandler != null) {
            animationHandler.setDead(true);
        }
    }
    
    /**
     * Soigne le personnage.
     * 
     * @param amount Montant de soin
     */
    public void heal(int amount) {
        health = Math.min(maxHealth, health + amount);
    }
    
    /**
     * Vérifie si le personnage est vivant.
     * 
     * @return true si vivant
     */
    public boolean isAlive() {
        return health > 0;
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
    
    // Shield getters et setters
    public int getShield() {
        return shield;
    }
    
    public void setShield(int shield) {
        this.shield = Math.max(0, Math.min(maxShield, shield));
    }
    
    public int getMaxShield() {
        return maxShield;
    }
    
    public void setMaxShield(int maxShield) {
        this.maxShield = maxShield;
        if (shield > maxShield) {
            shield = maxShield;
        }
    }
}

