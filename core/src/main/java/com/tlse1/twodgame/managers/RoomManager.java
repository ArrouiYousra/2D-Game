package com.tlse1.twodgame.managers;

import com.tlse1.twodgame.entities.Enemy;
import com.tlse1.twodgame.entities.Player;
import com.tlse1.twodgame.rooms.Room;
import com.tlse1.twodgame.utils.Difficulty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gère la progression entre les salles et la génération des salles.
 */
public class RoomManager {
    
    private Difficulty difficulty;
    private int currentRoomNumber;
    private Room currentRoom;
    private Player player;
    
    // Toutes les salles créées (pour permettre de revenir en arrière)
    private Map<Integer, Room> rooms;
    
    // Dimensions de base d'une salle (augmentent progressivement)
    private static final float BASE_ROOM_WIDTH = 640f;
    private static final float BASE_ROOM_HEIGHT = 480f;
    private static final float ROOM_SIZE_INCREMENT = 100f; // Augmentation par salle
    
    // Paramètres d'ennemis
    private static final float BASE_ENEMY_SPEED = 80f;
    private static final int BASE_ENEMY_HEALTH = 50;
    private static final int BASE_ENEMY_COUNT = 3;
    
    /**
     * Constructeur
     * @param difficulty Difficulté choisie
     * @param player Référence au joueur
     */
    public RoomManager(Difficulty difficulty, Player player) {
        this.difficulty = difficulty;
        this.player = player;
        this.currentRoomNumber = 0;
        this.rooms = new HashMap<>();
    }
    
    /**
     * Initialise la première salle
     */
    public void initialize() {
        currentRoomNumber = 1;
        createRoom(currentRoomNumber);
    }
    
    /**
     * Crée une nouvelle salle
     * @param roomNumber Numéro de la salle
     */
    private void createRoom(int roomNumber) {
        // Calculer la taille de la salle (augmente progressivement)
        float roomWidth = BASE_ROOM_WIDTH + (roomNumber - 1) * ROOM_SIZE_INCREMENT;
        float roomHeight = BASE_ROOM_HEIGHT + (roomNumber - 1) * ROOM_SIZE_INCREMENT;
        
        // Créer la salle
        Room room = new Room(roomNumber, roomWidth, roomHeight, player);
        
        // Générer les ennemis pour cette salle
        generateEnemies(room, roomNumber);
        
        // Stocker la salle
        rooms.put(roomNumber, room);
        currentRoom = room;
        
        // Téléporter le joueur à la position de spawn
        player.setX(room.getPlayerSpawnX());
        player.setY(room.getPlayerSpawnY());
    }
    
    /**
     * Génère les ennemis pour une salle
     * @param room La salle
     * @param roomNumber Numéro de la salle
     */
    private void generateEnemies(Room room, int roomNumber) {
        // Calculer le nombre d'ennemis (progression)
        int enemyCount = BASE_ENEMY_COUNT + (roomNumber - 1);
        
        // Calculer les stats des ennemis (augmentent avec le numéro de salle)
        float difficultyMultiplier = 1.0f + (roomNumber - 1) * 0.15f; // +15% par salle
        float enemySpeed = BASE_ENEMY_SPEED * difficultyMultiplier;
        int enemyHealth = (int)(BASE_ENEMY_HEALTH * difficultyMultiplier);
        
        // Si la salle a déjà été visitée, les ennemis sont plus forts
        if (room.hasBeenVisited()) {
            float revisitMultiplier = 1.5f; // +50% de difficulté supplémentaire
            enemySpeed *= revisitMultiplier;
            enemyHealth = (int)(enemyHealth * revisitMultiplier);
        }
        
        // Créer les ennemis à des positions aléatoires dans la salle
        float roomWidth = room.getWidth();
        float roomHeight = room.getHeight();
        
        for (int i = 0; i < enemyCount; i++) {
            // Position aléatoire (éviter les bords et le centre où spawn le joueur)
            float minX = 100f;
            float maxX = roomWidth - 100f;
            float minY = 150f; // Éviter la zone de la porte d'entrée
            float maxY = roomHeight - 150f; // Éviter la zone de la porte de sortie
            
            float enemyX = minX + (float)(Math.random() * (maxX - minX));
            float enemyY = minY + (float)(Math.random() * (maxY - minY));
            
            Enemy enemy = new Enemy(enemyX, enemyY, enemySpeed, enemyHealth, player);
            room.addEnemy(enemy);
        }
    }
    
    /**
     * Passe à la salle suivante
     * @return true si on peut passer à la salle suivante, false si c'est la dernière salle
     */
    public boolean nextRoom() {
        if (currentRoomNumber >= difficulty.getMaxRooms()) {
            return false; // Dernière salle atteinte
        }
        
        // Marquer la salle actuelle comme visitée
        if (currentRoom != null) {
            currentRoom.markAsVisited();
        }
        
        // Passer à la salle suivante
        currentRoomNumber++;
        
        // Vérifier si la salle existe déjà (si on revient en arrière)
        if (rooms.containsKey(currentRoomNumber)) {
            currentRoom = rooms.get(currentRoomNumber);
            // Réinitialiser les ennemis avec des stats améliorées
            currentRoom.clearEnemies();
            generateEnemies(currentRoom, currentRoomNumber);
        } else {
            // Créer une nouvelle salle
            createRoom(currentRoomNumber);
        }
        
        return true;
    }
    /**
     * Retourne à la salle précédente
     * @return true si on peut revenir en arrière, false si on est à la première salle
     */
    public boolean previousRoom() {
        if (currentRoomNumber <= 1) {
            return false; // Première salle
        }
        
        // Marquer la salle actuelle comme visitée
        if (currentRoom != null) {
            currentRoom.markAsVisited();
        }
        
        // Revenir à la salle précédente
        currentRoomNumber--;
        currentRoom = rooms.get(currentRoomNumber);
        
        // Si on revient dans une salle déjà visitée, réinitialiser les ennemis
        if (currentRoom.hasBeenVisited()) {
            currentRoom.clearEnemies();
            generateEnemies(currentRoom, currentRoomNumber);
        }
        
        // Téléporter le joueur devant la porte d'entrée
        player.setX(currentRoom.getPlayerSpawnX());
        player.setY(currentRoom.getPlayerSpawnY());
        
        return true;
    }
    
    /**
     * Vérifie si on est à la dernière salle
     */
    public boolean isLastRoom() {
        return currentRoomNumber >= difficulty.getMaxRooms();
    }
    
    /**
     * Met à jour la salle actuelle
     */
    public void update(float deltaTime) {
        if (currentRoom != null) {
            currentRoom.update(deltaTime);
        }
    }
    
    /**
     * Dessine la salle actuelle
     */
    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        if (currentRoom != null) {
            currentRoom.render(batch);
        }
    }
    
    /**
     * Libère les ressources
     */
    public void dispose() {
        for (Room room : rooms.values()) {
            if (room != null) {
                room.dispose();
            }
        }
        rooms.clear();
    }
    
    // Getters et Setters
    public Room getCurrentRoom() {
        return currentRoom;
    }
    
    public int getCurrentRoomNumber() {
        return currentRoomNumber;
    }
    
    public int getMaxRooms() {
        return difficulty.getMaxRooms();
    }
    
    public Difficulty getDifficulty() {
        return difficulty;
    }
}
