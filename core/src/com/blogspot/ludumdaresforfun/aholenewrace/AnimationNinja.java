package com.blogspot.ludumdaresforfun.aholenewrace;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

/** <p>
 * An Animation stores a list of {@link TextureRegion}s representing an animated sequence, e.g. for running or jumping. Each
 * region of an Animation is called a key frame, multiple key frames make up the animation.
 * </p>
 *
 * @author mzechner */
//Improved by MadGearGames

public class AnimationNinja{
	public static final int NORMAL = 0;
	public static final int REVERSED = 1;
	public static final int LOOP = 2;
	public static final int LOOP_REVERSED = 3;
	public static final int LOOP_PINGPONG = 4;
	public static final int LOOP_RANDOM = 5;

	public final TextureRegion[] keyFrames;
	public float frameDuration;
	private float[] frameDurationArray = new float[] {0};

	//Getter to ask for the array of durations
	public float[] getFrameDurationArray() {
		return frameDurationArray;
	}

	//Returns a specific key frame by its index
	public TextureRegion getKeyFrame(int keyFrameIndex) {
		if(keyFrameIndex >= 0 && keyFrameIndex < this.keyFrames.length) {
			return keyFrames[keyFrameIndex];
		}
		throw new IllegalArgumentException("Requested key frame is out of index. It doesn't exist.");
	}
	
	//Returns the total number of keyframes for this animation.
	public int getAnimationLength() {
		return keyFrames.length;
	}

	//Its possible to set the array of frames with the first time or a posteriori through this method
	public void setFrameDurationArray(float[] frameDurationArray) {
		this.frameDurationArray = frameDurationArray;
		this.animationDuration = 0;
		for (int i = 0; i < frameDurationArray.length; i++) {
			this.animationDuration += frameDurationArray[i];
		}
		constructorArray = true;
	}

	//It's possible to set only one time different
	public void setFrameDurationArray(float f, int i) {
		if (!constructorArray)
			System.out.println("animation without duration array");
		else {
			if (i < frameDurationArray.length)
				this.frameDurationArray[i] = f;
			else
				System.out.println("index out of array");

			this.animationDuration = 0;
			for (int j = 0; j < frameDurationArray.length; j++) {
				this.animationDuration += frameDurationArray[j];
			}
		}
	}

	public float animationDuration;

	private boolean constructorArray = false;

	public int playMode = NORMAL;

	/** Constructor, storing the frame duration and key frames.
	 *
	 * @param frameDuration the time between frames in seconds.
	 * @param keyFrames the {@link TextureRegion}s representing the frames. */
	public AnimationNinja (float frameDuration, Array<? extends TextureRegion> keyFrames) {
		this.frameDuration = frameDuration;
		this.animationDuration = keyFrames.size * frameDuration;
		this.keyFrames = new TextureRegion[keyFrames.size];
		for (int i = 0, n = keyFrames.size; i < n; i++) {
			this.keyFrames[i] = keyFrames.get(i);
		}

		this.playMode = NORMAL;
	}

	/** Constructor, storing the frame duration, key frames and play type.
	 *
	 * @param frameDuration the time between frames in seconds.
	 * @param keyFrames the {@link TextureRegion}s representing the frames.
	 * @param playType the type of animation play (NORMAL, REVERSED, LOOP, LOOP_REVERSED, LOOP_PINGPONG, LOOP_RANDOM) */
	public AnimationNinja (float frameDuration, Array<? extends TextureRegion> keyFrames, int playType) {

		this.frameDuration = frameDuration;
		this.animationDuration = keyFrames.size * frameDuration;
		this.keyFrames = new TextureRegion[keyFrames.size];
		for (int i = 0, n = keyFrames.size; i < n; i++) {
			this.keyFrames[i] = keyFrames.get(i);
		}

		this.playMode = playType;
	}

