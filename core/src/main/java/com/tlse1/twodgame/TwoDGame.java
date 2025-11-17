package com.tlse1.twodgame;

import com.badlogic.gdx.Game;
import com.tlse1.twodgame.screens.GameScreen;

/**
 * Classe principale du jeu utilisant le système de screens de LibGDX.
 * Gère la navigation entre les différents écrans (menu, jeu, etc.).
 */
public class TwoDGame extends Game {
    
    @Override
    public void create() {
        // Lancer l'écran de jeu principal
        setScreen(new GameScreen(this));
    }
    
    @Override
    public void dispose() {
        // Libérer les ressources globales si nécessaire
        super.dispose();
    }
}

