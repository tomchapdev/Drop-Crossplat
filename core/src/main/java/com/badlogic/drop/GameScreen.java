package com.badlogic.drop;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {
    final Drop game;
    Texture dropImage;
    Texture bucketImage;
    Sound dropSound;
    Music rainMusic;
    OrthographicCamera camera;
    Rectangle bucket;
    Array<Rectangle> raindrops;
    long lastDropTime;
    int dropsGathered;

    public GameScreen(final Drop game) {
        this.game = game;

        dropImage = new Texture(Gdx.files.internal("drop.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));

        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        rainMusic.setLooping(true);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Drop.screenSize.x, Drop.screenSize.y);

        bucket = new Rectangle();
        bucket.width = Drop.spriteSize;
        bucket.height = Drop.spriteSize;
        bucket.setPosition((Drop.screenSize.x / 2) - (bucket.width /2), 20);

        // create the raindrops array and spawn the first raindrop
        raindrops = new Array<>();
        spawnRaindrop();
        dropsGathered = 0;
    }

    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, Drop.screenSize.x - Drop.spriteSize);
        raindrop.y = Drop.screenSize.y;
        raindrop.width = Drop.spriteSize;
        raindrop.height = Drop.spriteSize;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    private void update() {
        final float bucketSpeed = 200.f;
        final long secInNanos = 1000000000;

        // Input - Mouse or Touch
        if(Gdx.input.isTouched()) {
            Vector3 mousePos = new Vector3();
            mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(mousePos);
            bucket.setX(mousePos.x - (bucket.width / 2));
        }
        // Input - Keyboard
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= bucketSpeed * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += bucketSpeed * Gdx.graphics.getDeltaTime();

        // Clamp bucket to the screen
        if(bucket.getX() < 0) bucket.setX(0);
        if(bucket.getX() > Drop.screenSize.x - Drop.spriteSize) bucket.setX(Drop.screenSize.x - Drop.spriteSize);

        // Spawn more raindrops after a set time
        if(TimeUtils.nanoTime() - lastDropTime > secInNanos) spawnRaindrop();

        for (Iterator<Rectangle> it = raindrops.iterator(); it.hasNext(); ) {
            Rectangle raindrop = it.next();
            raindrop.y -= bucketSpeed * Gdx.graphics.getDeltaTime();

            if(raindrop.overlaps(bucket)) {
                dropSound.play();
                it.remove();
                dropsGathered++;
            } else if(raindrop.y + Drop.spriteSize < 0) it.remove();
        }
    }

    @Override
    public void render(float delta) {
        update();

        ScreenUtils.clear(1.f, 0, 0, 1);
        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, Drop.screenSize.y);
        game.batch.draw(bucketImage, bucket.x, bucket.y, bucket.width, bucket.height);
        for (Rectangle raindrop : raindrops) {
            game.batch.draw(dropImage, raindrop.x, raindrop.y);
        }
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        rainMusic.play();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        dropImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
    }

}
