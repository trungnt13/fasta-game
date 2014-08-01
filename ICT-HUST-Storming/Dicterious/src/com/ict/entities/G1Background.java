
package com.ict.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.ict.DicteriousGame;
import com.ict.data.I;

public class G1Background extends Entity {
	// ///////////////////////////////////////////////////////////////
	// static
	// ///////////////////////////////////////////////////////////////
	public static final float CloudSpeed = 30f;

	// ///////////////////////////////////////////////////////////////
	// main
	// ///////////////////////////////////////////////////////////////

	private Texture mLand;
	private Texture mSky;

	private Sprite mCloud1;
	private Sprite mCloud2;

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
	public void show () {
		mLand = DicteriousGame.AssetManager.get(I.G1.LandLight, Texture.class);
		mSky = DicteriousGame.AssetManager.get(I.G1.Sky, Texture.class);

		mCloud1 = new Sprite(DicteriousGame.AssetManager.get(I.G1.Cloud1, Texture.class));
		mCloud1.setPosition(-mCloud1.getWidth(), 1000);

		mCloud2 = new Sprite(DicteriousGame.AssetManager.get(I.G1.Cloud2, Texture.class));
		mCloud2.setPosition(-mCloud2.getWidth() * 3, 800);

		mSkyHeight = mSky.getHeight();
	}

	@Override
	public void render (Batch batch) {
		batch.draw(mSky, 0, yPosition);
		batch.draw(mLand, 0, 0);
		mCloud1.draw(batch);
		mCloud2.draw(batch);
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
		// update cloud
		mCloud1.translateX(CloudSpeed * delta);
		mCloud2.translateX(CloudSpeed * delta);

		if (mCloud1.getX() > DicteriousGame.ScreenWidth) {
			mCloud1.setX(-mCloud1.getWidth());

		}

		if (mCloud2.getX() > DicteriousGame.ScreenWidth) {
			mCloud2.setX(-mCloud2.getWidth());
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
