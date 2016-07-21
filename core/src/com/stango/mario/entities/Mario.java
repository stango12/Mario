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

//TODO: fix block logic for big mario. can go under one block high spaces.

public class Mario 
{
	//small mario is 14x20, falling 16x20
	//big mario is 15x28, falling 16x29
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
		init();
	}
	
	/**
	 * Initialize everything, can be used to reset the level
	 */
	public void init()
	{
		position = new Vector2(20,32);
		velocity = new Vector2();
		lastPosition = new Vector2(position);
		facing = Facing.RIGHT;
		jumpState = JumpState.GROUNDED;
		hitFlag = true;
		size = Size.SMALL;
		level.init();
	}
	
	/**
	 * All the logic for mario
	 * @param delta
	 * @param qBlocks array of qblocks in the level
	 * @param pipes array of pipes in the level
	 */
	public void update(float delta, Array<QBlock> qBlocks, Array<Pipe> pipes, Array<Platform> platforms)
	{
		lastPosition.set(position);
		
		//simulating gravity
		velocity.y -= 25;
		position.mulAdd(velocity, delta);
		
		//mario collision detection
		Rectangle marioCollision;
		if(size == Size.SMALL)
			marioCollision = new Rectangle(position.x, position.y, 14, 20);
		else
			marioCollision = new Rectangle(position.x, position.y, 14, 28);
		
		//jump logic
		if(jumpState != JumpState.JUMPING)
		{
			jumpState = JumpState.FALLING;
			//ground logic
			for(int i = 0; i < platforms.size; i++)
			{
				if(marioCollision.overlaps(platforms.get(i).getCollidingRectangle()) && lastPosition.y >= platforms.get(i).y + 32)
				{
					jumpState = JumpState.GROUNDED;
					velocity.y = 0;
					position.y = platforms.get(i).y + 32;
				}
			}
			if(position.y < 0)
			{
				init();
			}
		}
		
		//block logic
		for(int i = 0; i < qBlocks.size; i++)
		{
			//block size is 16x16
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
		
		//pipe logic
		for(int i = 0; i < pipes.size; i++)
		{
			if(landedOnPipe(pipes.get(i)))
			{
				jumpState = JumpState.GROUNDED;
				velocity.y = 0;
				position.y = pipes.get(i).y + pipes.get(i).height;
			}
			else if(hitLeftSide(pipes.get(i)))
			{
				position.x = pipes.get(i).x - 16;
			}
			else if(hitRightSide(pipes.get(i)))
			{
				position.x = pipes.get(i).x + 32;
			}
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
		
		//debug stuff
		if(Gdx.input.isKeyPressed(Keys.NUM_1))
		{
			init();
			position = new Vector2(1162, 32);
		}
		
		for(Goomba g : level.getEnemies())
		{
			if(Math.abs(position.x - g.position.x) <= 200) //checking if goombas are close enough to be updated
				g.active(true);
			else
				g.active(false);
			
			Rectangle enemyCollision = new Rectangle(g.position.x, g.position.y, 16, 15);
			if(marioCollision.overlaps(enemyCollision))
			{
				if(lastPosition.y > g.position.y + 15)
					killEnemy(g);
				else if(hitFlag)
					hitEnemy();
				else
					continueInvulnerability();
				g.changeDirection();
			}
			for(Pipe p : pipes)
			{
				if(p.getCollidingRectangle().overlaps(enemyCollision))
					g.changeDirection();
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
		
		//16 = block width, 14 = mario width, 16 = block height
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
			//20 = small  mario height
			if(lastPosition.y + 20 < qBlock.y && position.y + 20 > qBlock.y)
			{
				leftFoot = qBlock.x < position.x && qBlock.x + 16 > position.x;
				rightFoot = qBlock.x < position.x + 14 && qBlock.x + 16 > position.x + 14;
				straddle = qBlock.x > position.x && qBlock.x + 16 < position.x + 14;
			}
		}
		else
		{
			//28 = big mario height
			if(lastPosition.y + 28 < qBlock.y && position.y + 28 > qBlock.y)
			{
				//System.out.println("Flag");
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
		
		if(size == Size.SMALL)
		{
			if(position.y >= qBlock.y) //added if statement that fixed random clipping
			{
				if(position.x < qBlock.x && position.x + 16 > qBlock.x)
				{
					head = qBlock.y < position.y + 20 && qBlock.y + 16 > position.y + 20;
					foot = qBlock.y < position.y && qBlock.y + 16 > position.y;
					straddle = qBlock.y > position.y && qBlock.y + 16 < position.y + 20;
				}
			}
		}
		else
		{
			if(position.y >= qBlock.y) //added if statement that fixed random clipping
			{
				if(position.x < qBlock.x && position.x + 16 > qBlock.x)
				{
					head = qBlock.y < position.y + 28 && qBlock.y + 16 > position.y + 28;
					foot = qBlock.y < position.y && qBlock.y + 16 > position.y;
					straddle = qBlock.y > position.y && qBlock.y + 16 < position.y + 28;
				}
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
		
		if(size == Size.SMALL)
		{
			if(position.y >= qBlock.y) //added if statement that fixed random clipping
			{
				if(position.x < qBlock.x + 16 && position.x + 16 > qBlock.x + 16)
				{
					head = qBlock.y < position.y + 20 && qBlock.y + 16 > position.y + 20;
					foot = qBlock.y < position.y && qBlock.y + 16 > position.y;
					straddle = qBlock.y > position.y && qBlock.y + 16 < position.y + 20;
				}
			}
		}
		else
		{
			if(position.y >= qBlock.y) //added if statement that fixed random clipping
			{
				if(position.x < qBlock.x + 16 && position.x + 16 > qBlock.x + 16)
				{
					head = qBlock.y < position.y + 28 && qBlock.y + 16 > position.y + 28;
					foot = qBlock.y < position.y && qBlock.y + 16 > position.y;
					straddle = qBlock.y > position.y && qBlock.y + 16 < position.y + 28;
				}
			}
		}
		
		return head || foot || straddle;
	}
	
	/**
	 * Checks if mario is on top of a pipe
	 * @param pipe Pipe
	 * @return True or false
	 */
	boolean landedOnPipe(Pipe pipe)
	{
		boolean leftFoot = false;
		boolean rightFoot = false;
		boolean straddle = false;
		
		if(lastPosition.y >= pipe.y + pipe.height && position.y < pipe.y + pipe.height)
		{
			leftFoot = pipe.x < position.x && pipe.x + 32 > position.x;
			rightFoot = pipe.x < position.x + 14 && pipe.x + 32 > position.x + 14;
			straddle = pipe.x > position.x && pipe.x + 32 < position.x + 14;
		}
		
		return leftFoot || rightFoot || straddle;
	}
	
	/**
	 * Checks if mario is hitting the pipe from the left
	 * @param pipe pipe
	 * @return True or false
	 */
	boolean hitLeftSide(Pipe pipe)
	{
		boolean head = false;
		boolean foot = false;
		boolean straddle = false;
		
		if(position.y >= pipe.y) //added if statement that fixed random clipping
		{
			if(position.x < pipe.x && position.x + 16 > pipe.x)
			{
				head = pipe.y < position.y + 20 && pipe.y + pipe.height > position.y + 20;
				foot = pipe.y < position.y && pipe.y + pipe.height > position.y;
				straddle = pipe.y > position.y && pipe.y + pipe.height < position.y + 20;
			}
		}
		return head || foot || straddle;
	}
	
	/**
	 * Checks if mario is hitting pipe from the right
	 * @param pipe pipe
	 * @return True or false
	 */
	boolean hitRightSide(Pipe pipe)
	{
		boolean head = false;
		boolean foot = false;
		boolean straddle = false;
		
		if(position.y >= pipe.y) //added if statement that fixed random clipping
		{
			if(position.x < pipe.x + 32 && position.x + 16 > pipe.x + 32)
			{
				head = pipe.y < position.y + pipe.height && pipe.y + pipe.height > position.y + 20;
				foot = pipe.y < position.y && pipe.y + pipe.height > position.y;
				straddle = pipe.y > position.y && pipe.y + pipe.height < position.y + 20;
			}
		}
		return head || foot || straddle;
	}
	
	/**
	 * Logic for when mario is hurt by a goomba
	 */
	public void hitEnemy()
	{
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
		g.dispose();
		velocity.y = 256;
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
