# Guide de Nommage des Branches

## Convention de nommage

Format recommandé : `feature/nom-de-la-feature` ou `feature/categorie/nom-de-la-feature`

## Exemples de branches par catégorie

### 🎮 Combat & Actions
- `feature/combat/attaque-melee` - Système d'attaque au corps à corps (punch)
- `feature/combat/attaque-distance` - Système d'attaque à distance
- `feature/combat/degats-collision` - Système de dégâts par collision
- `feature/combat/animations-combat` - Animations de combat (punch)

### 🎯 Gameplay
- `feature/gameplay/inventaire` - Système d'inventaire
- `feature/gameplay/pickup-items` - Ramassage d'objets (utilise les sprites Pick-up)
- `feature/gameplay/score-system` - Système de score
- `feature/gameplay/vie-ui` - Interface utilisateur pour la santé
- `feature/gameplay/game-over-screen` - Écran de fin de jeu
- `feature/gameplay/pause-menu` - Menu de pause

### 🗺️ Monde & Environnement
- `feature/map/camera-system` - Système de caméra qui suit le joueur
- `feature/map/map-tiles` - Système de tuiles/cartes
- `feature/map/collisions-murs` - Collisions avec les murs/obstacles
- `feature/map/background-parallax` - Effet de parallaxe

### 👾 Ennemis & IA
- `feature/ai/patrouille-ennemis` - Ennemis qui patrouillent
- `feature/ai/spawn-system` - Système de spawn d'ennemis
- `feature/ai/ennemis-types` - Différents types d'ennemis
- `feature/ai/chemin-pathfinding` - Pathfinding pour les ennemis

### 🎨 Interface & UI
- `feature/ui/hud` - HUD (Heads-Up Display) principal
- `feature/ui/menu-principal` - Menu principal amélioré
- `feature/ui/options-menu` - Menu des options
- `feature/ui/credits-screen` - Écran des crédits

### 🎵 Audio & Effets
- `feature/audio/sound-effects` - Effets sonores
- `feature/audio/musique-background` - Musique de fond
- `feature/audio/audio-manager` - Gestionnaire audio

### 💾 Progression & Sauvegarde
- `feature/save/load-system` - Système de sauvegarde/chargement
- `feature/save/checkpoints` - Système de checkpoints
- `feature/progression/leveling` - Système de niveaux/XP

### 🔧 Technique & Optimisation
- `feature/tech/asset-loader` - Amélioration du chargement d'assets
- `feature/tech/performance-optimization` - Optimisations de performance
- `feature/tech/refactoring-architecture` - Refactoring de l'architecture

## Exemples pour votre projet actuel

Basé sur ce qui existe déjà, voici des suggestions prioritaires :

1. **Système de combat** (utilise les sprites Punch existants)
   - `feature/combat/attaque-melee`
   - `feature/combat/degats-system`

2. **Ramassage d'objets** (utilise les sprites Pick-up existants)
   - `feature/gameplay/pickup-items`
   - `feature/gameplay/inventaire`

3. **Système de caméra**
   - `feature/map/camera-system`

4. **Interface utilisateur**
   - `feature/ui/hud`
   - `feature/ui/vie-bar`

5. **Amélioration des ennemis**
   - `feature/ai/enemy-behavior`
   - `feature/ai/spawn-waves`

## Commandes Git utiles

```bash
# Créer une nouvelle branche depuis main
git checkout main
git pull origin main
git checkout -b feature/nom-de-la-feature

# Ou en une seule commande
git checkout -b feature/nom-de-la-feature main

# Pousser la branche
git push -u origin feature/nom-de-la-feature

# Lister les branches
git branch -a

# Supprimer une branche locale (après merge)
git branch -d feature/nom-de-la-feature
```

## Bonnes pratiques

1. ✅ **Toujours partir de `main`** pour créer une nouvelle branche
2. ✅ **Nom court et descriptif** en minuscules avec tirets
3. ✅ **Une feature = une branche** (ou deux si très grande feature)
4. ✅ **Commits fréquents** avec messages clairs
5. ✅ **Merge via Pull Request** si vous travaillez en équipe
6. ❌ **Éviter** les noms trop génériques comme `feature/test` ou `feature/update`
7. ❌ **Éviter** les caractères spéciaux (accents, espaces, underscores multiples)

