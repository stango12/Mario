package com.stango.mario.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.stango.mario.Level;

public class Mario 
{
	public Vector2 position;
	Vector2 velocity;
	Vector2 lastPosition;
	Facing facing;
	long jumpStartTime;
	JumpState jumpState;
	Size size;
	
	Texture marioL;
	Texture marioR;
	//Texture marioLJump;
	//Texture marioRJump;
	Texture marioLFall;
	Texture marioRFall;
	Texture marioLBig;
	Texture marioRBig;
	Texture marioLFallBig;
	Texture marioRFallBig;
	
	Level level;
	boolean hitFlag;
	long invulStartTime;
	
	public Mario(Level l)
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
		
		marioLBig = new Texture(Gdx.files.internal("marioLeftBig.png"));
		marioRBig = new Texture(Gdx.files.internal("marioRightBig.png"));
		marioLFallBig = new Texture(Gdx.files.internal("marioLeftFallingBig.png"));
		marioRFallBig = new Texture(Gdx.files.internal("marioRightFallingBig.png"));
		level = l;
		hitFlag = true;
		size = Size.SMALL;
	}
	
	public void init()
	{
		position = new Vector2(20,20);
		velocity = new Vector2();
		lastPosition = new Vector2(position);
		facing = Facing.RIGHT;
		jumpState = JumpState.FALLING;
		hitFlag = true;
		size = Size.SMALL;
		level.init();
	}
	public void update(float delta, Array<QBlock> qBlocks)
	{
		lastPosition.set(position);
		
		//simulating gravity
		velocity.y -= 25;
		position.mulAdd(velocity, delta);
		
		//jump logic
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
		
		//block logic
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
			
			if(qBlocks.get(i).mushroomCheck(position, size))
				size = Size.BIG;
		}
		
		
		//movement
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
		
		//jumping
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
		
		//restart level
		if(Gdx.input.isKeyPressed(Keys.R))
		{
			init();
		}
		
		//enemy collision detection
		Rectangle marioCollision;
		if(size == Size.SMALL)
			marioCollision = new Rectangle(position.x, position.y, 14, 20);
		else
			marioCollision = new Rectangle(position.x, position.y, 14, 28);
		
		for(Goomba g : level.getEnemies())
		{
			Rectangle enemyCollision = new Rectangle(g.position.x, g.position.y, 16, 15);
			if(marioCollision.overlaps(enemyCollision))
			{
				if(lastPosition.y > g.position.y + 15)
					killEnemy(g);
				else if(hitFlag)
					hitEnemy();
				else
					continueInvulnerability();
			}
		}
	}
		
	/**
	 * Starts the jump for mario and gets the time mario started jumping
	 */
	private void startJump()
	{
		jumpState = JumpState.JUMPING;
		jumpStartTime = TimeUtils.nanoTime();
		continueJump();
	}
	
	/**
	 * Continues the jump until 0.25 seconds passed or if player stops holding z
	 */
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
	
	/**
	 * Ends jump
	 */
	private void endJump()
	{
		if(jumpState == JumpState.JUMPING)
			jumpState = JumpState.FALLING;
	}
	
	/**
	 * Checks if mario is on top of a block
	 * @param qBlock Question mark block
	 * @return True or false
	 */
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
	
	/**
	 * Checks if mario is hitting a block from below
	 * @param qBlock Question mark block
	 * @return True or false
	 */
	boolean hitBotSide(QBlock qBlock)
	{
		boolean leftFoot = false;
		boolean rightFoot = false;
		boolean straddle = false;
		
		if(size == Size.SMALL)
		{
			if(lastPosition.y + 20 < qBlock.y && position.y + 20 > qBlock.y)
			{
				leftFoot = qBlock.x < position.x && qBlock.x + 16 > position.x;
				rightFoot = qBlock.x < position.x + 14 && qBlock.x + 16 > position.x + 14;
				straddle = qBlock.x > position.x && qBlock.x + 16 < position.x + 14;
			}
		}
		else
		{
			if(lastPosition.y + 28 < qBlock.y && position.y + 28 > qBlock.y)
			{
				leftFoot = qBlock.x < position.x && qBlock.x + 16 > position.x;
				rightFoot = qBlock.x < position.x + 15 && qBlock.x + 16 > position.x + 15;
				straddle = qBlock.x > position.x && qBlock.x + 16 < position.x + 15;
			}
		}
		return leftFoot || rightFoot || straddle;
	}
	
	/**
	 * Checks if mario is hitting the block from the left
	 * @param qBlock Question mark block
	 * @return True or false
	 */
	boolean hitLeftSide(QBlock qBlock)
	{
		boolean head = false;
		boolean foot = false;
		boolean straddle = false;
		
		if(position.y >= qBlock.y) //added if statement that fixed random clipping
		{
			if(position.x < qBlock.x && position.x + 16 > qBlock.x)
			{
				head = qBlock.y < position.y + 20 && qBlock.y + 16 > position.y + 20;
				foot = qBlock.y < position.y && qBlock.y + 16 > position.y;
				straddle = qBlock.y > position.y && qBlock.y + 16 < position.y + 20;
			}
		}
		return head || foot || straddle;
	}
	
	/**
	 * Checks if mario is hitting block from the right
	 * @param qBlock Question mark block
	 * @return True or false
	 */
	boolean hitRightSide(QBlock qBlock)
	{
		boolean head = false;
		boolean foot = false;
		boolean straddle = false;
		
		if(position.y >= qBlock.y) //added if statement that fixed random clipping
		{
			if(position.x < qBlock.x + 16 && position.x + 16 > qBlock.x + 16)
			{
				head = qBlock.y < position.y + 20 && qBlock.y + 16 > position.y + 20;
				foot = qBlock.y < position.y && qBlock.y + 16 > position.y;
				straddle = qBlock.y > position.y && qBlock.y + 16 < position.y + 20;
			}
		}
		return head || foot || straddle;
	}
	
	/**
	 * Logic for when mario is hurt by a goomba
	 */
	public void hitEnemy()
	{
		System.out.println("Ouch!");
		if(size == Size.BIG)
		{
			size = size.SMALL;
			startInvulnerability();
		}
		else
			init();
		//To Do: do a little recoil of mario when he hits an enemy or make him invulnerable for < 1 sec
	}
	
	private void startInvulnerability()
	{
		invulStartTime = TimeUtils.nanoTime();
		hitFlag = false;
		continueInvulnerability();
	}
	
	private void continueInvulnerability()
	{
		if(MathUtils.nanoToSec * (TimeUtils.nanoTime() - invulStartTime) > 1)
			hitFlag = true;
	}
	
	public void killEnemy(Goomba g)
	{
		level.getEnemies().removeValue(g, true);
		velocity.y = 256;
		System.out.println("RIP");
		//To Do: make mario jump a bit when he steps on a goomba
	}
	
	public void render(SpriteBatch batch)
	{
		if(size == Size.SMALL)
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
		else
		{
			if(facing == Facing.LEFT)
			{
				if(jumpState == JumpState.JUMPING || jumpState == JumpState.FALLING)
					//batch.draw(marioLJump, position.x, position.y);
				//else if(jumpState == JumpState.FALLING)
					batch.draw(marioLFallBig, position.x, position.y);
				else
					batch.draw(marioLBig, position.x, position.y);
			}
			else
			{
				if(jumpState == JumpState.JUMPING ||jumpState == JumpState.FALLING)
					//batch.draw(marioRJump, position.x, position.y);
				//else if(jumpState == JumpState.FALLING)
					batch.draw(marioRFallBig, position.x, position.y);
				else
					batch.draw(marioRBig, position.x, position.y);
			}
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
		marioLBig.dispose();
		marioRBig.dispose();
		marioLFallBig.dispose();
		marioRFallBig.dispose();
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
	
	enum Size
	{
		BIG,
		SMALL
	}
}
