package com.stango.mario.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class QBlock 
{
	int x, y;
	PowerUp powerUp;
	boolean hit;
	Texture block;
	Texture hitBlock;
	Texture coin;
	Texture mushroom;
	long hitStartTime;
	Sound gotCoin;
	int counter;
	
	public QBlock(int x, int y, PowerUp p)
	{
		counter = 0;
		this.x = x;
		this.y = y;
		powerUp = p;
		hit = false;
		block = new Texture(Gdx.files.internal("QBlock.png"));
		hitBlock = new Texture(Gdx.files.internal("hitBlock.png"));
		coin = new Texture(Gdx.files.internal("coin.png"));
		mushroom = new Texture(Gdx.files.internal("mushroom.png"));
		gotCoin = Gdx.audio.newSound(Gdx.files.internal("coin.wav"));
	}
	
	public void setHit(boolean h)
	{
		if(!hit)
			hitStartTime = TimeUtils.nanoTime();
		hit = h;
	}
	
	public void render(SpriteBatch batch)
	{
		if(hit)
		{
			batch.draw(hitBlock, x, y);
			if(powerUp == PowerUp.COIN)
			{
				if(MathUtils.nanoToSec * (TimeUtils.nanoTime() - hitStartTime) < 0.25)
				{
					batch.draw(coin, x + 2, y + 18);
					counter++;
					if(counter == 1)
					gotCoin.play();
				}
			}
		}
		else
			batch.draw(block, x, y);
	}
	
	public void dispose()
	{
		block.dispose();
		coin.dispose();
		gotCoin.dispose();
	}
	
	public enum PowerUp
	{
		COIN,
		MUSHROOM
	}
}
