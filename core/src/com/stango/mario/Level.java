package com.stango.mario;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.stango.mario.entities.*;


public class Level 
{
	Mario mario;
	Array<QBlock> qBlocks;
	Array<Goomba> goombas;
	Goomba g;
	
	public Level()
	{
		mario = new Mario(this);
	}
	
	public void init()
	{

		qBlocks = new Array<QBlock>();
		goombas = new Array<Goomba>();
		
		//adding first part before pipes
		qBlocks.add(new QBlock(100, 50, QBlock.PowerUp.COIN));
		qBlocks.add(new QBlock(164, 50, QBlock.PowerUp.NONE));
		qBlocks.add(new QBlock(180, 50, QBlock.PowerUp.MUSHROOM));
		qBlocks.add(new QBlock(196, 50, QBlock.PowerUp.NONE));
		qBlocks.add(new QBlock(212, 50, QBlock.PowerUp.COIN));
		qBlocks.add(new QBlock(228, 50, QBlock.PowerUp.NONE));
		qBlocks.add(new QBlock(196, 114, QBlock.PowerUp.COIN));
		
		goombas.add(new Goomba(new Vector2(164, 0)));
	}
	public void update(float delta)
	{
		mario.update(delta, qBlocks);
		for(Goomba g : goombas)
			g.update(delta);
		for(QBlock q : qBlocks)
			q.update(delta, qBlocks);
	}
	
	public void render(SpriteBatch batch)
	{
		batch.begin();
		for(int i = 0; i < qBlocks.size; i++)
			qBlocks.get(i).render(batch);	
		for(Goomba g : goombas)
			g.render(batch);
		mario.render(batch);
		batch.end();
	}
	
	public Array<Goomba> getEnemies()
	{
		return goombas;
	}
	
	public void dispose()
	{
		mario.dispose();
		for(int i = 0; i < qBlocks.size; i++)
			qBlocks.get(i).dispose();
	}
}
