package com.tlse1.twodgame;

import com.badlogic.gdx.Game;
import com.tlse1.twodgame.screens.SwordsmanTestScreen;

/**
 * Classe principale du jeu utilisant le système de screens de LibGDX.
 * Gère la navigation entre les différents écrans (menu, jeu, etc.).
 * 
 * TEMPORAIRE : Test du Swordsman
 */
public class TwoDGame extends Game {
    
    @Override
    public void create() {
        // TEMPORAIRE : Tester le chargement des sprites du Swordsman
        setScreen(new SwordsmanTestScreen(this));
    }
    
    @Override
    public void dispose() {
        // Libérer les ressources globales si nécessaire
        super.dispose();
    }
}

