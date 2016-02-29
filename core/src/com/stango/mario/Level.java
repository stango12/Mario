package com.stango.mario;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.stango.mario.entities.*;


public class Level 
{
	Mario mario;
	Array<QBlock> qBlocks;
	Goomba g;
	
	public Level()
	{
		mario = new Mario();
		qBlocks = new Array<QBlock>();
		qBlocks.add(new QBlock(128, 70, QBlock.PowerUp.COIN));
		qBlocks.add(new QBlock(112, 55, QBlock.PowerUp.COIN));
		qBlocks.add(new QBlock(144, 55, QBlock.PowerUp.COIN));
		g = new Goomba(new Vector2(0, 0));
	}
	
	public void update(float delta)
	{
		mario.update(delta, qBlocks);
			g.update(delta);
	}
	
	public void render(SpriteBatch batch)
	{
		batch.begin();
		for(int i = 0; i < qBlocks.size; i++)
			qBlocks.get(i).render(batch);	
		g.render(batch);
		mario.render(batch);
		batch.end();
	}
	
	public void dispose()
	{
		mario.dispose();
		for(int i = 0; i < qBlocks.size; i++)
			qBlocks.get(i).dispose();
	}
}
