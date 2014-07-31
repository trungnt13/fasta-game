
package com.ict.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.ict.DicteriousGame;

public class G1Brick extends Entity {
	// ///////////////////////////////////////////////////////////////
	// static
	// ///////////////////////////////////////////////////////////////
	private static final float DeltaPosition = 10;
	/** Time text stay remain uncovered */
	private static final float ShowTextTime = 0.5f;
	/** how the brick move in */
	private static final Interpolation InterpolationType = Interpolation.linear;
	/** falling done after 0.5s */
	private static final float FallingSpeed = 0.5f;

	private static final Vector2 Bounds = new Vector2();

	public static final Vector2 getBrickSize (String text) {
		TextBounds bound = DicteriousGame.FontNormal.getBounds(text);
		Bounds.set(bound.width + DeltaPosition * 2, bound.height + DeltaPosition * 2);
		return Bounds;
	}

	// ///////////////////////////////////////////////////////////////
	// main stuff
	// ///////////////////////////////////////////////////////////////

	private Sprite mBrickLight;
	private Sprite mBrickDark;
	Sprite mCurrentSprite;

	private String mText;

	private boolean isShowText = true;
	private BrickStatus mBrickStatus = BrickStatus.None;

	/** Param for dropping */
	private float mTimeWillDrop;
	private float mStopY;
	private float mTimeCounter = 0;
	private BrickDropListener mDropListener = null;

	/** Param for falling */
	private BrickFallListener mFallListener = null;
	private float mFallRotation = 0;
	private float mStartFallingY = 0;

	@Override
	public void show (float viewWidth, float viewHeight) {
		mBrickLight = new Sprite(DicteriousGame.AssetManager.get("game1/brick_light.png", Texture.class));
		mBrickDark = new Sprite(DicteriousGame.AssetManager.get("game1/brick_dark.png", Texture.class));
		mCurrentSprite = mBrickLight;
	}

	@Override
	public void render (Batch batch) {
		mCurrentSprite.draw(batch);
		if (isShowText)
			DicteriousGame.FontNormal.draw(batch, mText, mCurrentSprite.getX() + DeltaPosition, mCurrentSprite.getY()
				+ mCurrentSprite.getHeight() / 2 + DeltaPosition * 2);
	}

	@Override
	public void update (float delta) {
		if (mBrickStatus == BrickStatus.Drop) {
			mTimeCounter += delta;
			mCurrentSprite.setY(InterpolationType.apply(DicteriousGame.ScreenHeight, mStopY,
				Math.min(1, mTimeCounter / mTimeWillDrop)));
			// drop done
			if (mTimeCounter / mTimeWillDrop > 1) {
				mBrickStatus = BrickStatus.None;
				mCurrentSprite.setY(mStopY);
				if (mDropListener != null) mDropListener.dropDone(this);
				Timer.schedule(new Timer.Task() {
					@Override
					public void run () {
						postEvent("dark");
					}
				}, ShowTextTime);
			}
		} else if (mBrickStatus == BrickStatus.Fall) {
			mTimeCounter += delta;
			mCurrentSprite.setY(Interpolation.circleIn.apply(mStartFallingY, -100, Math.min(1, mTimeCounter / FallingSpeed)));
			mCurrentSprite.setRotation(Interpolation.circleIn.apply(0, mFallRotation, Math.min(1, mTimeCounter / FallingSpeed * 2)));
			if (mCurrentSprite.getY() + mCurrentSprite.getHeight() < 0) {
				mBrickStatus = BrickStatus.None;
				if (mDropListener != null) mFallListener.fallDone(this);
			}
		}
	}

	@Override
	/** @param params <br>
	 *           dark : change brick to dark mode, hide text <br>
	 *           light: change brick to light mode, show text <br>
	 *           drop_text_startX_stopY_speed: set start position for brick */
	public void postEvent (Object... params) {
		String eventType = ((String)params[0]).toLowerCase();
		/** Dark light */
		if (eventType.contains("dark")) {
			mBrickDark
				.setBounds(mCurrentSprite.getX(), mCurrentSprite.getY(), mCurrentSprite.getWidth(), mCurrentSprite.getHeight());
			mCurrentSprite = mBrickDark;
			isShowText = false;
		} else if (eventType.contains("light")) {
			mBrickLight.setBounds(mCurrentSprite.getX(), mCurrentSprite.getY(), mCurrentSprite.getWidth(),
				mCurrentSprite.getHeight());
			mCurrentSprite = mBrickLight;
			isShowText = true;
			if (mText == null) mText = "";
		}

		/** drop from top screen */
		else if (eventType.contains("drop")) {
			// brick info
			mText = (String)params[1];
			Vector2 brickSize = getBrickSize(mText);
			float startX = (Float)params[2];
			mCurrentSprite.setBounds(startX, DicteriousGame.ScreenHeight, brickSize.x, brickSize.y);
			mCurrentSprite = mBrickLight;
			isShowText = true;

			// moving info
			mStopY = (Float)params[3];
			mTimeWillDrop = (DicteriousGame.ScreenHeight - mStopY) / (Float)params[4];
			mTimeCounter = 0;

			mBrickStatus = BrickStatus.Drop;

			// set drop listener
			if (params.length > 5) mDropListener = (BrickDropListener)params[5];
		}
		/** fall from current position */
		else if (eventType.contains("fall")) {
			mBrickStatus = BrickStatus.Fall;
			mTimeCounter = 0;
			if (mCurrentSprite.getX() + mCurrentSprite.getWidth() / 2 < DicteriousGame.ScreenWidth / 2)
				mFallRotation = -60;
			else
				mFallRotation = 60;

			mStartFallingY = mCurrentSprite.getY();

			// set drop listener
			if (params.length > 1) mFallListener = (BrickFallListener)params[1];
		}
	}

	// ///////////////////////////////////////////////////////////////
	// helper data type
	// ///////////////////////////////////////////////////////////////

	public static interface BrickDropListener {
		public void dropDone (G1Brick brick);
	}

	public static interface BrickFallListener {
		public void fallDone (G1Brick brick);
	}

	private enum BrickStatus {
		Fall, Drop, None
	}
}
