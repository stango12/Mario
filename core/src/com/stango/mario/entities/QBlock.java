package com.stango.mario.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.stango.mario.entities.Mario.Size;

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
	
	Vector2 mushroomPos, velocity;
	boolean mushroomGot;
	
	/**
	 * Initialize a question block
	 * @param x x coordinate of block
	 * @param y y coordinate of block
	 * @param p if the block has a coin or a mushroom
	 */
	public QBlock(int x, int y, PowerUp p)
	{
		counter = 0;
		this.x = x;
		this.y = y;
		powerUp = p;
		hit = false;
		mushroomPos = new Vector2();
		velocity = new Vector2();
		mushroomGot = true;
		
		block = new Texture(Gdx.files.internal("QBlock.png"));
		hitBlock = new Texture(Gdx.files.internal("hitBlock.png"));
		coin = new Texture(Gdx.files.internal("coin.png"));
		mushroom = new Texture(Gdx.files.internal("mushroom.png"));
		gotCoin = Gdx.audio.newSound(Gdx.files.internal("coin.wav"));
	}
	
	/**
	 * Sets if the block was hit or not
	 * @param h boolean
	 */
	public void setHit(boolean h)
	{
		if(!hit)
			hitStartTime = TimeUtils.nanoTime();
		hit = h;
	}
	
	public void render(SpriteBatch batch)
	{
		//checks if the block was hit from the bottom
		if(hit)
		{
			batch.draw(hitBlock, x, y);
			//coin logic
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
			counter++;
			//mushroom logic
			if(counter == 1)
			{
			if(powerUp == PowerUp.MUSHROOM)
			{
				mushroomPos.x = x;
				mushroomPos.y = y + 16;
				mushroomGot = false;
				batch.draw(mushroom, mushroomPos.x, mushroomPos.y);
			}
			}
		}
		else
			batch.draw(block, x, y);
		
		//draws mushroom 
		if(powerUp == PowerUp.MUSHROOM && !mushroomGot)
		{
			batch.draw(mushroom, mushroomPos.x, mushroomPos.y);
		}
	}
	
	public void update(float delta)
	{
		//moving the mushroom and making it stay for 5 seconds/if mario gets it
		if(!mushroomGot)
		{
			if(MathUtils.nanoToSec * (TimeUtils.nanoTime() - hitStartTime) > 5)
			{
				mushroomGot = true;
			}
			
			mushroomPos.x += 20 * delta;
			velocity.y -= 15;
			mushroomPos.mulAdd(velocity, delta);
			if(onBlock())
			{
				velocity.y = 0;
				mushroomPos.y = y + 16;
			}
			if(mushroomPos.y < 0)
			{
				velocity.y = 0;
				mushroomPos.y = 0;
			}
		}
	}
	
	/**
	 * On block logic
	 * @return if mushroom is on block
	 */
	boolean onBlock()
	{
		boolean leftFoot = false;
		boolean rightFoot = false;
		boolean straddle = false;
		
		leftFoot = x < mushroomPos.x && x + 16 > mushroomPos.x;
		rightFoot = x < mushroomPos.x + 14 && x + 16 > mushroomPos.x + 14;
		straddle = x > mushroomPos.x && x + 16 < mushroomPos.x + 14;

		
		return leftFoot || rightFoot || straddle;
	}
	
	/**
	 * Returns mushroom position
	 * @return mushroom position
	 */
	public Vector2 getMushroomPos()
	{
		return mushroomPos;
	}
	
	/**
	 * Checks if mushroom is obtained
	 * @param pos Mario's position
	 * @return if mario obtained mushroom
	 */
	public boolean mushroomCheck(Vector2 pos, Size s)
	{
		if(mushroomGot)
			return false;
		
		Rectangle mushroomCollision = new Rectangle(mushroomPos.x, mushroomPos.y, 16, 16);
		Rectangle marioCollision;
		if(s == Size.SMALL)
			marioCollision = new Rectangle(pos.x, pos.y, 14, 20);
		else
			marioCollision = new Rectangle(pos.x, pos.y, 14, 28);
		if(marioCollision.overlaps(mushroomCollision))
		{
			mushroomGot = true;
			return true;
		}
		return false;
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
