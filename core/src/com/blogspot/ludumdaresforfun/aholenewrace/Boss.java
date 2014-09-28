package com.blogspot.ludumdaresforfun.aholenewrace;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class Boss extends Image {
	final float VELOCITY = 50f;
	final float ATTACK_VELOCITY = 120f;
	final float MAX_VELOCITY = 120f;
	final float JUMP_VELOCITY = 400f;
	final int ACTIVATE_DISTANCE = 250;
	final int MAX_LIFES = 24;

	enum State {
		Standing, Walking, Jumping, Falling, Attack, Summon, Hurting, BeingHit, Die, Running
	}

	enum FlowState {
		WalkingLeft, WalkingRight, Jumping, Transition, Attack, Summon, BeingHurt, Die, Standing
	}

	Vector2 desiredPosition = new Vector2();
	final Vector2 velocity = new Vector2();
	State state = State.Standing;
	FlowState flowState = FlowState.WalkingLeft;
	boolean facesRight = true;
	public boolean updateVelocity;
	public boolean setToDie = false;
	public boolean grounded;
	public Counter counter = new Counter(this.MAX_LIFES); // the same as megaman
															// enemies

	public int lifesToGain = 0;
	public float lifesTimer = 0f;

	public enum Direction {
		Left, Right
	}

	public Direction dir = Direction.Right;

	public Rectangle rect = new Rectangle();

	public int diffInitialPos = 0;
	public final int RANGE = 100;
	public final int ATTACK_DISTANCE = 15 * 16;

	protected Animation animation = null;
	float stateTime = 0;
	float flowTime = 0;
	public float offSetX;
	public boolean invincible = false;
	public boolean noControl = false;
	public boolean dead = false;
	public boolean shooting = false;
	public boolean toggle = false;
	public float offSetY;
	public float rightOffset = 0;
	// public AtlasRegion actualFrame;
	public AtlasRegion actualFrame;
	float superWidth, superHeight;
	public long lastTimeLeftBoss = -1L;
	public long lastTimeRightBoss = -1L;
	public boolean run = false;

	public Boss(Animation animation) {
		super(animation.getKeyFrame(0));
		this.animation = animation;
		this.actualFrame = ((AtlasRegion) animation.getKeyFrame(0));
		this.offSetX = this.actualFrame.offsetX;
		this.offSetY = this.actualFrame.offsetY;
		superWidth = this.actualFrame.packedWidth;
		superHeight = this.actualFrame.packedHeight;
	}

	public Rectangle getRect() {
		this.rect.set((this.getX() + this.actualFrame.offsetX) - this.offSetX,
				(this.getY() + this.actualFrame.offsetY) - this.offSetY,
				this.actualFrame.packedWidth, this.actualFrame.packedHeight);
		return this.rect;
	}

	public Rectangle getRect2() {
		this.rect.set(this.getX(), this.getY(), superWidth, superHeight - 2);
		return this.rect;
	}

	public void beingHit() {
		if (!this.invincible) {
			Assets.playSound("bossHurt");
			this.invincible = true;
			this.state = Boss.State.BeingHit;
			this.stateTime = 0;
			//this.velocity.y = 150;
			//this.noControl = true;
			int lifes = this.counter.lostLife();
			if (lifes <= 0) {
				this.die();
			}
			Timer.schedule(new Task() {
				@Override
				public void run() {
					Boss.this.invincible = false;
					Boss.this.state = Boss.State.Standing;
				}
			}, 1);
		}
	}

	public void die() {
		// animate, sound and set to die
		Assets.playSound("bossDead");
		this.setToDie = true;
		System.out.println("YOU KILL THE BOSS");
		this.state = Boss.State.Die;
		this.stateTime = 0;
		this.noControl = true;
		this.dead = true;
	}

	public int getLifes() {
		return this.counter.currentLifes;
	}

	@Override
	public void act(float delta) {
		((TextureRegionDrawable) this.getDrawable()).setRegion(this.animation.getKeyFrame(
				this.stateTime += delta, true));
		super.act(delta);
	}

}