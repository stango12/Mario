package com.stango.mario;

import com.badlogic.gdx.Game;

public class Platformer extends Game
{

    @Override
    public void create() {
        setScreen(new GameplayScreen());
    }

}
