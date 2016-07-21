package com.stango.mario;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.stango.mario.entities.*;

//World 1-1
public class Level 
{
	Mario mario;
	Array<QBlock> qBlocks;
	Array<Goomba> goombas;
	Array<Pipe> pipes;
	Array<Platform> platforms;
	
	Goomba g;
	
	public Level()
	{
		mario = new Mario(this);
	}
	
	public void init()
	{

		qBlocks = new Array<QBlock>();
		goombas = new Array<Goomba>();
		pipes = new Array<Pipe>();
		platforms = new Array<Platform>();
		
		platforms.add(new Platform(-200, 0, 1200));
		
		//adding first part before pipes
		qBlocks.add(new QBlock(100, 82, QBlock.PowerUp.COIN));
		qBlocks.add(new QBlock(164, 82, QBlock.PowerUp.NONE));
		qBlocks.add(new QBlock(180, 82, QBlock.PowerUp.MUSHROOM));
		qBlocks.add(new QBlock(196, 82, QBlock.PowerUp.NONE));
		qBlocks.add(new QBlock(212, 82, QBlock.PowerUp.COIN));
		qBlocks.add(new QBlock(228, 82, QBlock.PowerUp.NONE));
		qBlocks.add(new QBlock(196, 146, QBlock.PowerUp.COIN));
		
		//blocks one block between two things as big mario logic isn't working completely
//		qBlocks.add(new QBlock(196, 18, QBlock.PowerUp.NONE));
//		qBlocks.add(new QBlock(212, 18, QBlock.PowerUp.COIN));
//		qBlocks.add(new QBlock(228, 18, QBlock.PowerUp.NONE));
		
		goombas.add(new Goomba(new Vector2(164, 32)));
		
		pipes.add(new Pipe(292, 32, 50));
		pipes.add(new Pipe(452, 32, 67));
		goombas.add(new Goomba(new Vector2(550, 32)));
		pipes.add(new Pipe(580, 32, 84));
		goombas.add(new Goomba(new Vector2(676, 32)));
		goombas.add(new Goomba(new Vector2(700, 32)));
		pipes.add(new Pipe(756, 32, 84));
		platforms.add(new Platform(1050, 0, 240));
		
		qBlocks.add(new QBlock(1146, 82, QBlock.PowerUp.NONE));
		qBlocks.add(new QBlock(1162, 82, QBlock.PowerUp.MUSHROOM));
		qBlocks.add(new QBlock(1178, 82, QBlock.PowerUp.NONE));
		goombas.add(new Goomba(new Vector2(1175, 100)));
		
		qBlocks.add(new QBlock(1194, 160, QBlock.PowerUp.NONE));
		qBlocks.add(new QBlock(1210, 160, QBlock.PowerUp.NONE));
		qBlocks.add(new QBlock(1226, 160, QBlock.PowerUp.NONE));
		qBlocks.add(new QBlock(1242, 160, QBlock.PowerUp.NONE));
		qBlocks.add(new QBlock(1258, 160, QBlock.PowerUp.NONE));
		qBlocks.add(new QBlock(1274, 160, QBlock.PowerUp.NONE));
		qBlocks.add(new QBlock(1290, 160, QBlock.PowerUp.NONE));
		qBlocks.add(new QBlock(1306, 160, QBlock.PowerUp.NONE));
		goombas.add(new Goomba(new Vector2(1226, 176)));
		
		platforms.add(new Platform(1338, 0, 2000));
		qBlocks.add(new QBlock(1370, 160, QBlock.PowerUp.NONE));
		qBlocks.add(new QBlock(1386, 160, QBlock.PowerUp.NONE));
		qBlocks.add(new QBlock(1402, 160, QBlock.PowerUp.NONE));
		qBlocks.add(new QBlock(1418, 160, QBlock.PowerUp.COIN));
		qBlocks.add(new QBlock(1418, 82, QBlock.PowerUp.COIN)); //NOTE: This block should have multiple coins
		goombas.add(new Goomba(new Vector2(1434, 32)));
		goombas.add(new Goomba(new Vector2(1458, 32)));
		
		qBlocks.add(new QBlock(1514, 82, QBlock.PowerUp.NONE));
		qBlocks.add(new QBlock(1530, 82, QBlock.PowerUp.NONE)); //star powerup here
		
		qBlocks.add(new QBlock(1610, 82, QBlock.PowerUp.COIN));
		qBlocks.add(new QBlock(1658, 82, QBlock.PowerUp.COIN));
		qBlocks.add(new QBlock(1658, 160, QBlock.PowerUp.MUSHROOM));
		qBlocks.add(new QBlock(1706, 82, QBlock.PowerUp.COIN));
	
		qBlocks.add(new QBlock(1802, 82, QBlock.PowerUp.NONE));
		qBlocks.add(new QBlock(1850, 160, QBlock.PowerUp.NONE));
		qBlocks.add(new QBlock(1866, 160, QBlock.PowerUp.NONE));
		qBlocks.add(new QBlock(1882, 160, QBlock.PowerUp.NONE));
		goombas.add(new Goomba(new Vector2(1885, 32)));
		goombas.add(new Goomba(new Vector2(1909, 32)));
		
		goombas.add(new Goomba(new Vector2(1952, 32)));
		goombas.add(new Goomba(new Vector2(1976, 32)));
		qBlocks.add(new QBlock(1962, 160, QBlock.PowerUp.NONE));
		qBlocks.add(new QBlock(1978, 160, QBlock.PowerUp.COIN));
		qBlocks.add(new QBlock(1994, 160, QBlock.PowerUp.COIN));
		qBlocks.add(new QBlock(2010, 160, QBlock.PowerUp.NONE));
		qBlocks.add(new QBlock(1978, 82, QBlock.PowerUp.NONE));
		qBlocks.add(new QBlock(1994, 82, QBlock.PowerUp.NONE));
		
	}
	public void update(float delta)
	{
		mario.update(delta, qBlocks, pipes, platforms);
		for(Goomba g : goombas)
		{
			g.update(delta, qBlocks, platforms);
			if(g.position.y < -20)
			{
				goombas.removeValue(g, true);
				g.dispose();
			}
		}
		for(QBlock q : qBlocks)
			q.update(delta, qBlocks, platforms, pipes);
		
		
	}
	
	public void render(SpriteBatch batch)
	{
		batch.begin();
		for(Platform p : platforms)
			p.render(batch);
		for(QBlock q : qBlocks)
			q.render(batch);	
		for(Goomba g : goombas)
			g.render(batch);
		for(Pipe p : pipes)
			p.render(batch);

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
		for(QBlock q : qBlocks)
			q.dispose();	
		for(Goomba g : goombas)
			g.dispose();
		for(Pipe p : pipes)
			p.dispose();
	}
}
