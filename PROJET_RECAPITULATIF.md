# 📋 Récapitulatif du Projet 2D-Game

## 🎮 Vue d'ensemble

Jeu 2D développé avec **libGDX** en Java. Le joueur progresse à travers des salles en combattant des ennemis (zombies) pour avancer.

---

## 🏗️ Architecture du Projet

### Structure des packages

```
com.tlse1.twodgame/
├── entities/          # Entités du jeu
├── screens/           # Écrans (Menu, Game, Settings)
├── managers/          # Gestionnaires (Room, Asset)
├── rooms/             # Système de salles
├── ui/                # Interface utilisateur (HUD, HealthBar)
├── utils/             # Utilitaires (Direction, Difficulty, etc.)
└── weapons/           # Système d'armes
```

---

## ✅ Fonctionnalités Implémentées

### 1. **Système d'Entités** (`entities/`)

#### `Entity` (classe de base)
- Position (x, y), dimensions (width, height)
- Méthodes de base : `render()`, `dispose()`, `clampToBounds()`

#### `AnimatedEntity` (hérite de `Entity`)
- Gestion des animations (Idle, Run, Shoot)
- Support 4 directions : DOWN, UP, SIDE, SIDE_LEFT
- Chargement automatique depuis sprite sheets
- Gestion du temps d'animation et des états

#### `Character` (hérite de `AnimatedEntity`)
- Système de santé (health, maxHealth)
- Gestion des animations par direction
- Support des armes (hasWeapon)
- Chemins configurables pour les assets

#### `Player` (hérite de `Character`)
- ✅ **Contrôle clavier** : WASD / Flèches directionnelles
- ✅ **Contrôle souris** : Direction selon position de la souris
- ✅ **Système de tir** : Clic gauche ou touche E
- ✅ **Animations avec épée** : Idle, Walk, Attack (4 directions)
- ✅ **Limites d'écran** : Le joueur reste dans les bounds
- Assets utilisés : `freebase4directionmalecharacter/PNG/Sword/Without_shadow/`

#### `Enemy` (hérite de `Character`)
- ✅ **IA de poursuite** : Suit le joueur dans un rayon de détection (300px)
- ✅ **Animations zombies** : Idle et Walk (4 directions)
- ✅ **Vitesse et santé configurables**
- Assets utilisés : `PostApocalypse_AssetPack_v1.1.2/Enemies/Zombie_Small/`

#### `Door` (hérite de `Entity`)
- ✅ **Portes d'entrée et de sortie**
- ✅ **Système d'ouverture** : La porte de sortie s'ouvre quand tous les ennemis sont morts
- ✅ **Collisions** : Détection de collision avec le joueur

---

### 2. **Système de Salles** (`rooms/` et `managers/`)

#### `Room`
- ✅ **Génération de salles** : Largeur/hauteur configurables
- ✅ **Portes** : Entrée (bas) et sortie (haut)
- ✅ **Gestion des ennemis** : Liste d'ennemis par salle
- ✅ **État de la salle** : `isCleared`, `hasBeenVisited`
- ✅ **Position de spawn** : Point d'apparition du joueur

#### `RoomManager`
- ✅ **Progression entre salles** : Système de salles numérotées
- ✅ **Génération dynamique** : Crée les salles à la demande
- ✅ **Difficulté progressive** :
  - Taille des salles augmente (+100px par salle)
  - Nombre d'ennemis augmente (+1 par salle)
  - Stats des ennemis augmentent (+15% par salle)
- ✅ **Système de difficulté** : EASY, MEDIUM, HARD (nombre de salles différent)
- ✅ **Retour en arrière** : Possibilité de revenir aux salles précédentes
- ✅ **Réinitialisation** : Ennemis plus forts si on revient dans une salle visitée

---

### 3. **Interface Utilisateur** (`ui/`)

#### `HealthBar`
- ✅ **Barre de santé visuelle** : Fond + remplissage proportionnel
- ✅ **Assets utilisés** : `PostApocalypse_AssetPack_v1.1.2/UI/HP/`
- ✅ **Positionnement** : En haut à gauche de l'écran
- ✅ **Mise à jour dynamique** : Se met à jour selon la santé du joueur

