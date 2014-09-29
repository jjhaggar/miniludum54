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
	public CutScene1 CUT_SCENE1;
	public CutScene2 CUT_SCENE2;
	public CutScenes CUT_SCENES;
	public IntroScreen INTRO_SCREEN;
	public EndingScreen ENDING_SCREEN;
	// public GameOverScreen GAMEOVER_SCREEN;

	public MainScreen MAIN_SCREEN;

	@Override
	public void create() {
		instance = this;
		Assets.loadMusicAndSound();
		Assets.loadAnimation();
		this.setScreen(new MainScreen(1));// (IntroScreen()) (new MenuScreen())
											// (new MainScreen(1))

	}

	@Override
	public void render() {
		super.render();
	}

	public static AHoleNewRace getInstance() {
		return instance;
	}
}
