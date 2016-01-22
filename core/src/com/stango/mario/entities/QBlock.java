package com.stango.mario.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class QBlock 
{
	int x, y;
	PowerUp powerUp;
	boolean hit;
	Texture block;
	
	public QBlock(int x, int y, PowerUp p)
	{
		this.x = x;
		this.y = y;
		powerUp = p;
		hit = false;
		block = new Texture(Gdx.files.internal("QBlock.png"));
	}
	
	public void render(SpriteBatch batch)
	{
		batch.draw(block, x, y);
	}
	
	public void dispose()
	{
		block.dispose();
	}
	
	public enum PowerUp
	{
		COIN,
		MUSHROOM
	}
}
