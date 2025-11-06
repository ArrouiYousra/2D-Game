package com.tlse1.twodgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Classe représentant une porte dans le jeu.
 * Une porte peut être ouverte ou fermée, et peut être une porte d'entrée ou de sortie.
 */
public class Door extends Entity {
    
    public enum DoorType {
        ENTRANCE,  // Porte d'entrée (toujours ouverte)
        EXIT       // Porte de sortie (s'ouvre quand tous les ennemis sont tués)
    }
    
    private DoorType type;
    private boolean isOpen;
    private Texture closedTexture;
    private Texture openTexture;
    private TextureRegion currentTexture;
    
    // Dimensions par défaut d'une porte
    private static final float DEFAULT_DOOR_WIDTH = 64f;
    private static final float DEFAULT_DOOR_HEIGHT = 64f;
    
    /**
     * Constructeur pour une porte
     * @param x Position X
     * @param y Position Y
     * @param type Type de porte (ENTRANCE ou EXIT)
     */
    public Door(float x, float y, DoorType type) {
        super(x, y, 0, DEFAULT_DOOR_WIDTH, DEFAULT_DOOR_HEIGHT);
        this.type = type;
        this.isOpen = (type == DoorType.ENTRANCE); // Les portes d'entrée sont toujours ouvertes
        
        loadTextures();
        updateTexture();
    }
    
    /**
     * Charge les textures de la porte
     */
    private void loadTextures() {
        // Utiliser les sprites Wire-Fence pour l'instant
        // Porte fermée
        closedTexture = new Texture(Gdx.files.internal("PostApocalypse_AssetPack_v1.1.2/Tiles/Wire-Fence/Wire-Fence_Gate_Lock.png"));
        
        // Porte ouverte
        openTexture = new Texture(Gdx.files.internal("PostApocalypse_AssetPack_v1.1.2/Tiles/Wire-Fence/Wire-Fence_Opening_No-Lock_Sheet8.png"));
        
        // Initialiser la texture actuelle
        currentTexture = new TextureRegion(closedTexture);
    }
    
    /**
     * Met à jour la texture selon l'état de la porte
     */
    private void updateTexture() {
        if (isOpen) {
            currentTexture = new TextureRegion(openTexture);
        } else {
            currentTexture = new TextureRegion(closedTexture);
        }
    }
    
    /**
     * Ouvre la porte
     */
    public void open() {
        if (!isOpen) {
            isOpen = true;
            updateTexture();
        }
    }
    
    /**
     * Ferme la porte
     */
    public void close() {
        if (isOpen && type == DoorType.EXIT) {
            isOpen = false;
            updateTexture();
        }
    }
    
    /**
     * Vérifie si le joueur peut passer à travers la porte
     */
    public boolean canPassThrough() {
        return isOpen;
    }
    
    /**
     * Vérifie si une entité est en collision avec la porte
     */
    public boolean collidesWithEntity(Entity entity) {
        return collidesWith(entity);
    }
    
    @Override
    public void update(float deltaTime) {
        // Les portes sont statiques, pas besoin de mise à jour
    }
    
    @Override
    public void render(SpriteBatch batch) {
        if (currentTexture != null) {
            batch.draw(currentTexture, x, y, width, height);
        }
    }
    
    @Override
    public void dispose() {
        if (closedTexture != null) {
            closedTexture.dispose();
        }
        if (openTexture != null) {
            openTexture.dispose();
        }
    }
    
    // Getters et Setters
    public DoorType getType() {
        return type;
    }
    
    public boolean isOpen() {
        return isOpen;
    }
}
