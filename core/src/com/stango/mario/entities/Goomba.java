package com.stango.mario.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

public class Goomba 
{
	public Vector2 position;
	private Direction direction;
	private final float WALKING_SPEED = 15;
	private Texture goombaLeft, goombaRight;
	long startTime;
	
	public Goomba(Vector2 pos)
	{
		position = pos;
		direction = Direction.RIGHT;
		goombaLeft = new Texture(Gdx.files.internal("goombaLeft.png"));
		goombaRight = new Texture(Gdx.files.internal("goombaRight.png"));
		startTime = TimeUtils.nanoTime();
	}
	
	public void update(float delta)
	{
		switch (direction)
		{
			case LEFT:
				position.x -= WALKING_SPEED * delta;
				break;
			case RIGHT:
				position.x += WALKING_SPEED * delta;
				break;				
		}
		
		float secondsElapsed = (TimeUtils.nanoTime() - startTime) * MathUtils.nanoToSec;
		if(secondsElapsed % 6 > 5)
		{
			startTime = TimeUtils.nanoTime();
			if(direction == Direction.LEFT)
				direction = Direction.RIGHT;
			else
				direction = Direction.LEFT;
		}
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
	
	enum Direction
	{
		LEFT,
		RIGHT
	};
}
