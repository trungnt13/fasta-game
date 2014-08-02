
package com.ict.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.ict.DicteriousGame;
import com.ict.data.I;
import com.ict.utils.eMath;

public class MapSelectScreen extends ScreenAdapter {

	// ///////////////////////////////////////////////////////////////
	// main part
	// ///////////////////////////////////////////////////////////////

	private Texture mBackground;

	private Sprite mPoint1;
	private Sprite mPoint2;
	private Sprite mPoint3;
	private Sprite mPoint4;

	private Sprite mBackButton;

	private SpriteBatch mBatch;

	@Override
	public void show () {
		mBackground = DicteriousGame.AssetManager.get(I.MapScreen.Map, Texture.class);

		mBackButton = new Sprite(DicteriousGame.AssetManager.get(I.BackButton, Texture.class));
		mBackButton.setPosition(DicteriousGame.ScreenWidth - mBackButton.getWidth() - 13,
			DicteriousGame.ScreenHeight - mBackButton.getHeight() - 13);

		mBatch = DicteriousGame.Batch;
		mBatch.setProjectionMatrix(DicteriousGame.ScreenViewport);

		Gdx.input.setInputProcessor(mInput);

		/*-------- place --------*/
		Texture red = DicteriousGame.AssetManager.get(I.MapScreen.RedPlace, Texture.class);
		Texture brown = DicteriousGame.AssetManager.get(I.MapScreen.BrownPlace, Texture.class);

		mPoint1 = new Sprite(brown);
		mPoint1.setPosition(560 - mPoint1.getWidth() / 2, 480 - mPoint1.getHeight() / 2);

		mPoint2 = new Sprite(red);
		mPoint2.setPosition(180 - mPoint2.getWidth() / 2, 705 - mPoint2.getHeight() / 2);

		mPoint3 = new Sprite(brown);
		mPoint3.setPosition(330 - mPoint1.getWidth() / 2, 965 - mPoint1.getHeight() / 2);

		mPoint4 = new Sprite(brown);
		mPoint4.setPosition(600 - mPoint4.getWidth() / 2, 765 - mPoint4.getHeight() / 2);

	}

	@Override
	public void hide () {
		Gdx.input.setInputProcessor(new InputAdapter());
	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		mBatch.begin();
		mBatch.draw(mBackground, 0, 0, DicteriousGame.ScreenWidth, DicteriousGame.ScreenHeight);
		mBackButton.draw(mBatch);

		mPoint1.draw(mBatch);
		mPoint2.draw(mBatch);
		mPoint3.draw(mBatch);
		mPoint4.draw(mBatch);

		mBatch.end();
	}

	// ///////////////////////////////////////////////////////////////
	// input
	// ///////////////////////////////////////////////////////////////

	private InputAdapter mInput = new InputAdapter() {
		public boolean keyUp (int keycode) {
			if (keycode == Keys.BACK) DicteriousGame.Game.setScreen(DicteriousGame.SStoriesSelect);
			return false;
		};

		public boolean touchUp (int screenX, int screenY, int pointer, int button) {
			Vector2 projected = eMath.convertToScreenCoordiate(screenX, screenY);
			if (mBackButton.getBoundingRectangle().contains(projected))
				DicteriousGame.Game.setScreen(DicteriousGame.SStoriesSelect);
			else if (mPoint1.getBoundingRectangle().contains(projected)) {
				DicteriousGame.Game.setScreen(new G1BuildingCastle());
			} else if (mPoint4.getBoundingRectangle().contains(projected)) {
				DicteriousGame.Game.setScreen(new G4DefendEnemies());
			} else if (mPoint3.getBoundingRectangle().contains(projected)) {
				DicteriousGame.Game.setScreen(new G3CraftCrossbow());
			}
			return false;
		};
	};
}
