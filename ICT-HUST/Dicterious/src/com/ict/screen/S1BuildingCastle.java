
package com.ict.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.ict.DicteriousGame;
import com.ict.entities.EntitiesManager;
import com.ict.entities.G1Background;
import com.ict.entities.G1Brick;

public class S1BuildingCastle implements Screen {
	private SpriteBatch batch;
	private final EntitiesManager mEntitiesManager = new EntitiesManager();
	private boolean isInit = false;

	G1Brick b;

	@Override
	public void show () {
		if (!isInit) {
			isInit = true;

			batch = DicteriousGame.Batch;
			batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, DicteriousGame.ScreenWidth, DicteriousGame.ScreenHeight));

			G1Background background = new G1Background();
			background.setName("background");
			mEntitiesManager.add(background);
		} else {

		}
		mEntitiesManager.show(DicteriousGame.ScreenWidth, DicteriousGame.ScreenHeight);
		mEntitiesManager.findEntityByName("background").postEvent("maxtime_90");

		b = new G1Brick();
		b.show(1, 1);
		b.postEvent("text_Trung Love Anh");
		b.postEvent("position_100_100");
	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		mEntitiesManager.update(Gdx.graphics.getDeltaTime());

		batch.begin();
		mEntitiesManager.render(batch);
		b.render(batch);
		batch.end();
		
		if(Gdx.input.justTouched()){
			b.postEvent("dark");
		}
	}

	@Override
	public void resize (int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide () {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause () {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume () {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose () {
	}

}
