
package com.ict.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.ict.DicteriousGame;
import com.ict.data.I;
import com.ict.entities.G4CrossBow.Arrow;
import com.ict.utils.eMath;

public class G4Enemies extends Entity {
	// ///////////////////////////////////////////////////////////////
	// static
	// ///////////////////////////////////////////////////////////////
	public static final float ENEMY_WIDTH = 150;

	public static final float ENEMY_ATTACK_DURATION = 0.2f;

	public static final float ENEMY_MOVING_SPEED = 50;
	public static final float ENEMY_MOVING_DURATION = 0.2f;

	public static final int MAX_SPAWN = 5;
	public static final int MIN_SPAWN = 3;
	public static final float SPAWN_TIME_INTERVAL = 5f;

	public static final float CASTLE_Y = DicteriousGame.ScreenHeight / 5;
	// ///////////////////////////////////////////////////////////////
	// main
	// ///////////////////////////////////////////////////////////////
	/*-------- statistic --------*/
	private int mNumberOfDead;
	private int mNumberOfAttack;

	/*-------- manage enemy --------*/
	private final ArrayList<Enemy> mSprites = new ArrayList<G4Enemies.Enemy>();
	private final ArrayList<Enemy> mTmps = new ArrayList<G4Enemies.Enemy>();

	/*-------- texture --------*/
	private final ArrayList<TextureRegion> mRunAnimation = new ArrayList<TextureRegion>();
	private final ArrayList<TextureRegion> mAttackAnimation = new ArrayList<TextureRegion>();
	private Texture mDeadArrowTexture;

	/*-------- for random enemy position --------*/
	private final ArrayList<Float> mOriginalX = new ArrayList<Float>();
	private final ArrayList<Float> mCurrentX = new ArrayList<Float>();

	/*-------- spawn info --------*/
	private float mSpawnTimeCounter = 0;

	// ///////////////////////////////////////////////////////////////
	// helper control
	// ///////////////////////////////////////////////////////////////
	public ArrayList<Enemy> safeClone () {
		mTmps.clear();
		mTmps.addAll(mSprites);
		return mTmps;
	}

	private float getRandomXPosition () {
		if (mCurrentX.size() == 0) mCurrentX.addAll(mOriginalX);
		float x = mCurrentX.get(eMath.Rand.nextInt(mCurrentX.size()));
		mCurrentX.remove(x);
		return x;
	}

	public int getNumberOfDead () {
		return mNumberOfDead;
	}

	public int getNumberOfAttack () {
		return mNumberOfAttack;
	}

	// ///////////////////////////////////////////////////////////////
	// override
	// ///////////////////////////////////////////////////////////////

	@Override
	public void show () {
		/** get resources */
		for (String s : I.G4.EnemyRun) {
			mRunAnimation.add(new TextureRegion(DicteriousGame.AssetManager.get(s, Texture.class)));
		}

		for (String s : I.G4.EnemyAttack) {
			mAttackAnimation.add(new TextureRegion(DicteriousGame.AssetManager.get(s, Texture.class)));
		}

		mDeadArrowTexture = DicteriousGame.AssetManager.get(I.G4.DeadArrow, Texture.class);

		/** prepare postion */
		Enemy e = new Enemy();
		e.setPosition(0, DicteriousGame.ScreenHeight);
		int size = (int)(DicteriousGame.ScreenWidth / e.getWidth()) + 1;
		for (int i = 0; i < size; i++) {
			mOriginalX.add(i * e.getWidth());
		}
		mCurrentX.clear();
		mCurrentX.addAll(mOriginalX);

	}

	@Override
	public void update (float delta) {
		/** check spawn */
		mSpawnTimeCounter += delta;
		if (mSpawnTimeCounter >= SPAWN_TIME_INTERVAL) {
			mSpawnTimeCounter = 0;
			int numberOfNewEnemy = eMath.randInt(MIN_SPAWN, MAX_SPAWN);
			for (int i = 0; i < numberOfNewEnemy; i++) {
				Enemy e = new Enemy();
				e.setX(getRandomXPosition());
			}
		}

		/** update all */
		safeClone();
		for (Enemy e : mTmps) {
			e.update(delta);
		}
	}

