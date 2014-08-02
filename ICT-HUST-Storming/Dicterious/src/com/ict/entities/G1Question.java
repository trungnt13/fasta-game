
package com.ict.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.ict.DicteriousGame;
import com.ict.data.I;
import com.ict.screen.G1BuildingCastle;

public class G1Question extends Entity {

	// ///////////////////////////////////////////////////////////////
	// main
	// ///////////////////////////////////////////////////////////////

	private Sprite mTrue;
	private Sprite mFalse;
	private boolean isHided = true;
	private final Vector2 tmp = new Vector2();

	// ///////////////////////////////////////////////////////////////
	// override
	// ///////////////////////////////////////////////////////////////

	@Override
	public void show () {
		mTrue = new Sprite(DicteriousGame.AssetManager.get(I.G1.True, Texture.class));
		mTrue.setPosition(G1BuildingCastle.CenterTextPadding, 0);

		mFalse = new Sprite(DicteriousGame.AssetManager.get(I.G1.False, Texture.class));
		mFalse.setPosition(DicteriousGame.ScreenWidth - G1BuildingCastle.CenterTextPadding - mFalse.getWidth(), 0);

	}

	@Override
	public void update (float delta) {

	}

	@Override
	public void render (Batch batch) {
		if (isHided) return;

		mTrue.draw(batch);
		mFalse.draw(batch);
	}

	public Vector2 getPositionOfAnswer (String typeOfAnswer) {
		if (typeOfAnswer.contains("T")) {
			tmp.set(mTrue.getX() + mTrue.getWidth() / 2, mTrue.getY() + mTrue.getHeight() / 2);
			return tmp;
		} else {
			tmp.set(mFalse.getX() + mFalse.getWidth() / 2, mFalse.getY() + mFalse.getHeight() / 2);
			return tmp;
		}
	}

	public String whichOneTouched (float x, float y) {
		if (mTrue.getBoundingRectangle().contains(x, y)) return "T";
		if (mFalse.getBoundingRectangle().contains(x, y)) return "F";
		return "";
	}

	/** @param <br> hide: hide the question <br>
	 *           show: show the question <br>
	 *           fall: falling <br> */
	public void postEvent (Object... params) {
		String eventType = ((String)params[0]).toLowerCase();
		if (eventType.contains("hide")) {
			isHided = true;
		} else if (eventType.contains("show")) {
			isHided = false;
			float y = (Float)params[1];
			mTrue.setPosition(G1BuildingCastle.CenterTextPadding, y - mTrue.getHeight() - 50);
			mFalse.setPosition(DicteriousGame.ScreenWidth - G1BuildingCastle.CenterTextPadding - mFalse.getWidth(),
				y - mFalse.getHeight() - 50);
		} else if (eventType.contains("fall")) {

		}
	}
}
