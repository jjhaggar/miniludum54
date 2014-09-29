package com.blogspot.ludumdaresforfun.aholenewrace;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class EndingScreen extends BaseScreen{

    BGAnimated bg;
    private ConfigControllers configControllers;

    public EndingScreen(int winner) {

    	if (winner == 0){
    		this.bg = new BGAnimated(Assets.Ending1P);		//put cutscene1
        	this.stage.addActor(this.bg);
    	}
    	else if (winner == 1){
    		this.bg = new BGAnimated(Assets.Ending2P);		//put cutscene1
        	this.stage.addActor(this.bg);
    	}
    	else if (winner == 2){
    		this.bg = new BGAnimated(Assets.EndingDraw);		//put cutscene1
        	this.stage.addActor(this.bg);
    	}

    	this.stage.addAction(new Action() {
            @Override
            public boolean act(float delta) {
            	EndingScreen.this.bg.act(delta);
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
