package com.tlse1.twodgame.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe pour charger et gérer le mapping JSON des éléments de Settings.png.
 * Permet de découper automatiquement les sprites selon les coordonnées définies dans le JSON.
 */
public class SettingsMapping {
    
    private Texture settingsTexture;
    private JsonValue mappingData;
    
    // TextureRegions pour les screens
    private Map<String, TextureRegion> screens;
    
    // TextureRegions pour les boutons
    private Map<String, TextureRegion> buttons;
    
    // TextureRegions pour les icônes
    private Map<String, TextureRegion> icons;
    
    // TextureRegions pour les éléments interactifs
    private Map<String, TextureRegion> interactiveElements;
    
    // Tous les sprites chargés (par nom de sprite)
    private Map<String, TextureRegion> allSprites;
    
    // Mapping des noms sémantiques vers les noms de sprites
    private Map<String, String> spriteNameMapping;
    
    // Informations sur les zones cliquables (pour les interactions)
    private Map<String, ClickableArea> clickableAreas;
    
    /**
     * Classe interne pour représenter une zone cliquable
     */
    public static class ClickableArea {
        public String name;
        public float x;
        public float y;
        public float width;
        public float height;
        public String type; // "button", "slider", "icon", etc.
        
        public ClickableArea(String name, float x, float y, float width, float height, String type) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.type = type;
        }
        
