package com.blogspot.ludumdaresforfun.aholenewrace;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;


public class Assets {
	static AssetManager assetManager, assetManagerCutScenes;
	static Animation enemy_bat_fly, enemy_spider_attack, enemy_spider_walk;
	static Animation race_start, race_finish, race_finish_left, race_finish_right;
    static Animation playerAttack, playerEmpty, playerIntro, playerStand, playerWalk, playerRun, playerJump, playerBeingHit, playerDie;
    static Animation playerShot, playerShotHit;
    static Animation enemyWalk, enemyRun, enemyHurt, enemyAppearing;
    static Animation bossGethit, bossStanding,  bossWalking, bossRunning, bossJumping, bossFalling, bossAttack, bossSummon, bossDie;
    static Animation Ending, GameOver, Intro, SequenceIntro, SequenceEnding;
    static Animation hudBase, hudPositionBoss, hudPositionPlayer, hudLifeBoss, hudLifePlayer;
    static AtlasRegion intro_screen_logo_base;
    static AnimationNinja intro_screen_logo, intro_screen_logo_loop;
    static AnimationNinja intro_BADLY_DONE;
    static AtlasRegion item_apple, item_banana, item_chicken, item_invulnerability, item_jump, item_speed;
	static float offsetPlayer, offsetBoss, offsetShot, offsetEnemy, offsetPositionBoss, offsetPositionPlayer;
	static Vector2 offsetLifeBoss, offsetLifePlayer;

	// Music and Sounds
	public static Music musicBoss, musicStage;
    public static HashMap<String, Sound> sounds = new HashMap<String, Sound>();

