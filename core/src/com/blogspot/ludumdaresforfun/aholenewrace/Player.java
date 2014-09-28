package com.blogspot.ludumdaresforfun.aholenewrace;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class Player extends Image {
    float MAX_VELOCITY = 120f;
    float JUMP_VELOCITY = 320f; // 300f; // 210f;
    final int MAX_LIFES = 5;
    enum State {
        Standing, Walking, Jumping, StandingShooting, Attacking, Intro, BeingHit, Die, Running
    }
    Vector2 desiredPosition = new Vector2();
    final Vector2 velocity = new Vector2();
    State state = State.Walking;
    boolean facesRight = true;
    boolean grounded = true;
    public boolean updateVelocity;
    public boolean shooting = false;
    public boolean invincible = false;
    public boolean noControl = true;
    public boolean dead = false;
    public int toggle = 0;

    public Counter counter = new Counter(this.MAX_LIFES);


    public Rectangle rect = new Rectangle();
    protected Animation animation = null;
    float stateTime = 0;
    public float offSetX;
    public float offSetY;
    public float rightOffset = 0;
    public AtlasRegion actualFrame;
    float superWidth, superHeight;

	public long lastTimeRightPlayer = -1;
	public long lastTimeLeftPlayer = -1;
	public boolean run = false;
	public boolean pushedBack = false;

    public Player(Animation animation) {
        super(animation.getKeyFrame(0));
        this.animation = animation;
        this.actualFrame = ((AtlasRegion)Assets.playerWalk.getKeyFrame(0));
		this.offSetX = this.actualFrame.offsetX;
		this.offSetY = this.actualFrame.offsetY;
		superWidth = this.actualFrame.packedWidth;
		superHeight = this.actualFrame.packedHeight;
    }

    public Rectangle getRect() {
    	this.rect.set((this.getX() + this.actualFrame.offsetX) - this.offSetX, (this.getY() + this.actualFrame.offsetY) - this.offSetY , this.actualFrame.packedWidth, this.actualFrame.packedHeight);
        return this.rect;
    }

    public Rectangle getRect2() {
    	this.rect.set(this.getX(), this.getY(), superWidth, superHeight - 2);
    	//in the future center - halfwidth, center - halfheight, width, height
        return this.rect;
    }

 //   @Override
 //   public float getWidth(){
 //   	return 24f;		//taken from picture
 //   }

    public void beingHit() {
        if (!this.invincible) {
            Assets.playSound("playerHurt");
            this.invincible = true;

            this.state = Player.State.BeingHit;
            this.stateTime = 0;
            this.velocity.y = 150;
            this.noControl = true;
            this.pushedBack  = true;

            int lifes = this.counter.lostLife();
            if (lifes <= 0) {
                this.die();
            }
            Timer.schedule(new Task() {
                @Override
                public void run() {
                    Player.this.invincible = false;
                    Player.this.state = Player.State.Standing;
                }
            }, 1.8f);
        }

    }

    public void die() {
        this.state = Player.State.Die;
        this.stateTime = 0;
        this.noControl = true;
        this.dead = true;
    }

    public int getLifes() {
        return this.counter.currentLifes;
    }

    public float getVelocityX() {
    	return this.velocity.x;
    }

    @Override		//is not doing anything in this case
    public void act(float delta) {
        ((TextureRegionDrawable)this.getDrawable()).setRegion(this.animation.getKeyFrame(this.stateTime+=delta, true));
        super.act(delta);
    }

	public void revive() {
		if (this.dead){
			this.noControl = false;
			this.dead = false;
			this.state = Player.State.Standing;
			this.counter.gainLife(1);
			Assets.playSound("gainLifePlayer");
		}
	}

    public void powerUpInvincible(){
    	this.invincible = true;
    	Timer.schedule(new Task() {
            @Override
            public void run() {
                Player.this.invincible = false;
            }
        }, 7f);
    }

	public void powerUpJump() {
		this.JUMP_VELOCITY = 320f * 1.5f;		//the boss jumps 1.25f more but we put 1.5
		Timer.schedule(new Task() {
            @Override
            public void run() {
            	Player.this.JUMP_VELOCITY = 320f;
            }
        }, 10f);

	}

	public void powerUpVelocity() {
		this.MAX_VELOCITY = 120f * 1.5f;		//the boss jumps 1.25f more but we put 1.5
		Timer.schedule(new Task() {
            @Override
            public void run() {
            	Player.this.MAX_VELOCITY = 120f;
            }
        }, 10f);

	}

}
