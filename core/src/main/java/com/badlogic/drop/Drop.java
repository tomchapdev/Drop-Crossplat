package com.badlogic.drop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Drop extends Game {
    public SpriteBatch batch;
    public BitmapFont font;

    public final static Vector2 screenSize = new Vector2(800, 480);
    public final static int spriteSize = 64;

    public void create () {
        batch = new SpriteBatch();
        font = new BitmapFont();

        this.setScreen(new MainMenuScreen(this));
    }

    public void render () {
        super.render();
    }

    @Override
    public void dispose () {
        batch.dispose();
        font.dispose();
    }
}
