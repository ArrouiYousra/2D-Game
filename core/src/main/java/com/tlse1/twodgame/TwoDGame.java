package com.tlse1.twodgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.tlse1.twodgame.screens.SettingsScreen;

/**
 * Classe principale du jeu utilisant le système de screens de LibGDX.
 * Gère la navigation entre les différents écrans (menu, jeu, etc.).
 * 
 * TEMPORAIRE : Lance directement SettingsScreen pour travailler sur la branche options-menu
 */
public class TwoDGame extends Game {
    
    @Override
    public void create() {
        // Démarrer directement avec l'écran de paramètres (pour la branche options-menu)
        setScreen(new SettingsScreen(this));
    }
    
    @Override
    public void dispose() {
        // Libérer les ressources globales si nécessaire
        super.dispose();
    }
}

