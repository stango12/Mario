package com.stango.mario.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Platform 
{
	int x, y, width;
	//all tiles are 16x16
	private Texture topLeft, topMid, topRight, botLeft, botMid, botRight;
	
	public Platform(int x, int y, int w)
	{
		this.x = x;
		this.y = y;
		width = w;
		topLeft = new Texture(Gdx.files.internal("topLeftGround.png"));
		topMid = new Texture(Gdx.files.internal("topMidGround.png"));
		topRight = new Texture(Gdx.files.internal("topRightGround.png"));
		botLeft = new Texture(Gdx.files.internal("botLeftGround.png"));
		botMid = new Texture(Gdx.files.internal("botMidGround.png"));
		botRight = new Texture(Gdx.files.internal("botRightGround.png"));
	}
	
	public Rectangle getCollidingRectangle()
	{
		return new Rectangle(x, y, width, 32);
	}
	
	public void render(SpriteBatch batch)
	{
		//calculating how many body blocks needed for pipe
		int bodyWidth = width - 32;
		int bodyCount = bodyWidth / 16;
		int positionX = x;
		
		//drawing the left side of the body and head
		batch.draw(botLeft, x, y);
		batch.draw(topLeft, x, y + 16);
		positionX += 16;
		
		for(int i = 0; i < bodyCount; i++)
		{
			//drawing the body sections to create pipe length
			batch.draw(botMid, positionX, y);
			batch.draw(topMid, positionX, y + 16);
			positionX += 16;
		}
		
		batch.draw(botRight, positionX, y);
		batch.draw(topRight, positionX, y + 16);
	}
	
	public void dispose()
	{
		botLeft.dispose();
		botMid.dispose();
		botRight.dispose();
		topLeft.dispose();
		topMid.dispose();
		topRight.dispose();
	}
}
