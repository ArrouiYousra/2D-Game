package com.tlse1.twodgame.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant l'inventaire du joueur.
 * Gère les items collectés (shields, heals, etc.)
 */
public class Inventory {
    
    /**
     * Types d'items disponibles
     */
    public enum ItemType {
        SHIELD,  // Item pour restaurer le shield
        HEAL     // Item pour restaurer la santé
    }
    
    /**
     * Classe représentant un item dans l'inventaire
     */
    public static class Item {
        private ItemType type;
        
        public Item(ItemType type) {
            this.type = type;
        }
        
        public ItemType getType() {
            return type;
        }
    }
    
    // Liste des items dans l'inventaire
    private List<Item> items;
    
    // Capacité maximale de l'inventaire (sprite1 fait 168x20, on peut mettre plusieurs items)
    private int maxCapacity = 10;
    
    public Inventory() {
        this.items = new ArrayList<>();
    }
    
    /**
     * Ajoute un item à l'inventaire.
     * 
     * @param item L'item à ajouter
     * @return true si l'item a été ajouté, false si l'inventaire est plein
     */
    public boolean addItem(Item item) {
        if (items.size() >= maxCapacity) {
            return false;
        }
        items.add(item);
        return true;
    }
    
    /**
     * Ajoute un item par type.
     * 
     * @param type Le type d'item à ajouter
     * @return true si l'item a été ajouté, false si l'inventaire est plein
     */
    public boolean addItem(ItemType type) {
        return addItem(new Item(type));
    }
    
    /**
     * Utilise le premier item du type spécifié.
     * 
     * @param type Le type d'item à utiliser
     * @return true si un item a été utilisé, false si aucun item de ce type n'est disponible
     */
    public boolean useItem(ItemType type) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getType() == type) {
                items.remove(i);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Compte le nombre d'items d'un type donné.
     * 
     * @param type Le type d'item
     * @return Le nombre d'items de ce type
     */
    public int getItemCount(ItemType type) {
        int count = 0;
        for (Item item : items) {
            if (item.getType() == type) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Retourne le nombre total d'items dans l'inventaire.
     * 
     * @return Le nombre d'items
     */
    public int getItemCount() {
        return items.size();
    }
    
    /**
     * Vérifie si l'inventaire est plein.
     * 
     * @return true si l'inventaire est plein
     */
    public boolean isFull() {
        return items.size() >= maxCapacity;
    }
    
    /**
     * Vide l'inventaire.
     */
    public void clear() {
        items.clear();
    }
    
    /**
     * Retourne tous les items.
     * 
     * @return La liste des items
     */
    public List<Item> getItems() {
        return new ArrayList<>(items);
    }
    
    /**
     * Définit la capacité maximale de l'inventaire.
     * 
     * @param maxCapacity La nouvelle capacité maximale
     */
    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
    
    /**
     * Retourne la capacité maximale de l'inventaire.
     * 
     * @return La capacité maximale
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }
}

