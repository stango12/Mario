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
	public Vector2 position;
	Vector2 velocity;
	Vector2 lastPosition;
	Facing facing;
	long jumpStartTime;
	JumpState jumpState;
	Texture marioL;
	Texture marioR;
	//Texture marioLJump;
	//Texture marioRJump;
	Texture marioLFall;
	Texture marioRFall;
	
	public Mario()
	{
		position = new Vector2(20,20);
		velocity = new Vector2();
		lastPosition = new Vector2(position);
		facing = Facing.RIGHT;
		jumpState = JumpState.FALLING;
		marioL = new Texture(Gdx.files.internal("marioLeft.png"));
		marioR = new Texture(Gdx.files.internal("marioRight.png"));
		//marioLJump = new Texture(Gdx.files.internal("marioLeftJump.png"));
		//marioRJump = new Texture(Gdx.files.internal("marioRightJump.png"));
		marioLFall = new Texture(Gdx.files.internal("marioLeftFalling.png"));
		marioRFall = new Texture(Gdx.files.internal("marioRightFalling.png"));
	}
	
	public void update(float delta, Array<QBlock> qBlocks)
	{
		lastPosition.set(position);
		velocity.y -= 25;
		position.mulAdd(velocity, delta);
		
		if(jumpState != JumpState.JUMPING)
		{
			jumpState = JumpState.FALLING;
			if(position.y < 0)
			{
				jumpState = JumpState.GROUNDED;
				velocity.y = 0;
				position.y = 0;
			}
		}
			for(int i = 0; i < qBlocks.size; i++)
			{
				if(landedOnBlock(qBlocks.get(i)))
				{
					jumpState = JumpState.GROUNDED;
					velocity.y = 0;
					position.y = qBlocks.get(i).y + 16;
				}
				
				else if(hitBotSide(qBlocks.get(i)))
				{
					jumpState = JumpState.FALLING;
					velocity.y = 0;
					qBlocks.get(i).setHit(true);
				}
				
				else if(hitLeftSide(qBlocks.get(i)))
				{
					position.x = qBlocks.get(i).x - 16;
				}
				
				else if(hitRightSide(qBlocks.get(i)))
				{
					position.x = qBlocks.get(i).x + 16;
				}
			}
		//}
		
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
	
	boolean landedOnBlock(QBlock qBlock)
	{
		boolean leftFoot = false;
		boolean rightFoot = false;
		boolean straddle = false;
		
		if(lastPosition.y >= qBlock.y + 16 && position.y < qBlock.y + 16)
		{
			leftFoot = qBlock.x < position.x && qBlock.x + 16 > position.x;
			rightFoot = qBlock.x < position.x + 14 && qBlock.x + 16 > position.x + 14;
			straddle = qBlock.x > position.x && qBlock.x + 16 < position.x + 14;
		}
		
		return leftFoot || rightFoot || straddle;
	}
	
	boolean hitBotSide(QBlock qBlock)
	{
		boolean leftFoot = false;
		boolean rightFoot = false;
		boolean straddle = false;
		
		if(lastPosition.y + 20 < qBlock.y && position.y + 20 > qBlock.y)
		{
			leftFoot = qBlock.x < position.x && qBlock.x + 16 > position.x;
			rightFoot = qBlock.x < position.x + 14 && qBlock.x + 16 > position.x + 14;
			straddle = qBlock.x > position.x && qBlock.x + 16 < position.x + 14;
		}
		return leftFoot || rightFoot || straddle;
	}
	
	boolean hitLeftSide(QBlock qBlock)
	{
		boolean head = false;
		boolean foot = false;
		boolean straddle = false;
		
		if(position.y >= qBlock.y)
		if(position.x < qBlock.x && position.x + 16 > qBlock.x)
		{
			head = qBlock.y < position.y + 20 && qBlock.y + 16 > position.y + 20;
			foot = qBlock.y < position.y && qBlock.y + 16 > position.y;
			straddle = qBlock.y > position.y && qBlock.y + 16 < position.y + 20;
		}
		return head || foot || straddle;
	}
	
	boolean hitRightSide(QBlock qBlock)
	{
		boolean head = false;
		boolean foot = false;
		boolean straddle = false;
		
		if(position.y >= qBlock.y)
		if(position.x < qBlock.x + 16 && position.x + 16 > qBlock.x + 16)
		{
			head = qBlock.y < position.y + 20 && qBlock.y + 16 > position.y + 20;
			foot = qBlock.y < position.y && qBlock.y + 16 > position.y;
			straddle = qBlock.y > position.y && qBlock.y + 16 < position.y + 20;
		}
		return head || foot || straddle;
	}
	
	public void render(SpriteBatch batch)
	{
		if(facing == Facing.LEFT)
		{
			if(jumpState == JumpState.JUMPING || jumpState == JumpState.FALLING)
				//batch.draw(marioLJump, position.x, position.y);
			//else if(jumpState == JumpState.FALLING)
				batch.draw(marioLFall, position.x, position.y);
			else
				batch.draw(marioL, position.x, position.y);
		}
		else
		{
			if(jumpState == JumpState.JUMPING ||jumpState == JumpState.FALLING)
				//batch.draw(marioRJump, position.x, position.y);
			//else if(jumpState == JumpState.FALLING)
				batch.draw(marioRFall, position.x, position.y);
			else
				batch.draw(marioR, position.x, position.y);
		}
	}
	
	public void dispose()
	{
		marioL.dispose();
		marioR.dispose();
		//marioLJump.dispose();
		//marioRJump.dispose();
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
