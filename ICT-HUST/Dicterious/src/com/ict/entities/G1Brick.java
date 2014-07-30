
package com.ict.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.ict.DicteriousGame;

public class G1Brick extends Entity {

	private Sprite mBrickLight;
	private Sprite mBrickDark;
	private Sprite mCurrentSprite;

	private String mText;

	private boolean isShowText = true;

	private static float DeltaPosition = 20;

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
		mCurrentSprite.translateY(-100*delta);
	}

	@Override
	/** @param event <br>
	 *           text_trungloveanh : set text for brick. <br>
	 *           dark : change brick to dark mode, hide text <br>
	 *           light: change brick to light mode, show text <br>
	 *           postion_x_y: set start position for brick */
	public void postEvent (String event) {
		String eventLower = event.toLowerCase();
		if (eventLower.contains("text")) {
			mText = event.split("_")[1];
			TextBounds bound = DicteriousGame.FontNormal.getBounds(mText);
			mCurrentSprite.setSize(bound.width + DeltaPosition * 2, bound.height + DeltaPosition * 2);

			mCurrentSprite = mBrickLight;
			isShowText = true;
		} else if (eventLower.contains("dark")) {
			mBrickDark
				.setBounds(mCurrentSprite.getX(), mCurrentSprite.getY(), mCurrentSprite.getWidth(), mCurrentSprite.getHeight());
			mCurrentSprite = mBrickDark;
			isShowText = false;
		} else if (eventLower.contains("light")) {
			mBrickLight.setBounds(mCurrentSprite.getX(), mCurrentSprite.getY(), mCurrentSprite.getWidth(),
				mCurrentSprite.getHeight());
			mCurrentSprite = mBrickLight;
			isShowText = true;
			if (mText == null) mText = "";
		} else if (eventLower.contains("position")) {
			String[] param = event.split("_");
			mCurrentSprite.setPosition(Float.parseFloat(param[1]), Float.parseFloat(param[2]));
		}
	}
}
