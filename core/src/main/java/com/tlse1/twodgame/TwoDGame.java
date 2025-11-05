package com.tlse1.twodgame;

import com.badlogic.gdx.Game;
import com.tlse1.twodgame.screens.MenuScreen;

/**
 * Classe principale du jeu utilisant le système de screens de LibGDX.
 * Gère la navigation entre les différents écrans (menu, jeu, etc.).
 */
public class TwoDGame extends Game {
    
    @Override
    public void create() {
        // Démarrer avec l'écran de menu
        setScreen(new MenuScreen(this));
    }
    
    @Override
    public void dispose() {
        // Libérer les ressources globales si nécessaire
        super.dispose();
    }
}

