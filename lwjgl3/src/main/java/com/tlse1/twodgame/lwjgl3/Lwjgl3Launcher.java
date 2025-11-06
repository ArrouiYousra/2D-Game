package com.tlse1.twodgame.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.tlse1.twodgame.map.JsonMapLoader;  // ← Changez ici

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        System.out.println("=== LANCEMENT LWJGL3 ===");
        
        System.setProperty("org.lwjgl.openal.explicitInit", "true");
        
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("TileMap depuis JSON");
        config.setWindowedMode(800, 600);
        config.useVsync(true);
        config.setForegroundFPS(60);
        
        new Lwjgl3Application(new JsonMapLoader(), config);  // ← Et ici
    }
}