	@Override
	public void render (Batch batch) {
		safeClone();
		for (Enemy e : mTmps) {
			e.draw(batch, e.mAlpha);
		}
	}

	@Override
	public void postEvent (Object... params) {
		// TODO Auto-generated method stub

	}

	// ///////////////////////////////////////////////////////////////
	// helper data type
	// ///////////////////////////////////////////////////////////////

	public class Enemy extends SpriteA {
		private Sprite mDeadArrow;
		private float mDeadTimeCounter = 0;

		private EnemySate mState = EnemySate.Run;

		private float mAlpha = 1;

		private boolean isInitAttack = false;

		public Enemy () {
			super(mRunAnimation);
			float hwratio = getHeight() / getWidth();
			setSize(ENEMY_WIDTH, ENEMY_WIDTH * hwratio);
			setOrigin(getWidth() / 2, 0);
			setY(DicteriousGame.ScreenHeight);

			/** animation */
			setFrameDuration(ENEMY_MOVING_DURATION);
			setPlayMode(PlayMode.LOOP);
			start();

			/** add to managed list */
			mSprites.add(this);

			/** create dead arrow */
			mDeadArrow = new Sprite(mDeadArrowTexture);
			mDeadArrow.setSize(ENEMY_WIDTH, ENEMY_WIDTH * hwratio);
			mDeadArrow.setOrigin(getWidth() / 2, 0);
		}

		public void setState (EnemySate state) {
			mState = state;
			mDeadTimeCounter = 0;
		}

		public boolean isDead () {
			return mState == EnemySate.Dead || mState == EnemySate.Disappear;
		}

		@Override
		public void update (float delta) {
			super.update(delta);

			/** play move effect */
			if (mState == EnemySate.Run) {
				translateY(-ENEMY_MOVING_SPEED * delta);
				if (getY() < CASTLE_Y - Math.abs(DicteriousGame.ScreenWidth / 2 - getX()) / 10) mState = EnemySate.Attack;
			}
			/** play attack effect */
			else if (mState == EnemySate.Attack) {
				if (!isInitAttack) {
					setKeyFrames(mAttackAnimation);
					start(ENEMY_ATTACK_DURATION, PlayMode.LOOP);
					isInitAttack = true;
					mNumberOfAttack++;
				}
			}
			/** play dead effect */
			else if (mState == EnemySate.Dead) {
				stop();
				mDeadArrow.setPosition(getX(), getY());
				mDeadArrow.setRotation(getRotation());
				mDeadTimeCounter += 2 * delta;
				setRotation(Interpolation.circleIn.apply(0, 90, Math.min(1, mDeadTimeCounter)));
				// done dead
				if (mDeadTimeCounter > 1.3f) {
					mState = EnemySate.Disappear;
					mDeadTimeCounter = 0;
				}
			}
			/** play disappear effect */
			else if (mState == EnemySate.Disappear) {
				mDeadTimeCounter += 2 * delta;
				mAlpha = Interpolation.linear.apply(1, 0, Math.min(1, mDeadTimeCounter));
				// done disappear
				if (mDeadTimeCounter > 1) {
					mState = EnemySate.None;
					mNumberOfDead++;
					mSprites.remove(this);
				}
			}
		}

		@Override
		public void draw (Batch spriteBatch, float alpha) {
			super.draw(spriteBatch, alpha);
			if (mState == EnemySate.Dead || mState == EnemySate.Disappear) {
				mDeadArrow.draw(spriteBatch, alpha);
			}
		}

		public void hit (Arrow a) {
			if (mState == EnemySate.Run || mState == EnemySate.Attack) setState(EnemySate.Dead);
		}
	}

	private enum EnemySate {
		Run, Attack, Dead, Disappear, None
	}
}
