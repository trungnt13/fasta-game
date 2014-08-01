
package com.ict.entities;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.ict.DicteriousGame;
import com.ict.data.I;
import com.ict.utils.eMath;

public class MysteriousBook extends Entity {
	// ///////////////////////////////////////////////////////////////
	// static param
	// ///////////////////////////////////////////////////////////////
	private static final int MAX_SIMULTANOUS_ICONS = 3;
	private static final int MIN_SIMULTANOUS_ICONS = 1;

	private static final float MAX_SPEED = 130;
	private static final float MIN_SPEED = 100;

	private static final float MIN_DECELERATE_Y = 40;
	private static final float MAX_DECELERATE_Y = 60;

	private static final float ADD_NEW_ICONS_INTERVAL = 1f;

	/*-------- for better random algorithm --------*/
	private static final ArrayList<Float> POSITION = new ArrayList<Float>();
	private static final ArrayList<Float> CURRENT_POSITION = new ArrayList<Float>();

	// ///////////////////////////////////////////////////////////////
	// main
	// ///////////////////////////////////////////////////////////////

	/*-------- pool --------*/
	private static int IconCount = 0;
	private final Pool<LoadingIcon> mPool = new Pool<LoadingIcon>() {
		@Override
		protected LoadingIcon newObject () {
			if (mLoadingIcons == null) throw new RuntimeException("Loading Icons is not inited yet");
			LoadingIcon icon = new LoadingIcon(mLoadingIcons[IconCount++]);
			if (IconCount >= mLoadingIcons.length) IconCount = 0;
			return icon;
		}
	};

	/*-------- params --------*/
	private Sprite mBook;
	private ParticleEmitter mOriginalEmitter;
	private static LoadingIcon[] mLoadingIcons;

	private final ArrayList<LoadingIcon> mSprites = new ArrayList<LoadingIcon>();
	private final ArrayList<LoadingIcon> mTmps = new ArrayList<LoadingIcon>();

	private boolean isShowMysteriousStuffs = false;

	private float mTimerForAddIcons = 88;

	public MysteriousBook () {
		/** generate position */
		float start = 50;
		float end = 620;
		float delta = (end - start) / 10;
		for (int j = 0; j <= 10; j++) {
			POSITION.add(start + delta * j);
		}
		CURRENT_POSITION.addAll(POSITION);

		/** create book */
		mBook = new Sprite(new Texture(Gdx.files.internal(I.LoadingBook)));
		float hwratio = mBook.getHeight() / mBook.getWidth();
		mBook.setSize(DicteriousGame.ScreenWidth, hwratio * DicteriousGame.ScreenWidth);

		/** create emitter */
		ParticleEffect effect = new ParticleEffect();
		effect.load(Gdx.files.internal("data/loadingicons/star.p"), Gdx.files.internal("data/loadingicons/"));
		mOriginalEmitter = effect.findEmitter("star");

		/** create icons */
		mLoadingIcons = new LoadingIcon[I.LoadingIcons.length];
		int i = 0;
		for (String s : I.LoadingIcons) {
			mLoadingIcons[i++] = new LoadingIcon(new Texture(Gdx.files.internal(s)));
		}
	}

	// ///////////////////////////////////////////////////////////////
	// plug in control methods
	// ///////////////////////////////////////////////////////////////
	public void setPosition (float x, float y) {
		mBook.setPosition(x, y);
	}

	public float getWidth () {
		return mBook.getWidth();
	}

	public float getHeight () {
		return mBook.getHeight();
	}

	private ArrayList<LoadingIcon> safeClone () {
		mTmps.clear();
		mTmps.addAll(mSprites);
		return mTmps;
	}

	// ///////////////////////////////////////////////////////////////
	// override
	// ///////////////////////////////////////////////////////////////

	@Override
	public void show () {

	}

	@Override
	public void update (float delta) {
		// check add icon interval
		mTimerForAddIcons += delta;
		if (mTimerForAddIcons > ADD_NEW_ICONS_INTERVAL) {
			mTimerForAddIcons = 0;
			int numberOfNewIcons = eMath.randInt(MIN_SIMULTANOUS_ICONS, MAX_SIMULTANOUS_ICONS);
			for (int i = 0; i < numberOfNewIcons; i++) {
				mSprites.add(mPool.obtain());
			}
		}

		// update all icon
		safeClone();
		for (LoadingIcon sprite : mTmps)
			sprite.update(delta);
	}

	@Override
	public void render (Batch batch) {
		if (!isShowMysteriousStuffs) return;
		// render all current icons
		safeClone();
		for (LoadingIcon sprite : mTmps)
			sprite.draw(batch);

		// render book
		mBook.draw(batch);
	}

	@Override
	public void postEvent (Object... params) {
		String eventType = ((String)params[0]).toLowerCase();
		if (eventType.contains("show")) {
			isShowMysteriousStuffs = true;
		} else if (eventType.contains("hide")) {
			isShowMysteriousStuffs = false;
			// pool all sprite
			for (LoadingIcon i : mSprites)
				mPool.free(i);
			mSprites.clear();
		}
	}

	// ///////////////////////////////////////////////////////////////
	// helper data type
	// ///////////////////////////////////////////////////////////////
	private class LoadingIcon extends Sprite implements Poolable {
		private float mSpeedX;
		private float mSpeedY;

		private float mDecelerateX;
		private float mDecelerateY;

		private ParticleEmitter mEmitter;

		public LoadingIcon (Texture tex) {
			super(tex);
			mEmitter = new ParticleEmitter(mOriginalEmitter);
			reset();
		}

		public LoadingIcon (Sprite sprite) {
			super(sprite);
			mEmitter = new ParticleEmitter(mOriginalEmitter);
			reset();
		}

		@Override
		public void draw (Batch batch) {
			mEmitter.draw(batch);
			super.draw(batch);
		}

		public void update (float delta) {
			// outside screen
			if (getX() + getWidth() < 0 || getX() > DicteriousGame.ScreenWidth || getY() + getHeight() < 0
				|| getY() > DicteriousGame.ScreenHeight) {
				mSprites.remove(this);
				mPool.free(this);
			}

			// update
			translate(mSpeedX * delta, mSpeedY * delta);
			mSpeedY -= mDecelerateY * delta;
			if (mSpeedX < 0)
				mSpeedX += mDecelerateX * delta;
			else
				mSpeedX -= mDecelerateX * delta;

			// update emitter
			mEmitter.update(delta);
			mEmitter.setPosition(getX() + getWidth() / 2, getY() + getHeight() / 2);
		}

		@Override
		public void reset () {
			if (CURRENT_POSITION.size() == 0) CURRENT_POSITION.addAll(POSITION);

			float x = CURRENT_POSITION.get(eMath.Rand.nextInt(CURRENT_POSITION.size()));
			CURRENT_POSITION.remove(x);
			setPosition(x, mBook.getY() + mBook.getHeight() / 3);

			mSpeedY = (float)(MIN_SPEED + Math.random() * (MAX_SPEED - MIN_SPEED));

			mDecelerateY = (float)(MIN_DECELERATE_Y + Math.random() * (MAX_DECELERATE_Y - MIN_DECELERATE_Y));

		}
	}
}
