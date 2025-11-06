package com.tlse1.twodgame.rooms;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tlse1.twodgame.entities.Door;
import com.tlse1.twodgame.entities.Enemy;
import com.tlse1.twodgame.entities.Entity;
import com.tlse1.twodgame.entities.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant une salle dans le jeu.
 * Une salle contient des ennemis, des portes, et des obstacles.
 */
public class Room {
    
    private int roomNumber;
    private float width;
    private float height;
    
    // Portes
    private Door entranceDoor;  // Porte d'entrée (en bas)
    private Door exitDoor;      // Porte de sortie (en haut)
    
    // Ennemis
    private List<Enemy> enemies;
    private int initialEnemyCount;
    
    // Obstacles/Murs (à implémenter plus tard)
    private List<Entity> obstacles;
    
    // État de la salle
    private boolean isCleared;  // Tous les ennemis sont morts
    private boolean hasBeenVisited;
    
    // Position de spawn du joueur dans cette salle
    private float playerSpawnX;
    private float playerSpawnY;
    
    /**
     * Constructeur pour une salle
     * @param roomNumber Numéro de la salle (commence à 1)
     * @param width Largeur de la salle
     * @param height Hauteur de la salle
     * @param player Référence au joueur
     */
    public Room(int roomNumber, float width, float height, Player player) {
        this.roomNumber = roomNumber;
        this.width = width;
        this.height = height;
        this.enemies = new ArrayList<>();
        this.obstacles = new ArrayList<>();
        this.isCleared = false;
        this.hasBeenVisited = false;
        
        // Calculer les positions des portes (fixes : entrée en bas, sortie en haut)
        float doorWidth = 64f;
        float doorHeight = 64f;
        float entranceX = width / 2 - doorWidth / 2;
        float entranceY = 50f; // En bas
        float exitX = width / 2 - doorWidth / 2;
        float exitY = height - doorHeight - 50f; // En haut
        
        // Créer les portes
        entranceDoor = new Door(entranceX, entranceY, Door.DoorType.ENTRANCE);
        exitDoor = new Door(exitX, exitY, Door.DoorType.EXIT);
        
        // Position de spawn du joueur : devant la porte d'entrée
        playerSpawnX = entranceX;
        playerSpawnY = entranceY + doorHeight + 20f;
        
        // Créer les ennemis (sera fait par RoomManager)
    }
    
    /**
     * Ajoute un ennemi à la salle
     */
    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
        if (!hasBeenVisited) {
            initialEnemyCount = enemies.size();
        }
    }
    
    /**
     * Met à jour la salle et tous ses éléments
     */
    public void update(float deltaTime) {
        // Mettre à jour tous les ennemis actifs
        for (Enemy enemy : enemies) {
            if (enemy.isActive() && enemy.isAlive()) {
                enemy.update(deltaTime);
            }
        }
        
        // Vérifier si tous les ennemis sont morts
        checkIfCleared();
        
        // Ouvrir la porte de sortie si la salle est nettoyée
        if (isCleared && !exitDoor.isOpen()) {
            exitDoor.open();
        }
    }
    
    /**
     * Vérifie si tous les ennemis sont morts
     */
    private void checkIfCleared() {
        boolean allDead = true;
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                allDead = false;
                break;
            }
        }
        isCleared = allDead;
    }
    
    /**
     * Dessine tous les éléments de la salle
     */
    public void render(SpriteBatch batch) {
        // Dessiner les obstacles (à implémenter)
        for (Entity obstacle : obstacles) {
            obstacle.render(batch);
        }
        
        // Dessiner les portes
        entranceDoor.render(batch);
        exitDoor.render(batch);
        
        // Dessiner les ennemis
        for (Enemy enemy : enemies) {
            if (enemy.isActive() && enemy.isAlive()) {
                enemy.render(batch);
            }
        }
    }
    
    /**
     * Vérifie si le joueur entre en collision avec la porte de sortie
     */
    public boolean playerCollidesWithExitDoor(Player player) {
        return exitDoor.canPassThrough() && exitDoor.collidesWithEntity(player);
    }
    
    /**
     * Vérifie si le joueur entre en collision avec la porte d'entrée
     */
    public boolean playerCollidesWithEntranceDoor(Player player) {
        return entranceDoor.collidesWithEntity(player);
    }
    
    /**
     * Marque la salle comme visitée
     */
    public void markAsVisited() {
        hasBeenVisited = true;
    }
    
    /**
     * Réinitialise les ennemis de la salle (quand on revient dans une salle déjà visitée)
     * Les ennemis seront plus forts
     */
    public void clearEnemies() {
        for (Enemy enemy : enemies) {
            if (enemy != null) {
                enemy.dispose();
            }
        }
        enemies.clear();
        isCleared = false;
    }
    
    /**
     * Libère les ressources de la salle
     */
    public void dispose() {
        if (entranceDoor != null) {
            entranceDoor.dispose();
        }
        if (exitDoor != null) {
            exitDoor.dispose();
        }
        for (Enemy enemy : enemies) {
            if (enemy != null) {
                enemy.dispose();
            }
        }
        enemies.clear();
        for (Entity obstacle : obstacles) {
            if (obstacle != null) {
                obstacle.dispose();
            }
        }
        obstacles.clear();
    }
    
    // Getters et Setters
    public int getRoomNumber() {
        return roomNumber;
    }
    
    public float getWidth() {
        return width;
    }
    
    public float getHeight() {
        return height;
    }
    
    public Door getEntranceDoor() {
        return entranceDoor;
    }
    
    public Door getExitDoor() {
        return exitDoor;
    }
    
    public List<Enemy> getEnemies() {
        return enemies;
    }
    
    public boolean isCleared() {
        return isCleared;
    }
    
    public boolean hasBeenVisited() {
        return hasBeenVisited;
    }
    
    public float getPlayerSpawnX() {
        return playerSpawnX;
    }
    
    public float getPlayerSpawnY() {
        return playerSpawnY;
    }
    
    public int getAliveEnemyCount() {
        int count = 0;
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                count++;
            }
        }
        return count;
    }
}
