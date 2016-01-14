package com.stango.mario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class GameplayScreen extends ScreenAdapter
{
	Level level;
	SpriteBatch batch;
	ExtendViewport viewport;
	
	public void show()
	{
		batch = new SpriteBatch();
		viewport = new ExtendViewport(256, 256);
		level = new Level();
	}
	
	public void resize(int width, int height)
	{
		viewport.update(width, height, true);
	}
	
	public void dispose()
	{
		batch.dispose();
		level.dispose();
	}
	
	public void render(float delta)
	{
		level.update(delta);
		viewport.apply();
		Gdx.gl.glClearColor(Color.SKY.r, Color.SKY.g, Color.SKY.b, Color.SKY.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(viewport.getCamera().combined);
		level.render(batch);
	}
}
