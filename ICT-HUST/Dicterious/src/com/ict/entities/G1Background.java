
package com.ict.entities;

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
		if (yPosition + mSkyHeight < DicteriousGame.ScreenHeight)
			yPosition = -(mSkyHeight - DicteriousGame.ScreenHeight);
		else
			yPosition -= ySpeed * delta;

		if (yPosition + mSkyHeight < DicteriousGame.ScreenHeight * 2.5f && !isSwitchLand) {
			isSwitchLand = true;
			mLand = DicteriousGame.AssetManager.get("game1/land_dark.png", Texture.class);
		}
	}

	/** @param event <br>
	 *           maxtime_89 : set max time translating the sky in seconds. <br>
	 *           reset : reset all information bring light sky */
	public void postEvent (String event) {
		event = event.toLowerCase();
		if (event.contains("maxtime") && mSky != null) {
			float maxtime = Float.parseFloat(event.split("_")[1]);
			float distance = mSky.getHeight() - DicteriousGame.ScreenHeight;
			ySpeed = distance / maxtime;
		} else if (event.contains("reset")) {
			ySpeed = 0;
			yPosition = 0;
			isSwitchLand = false;
			mLand = DicteriousGame.AssetManager.get("game1/land_light.png", Texture.class);
			mSky = DicteriousGame.AssetManager.get("game1/sky.png", Texture.class);
		}
	}
}
