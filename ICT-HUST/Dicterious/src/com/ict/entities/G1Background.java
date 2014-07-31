
package com.ict.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.ict.DicteriousGame;

public class G1Background extends Entity {
	private Texture mLand;
	private Texture mSky;

	/** For translating the sky */
	private float yPosition = 0;
	private float ySpeed = 0;
	private float mSkyHeight = 0;

	private boolean isSwitchLand = false;
	private boolean isTimeUp = false;

	public boolean isTimeUp () {
		return isTimeUp;
	}

	@Override
	public void show (float viewWidth, float viewHeight) {
		mLand = DicteriousGame.AssetManager.get("game1/land_light.png", Texture.class);
		mSky = DicteriousGame.AssetManager.get("game1/sky.png", Texture.class);
		mSkyHeight = mSky.getHeight();
	}

	@Override
	public void render (Batch batch) {
		batch.draw(mSky, 0, yPosition);
		batch.draw(mLand, 0, 0);
	}

	@Override
	public void update (float delta) {
		// time up, sky go dark
		if (yPosition + mSkyHeight < DicteriousGame.ScreenHeight) {
			yPosition = -(mSkyHeight - DicteriousGame.ScreenHeight);
			isTimeUp = true;
		}
		// COntinue translate sky
		else
			yPosition -= ySpeed * delta;

		if (yPosition + mSkyHeight < DicteriousGame.ScreenHeight * 2.5f && !isSwitchLand) {
			isSwitchLand = true;
			mLand = DicteriousGame.AssetManager.get("game1/land_dark.png", Texture.class);
		}
	}

	/** @param params <br>
	 *           maxtime_89 : set max time translating the sky in seconds. <br> */
	public void postEvent (Object... params) {
		String eventType = ((String)params[0]).toLowerCase();
		if (eventType.contains("maxtime") && mSky != null) {
			// reset
			ySpeed = 0;
			yPosition = 0;
			isSwitchLand = false;
			mLand = DicteriousGame.AssetManager.get("game1/land_light.png", Texture.class);
			mSky = DicteriousGame.AssetManager.get("game1/sky.png", Texture.class);

			// set
			float maxtime = (Float)params[1];
			float distance = mSky.getHeight() - DicteriousGame.ScreenHeight;
			ySpeed = distance / maxtime;
		}
	}
}
