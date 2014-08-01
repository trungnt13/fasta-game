
package com.ict.utils;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
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

	public static float calVectorAngle (float x, float y, float x1, float y1) {
		if (x1 == x) {
			if (y1 > y) return 90;
			if (y1 < y) return 270;
		}

		if (y1 == y) {
			if (x < x1) return 360;
			if (x > x1) return 180;
		}

		if (x1 > x) return (float)(180.0f / MathUtils.PI * Math.atan((y1 - y) / (x1 - x)));
		if (x1 < x) return (float)(180.0f / MathUtils.PI * Math.atan((y1 - y) / (x1 - x))) - 180;

		return 0;
	}

	public static int randInt (int min, int max) {
		return min + Rand.nextInt(max - min);
	}
}