#### `HUD`
- ✅ **Affichage du HUD** : Barre de santé intégrée
- ✅ **Référence au joueur** : Récupère les stats en temps réel
- ✅ **Responsive** : S'adapte au redimensionnement de l'écran
- ⏳ **À venir** : Barre d'XP

---

### 4. **Écrans** (`screens/`)

#### `MenuScreen`
- ✅ **Menu principal** : Titre "Ruins of the fallen"
- ✅ **Boutons interactifs** :
  - Play (Jouer)
  - Settings (Paramètres)
  - Quit (Quitter)
- ✅ **Textures de boutons** : États pressed/not-pressed
- ✅ **Navigation** : ESPACE/ENTER pour jouer
- Assets utilisés : `PostApocalypse_AssetPack_v1.1.2/UI/Menu/Main Menu/`

#### `GameScreen`
- ✅ **Écran de jeu principal** : Gère toute la logique de jeu
- ✅ **Gestion des salles** : Utilise `RoomManager`
- ✅ **Système de transition** : Transitions entre salles (`RoomTransition`)
- ✅ **HUD intégré** : Affiche la barre de santé
- ✅ **Retour au menu** : Touche ÉCHAP
- ✅ **Caméra** : OrthographicCamera pour le rendu

#### `SettingsScreen`
- ✅ **Écran de paramètres** : Structure de base créée
- ⏳ **À implémenter** : Options de jeu

---

### 5. **Utilitaires** (`utils/`)

#### `Direction`
- ✅ **Enum des directions** : DOWN, UP, SIDE, SIDE_LEFT
- Utilisé pour les animations et le mouvement

#### `Difficulty`
- ✅ **Niveaux de difficulté** : EASY (5 salles), MEDIUM (10 salles), HARD (15 salles)
- Utilisé par `RoomManager` pour la progression

#### `RoomTransition`
- ✅ **Système de transition** : Transitions visuelles entre salles
- Gère les animations de transition

#### `AnimationController`
- ✅ **Contrôleur d'animations** : Gestion avancée des animations
- Utilisé par `AnimatedEntity`

---

### 6. **Système d'Armes** (`weapons/`)

#### `Weapon`
- ✅ **Classe de base** : Structure pour les armes
- ✅ **Types d'armes** : GUN, PISTOL, SHOTGUN (enum)
- ⏳ **À implémenter** : Dégâts, portée, cadence de tir

---

### 7. **Gestionnaires** (`managers/`)

#### `AssetManager`
- ⏳ **À implémenter** : Gestion centralisée des assets

#### `RoomManager`
- ✅ **Implémenté** : Voir section "Système de Salles"

---

## 🎨 Assets Utilisés

### Assets du joueur
- **Chemin** : `freebase4directionmalecharacter/PNG/Sword/Without_shadow/`
- **Fichiers** :
  - `Sword_Idle_without_shadow.png`
  - `Sword_Walk_without_shadow.png`
  - `Sword_attack_without_shadow.png`

### Assets des ennemis
- **Chemin** : `PostApocalypse_AssetPack_v1.1.2/Enemies/Zombie_Small/`
- **Format** : `Zombie_Small_{Direction}_{Action}-Sheet6.png`
- **Directions** : Down, Up, Side, Side-left
- **Actions** : Idle, walk/Walk

### Assets UI
- **Menu** : `PostApocalypse_AssetPack_v1.1.2/UI/Menu/Main Menu/`
  - `Play_Not-Pressed.png` / `Play_Pressed.png`
  - `Settings_Not-Pressed.png` / `Settings_Pressed.png`
  - `Quit_Not-Pressed.png` / `Quit_Pressed.png`
- **HP** : `PostApocalypse_AssetPack_v1.1.2/UI/HP/`
  - `HP-Bar.png` (fond)
  - `HP.png` (remplissage)

---

## 🔧 Points Techniques Importants

### Système d'animations
- Chargement automatique depuis sprite sheets 4 directions
- Détection automatique de la largeur des frames
- Gestion des états : Idle, Run, Shoot
- Support de 4 directions avec animations séparées

