package com.tlse1.twodgame.utils;

/**
 * Enum représentant les niveaux de difficulté du jeu.
 */
public enum Difficulty {
    EASY(3, "Facile"),
    MEDIUM(5, "Moyenne"),
    HARD(10, "Difficile");
    
    private final int maxRooms;
    private final String displayName;
    
    Difficulty(int maxRooms, String displayName) {
        this.maxRooms = maxRooms;
        this.displayName = displayName;
    }
    
    public int getMaxRooms() {
        return maxRooms;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}