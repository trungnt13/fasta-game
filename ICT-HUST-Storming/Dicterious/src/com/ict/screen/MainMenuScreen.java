
package com.ict.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
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

	@Override
	public void show () {
		mBackground = DicteriousGame.AssetManager.get(I.MainMenu.Background, Texture.class);

		mBatch = DicteriousGame.Batch;
		mBatch.setProjectionMatrix(DicteriousGame.ScreenViewport);

		DicteriousGame.InputMultiplexer.addProcessor(mInput);
	}

	@Override
	public void hide () {
		DicteriousGame.InputMultiplexer.removeProcessor(mInput);
	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		mBatch.begin();
		mBatch.draw(mBackground, 0, 0, DicteriousGame.ScreenWidth, DicteriousGame.ScreenHeight);
		mBatch.end();
	}

	// ///////////////////////////////////////////////////////////////
	// input adapter
	// ///////////////////////////////////////////////////////////////

	private final InputAdapter mInput = new InputAdapter() {

		@Override
		public boolean touchUp (int screenX, int screenY, int pointer, int button) {
			Vector2 projected = eMath.convertToScreenCoordiate(screenX, screenY);
			if (projected.y > DicteriousGame.ScreenHeight / 2) {
				DicteriousGame.Game.setScreen(DicteriousGame.SStoriesSelect);
			} else {
				DicteriousGame.Game.setScreen(DicteriousGame.SUserProfile);
			}

			return false;
		}
	};

}
