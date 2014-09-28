package com.blogspot.ludumdaresforfun.aholenewrace;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
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
import com.sun.jmx.snmp.Timestamp;

public class MainScreen extends BaseScreen {

	public boolean pause = false;
	public boolean toggle = false;
	ConfigControllers configControllers;
	Rectangle playerRect;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
	public Player player;
	public Boss boss;
	private ShapeRenderer shapeRenderer;
	private TiledMap map;
	private boolean normalGravity = true;
	private boolean normalGravityBoss = true;
	private float healingTimer = 50f;

	Timestamp time = new Timestamp();

	private Array<Enemy> enemies = new Array<Enemy>();
	private Array<Rectangle> tiles = new Array<Rectangle>();
	private Array<Rectangle> spikes = new Array<Rectangle>();
	public Array<Shot> shotArray = new Array<Shot>();
	Map<Vector2, Enemy.Type> spawns = new HashMap<Vector2, Enemy.Type>();
	private Array<Vector2> spawnsPositions = new Array<Vector2>();
	private Array<Vector2> lifes = new Array<Vector2>();
	private boolean callGameOver = false;
	// private Boss boss;
	private Vector2 door;

	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject() {
			return new Rectangle();
		}
	};
	// HUD hud;

	private final float GRAVITY = -600f; // -10 * 60
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
	private OrthographicCamera camera2;
	private int numberOfPlayers = 1;

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

		this.map = new TmxMapLoader().load("tilemap_01.tmx");
		this.MAP_HEIGHT = (Integer) this.map.getProperties().get("height");
		this.MAP_WIDTH = (Integer) this.map.getProperties().get("width");
		this.TILED_SIZE = (Integer) this.map.getProperties().get("tileheight");
		this.POS_LOWER_WORLD = ((this.MAP_HEIGHT / 2) * this.TILED_SIZE) - this.TILED_SIZE;
		this.POS_UPPER_WORLD = this.MAP_HEIGHT * this.TILED_SIZE;

		this.renderer = new OrthogonalTiledMapRenderer(this.map, 1);

		// Assets.dispose(); //TODO: for debugging

		numberOfPlayers = 2;

		this.camera = new OrthographicCamera();
		if (this.numberOfPlayers  == 1)
			this.camera.setToOrtho(false, this.SCREEN_WIDTH, this.SCREEN_HEIGHT);
		else{
			this.camera.setToOrtho(false, this.SCREEN_WIDTH / 2, this.SCREEN_HEIGHT);

			this.camera2 = new OrthographicCamera();
			this.camera2.setToOrtho(false, this.SCREEN_WIDTH / 2, this.SCREEN_HEIGHT);
		}

		this.player = new Player(Assets.playerStand);
		this.boss = new Boss(Assets.bossStanding);

		this.player.setPosition(200, 310);
		this.boss.setPosition(200, 310);

		this.configControllers = new ConfigControllers(this);
		this.configControllers.init();

		TiledMapTileLayer layerSpawn = (TiledMapTileLayer) (this.map.getLayers().get("Spawns"));
		this.rectPool.freeAll(this.tiles);
		this.tiles.clear();

		for (int x = 0; x <= layerSpawn.getWidth(); x++) {
			for (int y = 0; y <= layerSpawn.getHeight(); y++) {
				Cell cell = layerSpawn.getCell(x, y);
				if (cell != null) {
					String type = (String) cell.getTile().getProperties().get("type");
					if (type != null) {
						if (type.equals("spider")) {
							this.spawns.put(new Vector2(x * this.TILED_SIZE, y * this.TILED_SIZE), Enemy.Type.Spider);
							this.spawnsPositions.add(new Vector2(x * this.TILED_SIZE, y * this.TILED_SIZE));
						}else if (type.equals("bat")) {
							this.spawns.put(new Vector2(x * this.TILED_SIZE, y * this.TILED_SIZE), Enemy.Type.Bat);
							this.spawnsPositions.add(new Vector2(x * this.TILED_SIZE, y * this.TILED_SIZE));
						}else if (type.equals("pollo")) {
							this.lifes.add(new Vector2(x * this.TILED_SIZE, y * this.TILED_SIZE));
						} else if (type.equals("player")) {
							this.player.setPosition(x * this.TILED_SIZE, y * this.TILED_SIZE);
						} else if (type.equals("boss")) {
							this.boss.setPosition(x * this.TILED_SIZE, y * this.TILED_SIZE);
						} else if (type.equals("door")) {
							this.door = new Vector2(x, y);
						}
					}
				}
			}
		}
		this.camera.position.x = this.player.getX()+ SCREEN_WIDTH/8; //
	 	this.camera2.position.x = this.boss.getX()+ SCREEN_WIDTH/8; //

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//updateWorld
		updateWorld(delta);

		if (this.numberOfPlayers == 1){
			updateCameraForOnePlayer();
			drawFirstWorld(delta);
		}
		else{
			//Down Half
			Gdx.gl.glViewport( 0,0,(Gdx.graphics.getWidth() / 2) - 1,Gdx.graphics.getHeight());

			updateCameraForTwoPlayersTemplar();
			drawFirstWorld(delta);

			//Upper Half
			Gdx.gl.glViewport( ((Gdx.graphics.getWidth() / 2) + 1),0,(Gdx.graphics.getWidth() / 2 - 1),Gdx.graphics.getHeight());

			updateCameraForTwoPlayersBoss();
			drawSecondWorld(delta);
		}

	}

	private void updateWorld(float delta) {
		delta = Math.min(delta, 0.05f);

		this.updatePlayer(delta);
		this.updateShots(delta);
		this.updateBoss(delta);
		this.updateAttackBoss(delta);
		this.player.act(delta);
		this.boss.act(delta);

		spawnEnemies();

		// this.collisionLifes(delta);
		this.updateEnemies(delta);

		  //this.renderHUD(delta);

	}

	private void updateAttackBoss(float delta) {
		this.playerRect = this.rectPool.obtain();
		if (this.boss.shooting){
			this.playerRect = new Rectangle(this.boss.getRect().x + this.boss.getRect2().width,		//
					this.boss.getRect().y, this.boss.getRect().width - this.boss.getRect2().width, this.boss.getRect().height);


			for (Enemy enemy : this.enemies){	//attack kill
				if (this.playerRect.overlaps(enemy.getRect())) {
					if (!enemy.dying)
						enemy.die();
				}
				if (this.boss.getRect2().overlaps(enemy.getRect())){	//touch kill
					if (!enemy.dying && !this.boss.invincible){
						enemy.die();
						this.boss.beingHit();
					}
				}
			}

		}
		else{
			for (Enemy enemy : this.enemies){
				if (this.boss.getRect2().overlaps(enemy.getRect())) {	//touch kill
					if (!enemy.dying && !this.boss.invincible){
						enemy.die();
						this.boss.beingHit();
						break;
					}
				}
			}
		}
	}

	private void renderShot(Shot shot, float deltaTime){
		AtlasRegion frame = null;
		if (shot.state == Shot.State.Normal)
			frame = (AtlasRegion) Assets.playerShot.getKeyFrame(shot.stateTime);
		else if (shot.state == Shot.State.Exploding)
			frame = (AtlasRegion) Assets.playerShotHit.getKeyFrame(shot.stateTime);

		if (!this.normalGravity) {
		    if (!frame.isFlipY())
                frame.flip(false, true);
		}
		else {
		    if (frame.isFlipY())
                frame.flip(false, true);
		}

		Batch batch = this.renderer.getSpriteBatch();
		batch.begin();
		if (shot.shotGoesRight) {
			if (frame.isFlipX())
				frame.flip(true, false);
			batch.draw(frame, shot.getX(), shot.getY());
		} else {
			if (!frame.isFlipX())
				frame.flip(true, false);
			batch.draw(frame, shot.getX(), shot.getY());
		}

		batch.end();
	}

	private void spawnEnemies() {
		if (this.spawns.size() > 0) {
			Vector2 auxNextSpawn = this.spawnsPositions.first();
			if ((this.camera.position.x + this.DISTANCESPAWN) >= auxNextSpawn.x) {
				Enemy auxShadow = new Enemy(Assets.enemyWalk);
				auxShadow.enemyType = spawns.get(auxNextSpawn);
				if (auxNextSpawn.y < 240) {
					auxNextSpawn.y -= 5;
				}// Offset fixed collision

				auxShadow.setPosition(auxNextSpawn.x, auxNextSpawn.y);
				auxShadow.state = Enemy.State.BeingInvoked;
				auxShadow.stateTime = 0;
				auxShadow.beingInvoked = true;
				this.enemies.add(auxShadow);
				this.spawnsPositions.removeIndex(0);
				this.spawns.remove(auxNextSpawn);
				}
			}
	}

	private void drawFirstWorld(float delta) {
		this.renderer.setView(this.camera);
		this.renderer.render(new int[] { 0, 1, 3 }); // this line is totally a
														// mistery
		this.renderEnemies(delta);
		this.renderPlayer(delta);
		this.renderBoss(delta);
		renderShots(delta);
	}

	private void renderShots(float delta) {
		for (Shot shot : this.shotArray){
			if (shot != null)
				this.renderShot(shot, delta);
			}
	}

	private void drawSecondWorld(float delta) {
		this.renderer.setView(this.camera2);
		this.renderer.render(new int[] { 0, 1, 3 }); // this line is totally a
														// mistery
		this.renderEnemies(delta);
		this.renderPlayer(delta);
		this.renderBoss(delta);
		renderShots(delta);
	}

	private void updateCameraForTwoPlayersTemplar() {

		// update x
//		if (camera.position.x - this.SCREEN_WIDTH / 8 > this.player.getX() ){
//				camera.position.x = player.getX() - SCREEN_WIDTH / 8;
//		}
//		else if (camera.position.x + this.SCREEN_WIDTH / 8 < this.player.getX()){
//				camera.position.x = player.getX() + SCREEN_WIDTH / 8;
//		}
//		else{
		if (player.facesRight && camera.position.x + 10 < player.getX() + SCREEN_WIDTH / 8)
			camera.position.x += 5;
		else if (player.facesRight && camera.position.x < player.getX() + SCREEN_WIDTH / 8)
			camera.position.x = player.getX() + SCREEN_WIDTH / 8;
		else if (!player.facesRight && camera.position.x - 10 > player.getX() - SCREEN_WIDTH / 8)
			camera.position.x -= 5;
		else if (!player.facesRight && camera.position.x > player.getX() - SCREEN_WIDTH / 8)
			camera.position.x = player.getX() - SCREEN_WIDTH / 8;
		//}


		// update y
		if ((this.player.getY() - (this.SCREEN_HEIGHT / 2)) >= this.POS_LOWER_WORLD
				+ TILED_SIZE)
			this.camera.position.y = this.player.getY();
		else if (this.player.getY() + TILED_SIZE > this.POS_LOWER_WORLD)
			this.camera.position.y = this.POS_LOWER_WORLD + (this.SCREEN_HEIGHT / 2)
			+ TILED_SIZE;
		else if ((this.player.getY() + (this.SCREEN_HEIGHT / 2)) >= this.POS_LOWER_WORLD
				+ TILED_SIZE)
			this.camera.position.y = this.POS_LOWER_WORLD - (this.SCREEN_HEIGHT / 2)
			+ TILED_SIZE;
		else
			this.camera.position.y = this.player.getY();

		this.camera.update();

	}

	private void updateCameraForTwoPlayersBoss() {

		// update x
		if (boss.facesRight && camera2.position.x + 10 < boss.getX() + SCREEN_WIDTH / 8)
			camera2.position.x += 5;
		else if (boss.facesRight && camera2.position.x < boss.getX() + SCREEN_WIDTH / 8)
			camera2.position.x = boss.getX() + SCREEN_WIDTH / 8;
		else if (!boss.facesRight && camera2.position.x - 10 > boss.getX() - SCREEN_WIDTH / 8)
			camera2.position.x -= 5;
		else if (!boss.facesRight && camera2.position.x > boss.getX() - SCREEN_WIDTH / 8)
			camera2.position.x = boss.getX() - SCREEN_WIDTH / 8;

		// update y
		if ((this.boss.getY() - (this.SCREEN_HEIGHT / 2)) >= this.POS_LOWER_WORLD
				+ TILED_SIZE)
			this.camera2.position.y = this.boss.getY();
		else if (this.boss.getY() + TILED_SIZE > this.POS_LOWER_WORLD)
			this.camera2.position.y = this.POS_LOWER_WORLD + (this.SCREEN_HEIGHT / 2)
			+ TILED_SIZE;
		else if ((this.boss.getY() + (this.SCREEN_HEIGHT / 2)) >= this.POS_LOWER_WORLD
				+ TILED_SIZE)
			this.camera2.position.y = this.POS_LOWER_WORLD - (this.SCREEN_HEIGHT / 2)
			+ TILED_SIZE;
		else
			this.camera2.position.y = this.boss.getY();

		this.camera2.update();

	}

	private void updateCameraForOnePlayer() {
			// update x
			if ((this.player.getX() - (this.SCREEN_WIDTH / 2)) < this.TILED_SIZE)
				this.camera.position.x = (this.SCREEN_WIDTH / 2) + this.TILED_SIZE;
			else if ((this.player.getX() + (this.SCREEN_WIDTH / 2)) > (this.MAP_WIDTH * this.TILED_SIZE))
				this.camera.position.x = (this.MAP_WIDTH * 16) - (this.SCREEN_WIDTH / 2);
			else
				this.camera.position.x = this.player.getX();

			// update y
			if ((this.player.getY() - (this.SCREEN_HEIGHT / 2)) >= this.POS_LOWER_WORLD
					+ TILED_SIZE)
				this.camera.position.y = this.player.getY();
			else if (this.player.getY() + TILED_SIZE > this.POS_LOWER_WORLD)
				this.camera.position.y = this.POS_LOWER_WORLD + (this.SCREEN_HEIGHT / 2)
						+ TILED_SIZE;
			else if ((this.player.getY() + (this.SCREEN_HEIGHT / 2)) >= this.POS_LOWER_WORLD
					+ TILED_SIZE)
				this.camera.position.y = this.POS_LOWER_WORLD - (this.SCREEN_HEIGHT / 2)
						+ TILED_SIZE;
			else
				this.camera.position.y = this.player.getY();

			this.camera.update();
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

		// retreat if noControl //velocity y is changed in beingHit
		if (this.player.noControl
				&& !(this.player.state.equals(Player.State.Die) && Assets.playerDie
						.isAnimationFinished(this.player.stateTime))) {
			if (this.player.facesRight)
				this.player.velocity.x = -120f * deltaTime;
			else
				this.player.velocity.x = 120 * deltaTime;
		}

		// boolean collisionSpike = this.collisionWallsAndSpike();

		// unscale the velocity by the inverse delta time and set the latest
		// position
		this.player.desiredPosition.add(this.player.velocity);
		this.player.velocity.scl(1 / deltaTime);

		if (Assets.playerBeingHit.isAnimationFinished(this.player.stateTime) && !this.player.dead)
			this.player.noControl = false;

		if (this.player.noControl == false)
			this.player.velocity.x *= 0; // 0 is totally stopped if not pressed

		this.player.setPosition(this.player.desiredPosition.x, this.player.desiredPosition.y);

		/*
		 * if (Assets.playerDie.isAnimationFinished(this.player.stateTime) &&
		 * this.player.dead && !callGameOver){ callGameOver = true;
		 * Timer.schedule(new Task() {
		 *
		 * @Override public void run() { MainScreen.this.gameOver(); } }, 1f);
		 * this.player.velocity.x = 0; } if (collisionSpike) {
		 * this.player.beingHit(); }
		 */
	}

	private void updateBoss(float deltaTime) {

		if (deltaTime == 0)
			return;
		this.boss.stateTime += deltaTime;

		this.boss.desiredPosition.x = this.boss.getX();
		this.boss.desiredPosition.y = this.boss.getY();

		this.movingShootingJumpingBoss(deltaTime);
		this.gravityAndClampingBoss(deltaTime);

		this.checkCollisionWallsBoss(deltaTime);

		this.boss.velocity.scl(deltaTime);

		// retreat if noControl //velocity y is changed in beingHit
//		if (this.boss.noControl
//				&& !(this.boss.state.equals(Boss.State.Die) && Assets.bossDie
//						.isAnimationFinished(this.boss.stateTime))) {
//			if (this.boss.facesRight)
//				this.boss.velocity.x = -120f * deltaTime;
//			else
//				this.boss.velocity.x = 120 * deltaTime;
//		}

		// boolean collisionSpike = this.collisionWallsAndSpike();

		// unscale the velocity by the inverse delta time and set the latest
		// position
		this.boss.desiredPosition.add(this.boss.velocity);
		this.boss.velocity.scl(1 / deltaTime);

		if (Assets.bossGethit.isAnimationFinished(this.boss.stateTime) && !this.boss.dead)
			this.boss.noControl = false;

		if (this.boss.noControl == false)
			this.boss.velocity.x *= 0; // 0 is totally stopped if not pressed

		this.boss.setPosition(this.boss.desiredPosition.x, this.boss.desiredPosition.y);

		/*
		 * if (Assets.playerDie.isAnimationFinished(this.player.stateTime) &&
		 * this.player.dead && !callGameOver){ callGameOver = true;
		 * Timer.schedule(new Task() {
		 *
		 * @Override public void run() { MainScreen.this.gameOver(); } }, 1f);
		 * this.player.velocity.x = 0; } if (collisionSpike) {
		 * this.player.beingHit(); }
		 */
	}

	private void gravityAndClamping(float deltaTime) {
		if (this.normalGravity)
			this.player.velocity.add(0, this.GRAVITY * deltaTime);
		else
			this.player.velocity.add(0, -this.GRAVITY * deltaTime);

		if (this.player.getY() < this.POS_LOWER_WORLD) {
			// this.camera.position.y = this.POS_LOWER_WORLD;
			if (this.normalGravity == true) {
				this.normalGravity = false;
				// this.player.velocity.y = -this.player.JUMP_VELOCITY * 1.01f;
				// //3 tiles in both
			}
		} else {
			// this.camera.position.y = 0;//this.yPosUpperWorld;
			if (this.normalGravity == false) {
				this.normalGravity = true;
				// this.player.velocity.y = this.player.JUMP_VELOCITY / 1.3f;
				// //3 tiles in both
			}
		}

		// clamp the velocity to 0 if it's < 1, and set the state to standign
		if (Math.abs(this.player.velocity.x) < 1) {
			this.player.velocity.x = 0;
			if (this.player.grounded
					&& Assets.playerAttack.isAnimationFinished(this.player.stateTime)
					&& Assets.playerBeingHit.isAnimationFinished(this.player.stateTime)
					&& !this.player.invincible)
				this.player.state = Player.State.Standing;
		}
		// gravityAndClampingBoss(deltaTime);
	}

    public boolean updateShot(Shot shot, float deltaTime){
    	boolean killMe = false;
        shot.desiredPosition.y = shot.getY();

        shot.stateTime += deltaTime;

		if (this.normalGravity && !shot.state.equals(Shot.State.Exploding))
			shot.velocity.add(0, this.GRAVITY * deltaTime);
		else
			shot.velocity.add(0, -this.GRAVITY * deltaTime);

		shot.velocity.scl(deltaTime);

		//collision (destroy if necessary)
		boolean collided = this.collisionShotEnemy(shot);

		if (!collided)
			collided = this.collisionShot(shot);

		// unscale the velocity by the inverse delta time and set
		// the latest position
		if (shot != null){
			shot.desiredPosition.add(shot.velocity);
			shot.velocity.scl(1 / deltaTime);

			shot.setPosition(shot.desiredPosition.x, shot.desiredPosition.y);
			if (shot.normalGravity && (shot.getY() < this.POS_LOWER_WORLD))
				collided = true;	//dont traspass to the other world
			else if (!shot.normalGravity && (shot.getY() >= this.POS_LOWER_WORLD))
				collided = true;
			else if ((shot.getY() > (this.MAP_HEIGHT * this.TILED_SIZE)) || (shot.getY() < 0))
				collided = true;
		}

		if (collided && !shot.state.equals(Shot.State.Exploding)){
            Assets.playSound("holyWaterBroken");
            shot.state = Shot.State.Exploding;
            shot.stateTime = 0;
            shot.velocity.x = 0f;
            shot.velocity.y = 0f;
		}

		if (Assets.playerShotHit.isAnimationFinished(shot.stateTime) && shot.state.equals(Shot.State.Exploding))
			killMe = true;

		return killMe;
    }


	private boolean collisionShotEnemy(Shot shot) {
		boolean collided = false;

		this.playerRect = this.rectPool.obtain();

		shot.desiredPosition.y = Math.round(shot.getY());
		shot.desiredPosition.x = Math.round(shot.getX());

		this.playerRect = shot.getRect();

		for (Enemy enemy : this.enemies){
			if (this.playerRect.overlaps(enemy.getRect())) {
				if (!enemy.dying){
					enemy.die();
					collided = true;
					break;
				}
			}
		}

		if ((this.boss != null) && this.playerRect.overlaps(this.boss.getRect())) {

			if (!this.boss.invincible)
				this.boss.beingHit();

		    if (!this.boss.setToDie){
		    	this.boss.invincible = true;		//activates also the flickering
		    }
		    else if (this.boss.state != Boss.State.Die){
		    	this.boss.state = Boss.State.Die;
		    }
		    collided = true;
		}


		return collided;
	}


	private boolean collisionShot(Shot shot) {
		this.playerRect = this.rectPool.obtain();

		shot.desiredPosition.y = Math.round(shot.getY());
		shot.desiredPosition.x = Math.round(shot.getX());

		this.playerRect = shot.getRect();

		int startX, startY, endX, endY;

		if (shot.velocity.x > 0) {	//this.raya.velocity.x > 0
			startX = endX = (int)((shot.desiredPosition.x + shot.velocity.x + shot.actualFrame.packedWidth) / 16);
		}
		else {
			startX = endX = (int)((shot.desiredPosition.x + shot.velocity.x) / 16);
		}

		startY = (int)((shot.desiredPosition.y) / 16);
		endY = (int)((shot.desiredPosition.y + shot.actualFrame.packedHeight) / 16);

		this.getTiles(startX, startY, endX, endY, this.tiles);

		this.playerRect.x += shot.velocity.x;

		for (Rectangle tile : this.tiles){
			if (this.playerRect.overlaps(tile)) {
				shot = null;
				return true;
				}
		}

		this.playerRect.x = shot.desiredPosition.x;

		if (this.normalGravity){
			if (shot.velocity.y > 0) {
				startY = endY = (int)((shot.desiredPosition.y + shot.velocity.y + shot.actualFrame.packedHeight) / 16f);
			}
			else {
				startY = endY = (int)((shot.desiredPosition.y + shot.velocity.y) / 16f);
			}
		}
		else{
			if (shot.velocity.y < 0) {

				startY = endY = (int)((shot.desiredPosition.y + shot.velocity.y) / 16f);
			}
			else {
				startY = endY = (int)((shot.desiredPosition.y + shot.velocity.y + shot.actualFrame.packedHeight ) / 16f);
			}
		}

		startX = (int)((shot.desiredPosition.x + shot.offSetX) / 16);					//16 tile size
		endX = (int)((shot.desiredPosition.x + shot.actualFrame.packedWidth) / 16);


		// System.out.println(startX + " " + startY + " " + endX + " " + endY);

		this.getTiles(startX, startY, endX, endY, this.tiles);

		shot.desiredPosition.y += (int)(shot.velocity.y);

		for (Rectangle tile : this.tiles) {
			if (this.playerRect.overlaps(tile)) {
				shot = null;
				return true;
				}
			}
		return false;
	}

	private void updateEnemies(float deltaTime) {
	    for (Enemy enemy : this.enemies) {

	    	this.isEnemyInScreen(enemy);
	    	this.isEnemyFinishedInvoking(enemy);

	        // Collision between player vs enemy
	    	if (!enemy.dying){
	    		if (this.player.getRect2().overlaps(enemy.getRect())) {
	    			this.player.beingHit();
	    		}
	    	}


	        enemy.stateTime += deltaTime;
	        // Check if player is invincible and check distance to player for attack him.
	        if (!enemy.running && !enemy.dying && !enemy.beingInvoked && enemy.canMove){
	            // Attack
	        	if (!this.player.invincible &&
                        (Math.abs(enemy.getCenterX() - this.player.getCenterX()) <= enemy.ATTACK_DISTANCE) &&
	        	        (Math.abs(((enemy.getCenterY() - this.player.getCenterY()))) <= this.player.getHeight())) {
	        		if (enemy.getCenterX() < this.player.getCenterX()) {
	        				enemy.dir = Enemy.Direction.Right;
	        				enemy.run();
	        				enemy.attackHereX = this.player.getX();
	        				enemy.attackRight = true;
	        		}
	        		else {
	        				enemy.dir = Enemy.Direction.Left;
	        				enemy.run();
	        				enemy.attackHereX = this.player.getX();
	        				enemy.attackRight = false;
	        		}
	        	}
	        	else if (enemy.dir == Enemy.Direction.Left) {
	        		if (-enemy.RANGE >= enemy.diffInitialPos) {
	        			enemy.dir = Enemy.Direction.Right;
	        		}
	        		enemy.walk();
	        	}
	        	else if (enemy.dir == Enemy.Direction.Right) {
	        		if (enemy.diffInitialPos >= enemy.RANGE) {
	        			enemy.dir = Enemy.Direction.Left;
	        		}
	        		enemy.walk();
	        	}
	        }
	        else if ((enemy.getX() > enemy.attackHereX) && enemy.attackRight)
	        	enemy.running = false;
	        else if ((enemy.getX() < enemy.attackHereX) && !enemy.attackRight)
	        	enemy.running = false;

            enemy.velocity.scl(deltaTime);

            // Enviroment collision
            enemy.desiredPosition.y = Math.round(enemy.getY());
            enemy.desiredPosition.x = Math.round(enemy.getX());
            int startX, startY, endX, endY;
            if (enemy.velocity.x > 0) {
                startX = endX = (int)((enemy.desiredPosition.x + enemy.velocity.x + enemy.getWidth()) / this.TILED_SIZE);
            }
            else {
                startX = endX = (int)((enemy.desiredPosition.x + enemy.velocity.x) / this.TILED_SIZE);
            }
            startY = (int) enemy.getY() / this.TILED_SIZE;
            endY =  (int) (enemy.getY() + enemy.getHeight()) / this.TILED_SIZE;

            this.getTiles(startX, startY, endX, endY, this.tiles);

            enemy.getRect();
            enemy.rect.x += enemy.velocity.x;

            for (Rectangle tile : this.tiles) {
                if (enemy.rect.overlaps(tile)) {
                    enemy.velocity.x = 0;
                    enemy.running = false;
                    break;
                }
            }

            enemy.rect.x = enemy.desiredPosition.x;

            enemy.desiredPosition.add(enemy.velocity);
            enemy.velocity.scl(1 / deltaTime);

            enemy.setPosition(enemy.desiredPosition.x, enemy.desiredPosition.y);

            if (Assets.playerDie.isAnimationFinished(enemy.stateTime) && enemy.dying){
    			enemy.setToDie = true;
    		}

        }

	    int i = 0;
		boolean[] toBeDeleted = new boolean[this.enemies.size];
		for (Enemy enemy : this.enemies){
			if (enemy != null){
				if(enemy.setToDie == true) //&& animation finished
					toBeDeleted[i] = true;
			}
			i++;
		}

		for(int j = 0; j < toBeDeleted.length; j++){
			if (toBeDeleted[j] && (this.enemies.size >= (j + 1)))
				this.enemies.removeIndex(j);
		}
	}

	private void isEnemyFinishedInvoking(Enemy enemy) {

		if (Assets.enemyAppearing.isAnimationFinished(enemy.stateTime) && enemy.state.equals(Enemy.State.BeingInvoked)){
			enemy.beingInvoked = false;
		}

	}

	private void isEnemyInScreen(Enemy enemy) {
	    if (enemy.canMove)
	        return;
	    Rectangle cameraRect = new Rectangle(0, 0, this.SCREEN_WIDTH, this.SCREEN_HEIGHT);
	    cameraRect.setCenter(this.camera.position.x, this.camera.position.y);
	    if (enemy.rect.overlaps(cameraRect)) {
		    if ((this.player.getX() - enemy.getX()) < 0)
		        enemy.dir = Enemy.Direction.Left;
		    else
		        enemy.dir = Enemy.Direction.Right;
			enemy.canMove = true;
		}
	}

	private void gravityAndClampingBoss(float deltaTime) {
		if (this.normalGravityBoss)
			this.boss.velocity.add(0, this.GRAVITY * deltaTime);
		else
			this.boss.velocity.add(0, -this.GRAVITY * deltaTime);

		if (this.boss.getY() < this.POS_LOWER_WORLD) {
			// this.camera.position.y = this.POS_LOWER_WORLD;
			if (this.normalGravityBoss == true) {
				this.normalGravityBoss = false;
				this.boss.velocity.y = this.boss.velocity.y * 1.33333333333f;
				// //3 tiles in both
			}
		} else {
			// this.camera.position.y = 0;//this.yPosUpperWorld;
			if (this.normalGravityBoss == false) {
				this.normalGravityBoss = true;
				this.boss.velocity.y = this.boss.velocity.y * 0.75f;
				// //3 tiles in both
			}
		}

		// clamp the velocity to 0 if it's < 1, and set the state to standign
		if (Math.abs(this.boss.velocity.x) < 1) {
			this.boss.velocity.x = 0;
			if (this.boss.grounded && Assets.bossAttack.isAnimationFinished(this.boss.stateTime)
					&& Assets.bossGethit.isAnimationFinished(this.boss.stateTime)
					&& !this.boss.invincible)
				this.boss.state = Boss.State.Standing;
		}
	}

	private void checkCollisionWalls(float deltaTime) {
		TiledMapTileLayer layerTiles = (TiledMapTileLayer) (this.map.getLayers().get("Platfs")); // Esto debería ser "Collisions", lo cambio momentáneamente para editar las fases más rápido
		this.rectPool.freeAll(this.tiles);
		this.tiles.clear();

		// stop him in X
		if (player.velocity.x > 0) {
			int startX = (int) Math.ceil((player.getX() + player.getWidth()) / 16f); // lo
																						// que
																						// viene
			int finalX = (int) Math.floor((player.getX() + player.getWidth() + player.velocity.x
					* deltaTime) / 16f); // donde esté
			int startY = (int) Math.floor(player.getY() / 16f);
			int finalY = (int) Math.floor((player.getY() + player.getHeight()) / 16f);

			checkThisTiles(layerTiles, startX, finalX, startY, finalY, deltaTime);

		} else if (player.velocity.x < 0) {
			int startX = (int) Math.floor((player.getX() + player.velocity.x * deltaTime) / 16f); // lo
																									// que
																									// viene
			int finalX = (int) Math.floor((player.getX()) / 16f); // donde esté
			int startY = (int) Math.floor(player.getY() / 16f);
			int finalY = (int) Math.floor((player.getY() + player.getHeight()) / 16f);

			checkThisTiles2(layerTiles, startX, finalX, startY, finalY, deltaTime);
		} else { // velocity.x = 0
		}

		// Stop him in Y
		if (player.velocity.y > 0) {
			int startX = (int) Math.floor(player.getX() / 16f); // lo que viene
			int finalX = (int) Math.floor((player.getX() + player.getWidth()) / 16f); // donde
																						// esté
			int startY = (int) Math.floor((player.getY()) / 16f);
			int finalY = (int) Math.floor((player.getY() + player.getHeight() + player.velocity.y
					* deltaTime) / 16f);

			checkThisTiles4(layerTiles, startX, finalX, startY, finalY, deltaTime);
		} else if (player.velocity.y < 0) {
			int startX = (int) Math.floor(player.getX() / 16f); // lo que viene
			int finalX = (int) Math.floor((player.getX() + player.getWidth()) / 16f); // donde
																						// esté
			int startY = (int) Math.floor((player.getY() + player.velocity.y * deltaTime) / 16f);
			int finalY = (int) Math.floor((player.getY() + player.getHeight()) / 16f);

			checkThisTiles3(layerTiles, startX, finalX, startY, finalY, deltaTime);
		} else { // velocity.y = 0

		}

		// checkCollisionWallsBoss(deltaTime);
	}

	private void checkCollisionWallsBoss(float deltaTime) {
		TiledMapTileLayer layerTiles = (TiledMapTileLayer) (this.map.getLayers().get("Platfs")); // Esto debería ser "Collisions", lo cambio momentáneamente para editar las fases más rápido
		this.rectPool.freeAll(this.tiles);
		this.tiles.clear();

		// stop him in X
		if (boss.velocity.x > 0) {
			int startX = (int) Math.ceil((boss.getX() + boss.getWidth()) / 16f);
			int finalX = (int) Math.floor((boss.getX() + boss.getWidth() + boss.velocity.x	* deltaTime) / 16f); // donde esté
			int startY = (int) Math.floor(boss.getY() / 16f);
			int finalY = (int) Math.floor((boss.getY() + boss.getHeight()) / 16f);

			checkThisTilesBoss(layerTiles, startX, finalX, startY, finalY, deltaTime);

		} else if (boss.velocity.x < 0) {
			int startX = (int) Math.floor((boss.getX() + boss.velocity.x * deltaTime) / 16f);
			int finalX = (int) Math.floor((boss.getX()) / 16f); // donde esté
			int startY = (int) Math.floor(boss.getY() / 16f);
			int finalY = (int) Math.floor((boss.getY() + boss.getHeight()) / 16f);

			checkThisTiles2Boss(layerTiles, startX, finalX, startY, finalY, deltaTime);
		} else { // velocity.x = 0
		}

		// Stop him in Y
		if (boss.velocity.y > 0) {
			int startX = (int) Math.floor(boss.getX() / 16f); // lo que viene
			int finalX = (int) Math.floor((boss.getX() + boss.getWidth()) / 16f); // donde
																					// esté
			int startY = (int) Math.floor((boss.getY()) / 16f);
			int finalY = (int) Math.floor((boss.getY() + boss.getHeight() + boss.velocity.y
					* deltaTime) / 16f);

			checkThisTiles4Boss(layerTiles, startX, finalX, startY, finalY, deltaTime);
		} else if (boss.velocity.y < 0) {
			int startX = (int) Math.floor(boss.getX() / 16f); // lo que viene
			int finalX = (int) Math.floor((boss.getX() + boss.getWidth()) / 16f); // donde
																					// esté
			int startY = (int) Math.floor((boss.getY() + boss.velocity.y * deltaTime) / 16f);
			int finalY = (int) Math.floor((boss.getY() + boss.getHeight()) / 16f);

			checkThisTiles3Boss(layerTiles, startX, finalX, startY, finalY, deltaTime);
		} else { // velocity.y = 0

		}

	}

	private void checkThisTiles(TiledMapTileLayer layerTiles, int startX, int finalX, int startY,
			int finalY, float deltaTime) {
		for (int x = startX; x <= finalX; x++) {
			for (int y = startY; y <= finalY; y++) {
				Cell cell = layerTiles.getCell(x, y);
				if (cell != null) {
					Rectangle rect = this.rectPool.obtain();
					Rectangle playerRect = this.rectPool.obtain();
					rect.set(x * this.TILED_SIZE, y * this.TILED_SIZE, this.TILED_SIZE,
							this.TILED_SIZE);
					playerRect.set(this.player.getRect2().x, this.player.getRect2().y,
							this.player.getRect2().width + this.player.velocity.x * deltaTime,
							this.player.getRect2().height);
					/*
					 * System.out.println("cell is not null and y from tile is "
					 * + rect.y + " height is " + rect.height +
					 * "playerRect y is " + playerRect.y + " and height is " +
					 * playerRect.height);
					 */if (playerRect.overlaps(rect)) {
						//System.out.println(x);
						this.player.setX(x * this.TILED_SIZE - this.TILED_SIZE);
						this.player.velocity.x = 0;
						return;
					}
				}
			}
		}
	}

	private void checkThisTiles2(TiledMapTileLayer layerTiles, int startX, int finalX, int startY,
			int finalY, float deltaTime) {
		for (int x = startX; x <= finalX; x++) {
			for (int y = startY; y <= finalY; y++) {
				Cell cell = layerTiles.getCell(x, y);
				if (cell != null) {
					Rectangle rect = this.rectPool.obtain();
					Rectangle playerRect = this.rectPool.obtain();
					rect.set(x * this.TILED_SIZE, y * this.TILED_SIZE, this.TILED_SIZE,
							this.TILED_SIZE);
					playerRect.set(
							this.player.getRect2().x + this.player.velocity.x * deltaTime,
							this.player.getRect2().y,
							this.player.getRect2().width
									+ Math.abs(this.player.velocity.x * deltaTime),
							this.player.getRect2().height);
					/*
					 * System.out.println("cell is not null and y from tile is "
					 * + rect.y + " height is " + rect.height +
					 * "playerRect y is " + playerRect.y + " and height is " +
					 * playerRect.height);
					 */if (playerRect.overlaps(rect)) {
						//System.out.println(x);
						this.player.setX(x * this.TILED_SIZE + this.TILED_SIZE);
						this.player.velocity.x = 0;
						return;
					}
				}
			}
		}
	}

	private void checkThisTiles3(TiledMapTileLayer layerTiles, int startX, int finalX, int startY,
			int finalY, float deltaTime) {
		for (int x = startX; x <= finalX; x++) {
			for (int y = startY; y <= finalY; y++) {
				Cell cell = layerTiles.getCell(x, y);
				if (cell != null) {
					Rectangle rect = this.rectPool.obtain();
					Rectangle playerRect = this.rectPool.obtain();
					rect.set(x * this.TILED_SIZE, y * this.TILED_SIZE, this.TILED_SIZE,
							this.TILED_SIZE);
					playerRect.set(
							this.player.getRect2().x,
							this.player.getRect2().y + this.player.velocity.y * deltaTime,
							this.player.getRect2().width - 2,
							this.player.getRect2().height
									+ Math.abs(this.player.velocity.y * deltaTime));

					if (playerRect.overlaps(rect)) {
						this.player.desiredPosition.y = y * this.TILED_SIZE + this.TILED_SIZE;
						this.player.velocity.y = 0;
						if (normalGravity)
							this.player.grounded = true;
						return;
					}
				}
			}
		}
		this.player.grounded = false; // Im falling
	}

	private void checkThisTiles4(TiledMapTileLayer layerTiles, int startX, int finalX, int startY,
			int finalY, float deltaTime) {
		for (int x = startX; x <= finalX; x++) {
			for (int y = startY; y <= finalY; y++) {
				Cell cell = layerTiles.getCell(x, y);
				if (cell != null) {
					Rectangle rect = this.rectPool.obtain();
					Rectangle playerRect = this.rectPool.obtain();
					rect.set(x * this.TILED_SIZE, y * this.TILED_SIZE, this.TILED_SIZE,
							this.TILED_SIZE);
					playerRect.set(
							this.player.getRect2().x,
							this.player.getRect2().y,
							this.player.getRect2().width - 2,
							this.player.getRect2().height
									+ Math.abs(this.player.velocity.y * deltaTime));
					/*
					 * System.out.println("cell is not null and y from tile is "
					 * + rect.y + " height is " + rect.height +
					 * "playerRect y is " + playerRect.y + " and height is " +
					 * playerRect.height);
					 */if (playerRect.overlaps(rect)) {
						this.player.desiredPosition.y = y * this.TILED_SIZE
								- this.player.getHeight();
						this.player.velocity.y = 0;
						if (!normalGravity) {
							this.player.grounded = true;
							this.player.desiredPosition.y += 1;
						}
						return;
					}
				}
			}
		}
		this.player.grounded = false; // Im falling
	}

	private void checkThisTilesBoss(TiledMapTileLayer layerTiles, int startX, int finalX,
			int startY, int finalY, float deltaTime) {
		for (int x = startX; x <= finalX; x++) {
			for (int y = startY; y <= finalY; y++) {
				Cell cell = layerTiles.getCell(x, y);
				if (cell != null) {
					Rectangle rect = this.rectPool.obtain();
					Rectangle bossRect = this.rectPool.obtain();
					rect.set(x * this.TILED_SIZE, y * this.TILED_SIZE, this.TILED_SIZE,
							this.TILED_SIZE);
					bossRect.set(this.boss.getRect2().x, this.boss.getRect2().y,
							this.boss.getRect2().width + this.boss.velocity.x * deltaTime,
							this.boss.getRect2().height);
					/*
					 * System.out.println("cell is not null and y from tile is "
					 * + rect.y + " height is " + rect.height + "bossRect y is "
					 * + bossRect.y + " and height is " + bossRect.height);
					 */if (bossRect.overlaps(rect)) {
						//System.out.println(x);
						this.boss.desiredPosition.x = (x * this.TILED_SIZE - this.boss.getRect2().width);
						this.boss.velocity.x = 0;
						return;
					}
				}
			}
		}
	}

	private void checkThisTiles2Boss(TiledMapTileLayer layerTiles, int startX, int finalX,
			int startY, int finalY, float deltaTime) {
		for (int x = startX; x <= finalX; x++) {
			for (int y = startY; y <= finalY; y++) {
				Cell cell = layerTiles.getCell(x, y);
				if (cell != null) {
					Rectangle rect = this.rectPool.obtain();
					Rectangle bossRect = this.rectPool.obtain();
					rect.set(x * this.TILED_SIZE, y * this.TILED_SIZE, this.TILED_SIZE,
							this.TILED_SIZE);
					bossRect.set(
							this.boss.getRect2().x + this.boss.velocity.x * deltaTime,
							this.boss.getRect2().y,
							this.boss.getRect2().width + Math.abs(this.boss.velocity.x * deltaTime),
							this.boss.getRect2().height);
					/*
					 * System.out.println("cell is not null and y from tile is "
					 * + rect.y + " height is " + rect.height + "bossRect y is "
					 * + bossRect.y + " and height is " + bossRect.height);
					 */if (bossRect.overlaps(rect)) {
						//System.out.println(x);
						 this.boss.desiredPosition.x = (x * this.TILED_SIZE + this.TILED_SIZE);
						this.boss.velocity.x = 0;
						return;
					}
				}
			}
		}
	}

	private void checkThisTiles3Boss(TiledMapTileLayer layerTiles, int startX, int finalX,
			int startY, int finalY, float deltaTime) {
		for (int x = startX; x <= finalX; x++) {
			for (int y = startY; y <= finalY; y++) {
				Cell cell = layerTiles.getCell(x, y);
				if (cell != null) {
					Rectangle rect = this.rectPool.obtain();
					Rectangle bossRect = this.rectPool.obtain();
					rect.set(x * this.TILED_SIZE, y * this.TILED_SIZE, this.TILED_SIZE,
							this.TILED_SIZE);
					bossRect.set(
							this.boss.getRect2().x,
							this.boss.getRect2().y + this.boss.velocity.y * deltaTime,
							this.boss.getRect2().width - 2,
							this.boss.getRect2().height
									+ Math.abs(this.boss.velocity.y * deltaTime));

					if (bossRect.overlaps(rect)) {
						this.boss.desiredPosition.y = y * this.TILED_SIZE + this.TILED_SIZE;
						//System.out.println(" " + this.boss.desiredPosition.y);
						this.boss.velocity.y = 0;
						if (normalGravityBoss)
							this.boss.grounded = true;
						return;
					}
				}
			}
		}
		this.boss.grounded = false; // Im falling
	}

	private void checkThisTiles4Boss(TiledMapTileLayer layerTiles, int startX, int finalX,
			int startY, int finalY, float deltaTime) {
		for (int x = startX; x <= finalX; x++) {
			for (int y = startY; y <= finalY; y++) {
				Cell cell = layerTiles.getCell(x, y);
				if (cell != null) {
					Rectangle rect = this.rectPool.obtain();
					Rectangle bossRect = this.rectPool.obtain();
					rect.set(x * this.TILED_SIZE, y * this.TILED_SIZE, this.TILED_SIZE,
							this.TILED_SIZE);
					bossRect.set(
							this.boss.getRect2().x,
							this.boss.getRect2().y,
							this.boss.getRect2().width - 2,
							this.boss.getRect2().height
							+ Math.abs(this.boss.velocity.y * deltaTime));

					System.out.println("cell is not null and x from tile is "
							+ rect.x + " width is " + rect.width + "bossRect x is "
							+ bossRect.x + " and width is " + bossRect.width);
					if (bossRect.overlaps(rect)) {
						this.boss.desiredPosition.y = y * this.TILED_SIZE - this.boss.getHeight();
						System.out.println(" " + this.boss.desiredPosition.y);
						this.boss.velocity.y = 0;
						if (!normalGravityBoss) {
							// System.out.println("Boss ground1ng");
							this.boss.grounded = true;
							this.boss.desiredPosition.y += 2;
						}
						return;
					}
				}
			}
		}
		this.boss.grounded = false; // Im falling
	}

	private void renderPlayer(float deltaTime) {
		AtlasRegion frame = null;
		switch (this.player.state) {
		case Standing:
			frame = (AtlasRegion) Assets.playerStand.getKeyFrame(this.player.stateTime);
			break;
		case Walking:
			frame = (AtlasRegion) Assets.playerWalk.getKeyFrame(this.player.stateTime);
			break;
		case Jumping:
			frame = (AtlasRegion) Assets.playerJump.getKeyFrame(this.player.stateTime);
			break;
		case Intro:
			frame = (AtlasRegion) Assets.playerIntro.getKeyFrame(this.player.stateTime);
			break;
		case Attacking:
			frame = (AtlasRegion) Assets.playerAttack.getKeyFrame(this.player.stateTime);
			break;
		case Die:
			this.configControllers.terminate();
			frame = (AtlasRegion) Assets.playerDie.getKeyFrame(this.player.stateTime);
			break;
		case BeingHit:
			frame = (AtlasRegion) Assets.playerBeingHit.getKeyFrame(this.player.stateTime);
			break;
		case Running:
			frame = (AtlasRegion) Assets.playerRun.getKeyFrame(this.player.stateTime);
			break;
		}
		if (this.player.invincible && this.toggle) {
			frame = (AtlasRegion) Assets.playerEmpty.getKeyFrame(this.player.stateTime);
			this.toggle = !this.toggle;
		} else if (this.player.invincible && !this.toggle) {
			this.toggle = !this.toggle;
		} else if (!this.player.invincible) {
			this.toggle = false;
		}
		// draw the koala, depending on the current velocity
		// on the x-axis, draw the koala facing either right
		// or left
		Batch batch = this.renderer.getSpriteBatch();
		batch.begin();
		if (this.player.facesRight && frame.isFlipX()) {
			frame.flip(true, false);
			this.player.rightOffset = 1f; // fix differences
		} else if (!this.player.facesRight && !frame.isFlipX()) {
			frame.flip(true, false);
			this.player.rightOffset = -4f; // fix differences
		}

		if (this.normalGravity && frame.isFlipY()) {
			frame.flip(false, true);
			this.UpOffset = 0;
		} else if (!this.normalGravity && !frame.isFlipY()) {
			frame.flip(false, true);
			this.UpOffset = +1;
		}

		// batch.draw(frame, this.player.getX() + frame.offsetX,
		// this.player.getY() + frame.offsetY + this.UpOffset);
		batch.draw(frame, (this.player.getX() + this.player.actualFrame.offsetX)
				- this.player.offSetX, (this.player.getY() + this.player.actualFrame.offsetY)
				- this.player.offSetY + this.UpOffset); // upoffset para que se
														// vea que está en su
														// sitio

		batch.end();

		this.shapeRenderer.begin(ShapeType.Line);

		this.shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.rect(this.player.getRect2().x, this.player.getRect2().y - this.SCREEN_HEIGHT
				+ TILED_SIZE, this.player.getRect2().width, this.player.getRect2().height);
		// this.getTiles(0, 0, 25, 15, this.tiles);
		// for (Rectangle tile : this.tiles) {
		// shapeRenderer.rect(tile.x * 1.6f, tile.y * 2, tile.width * 2,
		// tile.height * 2);
		// }
		this.shapeRenderer.setColor(Color.RED);
		// shapeRenderer.rect(playerRect.x * 1.6f, playerRect.y * 2,
		// playerRect.width * 2, playerRect.height * 2);

		this.shapeRenderer.end();
	}

	private void renderBoss(float deltaTime) {
		AtlasRegion frame = null;
		switch (this.boss.state) {
		case Standing:
			frame = (AtlasRegion) Assets.bossStanding.getKeyFrame(this.boss.stateTime);
			break;
		case Walking:
			frame = (AtlasRegion) Assets.bossWalking.getKeyFrame(this.boss.stateTime);
			break;
		case Jumping:
			frame = (AtlasRegion) Assets.bossJumping.getKeyFrame(this.boss.stateTime);
			break;
		/*
		 * case Intro: frame = (AtlasRegion)
		 * Assets.playerIntro.getKeyFrame(this.player.stateTime); break;
		 */
		case Attack:
			frame = (AtlasRegion) Assets.bossAttack.getKeyFrame(this.boss.stateTime);
			break;
		case Die:
			this.configControllers.terminate();
			frame = (AtlasRegion) Assets.bossDie.getKeyFrame(this.boss.stateTime);
			break;
		case BeingHit:
			frame = (AtlasRegion) Assets.bossGethit.getKeyFrame(this.boss.stateTime);
			break;
		case Running:
			frame = (AtlasRegion) Assets.bossRunning.getKeyFrame(this.boss.stateTime);
			break;
		}
		if (this.boss.invincible && this.toggle) {
			frame = (AtlasRegion) Assets.bossGethit.getKeyFrame(this.boss.stateTime);
			this.toggle = !this.toggle;
		} else if (this.boss.invincible && !this.toggle) {
			this.toggle = !this.toggle;
		} else if (!this.boss.invincible) {
			this.toggle = false;
		}
		// draw the koala, depending on the current velocity
		// on the x-axis, draw the koala facing either right
		// or left
		Batch batch = this.renderer.getSpriteBatch();
		batch.begin();
		if (this.boss.facesRight && frame.isFlipX()) {
			frame.flip(true, false);
			this.boss.rightOffset = 1f; // fix differences
		} else if (!this.boss.facesRight && !frame.isFlipX()) {
			frame.flip(true, false);
			this.boss.rightOffset = -4f; // fix differences
		}

		if (this.normalGravityBoss && frame.isFlipY()) {
			frame.flip(false, true);
			this.UpOffset = 0;
		} else if (!this.normalGravityBoss && !frame.isFlipY()) {
			frame.flip(false, true);
			this.UpOffset = -2;
		}

		// batch.draw(frame, this.player.getX() + frame.offsetX,
		// this.player.getY() + frame.offsetY + this.UpOffset);
		this.boss.actualFrame = frame;
		// System.out.println("Anchura boss " + this.boss.actualFrame.offsetX);
		batch.draw(frame, (this.boss.getX() + this.boss.actualFrame.offsetX) - this.boss.offSetX,
				(this.boss.getY() + this.boss.actualFrame.offsetY) - this.boss.offSetY);

		batch.end();

		this.shapeRenderer.begin(ShapeType.Line);

		this.shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.rect(this.boss.getRect2().x, this.boss.getRect2().y - this.SCREEN_HEIGHT
				+ TILED_SIZE, this.boss.getRect2().width, this.boss.getRect2().height);
		// this.getTiles(0, 0, 25, 15, this.tiles);
		// for (Rectangle tile : this.tiles) {
		// shapeRenderer.rect(tile.x * 1.6f, tile.y * 2, tile.width * 2,
		// tile.height * 2);
		// }
		this.shapeRenderer.setColor(Color.RED);
		// shapeRenderer.rect(playerRect.x * 1.6f, playerRect.y * 2,
		// playerRect.width * 2, playerRect.height * 2);

		this.shapeRenderer.end();
	}

	private void renderEnemies(float deltaTime) {
	    for (Enemy enemy : this.enemies) {
	    		enemy.actualFrame = null;
	    		if (enemy.enemyType == Enemy.Type.Spider){
	    			switch (enemy.state) {
	    			case Walking:
	    				enemy.actualFrame = (AtlasRegion)Assets.enemy_spider_walk.getKeyFrame(enemy.stateTime);
	    				break;
	    			case Running:
	    				enemy.actualFrame = (AtlasRegion)Assets.enemy_spider_attack.getKeyFrame(enemy.stateTime);
	    				break;
	    			case Hurting:
	    				enemy.actualFrame = (AtlasRegion)Assets.enemy_spider_dying.getKeyFrame(enemy.stateTime);
	    				break;
	    			case BeingInvoked:
	    				enemy.actualFrame = (AtlasRegion)Assets.enemy_spider_walk.getKeyFrame(enemy.stateTime);
	    				break;
	    			}
	    		}
	    		else if (enemy.enemyType == Enemy.Type.Bat){
	    			switch (enemy.state) {
	    			case Walking:
	    				enemy.actualFrame = (AtlasRegion)Assets.enemy_bat_fly.getKeyFrame(enemy.stateTime);
	    				break;
	    			case Running:
	    				enemy.actualFrame = (AtlasRegion)Assets.enemy_bat_fly.getKeyFrame(enemy.stateTime);
	    				break;
	    			case Hurting:
	    				enemy.actualFrame = (AtlasRegion)Assets.enemy_bat_dying.getKeyFrame(enemy.stateTime);
	    				break;
	    			case BeingInvoked:
	    				enemy.actualFrame = (AtlasRegion)Assets.enemy_bat_fly.getKeyFrame(enemy.stateTime);
	    				break;
	    			}
	    		}

            Batch batch = this.renderer.getSpriteBatch();
            batch.begin();
    		if (enemy.getY() < POS_LOWER_WORLD && !enemy.actualFrame.isFlipY()) {
    			enemy.actualFrame.flip(false, true);
    			enemy.offsetY = 4;
    		} else if (enemy.getY() >= POS_LOWER_WORLD && enemy.actualFrame.isFlipY()) {
    			enemy.actualFrame.flip(false, true);
    			enemy.offsetY = 0;
    		}

            if (enemy.dir == Enemy.Direction.Right) {
                if (enemy.actualFrame.isFlipX())
                	enemy.actualFrame.flip(true, false);
                batch.draw(enemy.actualFrame, enemy.getX(), enemy.getY() + enemy.offsetY);
            } else {
                if (!enemy.actualFrame.isFlipX())
                	enemy.actualFrame.flip(true, false);
                batch.draw(enemy.actualFrame, enemy.getX(), enemy.getY() + enemy.offsetY);
            }
            batch.end();
	    }
	}

	private void movingShootingJumping(float deltaTime) {

		if (this.player.noControl == false) {

			if (Gdx.input.isKeyJustPressed(Keys.LEFT))
				activateLeftRunning();

			if (Gdx.input.isKeyPressed(Keys.LEFT) || this.configControllers.leftPressed)
				moveLeft();

			if (Gdx.input.isKeyJustPressed(Keys.RIGHT))
				activateRightRunning();

			if (Gdx.input.isKeyPressed(Keys.RIGHT) || this.configControllers.rightPressed)
				moveRight();

			if (Gdx.input.isKeyJustPressed(Keys.S) || this.configControllers.activateJump){
				this.jump();
				this.configControllers.activateJump = false;
			}

			if (Gdx.input.isKeyJustPressed(Keys.D)|| this.configControllers.activateShoot) {
				this.shoot();
				this.configControllers.activateShoot = false;
			}
		}

		if (Assets.playerAttack.isAnimationFinished(this.player.stateTime))
			this.player.shooting = false;
	}

	private void updateShots(float deltaTime) {
		int i = 0;
		 boolean[] toBeDeleted = new boolean[3];
		 for (Shot shot : this.shotArray){
			 if (shot != null){
				 if(this.updateShot(shot, deltaTime) == true){
					 toBeDeleted[i] = true; //pool of shots?
					 }
				 i++;
				 }
		 }

		 for(int j = 0; j < toBeDeleted.length; j++){
			 if (toBeDeleted[j] && (this.shotArray.size >= (j + 1)))
				 this.shotArray.removeIndex(j);
			 }
	}

	public void shoot() {
		if  (this.shotArray.size < 3){
			Assets.playSound("playerAttack");
			Shot shot = new Shot(Assets.playerShot);
			if (this.player.facesRight){
				//-1 necessary to be exactly the same as the other facing
				shot.Initialize((this.player.getCenterX()), ((this.player.getY() + (this.player.getHeight() / 2)) - 10), this.player.facesRight, this.normalGravity, this.player.getVelocityX());
			}
			else {
				shot.Initialize((this.player.getCenterX()), ((this.player.getY() + (this.player.getHeight() / 2)) - 10), this.player.facesRight, this.normalGravity, this.player.getVelocityX());
			}
			this.shotArray.add(shot);

			this.player.state = Player.State.Attacking;
			this.player.stateTime = 0;
			this.player.shooting = true;
		}
	}

	private void moveRight() {
		if (!this.player.run){
			this.player.velocity.x = this.player.MAX_VELOCITY;
			if (this.player.grounded
					&& Assets.playerAttack.isAnimationFinished(this.player.stateTime)
					&& Assets.playerBeingHit.isAnimationFinished(this.player.stateTime)) {
				this.player.state = Player.State.Walking;
			}
		}
		else {
			this.player.velocity.x = this.player.MAX_VELOCITY * 2f;
			if (this.player.grounded
					&& Assets.playerAttack.isAnimationFinished(this.player.stateTime)
					&& Assets.playerBeingHit.isAnimationFinished(this.player.stateTime)) {
				this.player.state = Player.State.Running;
			}
		}
		this.player.facesRight = true;
	}

	private void moveLeft() {
		if (!this.player.run){
			this.player.velocity.x = -this.player.MAX_VELOCITY;
			if (this.player.grounded
					&& Assets.playerAttack.isAnimationFinished(this.player.stateTime)
					&& Assets.playerBeingHit.isAnimationFinished(this.player.stateTime)) {
				this.player.state = Player.State.Walking;
			}
		}
		else {
			this.player.velocity.x = -this.player.MAX_VELOCITY * 2f;
			if (this.player.grounded
					&& Assets.playerAttack.isAnimationFinished(this.player.stateTime)
					&& Assets.playerBeingHit.isAnimationFinished(this.player.stateTime)) {
				this.player.state = Player.State.Running;
			}
		}
		this.player.facesRight = false;
	}

	public void activateRightRunning() {
		if ((System.currentTimeMillis() - this.player.lastTimeRightPlayer) < 200L)
			this.player.run = true;
		else if (this.player.state != Player.State.Jumping)
			this.player.run = false;

		this.player.lastTimeRightPlayer = System.currentTimeMillis();
	}

	public void activateLeftRunning() {
		if ((System.currentTimeMillis() - this.player.lastTimeLeftPlayer) < 200L)
			this.player.run = true;
		else if (this.player.state != Player.State.Jumping)
			this.player.run = false;

		this.player.lastTimeLeftPlayer = System.currentTimeMillis();
	}

	public void jump() {
		if (this.player.grounded) {
			Assets.playSound("playerJump");
			if (this.normalGravity)
				this.player.velocity.y = this.player.JUMP_VELOCITY;
			else
				this.player.velocity.y = -this.player.JUMP_VELOCITY;
			this.player.grounded = false;
			this.player.state = Player.State.Jumping;
		}
	}

	private void movingShootingJumpingBoss(float deltaTime) {

		if (this.boss.noControl == false) {
			if (Gdx.input.isKeyJustPressed(Keys.G))
				this.jumpBoss();

			if (Gdx.input.isKeyJustPressed(Keys.J))
				activateLeftRunningBoss();

			if (Gdx.input.isKeyPressed(Keys.J) || this.configControllers.leftPressed2P)
				moveLeftBoss();

			if (Gdx.input.isKeyJustPressed(Keys.L))
				activateRightRunningBoss();

			if (Gdx.input.isKeyPressed(Keys.L) || this.configControllers.rightPressed2P)
				moveRightBoss();

			if (Gdx.input.isKeyJustPressed(Keys.H)) {
				this.shootBoss();
			}
		}

		if (Assets.bossAttack.isAnimationFinished(this.boss.stateTime))
			this.boss.shooting = false;

		/*
		 * int i = 0; boolean[] toBeDeleted = new boolean[3]; for (Shot shot :
		 * this.shotArray){ if (shot != null){ if(this.updateShot(shot,
		 * deltaTime) == true) toBeDeleted[i] = true; //pool of shots? } i++; }
		 *
		 *
		 * for(int j = 0; j < toBeDeleted.length; j++){ if (toBeDeleted[j] &&
		 * (this.shotArray.size >= (j + 1))) this.shotArray.removeIndex(j); }
		 */
	}

	private void shootBoss(){
		if (!this.boss.shooting){
			this.boss.state = Boss.State.Attack;
			this.boss.stateTime = 0;
			this.boss.shooting = true;
		}
	}

	private void moveRightBoss() {
		if (!this.boss.run){
			this.boss.velocity.x = this.boss.MAX_VELOCITY;
			if (this.boss.grounded
					&& Assets.bossAttack.isAnimationFinished(this.boss.stateTime)
					&& Assets.bossGethit.isAnimationFinished(this.boss.stateTime)) {
				this.boss.state = Boss.State.Walking;
			}
		}
		else {
			this.boss.velocity.x = this.boss.MAX_VELOCITY * 2f;
			if (this.boss.grounded
					&& Assets.bossAttack.isAnimationFinished(this.boss.stateTime)
					&& Assets.bossGethit.isAnimationFinished(this.boss.stateTime)) {
				this.boss.state = Boss.State.Running;
			}
		}
		this.boss.facesRight = true;
	}

	public void activateRightRunningBoss() {
		if ((System.currentTimeMillis() - this.boss.lastTimeRightBoss) < 200L)
			this.boss.run = true;
		else if (this.boss.state != Boss.State.Jumping)
			this.boss.run = false;

		this.boss.lastTimeRightBoss = System.currentTimeMillis();
	}

	private void moveLeftBoss() {
		if (!this.boss.run){
			this.boss.velocity.x = -this.boss.MAX_VELOCITY;
			if (this.boss.grounded
					&& Assets.bossAttack.isAnimationFinished(this.boss.stateTime)
					&& Assets.bossGethit.isAnimationFinished(this.boss.stateTime)) {
				this.boss.state = Boss.State.Walking;
			}
		}
		else {
			this.boss.velocity.x = -this.boss.MAX_VELOCITY * 2f;
			if (this.boss.grounded
					&& Assets.bossAttack.isAnimationFinished(this.boss.stateTime)
					&& Assets.bossGethit.isAnimationFinished(this.boss.stateTime)) {
				this.boss.state = Boss.State.Running;
			}
		}
		this.boss.facesRight = false;
	}

	public void activateLeftRunningBoss() {
		if ((System.currentTimeMillis() - this.boss.lastTimeLeftBoss) < 200L)
			this.boss.run = true;
		else if (this.boss.state != Boss.State.Jumping)
			this.boss.run = false;

		this.boss.lastTimeLeftBoss = System.currentTimeMillis();
	}

	public void jumpBoss() {
		// System.out.println("Boss TRYING TO JUMP");
		if (this.boss.grounded) {
			// System.out.println("Boss GROUNDED AND ABOUT TO JUMP");
			Assets.playSound("playerJump");
			if (this.normalGravityBoss)
				this.boss.velocity.y = this.boss.JUMP_VELOCITY;
			else
				this.boss.velocity.y = -this.boss.JUMP_VELOCITY;
			this.boss.grounded = false;
			this.boss.state = Boss.State.Jumping;
		}
	}

	private void getTiles(int startX, int startY, int endX, int endY, Array<Rectangle> tiles) {
	    this.getTiles(startX, startY, endX, endY, tiles, null);
    }

	private void getTiles(int startX, int startY, int endX, int endY, Array<Rectangle> tiles, Array<Rectangle> spikes) {
		TiledMapTileLayer layer = (TiledMapTileLayer)(this.map.getLayers().get("Platfs"));
		TiledMapTileLayer layer2 = (TiledMapTileLayer)(this.map.getLayers().get("Platfs"));
		this.rectPool.freeAll(tiles);
		tiles.clear();
        if (spikes != null) {
            this.rectPool.freeAll(spikes);
            spikes.clear();
        }
		for (int y = startY; y <= endY; y++) {
			for (int x = startX; x <= endX; x++) {
				Cell cell = layer.getCell(x, y);
				if (cell != null) {
					Rectangle rect = this.rectPool.obtain();
					rect.set(x * this.TILED_SIZE, y  * this.TILED_SIZE, this.TILED_SIZE, this.TILED_SIZE);
					tiles.add(rect);
                }
				if (spikes != null) {
                    Cell cell2 = layer2.getCell(x, y);
                    if (cell2 != null) {
                        Rectangle rect = this.rectPool.obtain();
                        rect.set(x * this.TILED_SIZE, y  * this.TILED_SIZE, this.TILED_SIZE, this.TILED_SIZE);
                        spikes.add(rect);
                        tiles.add(rect);
                    }
				}
            }
        }
    }

}
