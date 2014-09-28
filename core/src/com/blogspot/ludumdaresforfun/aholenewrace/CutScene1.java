package com.blogspot.ludumdaresforfun.aholenewrace;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class CutScene1 extends BaseScreen{

    BGAnimated bg;
    private ConfigControllers configControllers;

    public CutScene1() {

    	this.bg = new BGAnimated(Assets.intro_screen_logo);		//put cutscene1
    	this.stage.addActor(this.bg);

    	this.stage.addAction(new Action() {
            @Override
            public boolean act(float delta) {
            	CutScene1.this.bg.act(delta);

                if (Assets.intro_screen_logo.getKeyFrameIndex(CutScene1.this.bg.stateTime) == Assets.intro_screen_logo.keyFrames.length - 1) {
                	 AHoleNewRace.getInstance().MAIN_SCREEN = new MainScreen(2);
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
