package com.blogspot.ludumdaresforfun.aholenewrace;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class MainScreen extends BaseScreen {

    public boolean pause = false;
    public boolean toggle = false;
	ConfigControllers configControllers;
	Rectangle playerRect;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
	public Player player;
	private ShapeRenderer shapeRenderer;
	private TiledMap map;
	private boolean normalGravity = true;
	private boolean bossActive = false;
	private float healingTimer = 50f;

	//private Array<Enemy> enemies = new Array<Enemy>();
	private Array<Rectangle> tiles = new Array<Rectangle>();
	private Array<Rectangle> spikes = new Array<Rectangle>();
	public Array<Shot> shotArray = new Array<Shot>();
	private Array<Vector2> spawns = new Array<Vector2>();
	private Array<Vector2> lifes = new Array<Vector2>();
	private boolean callGameOver = false;
	//private Boss boss;
	private Vector2 door;

	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject () {
            return new Rectangle();
		}
    };
    //HUD hud;

	private final float GRAVITY = -600f;  //-10 * 60
	final int SCREEN_HEIGHT = 240;
	final int SCREEN_WIDTH = 432;
	final int MAP_HEIGHT;
	final int MAP_WIDTH;
	final int POS_UPPER_WORLD;
	final int POS_LOWER_WORLD;
	final int DISTANCESPAWN = 410;

	final int TILED_SIZE;
	final float activateBossXPosition = 420;
	private float xRightBossWall = 420 + 200;
	private float xLeftBossWall = 420;

	public boolean bossCheckPoint = false;

	float UpOffset = 0;

	@Override
	public void backButtonPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void enterButtonPressed() {
		// TODO Auto-generated method stub

	}

	public MainScreen() {
		this.shapeRenderer = new ShapeRenderer();

		this.map = new TmxMapLoader().load("tilemap.tmx");
		this.MAP_HEIGHT = (Integer) this.map.getProperties().get("height");
		this.MAP_WIDTH = (Integer) this.map.getProperties().get("width");
		this.TILED_SIZE = (Integer) this.map.getProperties().get("tileheight");
		this.POS_LOWER_WORLD = ((this.MAP_HEIGHT / 2) * this.TILED_SIZE) - this.TILED_SIZE;
		this.POS_UPPER_WORLD = this.MAP_HEIGHT  * this.TILED_SIZE ;

		this.renderer = new OrthogonalTiledMapRenderer(this.map, 1);

		//Assets.dispose(); //TODO: for debugging

		this.camera = new OrthographicCamera();
		this.camera.setToOrtho(false, this.SCREEN_WIDTH, this.SCREEN_HEIGHT);
		this.camera.position.y = this.POS_UPPER_WORLD - this.MAP_HEIGHT;
		this.camera.update();

		this.player = new Player(Assets.playerStand);

		this.player.setPosition(200, 310);

        this.configControllers = new ConfigControllers(this);
        this.configControllers.init();

		TiledMapTileLayer layerSpawn = (TiledMapTileLayer)(this.map.getLayers().get("Spawns"));
		this.rectPool.freeAll(this.tiles);
		this.tiles.clear();

        for (int x = 0; x <= layerSpawn.getWidth(); x++) {
            for (int y = 0; y <= layerSpawn.getHeight(); y++) {
				Cell cell = layerSpawn.getCell(x, y);
				if (cell != null) {
				    String type = (String) cell.getTile().getProperties().get("type");
				    if (type != null) {
				        if (type.equals("enemy")) {
                            this.spawns.add(new Vector2(x * this.TILED_SIZE, y * this.TILED_SIZE));
                        }
				        else if (type.equals("pollo")) {
                            this.lifes.add(new Vector2(x * this.TILED_SIZE, y * this.TILED_SIZE));
                        }
				        else if (type.equals("player")) {
                            this.player.setPosition(x * this.TILED_SIZE, y * this.TILED_SIZE);
				        }
				        else if (type.equals("boss")) {
                            int a = 0;//this.boss.setPosition(x * this.TILED_SIZE, y * this.TILED_SIZE);
                        }
				        else if (type.equals("door")) {
                            this.door = new Vector2(x, y);
                        }
				    }
                }
            }
        }

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(.9f, .9f, .9f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		delta = Math.min(delta, 0.1f);

		//System.out.println("gravirty" + this.GRAVITY); TODO Player vibrarte when dead in down world
		this.updatePlayer(delta);
		this.player.act(delta);

		//this.activateBoss();


		if (!this.bossActive){
            //update x
            if ((this.player.getX() - (this.SCREEN_WIDTH / 2)) < this.TILED_SIZE)
            	this.camera.position.x = (this.SCREEN_WIDTH / 2) + this.TILED_SIZE;
            else if ((this.player.getX() + (this.SCREEN_WIDTH / 2)) > (this.MAP_WIDTH * this.TILED_SIZE))
            	this.camera.position.x =  (this.MAP_WIDTH * 16) - (this.SCREEN_WIDTH / 2);
            else
            	this.camera.position.x = this.player.getX();

            //update y
			if ((this.player.getY() - (this.SCREEN_HEIGHT / 2)) >= this.POS_LOWER_WORLD)
                this.camera.position.y = this.player.getY();
            else if (this.player.getY() > this.POS_LOWER_WORLD)
                this.camera.position.y = this.POS_LOWER_WORLD + (this.SCREEN_HEIGHT / 2);
            else if ((this.player.getY() + (this.SCREEN_HEIGHT / 2)) >= this.POS_LOWER_WORLD)
				this.camera.position.y = this.POS_LOWER_WORLD - (this.SCREEN_HEIGHT / 2);
			else
                this.camera.position.y = this.player.getY();

			this.camera.update();
		}


		/*
		if (this.spawns.size > 0) {
            Vector2 auxNextSpawn = this.spawns.first();
            if ((this.camera.position.x + this.DISTANCESPAWN) >= auxNextSpawn.x) {
                Enemy auxShadow = new Enemy(Assets.enemyWalk);
                if (auxNextSpawn.y < 240) {
                    auxNextSpawn.y -= 5; // Offset fixed collision
                }
                auxShadow.setPosition(auxNextSpawn.x, auxNextSpawn.y);
                auxShadow.state = Enemy.State.BeingInvoked;
                auxShadow.stateTime = 0;
                auxShadow.beingInvoked = true;
                this.enemies.add(auxShadow);
                this.spawns.removeIndex(0);
            }
		}
		*/

		//this.collisionLifes(delta);
		//this.updateEnemies(delta);
		this.renderer.setView(this.camera);
		this.renderer.render(new int[]{0, 1, 3});			//this line is totally a mistery

		//this.renderEnemies(delta);
		this.renderPlayer(delta);
		/*
		for (Shot shot : this.shotArray){
			if (shot != null)
				this.renderShot(shot, delta);
		}
		*/
		/*
		if (this.bossActive && (this.boss != null)) {
			this.updateBoss(delta);
			if (this.boss != null)
				this.renderBoss(delta);
		}
		this.renderHUD(delta);
		*/
	}

	private void updatePlayer(float deltaTime) {

		if (deltaTime == 0)
			return;
		this.player.stateTime += deltaTime;

		this.player.desiredPosition.x = this.player.getX();
		this.player.desiredPosition.y = this.player.getY();

		this.movingShootingJumping(deltaTime);
		this.gravityAndClamping(deltaTime);

		this.checkCollisionWalls(deltaTime);

		this.player.velocity.scl(deltaTime);

		//retreat if noControl //velocity y is changed in beingHit
		if (this.player.noControl && !(this.player.state.equals(Player.State.Die) && Assets.playerDie.isAnimationFinished(this.player.stateTime))){
			if (this.player.facesRight)
				this.player.velocity.x = -120f * deltaTime;
			else
				this.player.velocity.x = 120 * deltaTime;
		}

		//boolean collisionSpike = this.collisionWallsAndSpike();

		// unscale the velocity by the inverse delta time and set the latest position
		this.player.desiredPosition.add(this.player.velocity);
		this.player.velocity.scl(1 / deltaTime);

		if (Assets.playerBeingHit.isAnimationFinished(this.player.stateTime) && !this.player.dead)
			this.player.noControl = false;

		if (this.player.noControl == false)
			this.player.velocity.x *= 0;		//0 is totally stopped if not pressed

        this.player.setPosition(this.player.desiredPosition.x, this.player.desiredPosition.y);

        /*
		if (Assets.playerDie.isAnimationFinished(this.player.stateTime) && this.player.dead && !callGameOver){
			callGameOver = true;
            Timer.schedule(new Task() {
                @Override
                public void run() {
                    MainScreen.this.gameOver();
                }
            }, 1f);
            this.player.velocity.x = 0;
		}
		if (collisionSpike) {
		    this.player.beingHit();
		}
		*/
	}

	private void gravityAndClamping(float deltaTime) {
		if (this.normalGravity)
			this.player.velocity.add(0, this.GRAVITY  * deltaTime);
		else
			this.player.velocity.add(0, -this.GRAVITY * deltaTime);

		if (this.player.getY() < this.POS_LOWER_WORLD){
			//this.camera.position.y = this.POS_LOWER_WORLD;
			if (this.normalGravity == true){
				this.normalGravity = false;
				this.player.velocity.y = -this.player.JUMP_VELOCITY * 1.01f;	//3 tiles in both
			}
		}
		else {
			//this.camera.position.y = 0;//this.yPosUpperWorld;
			if (this.normalGravity == false){
				this.normalGravity = true;
				this.player.velocity.y = this.player.JUMP_VELOCITY / 1.3f;		//3 tiles in both
			}
		}

		// clamp the velocity to the maximum, x-axis only
		if (Math.abs(this.player.velocity.x) > this.player.MAX_VELOCITY) {
			this.player.velocity.x = Math.signum(this.player.velocity.x) * this.player.MAX_VELOCITY;
		}

		// clamp the velocity to 0 if it's < 1, and set the state to standign
		if (Math.abs(this.player.velocity.x) < 1) {
			this.player.velocity.x = 0;
			if (this.player.grounded && Assets.playerAttack.isAnimationFinished(this.player.stateTime) &&
					Assets.playerBeingHit.isAnimationFinished(this.player.stateTime) && !this.player.invincible)
				this.player.state = Player.State.Standing;
		}
	}

	private void checkCollisionWalls(float deltaTime) {
		TiledMapTileLayer layerTiles = (TiledMapTileLayer)(this.map.getLayers().get("Collisions"));
		this.rectPool.freeAll(this.tiles);
		this.tiles.clear();

		//stop him in X
		if (player.velocity.x > 0){
			int startX = (int)Math.ceil((player.getX() + player.getWidth()) / 16f);										//lo que viene
			int finalX = (int)Math.floor((player.getX() + player.getWidth() + player.velocity.x * deltaTime) / 16f);	//donde esté
			int startY = (int)Math.floor(player.getY() / 16f);
			int finalY = (int)Math.floor((player.getY() + player.getHeight()) / 16f);

			checkThisTiles(layerTiles, startX, finalX, startY, finalY, deltaTime);

		}
		else if (player.velocity.x < 0){
			int startX = (int)Math.floor((player.getX() + player.velocity.x * deltaTime) / 16f);										//lo que viene
			int finalX = (int)Math.floor((player.getX()) / 16f);	//donde esté
			int startY = (int)Math.floor(player.getY() / 16f);
			int finalY = (int)Math.floor((player.getY() + player.getHeight()) / 16f);

			checkThisTiles2(layerTiles, startX, finalX, startY, finalY, deltaTime);
		}
		else{ //velocity.x = 0
		}

		//Stop him in Y
		if (player.velocity.y > 0){
			int startX = (int)Math.floor(player.getX() / 16f);										//lo que viene
			int finalX = (int)Math.floor((player.getX() + player.getWidth()) / 16f);	//donde esté
			int startY = (int)Math.floor((player.getY()) / 16f);
			int finalY = (int)Math.floor((player.getY() + player.getHeight()  + player.velocity.y * deltaTime) / 16f);

			checkThisTiles4(layerTiles, startX, finalX, startY, finalY, deltaTime);
		}
		else if (player.velocity.y < 0){
			int startX = (int)Math.floor(player.getX() / 16f);										//lo que viene
			int finalX = (int)Math.floor((player.getX() + player.getWidth()) / 16f);	//donde esté
			int startY = (int)Math.floor((player.getY() + player.velocity.y * deltaTime) / 16f);
			int finalY = (int)Math.floor((player.getY() + player.getHeight()) / 16f);

			checkThisTiles3(layerTiles, startX, finalX, startY, finalY, deltaTime);
		}
		else{ //velocity.y = 0

		}


	}

	private void checkThisTiles(TiledMapTileLayer layerTiles, int startX,
			int finalX, int startY, int finalY, float deltaTime) {
		for (int x = startX; x <= finalX; x++) {
		    for (int y = startY; y <= finalY; y++) {
				Cell cell = layerTiles.getCell(x, y);
				if (cell != null) {
					Rectangle rect = this.rectPool.obtain();
					Rectangle playerRect = this.rectPool.obtain();
					rect.set(x * this.TILED_SIZE, y  * this.TILED_SIZE, this.TILED_SIZE, this.TILED_SIZE);
					playerRect.set(this.player.getRect2().x, this.player.getRect2().y, this.player.getRect2().width + this.player.velocity.x * deltaTime,  this.player.getRect2().height);
					System.out.println("cell is not null and y from tile is " + rect.y + " height is " + rect.height +
							"playerRect y is " + playerRect.y + " and height is " + playerRect.height);
					if(playerRect.overlaps(rect)){
						System.out.println(x);
						this.player.setX(x * this.TILED_SIZE - this.TILED_SIZE);
						this.player.velocity.x = 0;
						return;
					}
				}
		    }
		}
	}

	private void checkThisTiles2(TiledMapTileLayer layerTiles, int startX,
			int finalX, int startY, int finalY, float deltaTime) {
		for (int x = startX; x <= finalX; x++) {
		    for (int y = startY; y <= finalY; y++) {
				Cell cell = layerTiles.getCell(x, y);
				if (cell != null) {
					Rectangle rect = this.rectPool.obtain();
					Rectangle playerRect = this.rectPool.obtain();
					rect.set(x * this.TILED_SIZE, y  * this.TILED_SIZE, this.TILED_SIZE, this.TILED_SIZE);
					playerRect.set(this.player.getRect2().x + this.player.velocity.x * deltaTime, this.player.getRect2().y, this.player.getRect2().width + Math.abs(this.player.velocity.x * deltaTime),  this.player.getRect2().height);
					System.out.println("cell is not null and y from tile is " + rect.y + " height is " + rect.height +
							"playerRect y is " + playerRect.y + " and height is " + playerRect.height);
					if(playerRect.overlaps(rect)){
						System.out.println(x);
						this.player.setX(x * this.TILED_SIZE + this.TILED_SIZE);
						this.player.velocity.x = 0;
						return;
					}
				}
		    }
		}
	}

	private void checkThisTiles3(TiledMapTileLayer layerTiles, int startX,
			int finalX, int startY, int finalY, float deltaTime) {
		for (int x = startX; x <= finalX; x++) {
		    for (int y = startY; y <= finalY; y++) {
				Cell cell = layerTiles.getCell(x, y);
				if (cell != null) {
					Rectangle rect = this.rectPool.obtain();
					Rectangle playerRect = this.rectPool.obtain();
					rect.set(x * this.TILED_SIZE, y  * this.TILED_SIZE, this.TILED_SIZE, this.TILED_SIZE);
					playerRect.set(this.player.getRect2().x, this.player.getRect2().y + this.player.velocity.y * deltaTime, this.player.getRect2().width - 2,  this.player.getRect2().height + Math.abs(this.player.velocity.y * deltaTime));

					if(playerRect.overlaps(rect)){
						this.player.desiredPosition.y = y * this.TILED_SIZE + this.TILED_SIZE;
						this.player.velocity.y = 0;
						if (normalGravity)
							this.player.grounded = true;
						return;
					}
				}
		    }
		}
		this.player.grounded = false;		//Im falling
	}

	private void checkThisTiles4(TiledMapTileLayer layerTiles, int startX,
			int finalX, int startY, int finalY, float deltaTime) {
		for (int x = startX; x <= finalX; x++) {
		    for (int y = startY; y <= finalY; y++) {
				Cell cell = layerTiles.getCell(x, y);
				if (cell != null) {
					Rectangle rect = this.rectPool.obtain();
					Rectangle playerRect = this.rectPool.obtain();
					rect.set(x * this.TILED_SIZE, y  * this.TILED_SIZE, this.TILED_SIZE, this.TILED_SIZE);
					playerRect.set(this.player.getRect2().x, this.player.getRect2().y, this.player.getRect2().width - 2,  this.player.getRect2().height + Math.abs(this.player.velocity.y * deltaTime));
					System.out.println("cell is not null and y from tile is " + rect.y + " height is " + rect.height +
							"playerRect y is " + playerRect.y + " and height is " + playerRect.height);
					if(playerRect.overlaps(rect)){
						this.player.desiredPosition.y = y * this.TILED_SIZE - this.player.getHeight();
						this.player.velocity.y = 0;
						if (!normalGravity){
							this.player.grounded = true;
							this.player.desiredPosition.y += 1;
						}
						return;
					}
				}
		    }
		}
		this.player.grounded = false;		//Im falling
	}

	private void renderPlayer (float deltaTime) {
		AtlasRegion frame = null;
		switch (this.player.state) {
		case Standing:
			frame = (AtlasRegion)Assets.playerStand.getKeyFrame(this.player.stateTime);
			break;
		case Walking:
			frame = (AtlasRegion)Assets.playerWalk.getKeyFrame(this.player.stateTime);
			break;
		case Jumping:
			frame = (AtlasRegion)Assets.playerJump.getKeyFrame(this.player.stateTime);
			break;
		case Intro:
			frame = (AtlasRegion)Assets.playerIntro.getKeyFrame(this.player.stateTime);
			break;
		case Attacking:
			frame = (AtlasRegion)Assets.playerAttack.getKeyFrame(this.player.stateTime);
			break;
		case Die:
			this.configControllers.terminate();
			frame = (AtlasRegion)Assets.playerDie.getKeyFrame(this.player.stateTime);
			break;
		case BeingHit:
			frame = (AtlasRegion)Assets.playerBeingHit.getKeyFrame(this.player.stateTime);
			break;
		}
		if (this.player.invincible && this.toggle) {
			frame = (AtlasRegion)Assets.playerEmpty.getKeyFrame(this.player.stateTime);
		    this.toggle = !this.toggle;
		}
		else if (this.player.invincible && !this.toggle) {
		    this.toggle = !this.toggle;
		}
		else if (!this.player.invincible) {
		    this.toggle = false;
		}
		// draw the koala, depending on the current velocity
		// on the x-axis, draw the koala facing either right
		// or left
		Batch batch = this.renderer.getSpriteBatch();
		batch.begin();
		if (this.player.facesRight && frame.isFlipX()) {
            frame.flip(true, false);
            this.player.rightOffset = 1f;	//fix differences
		}
		else if (!this.player.facesRight && !frame.isFlipX()) {
			frame.flip(true, false);
			this.player.rightOffset = -4f;   //fix differences
		}

		if (this.normalGravity && frame.isFlipY()) {
			frame.flip(false, true);
			this.UpOffset = 0;
		}
		else if (!this.normalGravity && !frame.isFlipY()){
			frame.flip(false, true);
			this.UpOffset = -2;
		}

		//batch.draw(frame, this.player.getX() + frame.offsetX, this.player.getY() + frame.offsetY + this.UpOffset);
		batch.draw(frame, (this.player.getX() + this.player.actualFrame.offsetX) - this.player.offSetX, (this.player.getY() + this.player.actualFrame.offsetY) - this.player.offSetY);

		batch.end();

		this.shapeRenderer.begin(ShapeType.Line);

		this.shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.rect(this.player.getRect2().x, this.player.getRect2().y - this.SCREEN_HEIGHT + TILED_SIZE, this.player.getRect2().width, this.player.getRect2().height);
		//this.getTiles(0, 0, 25, 15, this.tiles);
		//for (Rectangle tile : this.tiles) {
		//	shapeRenderer.rect(tile.x * 1.6f, tile.y * 2, tile.width * 2, tile.height * 2);
		//}
		this.shapeRenderer.setColor(Color.RED);
		//shapeRenderer.rect(playerRect.x * 1.6f, playerRect.y * 2, playerRect.width * 2, playerRect.height * 2);

        this.shapeRenderer.end();
    }

	private void movingShootingJumping(float deltaTime) {

		if (this.player.noControl == false){
			if (Gdx.input.isKeyJustPressed(Keys.S)){
				this.jump();
				//this.player.stateTime = 0;
			}

			if (Gdx.input.isKeyPressed(Keys.LEFT) || this.configControllers.leftPressed){
				this.player.velocity.x = -this.player.MAX_VELOCITY;
				if (this.player.grounded && Assets.playerAttack.isAnimationFinished(this.player.stateTime)
						&& Assets.playerBeingHit.isAnimationFinished(this.player.stateTime)){
					this.player.state = Player.State.Walking;
					//this.player.stateTime = 0;
				}
				this.player.facesRight = false;
			}

			if (Gdx.input.isKeyPressed(Keys.RIGHT) || this.configControllers.rightPressed){
				this.player.velocity.x = this.player.MAX_VELOCITY;
				if (this.player.grounded && Assets.playerAttack.isAnimationFinished(this.player.stateTime)
						&& Assets.playerBeingHit.isAnimationFinished(this.player.stateTime)){
					this.player.state = Player.State.Walking;
					//this.player.stateTime = 0;
				}
				this.player.facesRight = true;
			}

			if (Gdx.input.isKeyJustPressed(Keys.D)){
				//this.shoot();
			}
//			if (Gdx.input.isKeyJustPressed(Keys.Y)){
//			    LD.getInstance().ENDING_SCREEN = new EndingScreen();
//			    LD.getInstance().setScreen(LD.getInstance().ENDING_SCREEN);
//			}
		}

		if (Assets.playerAttack.isAnimationFinished(this.player.stateTime))
			this.player.shooting = false;

		/*
		int i = 0;
		boolean[] toBeDeleted = new boolean[3];
		for (Shot shot : this.shotArray){
			if (shot != null){
				if(this.updateShot(shot, deltaTime) == true)
					toBeDeleted[i] = true;
					//pool of shots?
			}
			i++;
		}


		for(int j = 0; j < toBeDeleted.length; j++){
			if (toBeDeleted[j] && (this.shotArray.size >= (j + 1)))
				this.shotArray.removeIndex(j);
		}

		*/
	}

	public void jump() {
		if (this.player.grounded){
			Assets.playSound("playerJump");
			if (this.normalGravity)
				this.player.velocity.y = this.player.JUMP_VELOCITY;
			else
				this.player.velocity.y = -this.player.JUMP_VELOCITY;
			this.player.grounded = false;
			this.player.state = Player.State.Jumping;
		}
	}

}
