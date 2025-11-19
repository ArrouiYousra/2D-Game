package com.tlse1.twodgame;

import com.badlogic.gdx.Game;
import com.tlse1.twodgame.screens.MenuScreen;
import com.tlse1.twodgame.screens.PauseScreen;
import com.tlse1.twodgame.screens.SettingsScreen;
import com.tlse1.twodgame.screens.AnimatedSplashScreen;

/**
 * Classe principale du jeu utilisant le système de screens de LibGDX.
 * Gère la navigation entre les différents écrans (menu, jeu, etc.).
 */
public class TwoDGame extends Game {
    
    @Override
    public void create() {
        // Démarrer avec le menu principal
        setScreen(new SettingsScreen(this));
    }
    
    @Override
    public void dispose() {
        // Libérer les ressources globales si nécessaire
        super.dispose();
    }
}

