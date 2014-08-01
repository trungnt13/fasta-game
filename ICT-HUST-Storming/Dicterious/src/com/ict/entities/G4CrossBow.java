
package com.ict.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.ict.DicteriousGame;
import com.ict.data.I;
import com.ict.entities.G4Enemies.Enemy;
import com.ict.utils.eMath;

public class G4CrossBow extends Entity {

	// ///////////////////////////////////////////////////////////////
	// static
	// ///////////////////////////////////////////////////////////////
	private static final float RELOAD_TIME = 0.1f;
	private static final float ARROW_SPEED = 2600;

	private static final float CROSSBOW_X = DicteriousGame.ScreenWidth / 2;
	private static final float CROSSBOW_Y = DicteriousGame.ScreenHeight / 5;

	// ///////////////////////////////////////////////////////////////
	// main
	// ///////////////////////////////////////////////////////////////

	/*-------- data --------*/
	private Texture[] mCrossBow;
	private Sprite mCurrentCrossBow;

	private Texture mArrow;

	/*-------- manage arrows --------*/
	private final ArrayList<Arrow> mSprites = new ArrayList<G4CrossBow.Arrow>();
	private final ArrayList<Arrow> mTmps = new ArrayList<G4CrossBow.Arrow>();

	/*-------- reload params --------*/
	private float mReloadTimeCounter = 0;

	// ///////////////////////////////////////////////////////////////
	// helper methods
	// ///////////////////////////////////////////////////////////////

	public void setRotation (float rotation) {
		mCurrentCrossBow.setRotation(rotation);
	}

	public ArrayList<Arrow> safeClone () {
		mTmps.clear();
		mTmps.addAll(mSprites);
		return mTmps;
	}

	public void shotSingle (float x, float y) {
		mCurrentCrossBow.setTexture(mCrossBow[1]);
		mReloadTimeCounter = RELOAD_TIME;

		/** create arrow */
		Arrow a = new Arrow();

		float angle = eMath.calVectorAngle(getCenterX(), getCenterY(), x, y);
		a.setAngle(angle);
		mCurrentCrossBow.setRotation(angle);
	}

	public float getCenterX () {
		return mCurrentCrossBow.getX() + mCurrentCrossBow.getWidth() / 2;
	}

	public float getCenterY () {
		return mCurrentCrossBow.getY() + mCurrentCrossBow.getHeight() / 2;
	}

	// ///////////////////////////////////////////////////////////////
	// override methods
	// ///////////////////////////////////////////////////////////////

	@Override
	public void show () {
		/** init crossbow */
		mCrossBow = new Texture[I.G4.Crossbow.length];
		int i = 0;
		for (String s : I.G4.Crossbow) {
			mCrossBow[i++] = DicteriousGame.AssetManager.get(s, Texture.class);
		}
		mCurrentCrossBow = new Sprite(mCrossBow[0]);
		mCurrentCrossBow.setOrigin(mCurrentCrossBow.getWidth() / 2, mCurrentCrossBow.getHeight() / 2);
		mCurrentCrossBow.setRotation(90);
		mCurrentCrossBow.setPosition(CROSSBOW_X - mCurrentCrossBow.getWidth() / 2, CROSSBOW_Y - mCurrentCrossBow.getHeight() / 2);

		/** init arrow */
		mArrow = DicteriousGame.AssetManager.get(I.G4.Arrow, Texture.class);
	}

	@Override
	public void update (float delta) {
		/** reload */
		if (mReloadTimeCounter > 0) {
			mReloadTimeCounter -= delta;
		} else if (mReloadTimeCounter < 0) {
			mReloadTimeCounter = 0;
			mCurrentCrossBow.setTexture(mCrossBow[0]);
		}

		/** update all arrows */
		safeClone();
		for (Arrow a : mTmps) {
			a.update(delta);
		}
	}

	@Override
	public void render (Batch batch) {
		mCurrentCrossBow.draw(batch);

		safeClone();
		for (Arrow a : mTmps) {
			a.draw(batch);
		}
	}

	@Override
	public void postEvent (Object... params) {
		// TODO Auto-generated method stub

	}

	// ///////////////////////////////////////////////////////////////
	// helper data type
	// ///////////////////////////////////////////////////////////////

	public class Arrow extends Sprite {
		private float mMovingAngle;

		public Arrow () {
			super(mArrow);
			setOrigin(getWidth() / 2, getHeight() / 2);
			setPosition(getCenterX() - getWidth() / 2, getCenterY() - getHeight() / 2);
			setRotation(mCurrentCrossBow.getRotation());
			mSprites.add(this);
		}

		public void setAngle (float angle) {
			mMovingAngle = angle;
		}

		public void update (float delta) {
			/** check outside */
			if (getX() + getWidth() < 0 || getX() > DicteriousGame.ScreenWidth || getY() + getHeight() < 0
				|| getY() > DicteriousGame.ScreenHeight) {
				mSprites.remove(this);
			}

			/** update position */
			float speed = ARROW_SPEED * delta;
			translate(MathUtils.cosDeg(mMovingAngle) * speed, MathUtils.sinDeg(mMovingAngle) * speed);
			setRotation(mMovingAngle);
		}

		public void hit (Enemy e) {
			mSprites.remove(this);
		}
	}
}
