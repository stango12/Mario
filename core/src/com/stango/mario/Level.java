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
		qBlocks.add(new QBlock(128, 70, QBlock.PowerUp.COIN));
		qBlocks.add(new QBlock(112, 55, QBlock.PowerUp.COIN));
		qBlocks.add(new QBlock(144, 55, QBlock.PowerUp.COIN));
		qBlocks.add(new QBlock(200, 55, QBlock.PowerUp.MUSHROOM));
		g = new Goomba(new Vector2(100, 0));
		goombas = new Array<Goomba>();
		goombas.add(g);
	}
	public void update(float delta)
	{
		mario.update(delta, qBlocks);
		for(Goomba g : goombas)
			g.update(delta);
		for(QBlock q : qBlocks)
			q.update(delta);
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
