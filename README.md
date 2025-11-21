# 2D-Game

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project was generated with a template including simple application launchers and an `ApplicationAdapter` extension that draws libGDX logo.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.



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