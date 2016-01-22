package com.stango.mario.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Mario 
{
	Vector2 position;
	Vector2 velocity;
	Facing facing;
	long jumpStartTime;
	JumpState jumpState;
	Texture marioL;
	Texture marioR;
	Texture marioLJump;
	Texture marioRJump;
	Texture marioLFall;
	Texture marioRFall;
	
	public Mario()
	{
		position = new Vector2(20,20);
		velocity = new Vector2();
		facing = Facing.RIGHT;
		jumpState = JumpState.FALLING;
		marioL = new Texture(Gdx.files.internal("marioLeft.png"));
		marioR = new Texture(Gdx.files.internal("marioRight.png"));
		marioLJump = new Texture(Gdx.files.internal("marioLeftJump.png"));
		marioRJump = new Texture(Gdx.files.internal("marioRightJump.png"));
		marioLFall = new Texture(Gdx.files.internal("marioLeftFalling.png"));
		marioRFall = new Texture(Gdx.files.internal("marioRightFalling.png"));
	}
	
	public void update(float delta, Array<QBlock> qBlocks)
	{
		velocity.y -= 25;
		position.mulAdd(velocity, delta);
		if(position.y < 0)
		{
			jumpState = JumpState.GROUNDED;
			velocity.y = 0;
			position.y = 0;
		}
		
		if(Gdx.input.isKeyPressed(Keys.LEFT))
		{
			facing = Facing.LEFT;
			position.x -= 128 * delta;
		}
		
		if(Gdx.input.isKeyPressed(Keys.RIGHT))
		{
			facing = Facing.RIGHT;
			position.x += 128 * delta;
		}
		
		if(Gdx.input.isKeyPressed(Keys.Z))
		{
			switch(jumpState)
			{
				case GROUNDED:
					startJump();
					break;
				case JUMPING:
					continueJump();
					break;
				case FALLING:
					break;
			}
		}
		else
			endJump();
	}
	
	private void startJump()
	{
		jumpState = JumpState.JUMPING;
		jumpStartTime = TimeUtils.nanoTime();
		continueJump();
	}
	
	private void continueJump()
	{
		if(jumpState == JumpState.JUMPING)
		{
			if(MathUtils.nanoToSec * (TimeUtils.nanoTime() - jumpStartTime) < 0.25)
			{
				velocity.y = 1.2f * 256;
			}
			else
				endJump();
		}
	}
	
	private void endJump()
	{
		if(jumpState == JumpState.JUMPING)
			jumpState = JumpState.FALLING;
	}
	
	public void render(SpriteBatch batch)
	{
		if(facing == Facing.LEFT)
		{
			if(jumpState == JumpState.JUMPING)
				batch.draw(marioLJump, position.x, position.y);
			else if(jumpState == JumpState.FALLING)
				batch.draw(marioLFall, position.x, position.y);
			else
				batch.draw(marioL, position.x, position.y);
		}
		else
		{
			if(jumpState == JumpState.JUMPING)
				batch.draw(marioRJump, position.x, position.y);
			else if(jumpState == JumpState.FALLING)
				batch.draw(marioRFall, position.x, position.y);
			else
				batch.draw(marioR, position.x, position.y);
		}
	}
	
	public void dispose()
	{
		marioL.dispose();
		marioR.dispose();
		marioLJump.dispose();
		marioRJump.dispose();
		marioLFall.dispose();
		marioRFall.dispose();
	}
	
	enum Facing
	{
		LEFT,
		RIGHT
	}
	
	enum JumpState
	{
		JUMPING,
		FALLING,
		GROUNDED
	}
}
