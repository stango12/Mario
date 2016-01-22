package com.stango.mario;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.stango.mario.entities.*;


public class Level 
{
	Mario mario;
	Array<QBlock> qBlocks;
	
	public Level()
	{
		mario = new Mario();
		qBlocks = new Array<QBlock>();
		qBlocks.add(new QBlock(128, 70, QBlock.PowerUp.COIN));
	}
	
	public void update(float delta)
	{
		mario.update(delta, qBlocks);
	}
	
	public void render(SpriteBatch batch)
	{
		batch.begin();
		mario.render(batch);
		for(int i = 0; i < qBlocks.size; i++)
			qBlocks.get(i).render(batch);
		batch.end();
	}
	
	public void dispose()
	{
		mario.dispose();
		for(int i = 0; i < qBlocks.size; i++)
			qBlocks.get(i).dispose();
	}
}
