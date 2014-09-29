package com.blogspot.ludumdaresforfun.aholenewrace;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class CutScenes extends BaseScreen {

	static int playerPoints = 0;
	static int bossPoints = 0;
	Background bg;
	int currentSceneIndex = -1;

	/**
	 * Values "boss" or "player". Indexes from 0 (level 1) to 2 (level 3)
	 */
	static String[] levelWinner = new String[] { "", "", "" };

	/**
	 * 
	 * @param sceneIndex
	 *            (1, 2 or 3)
	 * @param winner
	 *            ("player" or "boss")
	 */
	public CutScenes(int sceneIndex, String winner) {
		bg = new Background(Math.random() * 10);
		currentSceneIndex = sceneIndex;
		stage.addActor(bg);
		// reset scores
		if (sceneIndex == 1) {
			playerPoints = 0;
			bossPoints = 0;
			levelWinner = new String[] { "", "", "" };
		}
		// apply score changes
		if (winner == "boss") {
			bossPoints += sceneIndex;
		} else if (winner == "player") {
			playerPoints += sceneIndex;
		}
		levelWinner[sceneIndex - 1] = winner;
		// debug
		System.out.println("Cut scene. Map ended: " + sceneIndex + " Winner: " + winner);
		System.out.println("\tPlayer points: " + playerPoints);
		System.out.println("\tBoss points: " + bossPoints);
		System.out.println("\tlevel 1 winner: " + levelWinner[0]);
		System.out.println("\tlevel 2 winner: " + levelWinner[1]);
		System.out.println("\tlevel 3 winner: " + levelWinner[2]);
		// schedule next screen change
		final int sceneIndexFinal = sceneIndex;
		this.stage.addAction(Actions.sequence(Actions.delay(5f), new Action() {

			@Override
			public boolean act(float delta) {
				if (sceneIndexFinal != 3) {
					AHoleNewRace.getInstance().MAIN_SCREEN = new MainScreen(sceneIndexFinal + 1);
					AHoleNewRace.getInstance().setScreen(AHoleNewRace.getInstance().MAIN_SCREEN);
				} else {
					AHoleNewRace.getInstance().ENDING_SCREEN = new EndingScreen();
					AHoleNewRace.getInstance().setScreen(AHoleNewRace.getInstance().ENDING_SCREEN);
				}
				return false;
			}
		}));
	}

	@Override
	public void backButtonPressed() {
		Assets.dispose();
		Gdx.app.exit();

	}

	@Override
	public void enterButtonPressed() {
		if (currentSceneIndex != 3) {
			AHoleNewRace.getInstance().MAIN_SCREEN = new MainScreen(currentSceneIndex + 1);
			AHoleNewRace.getInstance().setScreen(AHoleNewRace.getInstance().MAIN_SCREEN);
		} else {
			AHoleNewRace.getInstance().ENDING_SCREEN = new EndingScreen();
			AHoleNewRace.getInstance().setScreen(AHoleNewRace.getInstance().ENDING_SCREEN);
		}
	}

	@Override
	public void resize(int width, int height) {
		this.stage.setViewport(new FitViewport(432, 240, this.stage.getCamera()));
		this.stage.getViewport().update(width, height, true);
	}

	public class Background extends Actor {
		Animation bg = Assets.CutSceneBase;
		float random;

		public Background(double random) {
			this.random = (float) random;
			System.out.println("La animacion tiene " + bg.getKeyFrames().length + " frames");
		}

		@Override
		public void draw(Batch batch, float parentAlpha) {
			// batch.draw(bg.getKeyFrame(1), 0, 0);
		}

		@Override
		public void act(float delta) {
			super.act(delta);
		}
	}
}
