package com.blogspot.ludumdaresforfun.aholenewrace;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class IntroScreen extends BaseScreen{

    BGAnimated bg;
    private ConfigControllers configControllers;

    public IntroScreen() {

    	this.bg = new BGAnimated(Assets.intro_BADLY_DONE);
    	this.stage.addActor(this.bg);

    	this.stage.addAction(new Action() {
            @Override
            public boolean act(float delta) {
                IntroScreen.this.bg.act(delta);

                if (Assets.intro_BADLY_DONE.getKeyFrameIndex(IntroScreen.this.bg.stateTime) == Assets.intro_BADLY_DONE.keyFrames.length - 1) {
                	 AHoleNewRace.getInstance().MAIN_SCREEN = new MainScreen(1);
                     AHoleNewRace.getInstance().setScreen(AHoleNewRace.getInstance().MAIN_SCREEN);
                    return true;
                }
                return false;
            }
        });

        //this.configControllers = new ConfigControllers(this);
        //this.configControllers.init();
    }

	@Override
	public void backButtonPressed() {
        Assets.dispose();
        Gdx.app.exit();
	}

    @Override
    public void enterButtonPressed() {
        this.configControllers.terminate();
        // LD.getInstance().setScreen(new CreditsScreen());
    }

	@Override
	public void resize (int width, int height) {
		this.stage.setViewport(new FitViewport(432, 240, this.stage.getCamera()));
		this.stage.getViewport().update(width, height, true);
	}

}
