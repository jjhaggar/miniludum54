package com.blogspot.ludumdaresforfun.aholenewrace;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class AHoleNewRace extends Game {
	SpriteBatch batch;
	Texture img;
    static AHoleNewRace instance;
    //public MenuScreen MENU_SCREEN;
    public IntroScreen INTRO_SCREEN;
    //public EndingScreen ENDING_SCREEN;
    //public GameOverScreen GAMEOVER_SCREEN;

    public MainScreen MAIN_SCREEN;

    @Override
    public void create() {
        instance = this;
	    Assets.loadMusicAndSound();
	    Assets.loadAnimation();
        this.setScreen(new IntroScreen());			//(new MenuScreen());

    }

    @Override
    public void render() {
        super.render();
    }

    public static AHoleNewRace getInstance() {
        return instance;
    }
}
