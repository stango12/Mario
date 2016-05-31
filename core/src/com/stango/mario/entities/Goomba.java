package com.stango.mario.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
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
	
	public Goomba(Vector2 pos)
	{
		position = pos;
		velocity = new Vector2();
		direction = Direction.RIGHT;
		goombaLeft = new Texture(Gdx.files.internal("goombaLeft.png"));
		goombaRight = new Texture(Gdx.files.internal("goombaRight.png"));
		startTime = TimeUtils.nanoTime();
	}
	
	public void update(float delta)
	{
		//gravity logic
		velocity.y -= 25;
		position.mulAdd(velocity, delta);
		
		//make sure goomba doesnt fall through the ground
		if(position.y < 0)
		{
			velocity.y = 0;
			position.y = 0;
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
	
	enum Direction
	{
		LEFT,
		RIGHT
	};
}
