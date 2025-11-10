package com.tlse1.twodgame.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.tlse1.twodgame.map.JsonMapLoader;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        System.out.println("=== LANCEMENT ===");
        
        // Configuration
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("TileMap Loader");
        config.setWindowedMode(800, 600);
        config.setForegroundFPS(60);
        
        // Lancement
        new Lwjgl3Application(new JsonMapLoader(), config);
    }
}