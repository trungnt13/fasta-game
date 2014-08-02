
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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.ict.DicteriousGame;
import com.ict.data.I;
import com.ict.utils.eMath;

public class StoriesSelectScreen extends ScreenAdapter {
	// ///////////////////////////////////////////////////////////////
	// main part
	// ///////////////////////////////////////////////////////////////

	private Texture mBackground;

	private SpriteBatch mBatch;

	private Texture mStory1;
	private Texture mStory2;

	/*-------- ui --------*/
	Stage stage;

	private ClickListener mClickListener = new ClickListener() {
		public void clicked (com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
			String name = event.getListenerActor().getName();
			if (name.contains("back"))
				DicteriousGame.Game.setScreen(DicteriousGame.SMainMenu);
			else if (name.contains("story1")) DicteriousGame.Game.setScreen(DicteriousGame.SMapSelect);
		};
	};

	@Override
	public void show () {
		mBackground = DicteriousGame.AssetManager.get(I.MainMenu.StoriesReview, Texture.class);

		Image back = new Image(DicteriousGame.AssetManager.get(I.BackButton, Texture.class));
		back.setName("back");
		back.addListener(mClickListener);
		back.setPosition(DicteriousGame.ScreenWidth - back.getWidth() - 13, DicteriousGame.ScreenHeight - back.getHeight() - 13);

		mBatch = DicteriousGame.Batch;
		mBatch.setProjectionMatrix(DicteriousGame.ScreenViewport);

		mStory1 = DicteriousGame.AssetManager.get(I.MainMenu.Story1, Texture.class);
		Image img1 = new Image(mStory1);
		img1.setName("story1");
		img1.addListener(mClickListener);

		mStory2 = DicteriousGame.AssetManager.get(I.MainMenu.Story2, Texture.class);
		Image img2 = new Image(mStory2);
		img2.setName("story2");
		img2.addListener(mClickListener);

		/*-------- create scrollpane --------*/
		stage = DicteriousGame.Layout;

		Table table = new Table();
		final ScrollPane scroll = new ScrollPane(table);
		scroll.setBounds(0, 130, DicteriousGame.ScreenWidth, 1000);
		scroll.setScrollingDisabled(false, true);

		table.add(img1);
		table.add(img2);

		stage.addActor(back);
		stage.addActor(scroll);
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide () {
		Gdx.input.setInputProcessor(new InputAdapter());
		stage.clear();
	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		mBatch.begin();
		mBatch.draw(mBackground, 0, 0, DicteriousGame.ScreenWidth, DicteriousGame.ScreenHeight);
		mBatch.end();

		stage.act(delta);
		stage.draw();

	}

}
