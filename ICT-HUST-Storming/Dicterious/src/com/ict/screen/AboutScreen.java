
package com.ict.screen;

import com.badlogic.gdx.Gdx;
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

public class AboutScreen extends ScreenAdapter {
	private Texture mBackground;
	private Sprite mBack;

	private SpriteBatch mBatch;

	private InputAdapter mInput = new InputAdapter() {
		public boolean keyUp (int keycode) {
			DicteriousGame.Game.setScreen(DicteriousGame.SMainMenu);
			return false;
		};

		public boolean touchUp (int screenX, int screenY, int pointer, int button) {
			Vector2 pro = eMath.convertToScreenCoordiate(screenX, screenY);
			if (mBack.getBoundingRectangle().contains(pro)) DicteriousGame.Game.setScreen(DicteriousGame.SMainMenu);
			return false;
		};
	};

	@Override
	public void show () {
		mBackground = DicteriousGame.AssetManager.get(I.About, Texture.class);
		mBack = new Sprite(DicteriousGame.AssetManager.get(I.BackButton, Texture.class));
		mBack.setPosition(DicteriousGame.ScreenWidth - mBack.getWidth() - 13, DicteriousGame.ScreenHeight - mBack.getHeight() - 13);

		mBatch = DicteriousGame.Batch;

		Gdx.input.setInputProcessor(mInput);
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
		mBack.draw(mBatch);
		mBatch.end();
	}
}