### Gestion de la mémoire
- Méthode `dispose()` sur toutes les entités
- Libération des textures après utilisation
- Liste de textures dans `Character` pour cleanup

### Collisions
- Détection de collision avec les portes
- Limitation des entités dans les bounds (écran/salle)
- Collision joueur-porte pour changer de salle

---

## 📝 Fonctionnalités À Implémenter

### Priorité Haute
1. **Système de combat**
   - Dégâts du joueur sur les ennemis
   - Dégâts des ennemis sur le joueur
   - Animation d'attaque fonctionnelle
   - Système de mort (joueur et ennemis)

2. **Système de progression**
   - Barre d'XP dans le HUD
   - Système de niveaux
   - Points d'expérience par ennemi tué

3. **Caméra qui suit le joueur**
   - Caméra qui suit le joueur dans la salle
   - Limites de la caméra selon la taille de la salle

### Priorité Moyenne
4. **Système d'inventaire**
   - Ramassage d'objets (Pick-up sprites)
   - Inventaire avec slots
   - Utilisation d'objets

5. **Amélioration de l'IA**
   - Pathfinding pour les ennemis
   - Patrouille des ennemis
   - Types d'ennemis différents

6. **Système de sauvegarde**
   - Sauvegarde de la progression
   - Checkpoints

### Priorité Basse
7. **Audio**
   - Effets sonores
   - Musique de fond
   - Gestionnaire audio

8. **Améliorations visuelles**
   - Effet de parallaxe
   - Particules
   - Animations de mort

---

## 🌿 Branches Potentielles (selon BRANCH_NAMING_GUIDE.md)

### Branches suggérées pour les prochaines features :

1. **`feature/combat/degats-system`**
   - Système de dégâts joueur → ennemis
   - Système de dégâts ennemis → joueur
   - Mort des entités

2. **`feature/map/camera-system`**
   - Caméra qui suit le joueur
   - Limites de caméra selon la salle

3. **`feature/gameplay/xp-system`**
   - Barre d'XP dans le HUD
   - Calcul d'XP par ennemi tué
   - Système de niveaux

4. **`feature/gameplay/pickup-items`**
   - Ramassage d'objets
   - Sprites Pick-up

5. **`feature/ai/pathfinding`**
   - Pathfinding pour les ennemis
   - Éviter les obstacles

6. **`feature/ui/xp-bar`**
   - Ajout de la barre d'XP au HUD

---

## 📊 État Actuel du Code

### ✅ Fonctionnel
- Menu principal avec navigation
- Joueur contrôlable (clavier + souris)
- Ennemis avec IA de poursuite
- Système de salles avec progression
- Transitions entre salles
- HUD avec barre de santé
- Animations 4 directions pour joueur et ennemis

### ⚠️ Partiellement Implémenté
- Système d'armes (structure de base seulement)
- Système de tir (animation mais pas de dégâts)
- AssetManager (classe vide)

### ❌ Non Implémenté
- Système de dégâts
- Système de mort
- Caméra qui suit le joueur
- Système d'XP
- Inventaire
- Sauvegarde
- Audio

---

## 🎯 Prochaines Étapes Recommandées

1. **Implémenter le système de combat** (dégâts, mort)
2. **Ajouter la caméra qui suit le joueur**
3. **Implémenter le système d'XP et de niveaux**
4. **Améliorer l'IA des ennemis** (pathfinding)
5. **Ajouter le ramassage d'objets**

---

## 📁 Fichiers Clés

### Points d'entrée
- `MainGame.java` : Point d'entrée simple (test)
- `TwoDGame.java` : Point d'entrée principal avec écrans
- `GameScreen.java` : Écran de jeu principal

### Classes principales
- `Player.java` : Logique du joueur
- `Enemy.java` : Logique des ennemis
- `RoomManager.java` : Gestion de la progression
- `Room.java` : Structure d'une salle
- `HUD.java` : Interface utilisateur

### Utilitaires
- `Direction.java` : Directions pour animations
- `Difficulty.java` : Niveaux de difficulté
- `AnimatedEntity.java` : Base pour entités animées

---

*Dernière mise à jour : Après nettoyage du dossier assets*

