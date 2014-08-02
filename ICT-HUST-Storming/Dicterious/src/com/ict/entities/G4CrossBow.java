
package com.ict.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Timer;
import com.ict.DicteriousGame;
import com.ict.data.I;
import com.ict.entities.G4Enemies.Enemy;
import com.ict.utils.eMath;

public class G4CrossBow extends Entity {

	// ///////////////////////////////////////////////////////////////
	// static
	// ///////////////////////////////////////////////////////////////
	public static final float RELOAD_TIME = 0.1f;
	public static final float ARROW_SPEED = 2600;

	public static final float CROSSBOW_X = DicteriousGame.ScreenWidth / 2;
	public static final float CROSSBOW_Y = DicteriousGame.ScreenHeight / 5;

	public static final float SKILL_X = 50;
	public static final float SKILL_Y = 13;

	public static final int MULTI_SHOT = 3;
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

	/*-------- skill --------*/
	private final String[] mSkillName = new String[] {I.G4.SkillFire, I.G4.SkillMultishot, I.G4.SkillHeadshot,
		I.G4.SkillSingleshot};
	private final Sprite[] mSkill = new Sprite[mSkillName.length];
	private String mCurrentSkillName;
	private Sprite mCurrentSkill;
	private boolean isHideSkill = false;

	/*-------- particle effect --------*/
	private ParticleEmitter mEmitter;

	// ///////////////////////////////////////////////////////////////
	// helper methods
	// ///////////////////////////////////////////////////////////////

	public void attack (ArrayList<Enemy> e) {
		isHideSkill = true;
		showAnswerRightEffect(mCurrentSkill.getX() + mCurrentSkill.getWidth() / 2, mCurrentSkill.getY() + mCurrentSkill.getHeight()
			/ 2);
		Timer.schedule(new Timer.Task() {
			@Override
			public void run () {
				refreshSkill();
			}
		}, 1.3f);

		if (mCurrentSkillName.toLowerCase().contains("fire")) {
			for (Enemy enemy : e) {
				if (!enemy.isDead()) shotSingle(enemy.getCenterX(), enemy.getCenterY());
			}
		} else if (mCurrentSkillName.toLowerCase().contains("head")) {
			for (Enemy enemy : e) {
				if (!enemy.isDead()) shotSingle(enemy.getCenterX(), enemy.getCenterY());
			}
		} else if (mCurrentSkillName.toLowerCase().contains("multi")) {
			int count = 0;
			for (Enemy enemy : e) {
				if (!enemy.isDead()) {
					shotSingle(enemy.getCenterX(), enemy.getCenterY());
					count++;
					if (count > MULTI_SHOT) return;
				}
			}
		} else if (mCurrentSkillName.toLowerCase().contains("single")) {
			for (Enemy enemy : e) {
				if (!enemy.isDead()) {
					shotSingle(enemy.getCenterX(), enemy.getCenterY());
					return;
				}
			}
		}
	}

	public void setRotation (float rotation) {
		mCurrentCrossBow.setRotation(rotation);
	}

	public ArrayList<Arrow> safeClone () {
		mTmps.clear();
		mTmps.addAll(mSprites);
		return mTmps;
	}

	private void shotSingle (float x, float y) {
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

	private void setSkill (int index) {
		mCurrentSkillName = mSkillName[index];
		mCurrentSkill = mSkill[index];
	}

	public void refreshSkill () {
		isHideSkill = false;
		int index = eMath.Rand.nextInt(13);

		if (index == 0 || index == 1) {
			setSkill(0);
		} else if (index == 2 || index == 3 || index == 4) {
			setSkill(1);
		} else if (index == 5 || index == 6 || index == 7) {
			setSkill(2);
		} else {
			setSkill(3);
		}
	}

	private void showAnswerRightEffect (float x, float y) {
		mEmitter.setPosition(x, y);
		mEmitter.start();
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

		/** init skill */
		for (int j = 0; j < mSkillName.length; j++) {
			mSkill[j] = new Sprite(DicteriousGame.AssetManager.get(mSkillName[j], Texture.class));
			mSkill[j].setPosition(SKILL_X, SKILL_Y);
		}

		refreshSkill();

		/** prepare effect */
		mEmitter = DicteriousGame.AssetManager.get(I.G1.ParticleWin, ParticleEffect.class).findEmitter("star");
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

		/** update emitter */
		mEmitter.update(delta);
	}

	@Override
	public void render (Batch batch) {
		mCurrentCrossBow.draw(batch);

		safeClone();
		for (Arrow a : mTmps) {
			a.draw(batch);
		}

		if (!isHideSkill) mCurrentSkill.draw(batch);

		mEmitter.draw(batch);
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
