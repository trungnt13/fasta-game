
package com.ict.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
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
	private static final float DeltaPosition = 20;
	/** Time text stay remain uncovered */
	private static final float ShowTextTime = 0.5f;
	/** how the brick move in */
	private static final Interpolation InterpolationType = Interpolation.bounceOut;

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
	private Sprite mCurrentSprite;

	private String mText;

	private boolean isShowText = true;
	private boolean isInDropMode = false;

	private float mTimeWillDrop;
	private float mStopY;
	private float mTimeCounter = 0;

	private BrickDropListener mDropListener = null;

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
				+ mCurrentSprite.getHeight() / 2 + DeltaPosition);
	}

	@Override
	public void update (float delta) {
		if (isInDropMode) {
			mTimeCounter += delta;
			mCurrentSprite.setY(InterpolationType.apply(DicteriousGame.ScreenHeight, mStopY,
				Math.min(1, mTimeCounter / mTimeWillDrop)));
			// drop done
			if (mTimeCounter / mTimeWillDrop > 1) {
				isInDropMode = false;
				mCurrentSprite.setY(mStopY);
				if (mDropListener != null) mDropListener.dropDone(this);
				Timer.schedule(new Timer.Task() {
					@Override
					public void run () {
						postEvent("dark");
					}
				}, ShowTextTime);
			}
		}
	}

	@Override
	/** @param params <br>
	 *           text_trungloveanh : set text for brick. <br>
	 *           dark : change brick to dark mode, hide text <br>
	 *           light: change brick to light mode, show text <br>
	 *           drop_text_startX_stopY_speed: set start position for brick */
	public void postEvent (Object... params) {
		String eventType = ((String)params[0]).toLowerCase();
		if (eventType.contains("text")) {
			mText = (String)params[1];
			Vector2 brickSize = getBrickSize(mText);
			mCurrentSprite.setSize(brickSize.x, brickSize.y);

			mCurrentSprite = mBrickLight;
			isShowText = true;
		} else if (eventType.contains("dark")) {
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
		} else if (eventType.contains("drop")) {
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
			isInDropMode = true;

			// set drop listener
			if (params.length > 5) mDropListener = (BrickDropListener)params[5];
		}
	}

	public static interface BrickDropListener {
		public void dropDone (G1Brick brick);
	}
}