	/** Constructor, storing the frame duration, key frames and play type.
	 *
	 * @param frameDuration the time between frames in seconds. An array is given with the different times.
	 * @param keyFrames the {@link TextureRegion}s representing the frames.
	 * @param playType the type of animation play (NORMAL, REVERSED, LOOP, LOOP_REVERSED, LOOP_PINGPONG, LOOP_RANDOM) */
	public AnimationNinja (float[] frameDuration, Array<? extends TextureRegion> keyFrames, int playType) {
		this.constructorArray = true;
		this.frameDuration = frameDurationArray[0];
		this.animationDuration = 0;
		this.frameDurationArray = new float[frameDuration.length];

		for (int i = 0; i < frameDuration.length; i++) {
			this.animationDuration += frameDuration[i];
			this.frameDurationArray[i] = frameDuration[i];
		}

		this.keyFrames = new TextureRegion[keyFrames.size];
		for (int i = 0, n = keyFrames.size; i < n; i++) {
			this.keyFrames[i] = keyFrames.get(i);
		}

		this.playMode = playType;
	}


	/** Constructor, storing the frame duration and key frames.
	 *
	 * @param frameDuration the time between frames in seconds.
	 * @param keyFrames the {@link TextureRegion}s representing the frames. */
	public AnimationNinja (float frameDuration, TextureRegion... keyFrames) {
		this.frameDuration = frameDuration;
		this.animationDuration = keyFrames.length * frameDuration;
		this.keyFrames = keyFrames;
		this.playMode = NORMAL;
	}

	/** Returns a {@link TextureRegion} based on the so called state time. This is the amount of seconds an object has spent in the
	 * state this Animation instance represents, e.g. running, jumping and so on. The mode specifies whether the animation is
	 * looping or not.
	 *
	 * @param stateTime the time spent in the state represented by this animation.
	 * @param looping whether the animation is looping or not.
	 * @return the TextureRegion representing the frame of animation for the given state time. */
	public TextureRegion getKeyFrame (float stateTime, boolean looping) {
		// we set the play mode by overriding the previous mode based on looping
		// parameter value

		if (constructorArray){
			return getKeyFrameIfArray(stateTime, looping);
		}

		int oldPlayMode = playMode;
		if (looping && (playMode == NORMAL || playMode == REVERSED)) {
			if (playMode == NORMAL)
				playMode = LOOP;
			else
				playMode = LOOP_REVERSED;
		} else if (!looping && !(playMode == NORMAL || playMode == REVERSED)) {
			if (playMode == LOOP_REVERSED)
				playMode = REVERSED;
			else
				playMode = LOOP;
		}

		TextureRegion frame = getKeyFrame(stateTime);
		playMode = oldPlayMode;
		return frame;
	}

	//If an array was given this method is called instead
	public TextureRegion getKeyFrameIfArray (float stateTime, boolean looping) {
		// we set the play mode by overriding the previous mode based on looping
		// parameter value
		int oldPlayMode = playMode;
		if (looping && (playMode == NORMAL || playMode == REVERSED)) {
			if (playMode == NORMAL)
				playMode = LOOP;
			else
				playMode = LOOP_REVERSED;
		} else if (!looping && !(playMode == NORMAL || playMode == REVERSED)) {
			if (playMode == LOOP_REVERSED)
				playMode = REVERSED;
			else
				playMode = LOOP;
		}

		TextureRegion frame = getKeyFrameIfArray(stateTime);
		playMode = oldPlayMode;
		return frame;
	}

	/** Returns a {@link TextureRegion} based on the so called state time. This is the amount of seconds an object has spent in the
	 * state this Animation instance represents, e.g. running, jumping and so on using the mode specified by
	 * {@link #setPlayMode(int)} method.
	 *
	 * @param stateTime
	 * @return the TextureRegion representing the frame of animation for the given state time. */
	public TextureRegion getKeyFrame (float stateTime) {
		if (constructorArray)
			return getKeyFrameIfArray(stateTime);

		int frameNumber = getKeyFrameIndex (stateTime);
		return keyFrames[frameNumber];
	}


	public TextureRegion getKeyFrameIfArray (float stateTime) {
		int frameNumber = getKeyFrameIndexIfArray (stateTime);
		return keyFrames[frameNumber];
	}

