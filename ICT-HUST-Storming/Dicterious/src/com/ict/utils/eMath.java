
package com.ict.utils;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.ict.DicteriousGame;

public class eMath {
	private static final Vector2 mTmp = new Vector2();
	public final static Random Rand = new Random();

	public static Vector2 convertToScreenCoordiate (int x, int y) {
		int tmpY = Gdx.graphics.getHeight() - y;

		mTmp.x = (int)(((float)x / (float)Gdx.graphics.getWidth()) * DicteriousGame.ScreenWidth);
		mTmp.y = (int)(((float)tmpY / (float)Gdx.graphics.getHeight()) * DicteriousGame.ScreenHeight);
		return mTmp;
	}

}
