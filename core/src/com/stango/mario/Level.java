package com.stango.mario;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Level 
{
	Mario mario;
	
	public Level()
	{
		mario = new Mario();
	}
	
	public void update(float delta)
	{
		mario.update(delta);
	}
	
	public void render(SpriteBatch batch)
	{
		batch.begin();
		mario.render(batch);
		batch.end();
	}
	
	public void dispose()
	{
		mario.dispose();
	}
}
