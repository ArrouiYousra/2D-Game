package com.tlse1.twodgame.weapons;

/**
 * Classe représentant une arme.
 * Pour l'instant, simple classe pour indiquer qu'on a une arme.
 */
public class Weapon {
    
    public enum WeaponType {
        SWORD,
        WAND,
        BOW,
    }
    
    private WeaponType type;
    
    /**
     * Constructeur
     * @param type Type d'arme
     */
    public Weapon(WeaponType type) {
        this.type = type;
    }
    
    /**
     * Constructeur par défaut (GUN)
     */
    public Weapon() {
        this.type = WeaponType.GUN;
    }
    
    // Getters et Setters
    public WeaponType getType() {
        return type;
    }
    
    public void setType(WeaponType type) {
        this.type = type;
    }
}

