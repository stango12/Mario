package com.stango.mario;

import com.badlogic.gdx.graphics.Camera;
import com.stango.mario.entities.Mario;

public class ChaseCam 
{
	private Camera cam;
	private Mario target;
	
	public ChaseCam(Camera c, Mario m)
	{
		cam = c;
		target = m;
	}
	
	public void update() 
	{
		cam.position.x = target.position.x;
	}
}
