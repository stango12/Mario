package com.stango.mario.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Pipe 
{
	//x and y are bottom left coordinate of pipe
	int x, y, height;
	Texture head, body;
	//head 32x33
	//body 30x17
	
	public Pipe(int x, int y, int h)
	{
		this.x = x;
		this.y = y;
		height = h;
		assert (height - 33) % 17 == 0;
		head = new Texture(Gdx.files.internal("pipeHead.png"));
		body = new Texture(Gdx.files.internal("pipeBody.png"));
	}
	
	public Rectangle getCollidingRectangle()
	{
		return new Rectangle(x, y, 30, height);
	}
	
	public void render(SpriteBatch batch)
	{
		//calculating how many body blocks needed for pipe
		int bodyHeight = height - 33;
		int bodyCount = bodyHeight / 17;
		int positionY = y;
		
		for(int i = 0; i < bodyCount; i++)
		{
			//drawing the body sections to create pipe length
			batch.draw(body, x + 1, positionY);
			positionY += 17;
		}
		
		batch.draw(head, x, positionY);
	}
	
	public void dispose()
	{
		head.dispose();
		body.dispose();
	}
}
