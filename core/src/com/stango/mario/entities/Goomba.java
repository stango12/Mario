package com.stango.mario.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.stango.mario.entities.Mario.JumpState;

public class Goomba 
{
	//goombas are 16x15
	public Vector2 position, velocity;
	private Direction direction;
	private final float WALKING_SPEED = 15;
	private Texture goombaLeft, goombaRight;
	long startTime;
	boolean alive;
	
	public Goomba(Vector2 pos)
	{
		position = pos;
		velocity = new Vector2();
		direction = Direction.LEFT;
		goombaLeft = new Texture(Gdx.files.internal("goombaLeft.png"));
		goombaRight = new Texture(Gdx.files.internal("goombaRight.png"));
		startTime = TimeUtils.nanoTime();
		alive = true;
	}
	
	public void update(float delta, Array<QBlock> qBlocks, Array<Platform> platforms)
	{
		if(alive) //update movement only when mario is 200 units away from the goomba
		{
			//gravity logic
			velocity.y -= 25;
			position.mulAdd(velocity, delta);
			
			//make sure goomba doesnt fall through the ground
			Rectangle enemyCollision = new Rectangle(position.x, position.y, 16, 15);
			for(int i = 0; i < platforms.size; i++)
			{
				if(position.y < 32 && enemyCollision.overlaps(platforms.get(i).getCollidingRectangle()))
				{
					velocity.y = 0;
					position.y = 32;
				}
			}
			switch (direction)
			{
				case LEFT:
					position.x -= WALKING_SPEED * delta;
					break;
				case RIGHT:
					position.x += WALKING_SPEED * delta;
					break;				
			}
			
			for(int i = 0; i < qBlocks.size; i++)
			{
				if(onBlock(qBlocks.get(i)))
				{
					velocity.y = 0;
					position.y = qBlocks.get(i).getY() + 16;
				}
			}
		}
		//goomba moves in one direction for 5 seconds
//		float secondsElapsed = (TimeUtils.nanoTime() - startTime) * MathUtils.nanoToSec;
//		if(secondsElapsed % 6 > 5)
//		{
//			startTime = TimeUtils.nanoTime();
//			if(direction == Direction.LEFT)
//				direction = Direction.RIGHT;
//			else
//				direction = Direction.LEFT;
//		}
	}
	
	private boolean onBlock(QBlock q)
	{
		boolean leftFoot = false;
		boolean rightFoot = false;
		boolean straddle = false;
		
		if(position.y >= q.getY() && position.y <= q.getY() + 16)
		{
			int xBlock = q.getX();
			
			leftFoot = xBlock < position.x && xBlock + 16 > position.x;
			rightFoot = xBlock < position.x + 14 && xBlock + 16 > position.x + 14;
			straddle = xBlock > position.x && xBlock + 16 < position.x + 14;
			
			if(leftFoot || rightFoot || straddle)
				return true;
		}
		
		return false;
	}
	
	public void render(SpriteBatch batch)
	{
		switch(direction)
		{
			case LEFT:
				batch.draw(goombaLeft, position.x, position.y);
				break;
			case RIGHT:
				batch.draw(goombaRight, position.x, position.y);
				break;
		}	
	}
	
	public void dispose()
	{
		goombaLeft.dispose();
		goombaRight.dispose();
	}
	
	public void changeDirection()
	{
		if(direction == Direction.LEFT)
			direction = Direction.RIGHT;
		else
			direction = Direction.LEFT;
	}
	
	public void active(boolean b)
	{
		alive = b;
	}
	
	enum Direction
	{
		LEFT,
		RIGHT
	};
}
