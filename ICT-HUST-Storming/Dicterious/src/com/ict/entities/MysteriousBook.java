
package com.ict.entities;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.ict.DicteriousGame;
import com.ict.data.I;

public class MysteriousBook extends Entity {
	// ///////////////////////////////////////////////////////////////
	// static param
	// ///////////////////////////////////////////////////////////////
	private static final int MAX_SIMULTANOUS_ICONS = 3;

	private static final float MAX_SPEED = 100;
	private static final float MIN_SPEED = 100;

	private static final float DECELERATE_X = 500;
	private static final float DECELERATE_Y = 1000;

	// ///////////////////////////////////////////////////////////////
	// main
	// ///////////////////////////////////////////////////////////////

	private Sprite mBook;
	private Sprite[] mLoadingIcons;

	private final ArrayList<Sprite> mSprites = new ArrayList<Sprite>();
	private final ArrayList<Sprite> mTmps = new ArrayList<Sprite>();

	private boolean isShowMysteriousStuffs = false;

	private float x;
	private float y;

	public MysteriousBook () {
		mBook = new Sprite(new Texture(Gdx.files.internal(I.LoadingBook)));
		float hwratio = mBook.getHeight() / mBook.getWidth();
		mBook.setSize(DicteriousGame.ScreenWidth, hwratio * DicteriousGame.ScreenWidth);

		mLoadingIcons = new Sprite[I.LoadingIcons.length];
		int i = 0;
		for (String s : I.LoadingIcons) {
			mLoadingIcons[i++] = new Sprite(new Texture(Gdx.files.internal(s)));
		}
	}

	// ///////////////////////////////////////////////////////////////
	// plug in control methods
	// ///////////////////////////////////////////////////////////////
	public void setPosition (float x, float y) {
		this.x = x;
		this.y = y;
		mBook.setPosition(x, y);
	}

	public float getWidth () {
		return mBook.getWidth();
	}

	public float getHeight () {
		return mBook.getHeight();
	}

	private ArrayList<Sprite> safeClone () {
		mTmps.clear();
		mTmps.addAll(mSprites);
	}

	// ///////////////////////////////////////////////////////////////
	// override
	// ///////////////////////////////////////////////////////////////

	@Override
	public void show () {

	}

	@Override
	public void update (float delta) {

	}

	@Override
	public void render (Batch batch) {
		if (!isShowMysteriousStuffs) return;
		mBook.draw(batch);
	}

	@Override
	public void postEvent (Object... params) {
		String eventType = ((String)params[0]).toLowerCase();
		if (eventType.contains("show")) {
			isShowMysteriousStuffs = true;
		} else if (eventType.contains("hide")) {
			isShowMysteriousStuffs = false;
		}
	}
}
