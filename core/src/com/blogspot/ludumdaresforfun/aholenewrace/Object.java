package com.blogspot.ludumdaresforfun.aholenewrace;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Object extends Image{

    enum Type {
    	item_apple, item_banana, item_chicken, item_invulnerability, item_jump, item_speed;
    }

    enum State {
    	Object
    }

    public Type objectType;

    State state = State.Object;

    public Rectangle rect = new Rectangle();

    protected Animation animation = null;
    float stateTime = 0;
	float offSetX;
	float offsetY;
	public AtlasRegion actualFrame;

    public Object(Vector2 position, Animation animation, Type type) {
        super(animation.getKeyFrame(0));
        this.animation = animation;
        this.actualFrame = ((AtlasRegion)animation.getKeyFrame(0));
        this.setPosition(position.x, position.y);
        this.objectType = type;
    }

    public Rectangle getRect() {
        this.rect.set(this.getX(), this.getY(),this.actualFrame.packedWidth, this.actualFrame.packedHeight);
        return this.rect;

    }

    @Override
    public void act(float delta) {
        ((TextureRegionDrawable)this.getDrawable()).setRegion(this.animation.getKeyFrame(this.stateTime+=delta, true));
        super.act(delta);
    }
}
