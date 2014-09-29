package com.blogspot.ludumdaresforfun.aholenewrace;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class CutScenes extends BaseScreen {

	static int playerPoints = 0;
	static int bossPoints = 0;
	Background bg;
	int currentSceneIndex = -1;
	int winnerForEnding;

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
		// draw visual contents
		bg = new Background(Math.random() * 10, new int[] { playerPoints, bossPoints });
		currentSceneIndex = sceneIndex;
		stage.addActor(bg);
		// debug
		System.out.println("Cut scene. Map ended: " + sceneIndex + " Winner: " + winner);
		System.out.println("\tPlayer points: " + playerPoints);
		System.out.println("\tBoss points: " + bossPoints);
		System.out.println("\tlevel 1 winner: " + levelWinner[0]);
		System.out.println("\tlevel 2 winner: " + levelWinner[1]);
		System.out.println("\tlevel 3 winner: " + levelWinner[2]);
		// schedule next screen change
		final int sceneIndexFinal = sceneIndex;

		if (playerPoints > bossPoints)
			winnerForEnding = 0;
		else if (playerPoints < bossPoints)
			winnerForEnding = 1;
		else
			winnerForEnding = 2;

		this.stage.addAction(Actions.sequence(Actions.delay(5f), new Action() {

			@Override
			public boolean act(float delta) {
				if (sceneIndexFinal != 3) {
					AHoleNewRace.getInstance().MAIN_SCREEN = new MainScreen(sceneIndexFinal + 1);
					AHoleNewRace.getInstance().setScreen(AHoleNewRace.getInstance().MAIN_SCREEN);
				} else {
					AHoleNewRace.getInstance().ENDING_SCREEN = new EndingScreen(winnerForEnding);
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
			AHoleNewRace.getInstance().ENDING_SCREEN = new EndingScreen(this.winnerForEnding);
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
		TextureRegion tr1 = Assets.CutScenePoints1P.getKeyFrame(0);
		TextureRegion tr2 = Assets.CutScenePoints2P.getKeyFrame(0);
		float random;
		int[] scores;

		public Background(double random, int[] scores) {
			this.random = (float) random;
			this.scores = scores;
		}

		@Override
		public void draw(Batch batch, float parentAlpha) {
			batch.draw(bg.getKeyFrame(random), 0, 0);
			for (int i = 1; i <= scores[0]; i++) {
				batch.draw(tr1, Assets.offsetCutScene1PX + i * 10, Assets.offsetCutScene1PY);
			}
			for (int i = 1; i <= scores[1]; i++) {
				batch.draw(tr2, Assets.offsetCutScene2PX + i * 10, Assets.offsetCutScene2PY);
			}
		}

		@Override
		public void act(float delta) {
			super.act(delta);
		}

	}
}
