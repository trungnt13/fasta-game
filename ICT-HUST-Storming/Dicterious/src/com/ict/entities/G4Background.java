
package com.ict.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.ict.DicteriousGame;
import com.ict.data.I;

public class G4Background extends Entity {

	private Texture mBackground;

	@Override
	public void show () {
		mBackground = DicteriousGame.AssetManager.get(I.G4.Background, Texture.class);
	}

	@Override
	public void update (float delta) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render (Batch batch) {
		batch.draw(mBackground, 0, 0, DicteriousGame.ScreenWidth, DicteriousGame.ScreenHeight);
	}

	@Override
	public void postEvent (Object... params) {
		// TODO Auto-generated method stub

	}

}
