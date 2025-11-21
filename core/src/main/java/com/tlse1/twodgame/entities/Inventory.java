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
        DAMAGE_BOOST,   // Collectible pour augmenter les dégâts (+10)
        SPEED_BOOST,    // Collectible pour augmenter la vitesse (+10)
        SHIELD_POTION,  // Potion pour restaurer le shield (moitié du max)
        HEAL_POTION     // Potion pour restaurer la santé (+10 HP)
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
    
    public Inventory() {
        this.items = new ArrayList<>();
    }
    
    /**
     * Ajoute un item à l'inventaire.
     * L'inventaire peut contenir un nombre illimité d'items.
     * 
     * @param item L'item à ajouter
     * @return true si l'item a été ajouté (toujours true)
     */
    public boolean addItem(Item item) {
        items.add(item);
        return true;
    }
    
    /**
     * Ajoute un item par type.
     * L'inventaire peut contenir un nombre illimité d'items.
     * 
     * @param type Le type d'item à ajouter
     * @return true si l'item a été ajouté (toujours true)
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
     * L'inventaire peut contenir un nombre illimité d'items, donc cette méthode retourne toujours false.
     * 
     * @return false (l'inventaire n'est jamais plein)
     */
    public boolean isFull() {
        return false; // Capacité illimitée
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
     * Cette méthode est conservée pour compatibilité mais n'a plus d'effet (capacité illimitée).
     * 
     * @param maxCapacity La nouvelle capacité maximale (ignorée)
     */
    public void setMaxCapacity(int maxCapacity) {
        // Capacité illimitée, cette méthode n'a plus d'effet
    }
    
    /**
     * Retourne la capacité maximale de l'inventaire.
     * L'inventaire a une capacité illimitée, donc cette méthode retourne Integer.MAX_VALUE.
     * 
     * @return Integer.MAX_VALUE (capacité illimitée)
     */
    public int getMaxCapacity() {
        return Integer.MAX_VALUE; // Capacité illimitée
    }
}

