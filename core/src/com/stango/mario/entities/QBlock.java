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
	Texture hitBlock;
	
	public QBlock(int x, int y, PowerUp p)
	{
		this.x = x;
		this.y = y;
		powerUp = p;
		hit = false;
		block = new Texture(Gdx.files.internal("QBlock.png"));
		hitBlock = new Texture(Gdx.files.internal("hitBlock.png"));
	}
	
	public void setHit(boolean h)
	{
		hit = h;
	}
	
	public void render(SpriteBatch batch)
	{
		if(hit)
			batch.draw(hitBlock, x, y);
		else
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
