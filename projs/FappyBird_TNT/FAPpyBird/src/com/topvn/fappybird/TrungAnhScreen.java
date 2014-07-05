
package com.topvn.fappybird;

import org.tntstudio.Const.ScreenState;
import org.tntstudio.core.BaseScreen;
import org.tntstudio.core.Top;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TrungAnhScreen extends BaseScreen {
	Texture bg;

	@Override
	public void onCreate () {
		bg = Top.tres.rGet("TrungAnh.png", Texture.class);
	}

	@Override
	public void onShow (int screenWidth, int screenHeight) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdate (float delta) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRender (SpriteBatch batch) {
		batch.begin();
		batch.draw(bg, Top.gameWidth() - bg.getWidth() / 2, Top.gameHeight() - bg.getHeight() / 2);
		batch.end();
	}

	@Override
	public void onHide (ScreenState mode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisposed () {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isGamePlayScreen () {
		// TODO Auto-generated method stub
		return false;
	}

}
