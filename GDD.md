# 🎮 Game Design Document (GDD)

## 📋 Table des Matières

- [Concept](#-concept)
- [Histoire](#-histoire)
- [Style Visuel](#-style-visuel)
- [Mécaniques de Jeu](#-mécaniques-de-jeu)
- [Systèmes](#-systèmes)
- [Requirements Techniques](#-requirements-techniques)

---

## 🎯 Concept

### Pitch

**2D-Game** est un jeu d'action/aventure en vue de dessus (top-down) où le joueur incarne un guerrier explorant une carte remplie d'ennemis. Le gameplay se concentre sur le combat au corps à corps, la gestion des ressources (santé, bouclier, collectibles) et la survie face à des ennemis qui respawnent.

### Genre

- **Action/Aventure** : Combat en temps réel
- **Survie** : Gestion des ressources et survie face aux ennemis
- **Exploration** : Découverte de zones avec différents ennemis

### Public Cible

- Joueurs appréciant les jeux d'action 2D
- Joueurs recherchant un défi de survie
- Joueurs appréciant la progression et la collecte d'items

### Objectif Principal

Survivre et vaincre tous les ennemis dans les différentes zones de la carte en utilisant stratégiquement les collectibles et les potions.

---

## 📖 Histoire

### Contexte

Le joueur incarne un guerrier qui explore une zone dangereuse remplie de créatures hostiles. Chaque zone est contrôlée par des ennemis spécifiques qui défendent leur territoire.

### Progression

Le joueur doit explorer la carte, entrer dans les différentes zones, et vaincre les ennemis qui s'y trouvent. Les slimes respawnent après leur mort, créant un défi continu. Les vampires sont plus puissants et représentent des défis plus importants.

### Objectif

Survivre et éliminer tous les ennemis pour sécuriser la zone.

---

## 🎨 Style Visuel

### Esthétique

- **Style** : Pixel art 2D, vue de dessus
- **Palette de couleurs** : Tons sombres et atmosphériques
- **Résolution** : Sprites 16x16 et 32x32 pixels
- **Animations** : Animations fluides pour toutes les entités

### Assets Visuels

#### Personnages
- **Joueur** : Swordsman avec épée (sprites 64x64, rendu en 32x32)
- **Slimes** : Créatures vertes/gelées (sprites 16x16)
- **Vampires** : Créatures humanoïdes sombres (sprites 32x32)

#### Environnement
- **Carte** : Tiles 16x16 pixels
- **Layers** : Sol, ombres, reliefs, structures, structures au-dessus
- **Zones** : Définies par des layers invisibles dans la carte

#### Interface
- **Barres de statistiques** : HP (rouge) et Shield (vert)
- **Panneaux** : Character panel et Action panel (inventaire)
- **Style** : Interface pixel art cohérente avec le jeu

### Caméra

- **Type** : Orthographique, suit le joueur
- **Vue** : 180x140 pixels (zoom fixe)
- **Limites** : Caméra clampée aux bords de la carte

---

## 🎮 Mécaniques de Jeu

### Contrôles

#### Déplacement
- **Z/W/Flèche Haut** : Haut
- **S/Flèche Bas** : Bas
- **Q/A/Flèche Gauche** : Gauche
- **D/Flèche Droite** : Droite
- **Shift (maintenu)** : Courir (vitesse augmentée)

#### Actions
- **E** : Attaquer avec l'épée
- **T** : Ramasser les collectibles proches
- **1** : Utiliser Damage Boost
- **2** : Utiliser Speed Boost
- **3** : Utiliser Shield Potion
- **4** : Utiliser Heal Potion

### Système de Combat

#### Attaque du Joueur
- **Type** : Mêlée (épée)
- **Dégâts de base** : 10
- **Portée** : 25x10 pixels (horizontal) ou 10x25 pixels (vertical)
- **Cooldown** : 0.5 secondes
- **Direction** : Attaque dans la direction actuelle du joueur

#### Système de Dégâts
- **Shield d'abord** : Les dégâts touchent d'abord le shield, puis les HP
- **Mort** : Quand les HP atteignent 0

### Système de Zones

- **6 zones** : Chaque zone contient un ennemi spécifique
- **Agro** : L'ennemi s'active quand le joueur entre dans sa zone
- **Désagro** : L'ennemi retourne à sa position initiale quand le joueur sort
- **Confinement** : Les ennemis ne peuvent pas quitter leur zone

### Système de Collectibles

#### Types de Collectibles

1. **Damage Boost**
   - Effet : +2 dégâts par collectible
   - Durée : 5 secondes
   - Stack : Cumulable

2. **Speed Boost**
   - Effet : +2 vitesse par collectible
   - Durée : 5 secondes
   - Stack : Cumulable

3. **Shield Potion**
   - Effet : Restaure le shield à 50% du maximum
   - Usage : Immédiat

4. **Heal Potion**
   - Effet : +10 HP
   - Usage : Immédiat

#### Drop Rate
- **25%** de chance pour chaque type quand un ennemi meurt

### Système de Respawn

- **Slimes uniquement** : Respawn jusqu'à 2 fois (3 slimes au total par zone)
- **Délai** : 10 secondes après la mort
- **Position** : Même position initiale que le slime original

---

## ⚙️ Systèmes

### Système d'Entités

#### Joueur
- **HP** : 100 maximum
- **Shield** : 50 maximum
- **Vitesse** : 150 pixels/seconde (base)
- **Hitbox** : 13x15 pixels (centrée)

#### Ennemis

**Slimes** (Zones 1-3) :
- Zone 1 : 25 HP, 9 dégâts
- Zone 2 : 30 HP, 40 dégâts
- Zone 3 : 40 HP, 30 dégâts
- Vitesse : 37.5 pixels/seconde
- Portée de détection : 200 pixels
- Respawn : Jusqu'à 2 fois

**Vampires** (Zones 4-6) :
- Zone 4 : 50 HP, 18 dégâts
- Zone 5 : 75 HP, 30 dégâts
- Zone 6 : 125 HP, 45 dégâts
- Vitesse : 37.5 pixels/seconde
- Portée de détection : 400 pixels
- Pas de respawn

### Système d'IA

#### Comportement des Ennemis
1. **Idle** : L'ennemi reste à sa position initiale
2. **Agro** : Quand le joueur entre dans la zone, l'ennemi le poursuit
3. **Attaque** : Quand le joueur est à portée (80 pixels), l'ennemi attaque
4. **Désagro** : Quand le joueur sort de la zone, l'ennemi retourne à sa position initiale en courant
5. **Retour** : Une fois à sa position initiale, l'ennemi repasse en idle

### Système de Carte

- **Format** : JSON (`map.json`)
- **Taille** : 50x40 tuiles (800x640 pixels)
- **Tuiles** : 16x16 pixels
- **Layers** :
  - `ground` : Sol (rendu avant le joueur)
  - `shadow` : Ombres (rendu avant le joueur)
  - `relief` : Reliefs (rendu avant le joueur)
  - `structures` : Structures (rendu après le joueur)
  - `over_struct` : Structures au-dessus (rendu après le joueur)
  - `collisions` : Zones de collision (non visibles)
  - `zones` : Zones pour l'IA (non visibles)

### Système d'Inventaire

- **Capacité** : Illimitée
- **Types d'items** : 4 types (Damage Boost, Speed Boost, Shield Potion, Heal Potion)
- **Utilisation** : Touches 1-4 pour utiliser les items

### Système d'Animations

#### Animations du Joueur
- Idle, Walk, Run
- Attack, Walk Attack, Run Attack
- Hurt, Death
- **4 directions** : DOWN, UP, SIDE_LEFT, SIDE

#### Animations des Ennemis
- Idle, Walk, Run
- Attack, Hurt, Death
- **4 directions** : DOWN, UP, SIDE_LEFT, SIDE

---

## 🔧 Requirements Techniques

### Performance

- **FPS cible** : 60 FPS
- **Résolution** : Fullscreen par défaut
- **Optimisations** : Rendu uniquement des tuiles visibles

### Compatibilité

- **Java** : 17+
- **OS** : Windows, Linux, macOS
- **OpenGL** : Requis pour le rendu

### Assets

- **Format** : PNG pour les sprites
- **JSON** : Pour les configurations (animations, mappings)
- **Organisation** : Par type d'entité dans `assets/`

### Sauvegarde

- **État actuel** : Pas de système de sauvegarde
- **Futur** : Système de sauvegarde à implémenter

---

## 📊 Équilibrage

### Statistiques

#### Joueur
- HP : 100
- Shield : 50
- Dégâts : 10 (+2 par Damage Boost)
- Vitesse : 150 pixels/seconde

#### Ennemis
- Vitesse : 37.5 pixels/seconde (1/4 du joueur)
- Portée d'attaque : 80 pixels
- Cooldown d'attaque : 2.0 secondes

### Progression

- **Zones 1-3** : Slimes (difficulté croissante)
- **Zones 4-6** : Vampires (difficulté croissante)
- **Respawn** : Les slimes respawnent pour créer un défi continu

---

## 🎯 Objectifs de Design

### Expérience de Jeu

- **Action rapide** : Combat fluide et réactif
- **Stratégie** : Gestion des collectibles et timing des potions
- **Défi** : Ennemis qui respawnent créent une tension continue
- **Exploration** : Découverte des différentes zones

### Sensations

- **Satisfaction** : Vaincre un ennemi et obtenir des collectibles
- **Tension** : Gestion de la santé et du shield
- **Progression** : Exploration de nouvelles zones
- **Défi** : Survie face aux ennemis qui respawnent

---

## 📝 Notes de Design

- Le système de zones limite le comportement des ennemis pour un gameplay plus contrôlé
- Les slimes respawnent pour créer un défi continu sans surcharger la carte
- Le système de shield/HP ajoute une couche de stratégie
- Les boosts temporaires encouragent une utilisation stratégique des collectibles
- L'inventaire illimité permet de stocker des ressources pour les moments critiques

