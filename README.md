# 🎮 2D-Game

Jeu d'action/aventure 2D développé avec **LibGDX** en Java.

---

## 📋 Table des Matières

- [Requirements](#-requirements)
- [Installation](#-installation)
- [Architecture](#-architecture)
- [Déploiement](#-déploiement)
- [Documentation](#-documentation)

---

## 🔧 Requirements

### Prérequis Système

- **Java** : Version 17 ou supérieure
- **Gradle** : Inclus dans le projet via le wrapper (Gradle 8.x)
- **OS** : Windows, Linux, ou macOS

### Dépendances

Le projet utilise les dépendances suivantes (définies dans `gradle.properties`) :

- **LibGDX** : 1.14.0
- **Box2D** : Pour la physique (si nécessaire)
- **Anim8** : 0.5.4
- **SquidLib** : 3.0.6
- **Artemis ODB** : 2.3.0

### Outils de Développement

- **IDE** : IntelliJ IDEA, Eclipse, ou VS Code
- **Build Tool** : Gradle (inclus)
- **Version Control** : Git

---

## 🚀 Installation

### 1. Cloner le Projet

```bash
git clone <url-du-repo>
cd 2D-Game
```

### 2. Vérifier Java

```bash
java -version  # Doit afficher Java 17 ou supérieur
```

### 3. Compiler le Projet

```bash
# Linux/Mac
./gradlew build

# Windows
gradlew.bat build
```

### 4. Lancer le Jeu

```bash
# Linux/Mac
./gradlew :lwjgl3:run

# Windows
gradlew.bat :lwjgl3:run
```

---

## 🏗️ Architecture

### Structure du Projet

```
2D-Game/
├── assets/                    # Ressources du jeu
│   ├── gui/                  # Interface utilisateur (sprites, panels)
│   ├── map/                  # Cartes (map.json, tilesets)
│   ├── slims/                # Sprites des slimes
│   ├── swordsman1-3/         # Sprites du joueur
│   └── vampire_sprite_sheets/ # Sprites des vampires
├── core/                     # Module principal (logique du jeu)
│   └── src/main/java/com/tlse1/twodgame/
│       ├── entities/         # Entités du jeu
│       │   ├── Character.java      # Classe abstraite de base
│       │   ├── Player.java         # Joueur
│       │   ├── Enemy.java         # Ennemi de base
│       │   ├── Slime.java         # Slime (extends Enemy)
│       │   ├── Vampire.java       # Vampire (extends Enemy)
│       │   ├── Inventory.java     # Inventaire
│       │   ├── Collectible.java    # Collectibles
│       │   └── handlers/          # Handlers pour les entités
│       │       ├── AnimationHandler.java
│       │       ├── CombatHandler.java
│       │       ├── MovementHandler.java
│       │       ├── CollisionHandler.java
│       │       └── AnimationLoader.java
│       ├── screens/          # Écrans du jeu
│       │   ├── GameScreen.java    # Écran de jeu principal
│       │   ├── MenuScreen.java    # Menu principal
│       │   ├── SettingsScreen.java
│       │   └── ...
│       ├── managers/         # Gestionnaires
│       │   └── JsonMapLoader.java # Chargeur de carte JSON
│       ├── ui/               # Interface utilisateur
│       │   ├── HealthBar.java
│       │   └── ShieldBar.java
│       ├── utils/            # Utilitaires
│       │   ├── Direction.java
│       │   ├── CharacterPanelMapping.java
│       │   └── ...
│       └── TwoDGame.java     # Classe principale
├── lwjgl3/                   # Module desktop (launcher)
│   └── src/main/java/com/tlse1/twodgame/lwjgl3/
│       └── Lwjgl3Launcher.java
├── build.gradle              # Configuration Gradle principale
├── settings.gradle           # Configuration des modules
└── gradle.properties         # Propriétés Gradle
```

### Architecture Logicielle

#### Pattern : Handler System

Le projet utilise un système de handlers pour séparer les responsabilités :

```
Character (abstract)
├── AnimationHandler    → Gère toutes les animations
├── CombatHandler       → Gère santé, shield, dégâts
└── MovementHandler     → Gère mouvement et collisions
```

**Avantages** :
- Séparation des responsabilités (Single Responsibility Principle)
- Facilite les tests unitaires
- Code modulaire et maintenable

#### Hiérarchie d'Héritage

```
Character (abstract)
    ├── Player
    └── Enemy
        ├── Slime
        └── Vampire
```

**Concepts OOP utilisés** :
- **Héritage** : `Player` et `Enemy` héritent de `Character`
- **Abstraction** : `Character` est abstraite avec `loadAnimations()` abstraite
- **Polymorphisme** : `getAttackDamage()` et `calculateDirectionToTarget()` surchargées
- **Encapsulation** : Champs privés/protégés avec getters/setters

#### Gestion de la Carte

- **Format** : JSON (`map.json`)
- **Loader** : `JsonMapLoader` charge les layers et tilesets
- **Rendu** : `renderBeforePlayer()` et `renderAfterPlayer()` pour l'ordre de rendu
- **Zones** : Système de zones pour l'IA des ennemis

#### Système de Collisions

- **CollisionHandler** : Détecte les collisions entité ↔ carte
- **Hitboxes centrées** : Calcul automatique basé sur les dimensions des sprites
- **Collisions entité ↔ entité** : Détection AABB dans `GameScreen`

### Flux de Données

```
TwoDGame (main)
    ↓
GameScreen (écran principal)
    ↓
├── Player (entité contrôlée)
│   ├── AnimationHandler
│   ├── CombatHandler
│   └── MovementHandler
├── Enemies (ArrayList<Enemy>)
│   ├── Slime (3 instances)
│   └── Vampire (3 instances)
├── Collectibles (ArrayList<Collectible>)
├── JsonMapLoader (carte)
└── UI (HealthBar, ShieldBar)
```

---

## 📦 Déploiement

### Créer un JAR Exécutable

```bash
# Compiler le JAR
./gradlew :lwjgl3:jar

# Le JAR sera créé dans : lwjgl3/build/libs/
```

### Structure du JAR

Le JAR contient :
- Toutes les classes compilées
- Les assets du jeu (copiés dans le JAR)
- Les dépendances (si fat JAR)

### Distribution

Pour distribuer le jeu :

1. **Créer le JAR** : `./gradlew :lwjgl3:jar`
2. **Copier les assets** : S'assurer que les assets sont inclus
3. **Créer un launcher** : Script batch/shell pour lancer le JAR
4. **Packager** : Créer un installer ou un zip avec le JAR et les assets

### Exemple de Launcher

**Linux/Mac** (`run.sh`) :
```bash
#!/bin/bash
java -jar lwjgl3/build/libs/2D-Game-1.0.0.jar
```

**Windows** (`run.bat`) :
```batch
@echo off
java -jar lwjgl3\build\libs\2D-Game-1.0.0.jar
```

### Build pour Production

```bash
# Nettoyer et reconstruire
./gradlew clean build

# Créer le JAR
./gradlew :lwjgl3:jar

# Tests et couverture
./gradlew :core:test :core:jacocoTestReport
```

---

## 📚 Documentation

### Documentation Disponible

- **[GDD.md](GDD.md)** : Game Design Document (concept, mécaniques, histoire, style, requirements)
- **[GAMEPLAY_ENUMERATION.md](GAMEPLAY_ENUMERATION.md)** : Énumération détaillée du gameplay
- **[README_TESTING.md](README_TESTING.md)** : Guide pour les tests et la couverture de code
- **[DOCUMENTATION_GUIDE.md](DOCUMENTATION_GUIDE.md)** : Guide pour générer la JavaDoc

### Générer la JavaDoc

```bash
./gradlew :core:javadoc
# Disponible dans : core/build/docs/javadoc/index.html
```

### Tests et Couverture

```bash
# Lancer les tests
./gradlew :core:test

# Générer le rapport de couverture
./gradlew :core:jacocoTestReport

# Pipeline complet
./pipeline.sh  # ou pipeline.bat sur Windows
```

---

## 🔧 Commandes Utiles

```bash
# Compiler
./gradlew build

# Lancer le jeu
./gradlew :lwjgl3:run

# Tests
./gradlew :core:test

# Couverture
./gradlew :core:jacocoTestReport

# JavaDoc
./gradlew :core:javadoc

# JAR
./gradlew :lwjgl3:jar

# Nettoyer
./gradlew clean
```

---

## 📄 Licence

Projet dans le cadre d'un module EPITECH de pré-MSc.


## UML 

``` mermaid

classDiagram
    %% ===== ENTITÉS =====
    class Character {
        <<abstract>>
        #AnimationHandler animationHandler
        #CombatHandler combatHandler
        #MovementHandler movementHandler
        -float width
        -float height
        #float hitboxWidth
        #float hitboxHeight
        +Character()
        +Character(float x, float y)
        #loadAnimations()* void
        +update(float deltaTime) void
        +render(SpriteBatch batch) void
        +dispose() void
        +getX() float
        +getY() float
        +getWidth() float
        +getHeight() float
        +getHitboxWidth() float
        +getHitboxHeight() float
        +getHitboxX() float
        +getHitboxY() float
        +isMoving() boolean
        +isAttacking() boolean
        +isAlive() boolean
        +attack() void
        +takeDamage(int damage) void
        +getHealth() int
        +getShield() int
    }

    class Player {
        -Inventory inventory
        -Map~String, HitboxData~ attackHitboxes
        +Player()
        +Player(float x, float y)
        #loadAnimations() void
        +getInventory() Inventory
        +useShieldItem() boolean
        +useHealItem() boolean
        +getCurrentAttackHitbox() float[]
        -loadAttackHitboxes() void
        -loadHitboxFile(String filePath, String animationType, int spritesPerDirection) void
    }

    class Enemy {
        -float speed
        -Character target
        -float attackRange
        -float detectionRange
        -float hitboxWidth
        -float hitboxHeight
        -float attackCooldown
        -float attackCooldownTime
        #JsonMapLoader mapLoader
        +Enemy()
        +Enemy(float x, float y)
        #loadAnimations() void
        +setTarget(Character target) void
        +updateAI(float deltaTime) void
        #calculateDirectionToTarget(float dx, float dy) Direction
        +setMapLoader(JsonMapLoader mapLoader) void
        +createProjectileOnAttack() Projectile
        +getSpeed() float
        +setSpeed(float speed) void
        +getAttackRange() float
    }

    class Vampire {
        -int level
        +Vampire()
        +Vampire(float x, float y, int level)
        +getLevel() int
        #loadAnimations() void
        #calculateDirectionToTarget(float dx, float dy) Direction
        +createProjectileOnAttack() Projectile
    }

    class Slime {
        -int level
        +Slime()
        +Slime(float x, float y)
        +Slime(float x, float y, int level)
        #loadAnimations() void
        #calculateDirectionToTarget(float dx, float dy) Direction
    }

    class Projectile {
        -float x
        -float y
        -float width
        -float height
        -Direction direction
        -float speed
        -float damagePerSecond
        -float lifetime
        -float damageCooldown
        -boolean active
        -JsonMapLoader mapLoader
        +Projectile(float startX, float startY, Direction direction, JsonMapLoader mapLoader, float width, float height, float damagePerSecond, float speed)
        +update(float deltaTime) void
        +checkPlayerCollision(Player player) boolean
        +isActive() boolean
        +setActive(boolean active) void
        +getX() float
        +getY() float
        +getWidth() float
        +getHeight() float
        +getDirection() Direction
    }

    class Inventory {
        -List~Item~ items
        -int maxCapacity
        +Inventory()
        +addItem(Item item) boolean
        +addItem(ItemType type) boolean
        +useItem(ItemType type) boolean
        +getItemCount(ItemType type) int
        +getItemCount() int
        +isFull() boolean
        +clear() void
        +getItems() List~Item~
    }

    class Item {
        <<inner class>>
        -ItemType type
        +Item(ItemType type)
        +getType() ItemType
    }

    class ItemType {
        <<enumeration>>
        SHIELD
        HEAL
    }

    %% ===== HANDLERS =====
    class AnimationHandler {
        -Map~Direction, Animation~ idleAnimations
        -Map~Direction, Animation~ walkAnimations
        -Map~Direction, Animation~ runAnimations
        -Map~Direction, Animation~ attackAnimations
        -Map~Direction, Animation~ walkAttackAnimations
        -Map~Direction, Animation~ runAttackAnimations
        -Map~Direction, Animation~ hurtAnimations
        -Map~Direction, Animation~ deathAnimations
        -Animation currentAnimation
        -Direction currentDirection
        -boolean isMoving
        -boolean isRunning
        -boolean isAttacking
        -boolean isHurt
        -boolean isDead
        -float stateTime
        -float attackStateTime
        -float hurtStateTime
        -List~Texture~ textures
        -float scale
        +AnimationHandler()
        +update(float deltaTime) void
        +render(SpriteBatch batch, float x, float y) float[]
        +getCurrentFrameIndex() int
        +isAttackAnimation() boolean
        +getAttackAnimationType() String
        +dispose() void
        +setMoving(boolean moving) void
        +setRunning(boolean running) void
        +attack() void
        +setHurt(boolean hurt) void
        +setDead(boolean dead) void
        +addIdleAnimation(Direction direction, Animation animation) void
        +addWalkAnimation(Direction direction, Animation animation) void
        +addAttackAnimation(Direction direction, Animation animation) void
        -updateCurrentAnimation() void
    }

    class CombatHandler {
        -int health
        -int maxHealth
        -int shield
        -int maxShield
        -AnimationHandler animationHandler
        +CombatHandler(int maxHealth, AnimationHandler animationHandler)
        +takeDamage(int damage) void
        +heal(int amount) void
        +isAlive() boolean
        +getHealth() int
        +setHealth(int health) void
        +getMaxHealth() int
        +setMaxHealth(int maxHealth) void
        +getShield() int
        +setShield(int shield) void
        +getMaxShield() int
        +setMaxShield(int maxShield) void
    }

    class MovementHandler {
        -float x
        -float y
        -float speed
        -float runSpeedMultiplier
        -AnimationHandler animationHandler
        -CollisionHandler collisionHandler
        +MovementHandler(float x, float y, float speed, AnimationHandler animationHandler)
        +move(Direction direction, float deltaTime, boolean isRunning) void
        +stop() void
        +getX() float
        +setX(float x) void
        +getY() float
        +setY(float y) void
        +getSpeed() float
        +setSpeed(float speed) void
        +setCollisionHandler(CollisionHandler collisionHandler) void
    }

    class CollisionHandler {
        -JsonMapLoader mapLoader
        -float entityWidth
        -float entityHeight
        +CollisionHandler(JsonMapLoader mapLoader, float entityWidth, float entityHeight)
        +isValidPosition(float x, float y) boolean
        +canMove(float currentX, float currentY, Direction direction, float distance) boolean
        +adjustPosition(float currentX, float currentY, float desiredX, float desiredY) float[]
        +setMapLoader(JsonMapLoader mapLoader) void
    }

    class AnimationLoader {
        <<utility>>
        +loadAnimation(AnimationHandler handler, String jsonPath, String pngPath, String animationType, float frameDuration, int[] yRanges, boolean looping)$ void
        -createAndAddAnimation(AnimationHandler handler, List sprites, Direction direction, Texture texture, float frameDuration, String animationType, boolean looping)$ void
    }

    %% ===== MANAGERS =====
    class JsonMapLoader {
        -OrthogonalTiledMapRenderer mapRenderer
        -TiledMap tiledMap
        -int tileWidth
        -int tileHeight
        -int mapWidth
        -int mapHeight
        +JsonMapLoader(String jsonPath)
        +render(OrthographicCamera camera) void
        +isColliding(float x, float y, float width, float height) boolean
        +dispose() void
        +getTiledMap() TiledMap
        +getTileWidth() int
        +getTileHeight() int
        +getMapWidth() int
        +getMapHeight() int
    }

    %% ===== SCREENS =====
    class Screen {
        <<interface>>
        +show() void
        +render(float delta) void
        +resize(int width, int height) void
        +pause() void
        +resume() void
        +hide() void
        +dispose() void
    }

    class MenuScreen {
        -TwoDGame game
        -SpriteBatch batch
        -OrthographicCamera camera
        -MenuMapping menuMapping
        -Texture backgroundBlur
        -Texture abyssLogo
        -Texture levelsTexture
        -Texture playTexture
        -Rectangle settingsIconBounds
        -Rectangle levelsButtonBounds
        -Rectangle playButtonBounds
        -float screenWidth
        -float screenHeight
        +MenuScreen(TwoDGame game)
        +show() void
        +render(float delta) void
        +resize(int width, int height) void
        +dispose() void
        -drawBackground() void
        -drawMenuButtons() void
        -handleInput() void
        -navigateToSettings() void
        -startGame() void
    }

    class GameScreen {
        -TwoDGame game
        -OrthographicCamera camera
        -SpriteBatch batch
        -JsonMapLoader mapLoader
        -Player player
        -List~Enemy~ enemies
        -List~Projectile~ projectiles
        -CollisionHandler collisionHandler
        -boolean isPaused
        +GameScreen(TwoDGame game)
        +show() void
        +render(float delta) void
        +resize(int width, int height) void
        +dispose() void
        -update(float deltaTime) void
        -handleInput() void
        -handlePlayerMovement(float deltaTime) void
        -updateEnemies(float deltaTime) void
        -updateProjectiles(float deltaTime) void
        -handlePlayerAttack() void
        -checkCollisions() void
        -renderGame() void
    }

    class PauseScreen {
        -TwoDGame game
        -SpriteBatch batch
        -OrthographicCamera camera
        -MenuMapping menuMapping
        -Texture backgroundTexture
        -Texture resumeTexture
        -Texture restartTexture
        -Rectangle resumeBounds
        -Rectangle restartBounds
        -Rectangle settingsBounds
        -float screenWidth
        -float screenHeight
        +PauseScreen(TwoDGame game)
        +show() void
        +render(float delta) void
        +resize(int width, int height) void
        +dispose() void
        -drawButtons(float drawWidth, float drawHeight) void
        -handleInput() void
        -handleButtonClick(float x, float y) void
    }

    class SettingsScreen {
        -TwoDGame game
        -SpriteBatch batch
        -OrthographicCamera camera
        -MenuMapping menuMapping
        -Texture font
        -Texture click
        -Texture non_click
        -boolean isFullscreen
        -Rectangle clickButtonBounds
        -Rectangle fullscreenButtonBounds
        -float screenWidth
        -float screenHeight
        +SettingsScreen(TwoDGame game)
        +show() void
        +render(float delta) void
        +resize(int width, int height) void
        +dispose() void
        -drawButtons(float drawWidth, float drawHeight) void
        -handleInput() void
        -toggleFullscreen(boolean fullscreen) void
    }

    class DevScreen {
        -TwoDGame game
        -SpriteBatch batch
        -OrthographicCamera camera
        -MenuMapping menuMapping
        -Texture backgroundBlur
        -Texture backgroundPanel
        -Texture quitTexture
        -Rectangle crossButtonBounds
        -Rectangle quitButtonBounds
        -float screenWidth
        -float screenHeight
        +DevScreen(TwoDGame game)
        +show() void
        +render(float delta) void
        +resize(int width, int height) void
        +dispose() void
        -drawButtons() void
        -handleInput() void
        -returnToMenu() void
    }

    class GameSettingsScreen {
        -TwoDGame game
        -SpriteBatch batch
        -OrthographicCamera camera
        +GameSettingsScreen(TwoDGame game)
        +show() void
        +render(float delta) void
        +resize(int width, int height) void
        +dispose() void
    }

    %% ===== UTILITAIRES =====
    class Direction {
        <<enumeration>>
        UP
        DOWN
        SIDE
        SIDE_LEFT
    }

    class MenuMapping {
        -Texture menuTexture
        -Map~String, TextureRegion~ allSprites
        +MenuMapping()
        -loadMapping() void
        -loadAllSprites(JsonValue spritesData) void
        +getSprite(String spriteName) TextureRegion
        +getAllSprites() Map~String, TextureRegion~
        +getTexture() Texture
        +dispose() void
    }

    class ActionPanelMapping {
        -Texture panelTexture
        -Map~String, TextureRegion~ allSprites
        +ActionPanelMapping()
        -loadMapping() void
        -loadAllSprites(JsonValue spritesData) void
        +getSprite(String spriteName) TextureRegion
        +getAllSprites() Map~String, TextureRegion~
        +getTexture() Texture
        +dispose() void
    }

    class CharacterPanelMapping {
        -Texture panelTexture
        -Map~String, TextureRegion~ allSprites
        +CharacterPanelMapping()
        -loadMapping() void
        -loadAllSprites(JsonValue spritesData) void
        +getSprite(String spriteName) TextureRegion
        +getAllSprites() Map~String, TextureRegion~
        +getTexture() Texture
        +dispose() void
    }

    class TextMapping {
        -Texture textTexture
        -Map~String, TextureRegion~ allSprites
        +TextMapping()
        -loadMapping() void
        -loadAllSprites(JsonValue spritesData) void
        +getSprite(String spriteName) TextureRegion
        +getAllSprites() Map~String, TextureRegion~
        +getTexture() Texture
        +dispose() void
    }

    class SettingsMapping {
        -Texture settingsTexture
        -JsonValue mappingData
        -Map~String, TextureRegion~ screens
        -Map~String, TextureRegion~ buttons
        -Map~String, TextureRegion~ icons
        -Map~String, TextureRegion~ interactiveElements
        -Map~String, TextureRegion~ allSprites
        -Map~String, String~ spriteNameMapping
        -Map~String, ClickableArea~ clickableAreas
        +SettingsMapping()
        -loadMapping() void
        -loadAllSprites(JsonValue spritesData) void
        -loadSpriteNameMapping(JsonValue nameMapping) void
        -createSemanticMappings() void
        -loadRegions(JsonValue section, Map targetMap, String type) void
        +getScreen(String name) TextureRegion
        +getButton(String name) TextureRegion
        +getIcon(String name) TextureRegion
        +getInteractiveElement(String name) TextureRegion
        +getSprite(String spriteName) TextureRegion
        +getAllSprites() Map~String, TextureRegion~
        +getClickableAreas() Map~String, ClickableArea~
        +getClickableArea(String name) ClickableArea
        +getTexture() Texture
        +dispose() void
    }

    class ClickableArea {
        <<inner class>>
        +String name
        +float x
        +float y
        +float width
        +float height
        +String type
        +ClickableArea(String name, float x, float y, float width, float height, String type)
        +contains(float mouseX, float mouseY) boolean
    }

    %% ===== RELATIONS D'HÉRITAGE =====
    Character <|-- Player : hérite
    Character <|-- Enemy : hérite
    Enemy <|-- Vampire : hérite
    Enemy <|-- Slime : hérite
    
    Screen <|.. MenuScreen : implémente
    Screen <|.. GameScreen : implémente
    Screen <|.. PauseScreen : implémente
    Screen <|.. SettingsScreen : implémente
    Screen <|.. DevScreen : implémente
    Screen <|.. GameSettingsScreen : implémente

    %% ===== RELATIONS DE COMPOSITION =====
    Character *-- AnimationHandler : contient
    Character *-- CombatHandler : contient
    Character *-- MovementHandler : contient
    Player *-- Inventory : contient
    Inventory *-- Item : contient
    MovementHandler *-- CollisionHandler : contient

    %% ===== RELATIONS D'ASSOCIATION =====
    Player ..> ItemType : utilise
    Enemy --> Character : target
    Enemy ..> JsonMapLoader : utilise
    Enemy ..> Projectile : crée
    Vampire ..> Projectile : crée
    Projectile --> JsonMapLoader : utilise
    Projectile ..> Player : vérifie collision
    
    AnimationHandler ..> Direction : utilise
    MovementHandler ..> Direction : utilise
    CollisionHandler --> JsonMapLoader : utilise
    AnimationLoader ..> AnimationHandler : configure
    
    CombatHandler --> AnimationHandler : notifie
    
    GameScreen --> Player : gère
    GameScreen --> Enemy : gère
    GameScreen --> Projectile : gère
    GameScreen --> JsonMapLoader : utilise
    GameScreen --> CollisionHandler : utilise

    %% ===== RELATIONS D'UTILISATION =====
    MenuScreen --> MenuMapping : utilise
    PauseScreen --> MenuMapping : utilise
    SettingsScreen --> MenuMapping : utilise
    SettingsScreen --> SettingsMapping : utilise
    DevScreen --> MenuMapping : utilise
    
    SettingsMapping *-- ClickableArea : contient
    
    MenuScreen ..> GameScreen : navigue
    MenuScreen ..> SettingsScreen : navigue
    MenuScreen ..> DevScreen : navigue
    PauseScreen ..> GameScreen : navigue
    PauseScreen ..> SettingsScreen : navigue
    SettingsScreen ..> MenuScreen : navigue
    DevScreen ..> MenuScreen : navigue

```