	/** Returns the current frame number.
	 * @param stateTime
	 * @return current frame number */
	public int getKeyFrameIndex (float stateTime) {
		if(keyFrames.length == 1)
			return 0;

		int frameNumber = (int)(stateTime / frameDuration);
		switch (playMode) {
		case NORMAL:
			frameNumber = Math.min(keyFrames.length - 1, frameNumber);
			break;
		case LOOP:
			frameNumber = frameNumber % keyFrames.length;
			break;
		case LOOP_PINGPONG:
			frameNumber = frameNumber % ((keyFrames.length * 2) - 2);
         if (frameNumber >= keyFrames.length)
            frameNumber = keyFrames.length - 2 - (frameNumber - keyFrames.length);
         break;
		case LOOP_RANDOM:
			frameNumber = MathUtils.random(keyFrames.length - 1);
			break;
		case REVERSED:
			frameNumber = Math.max(keyFrames.length - frameNumber - 1, 0);
			break;
		case LOOP_REVERSED:
			frameNumber = frameNumber % keyFrames.length;
			frameNumber = keyFrames.length - frameNumber - 1;
			break;

		default:
			// play normal otherwise
			frameNumber = Math.min(keyFrames.length - 1, frameNumber);
			break;
		}

		return frameNumber;
	}

	//A different method is used if an array of frames was set
	/** Returns the current frame number.
	 * @param stateTime
	 * @return current frame number */
	public int getKeyFrameIndexIfArray (float stateTime) {
		if(keyFrames.length == 1)
			return 0;

		//With the difference between the actual time and the animation duration we will know the exact frame
		float frameNumberFloat = (stateTime % animationDuration);
		float frameTime[] = new float[keyFrames.length];

		//the float framePosition is the sum of the duration of the actual frame plus all the previous one -> Accumulated duration
		float framePosition[] = new float[keyFrames.length];
		for (int i = 0; i < framePosition.length; i++) {
			if (i == 0) {
				framePosition[i] = frameDurationArray[i];
				continue;
			}
			framePosition[i] = frameDurationArray[i] + framePosition[i - 1];
		}

		//With the framePosition and the actual time we can know which frame Number is the actual one
		int frameNumber = 0;
		if (frameNumberFloat < framePosition[0])
			frameNumber = 0;
		if (frameNumberFloat > framePosition[frameDurationArray.length - 1])
			frameNumber = frameDurationArray.length - 1;

		for (int i = 0; i < frameTime.length - 1; i++) {
			if (frameNumberFloat < framePosition[i + 1] && frameNumberFloat >= framePosition[i])
				frameNumber = i + 1;
		}

		//the rest of the method stays the same
		switch (playMode) {
		case NORMAL:
			frameNumber = Math.min(keyFrames.length - 1, frameNumber);
			break;
		case LOOP:
			frameNumber = frameNumber % keyFrames.length;
			break;
		case LOOP_PINGPONG:
			frameNumber = frameNumber % ((keyFrames.length * 2) - 2);
         if (frameNumber >= keyFrames.length)
            frameNumber = keyFrames.length - 2 - (frameNumber - keyFrames.length);
         break;
		case LOOP_RANDOM:
			frameNumber = MathUtils.random(keyFrames.length - 1);
			break;
		case REVERSED:
			frameNumber = Math.max(keyFrames.length - frameNumber - 1, 0);
			break;
		case LOOP_REVERSED:
			frameNumber = frameNumber % keyFrames.length;
			frameNumber = keyFrames.length - frameNumber - 1;
			break;

		default:
			// play normal otherwise
			frameNumber = Math.min(keyFrames.length - 1, frameNumber);
			break;
		}

		return frameNumber;
	}
	/** Returns the animation play mode. Will be one of the following: Animation.NORMAL, Animation.REVERSED, Animation.LOOP,
	 * Animation.LOOP_REVERSED, Animation.LOOP_PINGPONG, Animation.LOOP_RANDOM */
	public int getPlayMode() {
		return playMode;
	}
	/** Sets the animation play mode.
	 *
	 * @param playMode can be one of the following: Animation.NORMAL, Animation.REVERSED, Animation.LOOP, Animation.LOOP_REVERSED,
	 *           Animation.LOOP_PINGPONG, Animation.LOOP_RANDOM */
	public void setPlayMode (int playMode) {
		this.playMode = playMode;
	}

	/** Whether the animation would be finished if played without looping (PlayMode Animation#NORMAL), given the state time.
	 * @param stateTime
	 * @return whether the animation is finished. */
	public boolean isAnimationFinished (float stateTime) {
		int frameNumber = (int)(stateTime / frameDuration);
		return keyFrames.length - 1 < frameNumber;
	}

}