	static void loadAnimation() {

		final String TEXTURE_ATLAS_OBJECTS_CUTSCENES = "cutscenes.pack";
		assetManagerCutScenes = new AssetManager();
		assetManagerCutScenes.load(TEXTURE_ATLAS_OBJECTS_CUTSCENES, TextureAtlas.class);
		assetManagerCutScenes.finishLoading();

        TextureAtlas atlasCutScenes = assetManagerCutScenes.get(TEXTURE_ATLAS_OBJECTS_CUTSCENES);
		Array<AtlasRegion> regionsCutScenes;

		// INTRO

		// Intro Logo
		intro_screen_logo_base = atlasCutScenes.findRegion("intro_screen_logo_base");

		regionsCutScenes = atlasCutScenes.findRegions("intro_screen_logo");
		regionsCutScenes = new Array<AtlasRegion>(new AtlasRegion[] {
				regionsCutScenes.get(0), regionsCutScenes.get(1), regionsCutScenes.get(2), regionsCutScenes.get(3)});

		intro_screen_logo = new AnimationNinja(0.25f, regionsCutScenes);
		float [] frameDurations =  {0.5f, 0.5f, 0.5f, 1.0f};
		intro_screen_logo = new AnimationNinja(frameDurations, regionsCutScenes, AnimationNinja.NORMAL);

		regionsCutScenes = atlasCutScenes.findRegions("intro_screen_logo");
		regionsCutScenes = new Array<AtlasRegion>(new AtlasRegion[] { regionsCutScenes.get(3), regionsCutScenes.get(4)});
		intro_screen_logo_loop =  new AnimationNinja(0.25f, regionsCutScenes);

		// Complete Intro (BADLY DONE) :(
		regionsCutScenes = atlasCutScenes.findRegions("intro_BADLY_DONE");
		/*
		regionsCutScenes = new Array<AtlasRegion>(new AtlasRegion[] {
				regionsCutScenes.get(0),
				regionsCutScenes.get(1),
				regionsCutScenes.get(2),
				regionsCutScenes.get(3)
			});
		float [] frameDurations2 =  {0.5f, 0.5f, 0.5f, 1.0f};
		*/
		intro_BADLY_DONE =  new AnimationNinja(0.25f, regionsCutScenes);



        final String TEXTURE_ATLAS_OBJECTS = "characters.pack";
		assetManager = new AssetManager();
		assetManager.load(TEXTURE_ATLAS_OBJECTS, TextureAtlas.class);
		assetManager.finishLoading();

        TextureAtlas atlas = assetManager.get(TEXTURE_ATLAS_OBJECTS);
		Array<AtlasRegion> regions;

        // ITEMS (non animated)
		item_apple  = atlas.findRegion("item_apple");
		item_banana = atlas.findRegion("item_banana");
		item_chicken = atlas.findRegion("item_chicken");
		item_invulnerability = atlas.findRegion("item_invulnerability");
		item_jump = atlas.findRegion("item_jump");
		item_speed = atlas.findRegion("item_speed");



		//BG
		regions = atlas.findRegions("intro");
		Intro = new Animation(0.25f, regions);
		Intro.setPlayMode(PlayMode.LOOP);

		regions = atlas.findRegions("ending");
		Ending = new Animation(0.25f, regions);
		Ending.setPlayMode(PlayMode.LOOP);

		regions = atlas.findRegions("game_over");
		GameOver = new Animation(0f, regions);
		GameOver.setPlayMode(PlayMode.LOOP);

		regions = atlas.findRegions("sequence_intro");
		SequenceIntro = new Animation(0.55f, regions);
		SequenceIntro.setPlayMode(PlayMode.NORMAL);

		regions = atlas.findRegions("sequence_ending");
		SequenceEnding = new Animation(0.55f, regions);
		SequenceEnding.setPlayMode(PlayMode.NORMAL);


        // Race Start & Race End
        regions = atlas.findRegions("race_start");
        race_start = new Animation(0.55f, regions);
        race_start.setPlayMode(PlayMode.NORMAL);

        regions = atlas.findRegions("race_finish"); // Esta no es la que deberíamos usar, pero al ser de una pieza es más sencilla, la subo por si acaso
        race_finish = new Animation(0.55f, regions);
        race_finish.setPlayMode(PlayMode.NORMAL);

        regions = atlas.findRegions("race_finish_left");
        race_finish_left = new Animation(0.55f, regions);
        race_finish_left.setPlayMode(PlayMode.NORMAL);

        regions = atlas.findRegions("race_finish_right");
        race_finish_right = new Animation(0.55f, regions);
        race_finish_right.setPlayMode(PlayMode.NORMAL);


		// Player
		regions = atlas.findRegions("char_attack");
		playerAttack = new Animation(0.25f, regions);

		regions = atlas.findRegions("char_empty");
		playerEmpty = new Animation(0, regions);

		regions = atlas.findRegions("char_intro");
		playerIntro = new Animation(0.25f, regions);

		regions = atlas.findRegions("char_standing");
		playerStand = new Animation(0.15f, regions);

		regions = atlas.findRegions("char_walking");
		playerWalk = new Animation(0.15f, regions);
		playerWalk.setPlayMode(Animation.PlayMode.LOOP);

		regions = atlas.findRegions("char_running");
		playerRun = new Animation(0.15f, regions);
		playerRun.setPlayMode(Animation.PlayMode.LOOP);

		regions = atlas.findRegions("char_jumping");  //change to jumping
		playerJump = new Animation(0, regions.get(1));

		regions = atlas.findRegions("char_gethit");  //change to jumping
		playerBeingHit = new Animation(0.8f, regions);

		regions = atlas.findRegions("char_dying");  //change to jumping
		playerDie = new Animation(0.25f, regions);

		// Shot
		regions = atlas.findRegions("char_attack_holy_water");
		playerShot = new Animation(0, regions);
		offsetShot = regions.first().offsetX;

		regions = atlas.findRegions("char_attack_holy_water_hit");
		playerShotHit = new Animation(0.15f, regions);

		// Enemies
		regions = atlas.findRegions("enemy_walking");//"enemy_walk"); // change
		enemyWalk = new Animation(0.15f, regions);
		enemyWalk.setPlayMode(Animation.PlayMode.LOOP);

		regions = atlas.findRegions("enemy_attack");//"enemy_run"); // change
		enemyRun = new Animation(0.50f, regions);

		regions = atlas.findRegions("enemy_dying");//"enemy_hurt"); // change
		enemyHurt = new Animation(0.15f, regions);

		regions = atlas.findRegions("enemy_appearing");//"enemy_hurt"); // change
		enemyAppearing = new Animation(0.20f, regions);

		regions = atlas.findRegions("enemy_bat_fly");
		enemy_bat_fly = new Animation(0.15f, regions);
		enemy_bat_fly.setPlayMode(Animation.PlayMode.LOOP);

		regions = atlas.findRegions("enemy_spider_walk");
		enemy_spider_walk = new Animation(0.15f, regions);
		enemy_spider_walk.setPlayMode(Animation.PlayMode.LOOP);

		regions = atlas.findRegions("enemy_spider_attack");
		enemy_spider_attack = new Animation(0.20f, regions);


		// Boss
		regions = atlas.findRegions("boss_gethit");
		//regions = atlas.findRegions("boss_empty");	//where is boss empty?s
		bossGethit = new Animation(0.15f, regions);
		bossGethit.setPlayMode(PlayMode.LOOP);

		regions = atlas.findRegions("boss_standing");
		bossStanding = new Animation(0.15f, regions);
		bossStanding.setPlayMode(PlayMode.LOOP);

		regions = atlas.findRegions("boss_walking");
		bossWalking = new Animation(0.15f, regions);
		bossWalking.setPlayMode(PlayMode.LOOP);

		regions = atlas.findRegions("boss_running");
		bossRunning = new Animation(0.25f, regions);
		bossRunning.setPlayMode(PlayMode.LOOP);

		regions = atlas.findRegions("boss_jump_a");
		bossJumping = new Animation(0.15f, regions);
		bossJumping.setPlayMode(PlayMode.LOOP);

		regions = atlas.findRegions("boss_jump_d");
		bossFalling = new Animation(0.15f, regions);
		bossFalling.setPlayMode(PlayMode.LOOP);

		regions = atlas.findRegions("boss_attack_close");
		bossAttack = new Animation(0.08f, regions);

		regions = atlas.findRegions("boss_attack_distance");
		bossSummon = new Animation(0.15f, regions);

		regions = atlas.findRegions("boss_dying");
		bossDie = new Animation(0.30f, regions);

		// HUD
		regions = atlas.findRegions("hud_base");
		hudBase = new Animation(0, regions);

		regions = atlas.findRegions("hud_position_boss");
		hudPositionBoss = new Animation(0, regions);
		offsetPositionBoss = regions.first().offsetX;

		regions = atlas.findRegions("hud_position_player");
		hudPositionPlayer = new Animation(0, regions);
		offsetPositionPlayer = regions.first().offsetX;

		//which region is this one?
		//regions = atlas.findRegions("hud_");
		//hudBossHead = new Animation(0, regions);
		//offsetBoosHead = regions.first().offsetX;

		regions = atlas.findRegions("hud_life_counter_boss");
		hudLifeBoss = new Animation(0, regions);
		offsetLifeBoss = new Vector2(regions.first().offsetX, regions.first().offsetY);

		regions = atlas.findRegions("hud_life_counter_player");
		hudLifePlayer = new Animation(0, regions);
		offsetLifePlayer = new Vector2(regions.first().offsetX, regions.first().offsetY);
	}

	public static void loadMusicAndSound() {
		musicStage = Gdx.audio.newMusic(Gdx.files.internal("music/mainTheme.ogg"));
		musicBoss = Gdx.audio.newMusic(Gdx.files.internal("music/finalBoss.ogg"));
		musicBoss.setVolume(0.5f);
		musicStage.setVolume(0.5f);
		// Player
        addSound("playerAttack");
        addSound("playerHurt");
        addSound("playerDead");
        addSound("playerJump");
        addSound("playerShot");
        addSound("gainLifePlayer");
		// Enemy
        addSound("enemyAttack");
        addSound("enemyDead");
		// Boss
        addSound("bossAttack");
        addSound("bossHurt");
        addSound("bossDead");
        addSound("gainLifeBoss");
		// Others
        addSound("closeDoor");
        addSound("holyWaterBroken");
        addSound("lightning");
	}

    public static void addSound(final String name) {
        sounds.put(name, Gdx.audio.newSound(Gdx.files.internal("sounds/" + name + ".ogg")));
    }

    public static void playSound(final String name) {
    	sounds.get(name).play(1f);
    }

	static void dispose() {
		musicBoss.dispose();
		musicStage.dispose();
		assetManager.dispose();
	}
}