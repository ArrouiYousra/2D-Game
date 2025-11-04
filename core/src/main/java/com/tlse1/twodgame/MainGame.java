package com.tlse1.twodgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MainGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture sheet;
    private Animation<TextureRegion> anim;
    private float stateTime;

    // Config de ta sheet
    private static final int COLS = 6;  // 6 frames sur une ligne
    private static final int ROWS = 1;  // 1 ligne

    @Override
    public void create() {
        batch = new SpriteBatch();
        sheet = new Texture(Gdx.files.internal("characters/hero.png"));
        sheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest); // pixel-art net

        int frameW = sheet.getWidth() / COLS;   // = 13
        int frameH = sheet.getHeight() / ROWS;  // = 16

        TextureRegion[][] grid = TextureRegion.split(sheet, frameW, frameH);

        // Aplatis en 1D
        TextureRegion[] frames = new TextureRegion[COLS * ROWS];
        int i = 0;
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                frames[i++] = grid[r][c];

        anim = new Animation<>(0.12f, frames); // ~8 fps; ajuste (plus petit = plus rapide)
        stateTime = 0f;
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1); // fond blanc
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion frame = anim.getKeyFrame(stateTime, true); // true = boucle

        batch.begin();
        // La frame est minuscule (13x16). On la scale (ex: x4)
        float scale = 4f;
        float w = frame.getRegionWidth() * scale;   // 52
        float h = frame.getRegionHeight() * scale;  // 64
        batch.draw(frame, 100, 100, w, h);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        sheet.dispose();
    }
}