        /**
         * Vérifie si un point (souris) est dans cette zone
         */
        public boolean contains(float mouseX, float mouseY) {
            return mouseX >= x && mouseX <= x + width &&
                   mouseY >= y && mouseY <= y + height;
        }
    }
    
    /**
     * Charge le mapping depuis le fichier JSON
     */
    public SettingsMapping() {
        screens = new HashMap<>();
        buttons = new HashMap<>();
        icons = new HashMap<>();
        interactiveElements = new HashMap<>();
        allSprites = new HashMap<>();
        spriteNameMapping = new HashMap<>();
        clickableAreas = new HashMap<>();
        
        loadMapping();
    }
    
    /**
     * Charge le fichier JSON de mapping
     */
    private void loadMapping() {
        try {
            JsonReader jsonReader = new JsonReader();
            
            // Charger d'abord tous les sprites depuis settings_sprites.json
            JsonValue spritesData = jsonReader.parse(Gdx.files.internal("gui/settings_sprites.json"));
            loadAllSprites(spritesData);
            
            // Charger le mapping des noms sémantiques (si disponible)
            try {
                JsonValue nameMapping = jsonReader.parse(Gdx.files.internal("gui/settings_sprite_names.json"));
                loadSpriteNameMapping(nameMapping);
            } catch (Exception e) {
                Gdx.app.log("SettingsMapping", "Fichier settings_sprite_names.json non trouvé, utilisation des noms de sprites directement");
            }
            
            // Créer les mappings sémantiques depuis les noms de sprites
            createSemanticMappings();
            
            Gdx.app.log("SettingsMapping", String.format(
                "Mapping chargé: %d sprites, %d screens, %d boutons, %d icônes",
                allSprites.size(), screens.size(), buttons.size(), icons.size()));
        } catch (Exception e) {
            Gdx.app.error("SettingsMapping", "Erreur lors du chargement du mapping: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Charge tous les sprites depuis le fichier JSON
     */
    private void loadAllSprites(JsonValue spritesData) {
        // Charger la texture (on suppose qu'elle est dans gui/PNG/Settings.png)
        settingsTexture = new Texture(Gdx.files.internal("gui/PNG/Settings.png"));
        settingsTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        
        // Parcourir tous les sprites dans le tableau JSON
        for (int i = 0; i < spritesData.size; i++) {
            JsonValue sprite = spritesData.get(i);
            String name = sprite.getString("name");
            int x = sprite.getInt("x");
            int y = sprite.getInt("y");
            int width = sprite.getInt("width");
            int height = sprite.getInt("height");
            
            // Créer la TextureRegion
            TextureRegion region = new TextureRegion(settingsTexture, x, y, width, height);
            allSprites.put(name, region);
            
            Gdx.app.debug("SettingsMapping", String.format(
                "Sprite '%s' chargé: %dx%d à (%d,%d)", name, width, height, x, y));
        }
    }
    
    /**
     * Charge le mapping des noms sémantiques vers les noms de sprites
     */
    private void loadSpriteNameMapping(JsonValue nameMapping) {
        // Charger les screens
        JsonValue screensMapping = nameMapping.get("screens");
        if (screensMapping != null) {
            for (JsonValue screen = screensMapping.child(); screen != null; screen = screen.next()) {
                String semanticName = screen.name();
                String spriteName = screen.asString();
                spriteNameMapping.put("screen." + semanticName, spriteName);
            }
        }
        
        // Charger les boutons
        JsonValue buttonsMapping = nameMapping.get("buttons");
        if (buttonsMapping != null) {
            for (JsonValue button = buttonsMapping.child(); button != null; button = button.next()) {
                String semanticName = button.name();
                String spriteName = button.asString();
                spriteNameMapping.put("button." + semanticName, spriteName);
            }
        }
        
        // Charger les icônes
        JsonValue iconsMapping = nameMapping.get("icons");
        if (iconsMapping != null) {
            for (JsonValue icon = iconsMapping.child(); icon != null; icon = icon.next()) {
                String semanticName = icon.name();
                String spriteName = icon.asString();
                spriteNameMapping.put("icon." + semanticName, spriteName);
            }
        }
    }
    
    /**
     * Crée les mappings sémantiques depuis les noms de sprites
     */
    private void createSemanticMappings() {
        // Mapper les screens (sprite1, sprite2, sprite3 sont probablement les 3 screens)
        if (allSprites.containsKey("sprite1")) {
            screens.put("emptyWithX", allSprites.get("sprite1"));
        }
        if (allSprites.containsKey("sprite2")) {
            screens.put("withoutButtons", allSprites.get("sprite2"));
        }
        if (allSprites.containsKey("sprite3")) {
            screens.put("withButtons", allSprites.get("sprite3"));
        }
        
        // Mapper les boutons depuis le mapping de noms (si disponible)
        for (Map.Entry<String, String> entry : spriteNameMapping.entrySet()) {
            String key = entry.getKey();
            String spriteName = entry.getValue();
            
            if (key.startsWith("button.")) {
                String buttonName = key.substring(7); // Enlever "button."
                if (allSprites.containsKey(spriteName)) {
                    buttons.put(buttonName, allSprites.get(spriteName));
                }
            } else if (key.startsWith("icon.")) {
                String iconName = key.substring(5); // Enlever "icon."
                if (allSprites.containsKey(spriteName)) {
                    icons.put(iconName, allSprites.get(spriteName));
                }
            } else if (key.startsWith("screen.")) {
                String screenName = key.substring(7); // Enlever "screen."
                if (allSprites.containsKey(spriteName)) {
                    screens.put(screenName, allSprites.get(spriteName));
                }
            }
        }
        
        // Mapper le bouton X (sprite4 semble être le bouton X)
        if (allSprites.containsKey("sprite4")) {
            buttons.put("closeX", allSprites.get("sprite4"));
        }
        
        // Mapper le bouton Save (sprite32 semble être un gros bouton)
        if (allSprites.containsKey("sprite32")) {
            buttons.put("save", allSprites.get("sprite32"));
        }
    }
    
    /**
     * Charge les TextureRegions depuis une section du JSON
     */
    private void loadRegions(JsonValue section, Map<String, TextureRegion> targetMap, String type) {
        if (section == null) {
            return;
        }
        
        for (JsonValue element = section.child(); element != null; element = element.next()) {
            String name = element.name();
            int x = element.getInt("x");
            int y = element.getInt("y");
            int width = element.getInt("width");
            int height = element.getInt("height");
            
            // Vérifier que les coordonnées sont valides (pas 0,0,0,0)
            if (width > 0 && height > 0) {
                TextureRegion region = new TextureRegion(settingsTexture, x, y, width, height);
                targetMap.put(name, region);
                
                // Créer une zone cliquable (les coordonnées seront ajustées lors du rendu)
                clickableAreas.put(name, new ClickableArea(name, x, y, width, height, type));
                
                Gdx.app.log("SettingsMapping", String.format(
                    "%s '%s' chargé: %dx%d à (%d,%d)", type, name, width, height, x, y));
            } else {
                Gdx.app.log("SettingsMapping", String.format(
                    "%s '%s' ignoré (coordonnées non définies)", type, name));
            }
        }
    }
    
    /**
     * Retourne un screen par son nom
     */
    public TextureRegion getScreen(String name) {
        return screens.get(name);
    }
    
    /**
     * Retourne un bouton par son nom
     */
    public TextureRegion getButton(String name) {
        return buttons.get(name);
    }
    
    /**
     * Retourne une icône par son nom
     */
    public TextureRegion getIcon(String name) {
        return icons.get(name);
    }
    
    /**
     * Retourne un élément interactif par son nom
     */
    public TextureRegion getInteractiveElement(String name) {
        return interactiveElements.get(name);
    }
    
    /**
     * Retourne un sprite par son nom (sprite1, sprite2, etc.)
     */
    public TextureRegion getSprite(String spriteName) {
        return allSprites.get(spriteName);
    }
    
    /**
     * Retourne tous les sprites
     */
    public Map<String, TextureRegion> getAllSprites() {
        return allSprites;
    }
    
    /**
     * Retourne toutes les zones cliquables
     */
    public Map<String, ClickableArea> getClickableAreas() {
        return clickableAreas;
    }
    
    /**
     * Retourne une zone cliquable par son nom
     */
    public ClickableArea getClickableArea(String name) {
        return clickableAreas.get(name);
    }
    
    /**
     * Retourne la texture complète
     */
    public Texture getTexture() {
        return settingsTexture;
    }
    
    /**
     * Libère les ressources
     */
    public void dispose() {
        if (settingsTexture != null) {
            settingsTexture.dispose();
        }
    }
}

