
package com.ict.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.ict.DicteriousGame;
import com.ict.data.I;
import com.ict.utils.eMath;

public class MainMenuScreen extends ScreenAdapter {
	// ///////////////////////////////////////////////////////////////
	// override
	// ///////////////////////////////////////////////////////////////

	private Texture mBackground;
	private SpriteBatch mBatch;

	private Sprite mPlay;
	private Sprite mSetting;
	private Sprite mUserProfile;

	@Override
	public void show () {
		mBackground = DicteriousGame.AssetManager.get(I.MainMenu.Background, Texture.class);
		mPlay = new Sprite(DicteriousGame.AssetManager.get(I.MainMenu.PlayButton, Texture.class));
		mPlay.setPosition(DicteriousGame.ScreenWidth / 2 - mPlay.getWidth() / 2, 750);

		mSetting = new Sprite(DicteriousGame.AssetManager.get(I.MainMenu.SettingButton, Texture.class));
		mSetting.setPosition(DicteriousGame.ScreenWidth / 2 - mSetting.getWidth() / 2, 620);

		mUserProfile = new Sprite(DicteriousGame.AssetManager.get(I.MainMenu.UserProfileButton, Texture.class));
		mUserProfile.setPosition(DicteriousGame.ScreenWidth / 2 - mUserProfile.getWidth() / 2, 490);

		mBatch = DicteriousGame.Batch;
		mBatch.setProjectionMatrix(DicteriousGame.ScreenViewport);

		Gdx.input.setInputProcessor(mInput);
	}

	@Override
	public void hide () {
		Gdx.input.setInputProcessor(new InputAdapter());
	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		DicteriousGame.MyteriousBook.update(delta);

		mBatch.begin();
		mBatch.draw(mBackground, 0, 0, DicteriousGame.ScreenWidth, DicteriousGame.ScreenHeight);
		mPlay.draw(mBatch);
		mSetting.draw(mBatch);
		mUserProfile.draw(mBatch);
		DicteriousGame.MyteriousBook.render(mBatch);
		mBatch.end();
	}

	// ///////////////////////////////////////////////////////////////
	// input adapter
	// ///////////////////////////////////////////////////////////////

	private final InputAdapter mInput = new InputAdapter() {
		public boolean keyUp (int keycode) {
			if (keycode == Input.Keys.BACK) Gdx.app.exit();
			return false;
		};

		@Override
		public boolean touchUp (int screenX, int screenY, int pointer, int button) {
			Vector2 projected = eMath.convertToScreenCoordiate(screenX, screenY);
			System.out.println("TOuched .....");
			if (mPlay.getBoundingRectangle().contains(projected)) {
				DicteriousGame.Game.setScreen(DicteriousGame.SStoriesSelect);
			} else if (mUserProfile.getBoundingRectangle().contains(projected)) {
				DicteriousGame.Game.setScreen(DicteriousGame.SUserProfile);
			}

			return false;
		}
	};

}
