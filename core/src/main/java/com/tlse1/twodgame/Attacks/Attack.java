package com.tlse1.twodgame.Attacks;

import com.tlse1.twodgame.entities.Entity;
import com.tlse1.twodgame.entities.Character;
import com.tlse1.twodgame.entities.Player;
import com.tlse1.twodgame.entities.Enemy;
import com.tlse1.twodgame.utils.Direction;

public abstract class Attack {
    protected float damage;
    protected float range;
    protected float delay;

    public Attack(float damage, float range, float delay) {
        this.damage = damage;
        this.range = range;
        this.delay = delay;
    }

    public abstract void execute(Object attacker, Object target);
    
    // Getters
    public float getDamage() {
        return damage;
    }
    
    public float getRange() {
        return range;
    }
    
    public float getDelay() {
        return delay;
    }
    
    // Setters
    public void setDamage(float damage) {
        this.damage = damage;
    }
    
    public void setRange(float range) {
        this.range = range;
    }
    
    public void setDelay(float delay) {
        this.delay = delay;
    }
}
