package com.stango.mario.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.stango.mario.entities.Goomba.Direction;
import com.stango.mario.entities.Mario.Size;

public class QBlock 
{
	//blocks are 16x16
	//mushrooms are 16x16
	//coins are 12x16
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
	Direction mushroomDir;
	
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
		mushroomDir = Direction.RIGHT;
		
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
		if(powerUp == PowerUp.NONE)
		{
			hit = true;
		}
		
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
					if(counter == 1) //makes sure the coin sound is played only once
						gotCoin.play();
				}
			}
			counter++; //counter is to make sure the mushroom only activates once.
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
	
	public void update(float delta, Array<QBlock> qBlocks, Array<Platform> platforms, Array<Pipe> pipes)
	{
		//moving the mushroom and making it stay for 5 seconds/if mario gets it
		if(!mushroomGot)
		{
			if(MathUtils.nanoToSec * (TimeUtils.nanoTime() - hitStartTime) > 5)
			{
				mushroomGot = true;
			}
			
			if(mushroomDir == Direction.RIGHT)
				mushroomPos.x += 40 * delta;
			else
				mushroomPos.x -= 40 * delta;
			
			velocity.y -= 15;
			mushroomPos.mulAdd(velocity, delta);
			if(onBlock(qBlocks))
			{
				velocity.y = 0;
				mushroomPos.y = y + 16;
			}
			
			//checks if mushroom is on the ground
			Rectangle mushroomCollision = new Rectangle(mushroomPos.x, mushroomPos.y, 16, 16);
			for(int i = 0; i < platforms.size; i++)
			{
				if(mushroomPos.y < 32 && mushroomCollision.overlaps(platforms.get(i).getCollidingRectangle()))
				{
					velocity.y = 0;
					mushroomPos.y = 32;
				}
			}
			
			//despawn if the mushroom falls off the ground
			if(mushroomPos.y < -20)
				mushroomGot = true;
			
			//changes the direction of the mushroom if it collides with a pipe
			for(Pipe p : pipes)
			{
				if(mushroomCollision.overlaps(p.getCollidingRectangle()))
					changeDirection();
			}
		}
	}
	
	/**
	 * On block logic
	 * @return if mushroom is on block
	 */
	boolean onBlock(Array<QBlock> qBlocks)
	{
		boolean leftFoot = false;
		boolean rightFoot = false;
		boolean straddle = false;
		
		for(int i = 0; i < qBlocks.size; i++)
		{
			QBlock q = qBlocks.get(i);
			int xBlock = q.getX();
			
			//16 is the size of the block
			if(mushroomPos.y >= q.getY())
			{
				leftFoot = xBlock < mushroomPos.x && xBlock + 16 > mushroomPos.x;
				rightFoot = xBlock < mushroomPos.x + 14 && xBlock + 16 > mushroomPos.x + 14;
				straddle = xBlock > mushroomPos.x && xBlock + 16 < mushroomPos.x + 14;
				
				if(leftFoot || rightFoot || straddle)
					return true;
			}
		}
		
		return false;
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
	 * Return x position
	 * @return
	 */
	public int getX()
	{
		return x;
	}
	
	/**
	 * Return y position
	 * @return
	 */
	public int getY()
	{
		return y;
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
	
	public void changeDirection()
	{
		if(mushroomDir == Direction.LEFT)
			mushroomDir = Direction.RIGHT;
		else
			mushroomDir = Direction.LEFT;
	}
	
	public void dispose()
	{
		block.dispose();
		coin.dispose();
		gotCoin.dispose();
	}
	
	public enum PowerUp
	{
		NONE,
		COIN,
		MUSHROOM
	}
	
	enum Direction
	{
		LEFT,
		RIGHT
	};
}
