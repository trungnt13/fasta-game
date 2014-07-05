
package com.topvn.fappybird;

import org.tntstudio.Utils;
import org.tntstudio.core.Input.MotionCallback;
import org.tntstudio.core.Top;
import org.tntstudio.graphics.objs.SpriteMultiAnimation;

public class FappyBird extends SpriteMultiAnimation implements MotionCallback {
	/*-------- speed y --------*/

	public static final float Y_DECLINE_SPEED = -(98f * 6);
	public static final float PUSH_Y_SPEED = 1200f;
	private float mSpeedY = 0;

	/*-------- speed x --------*/

	public static final float X_DECLINE_SPEED = -(98f * 3);
	public static final float PUSH_X_SPEED = 700f;
	private float mSpeedX = 0;
	private float mSpeedXDirection = 0;

	/*-------- bound of speed --------*/

	private static final float MINIMUM_SPEED_Y_VALUE = 0.25f;
	private static final float MINIMUM_SPEED_X_VALUE = 0.20f;

	private static final float MAXIMUM_SPEED_Y_VALUE = 0.80f;
	private static final float MAXIMUM_SPEED_X_VALUE = 0.75f;

	/*-------- angle --------*/

	private static final float MAXIMUM_ANGLE_VALUE = 50f;

	@Override
	protected void updateInternal (float delta) {
		super.updateInternal(delta);
		mSpeedY += Y_DECLINE_SPEED * delta;

		if (mSpeedX > 0)
			mSpeedX += X_DECLINE_SPEED * delta;
		else
			mSpeedX = 0;

		translate(mSpeedX * delta * mSpeedXDirection, mSpeedY * delta);

		if (mSpeedY > 0) {
			setRotation(MAXIMUM_ANGLE_VALUE * mSpeedXDirection);
		} else {
			setRotation(-MAXIMUM_ANGLE_VALUE * mSpeedXDirection);
		}

		/*-------- just for debug --------*/
		if (getY() < -500) setY(100);
	}

	@Override
	public boolean onTouchDown (float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean onTouchUp (float x, float y, int pointer, int button) {
		mSpeedY = PUSH_Y_SPEED
			* Utils.boundTheValue(MAXIMUM_SPEED_Y_VALUE, Math.abs(y - getCenterY()) / Top.gameHeight(), MINIMUM_SPEED_Y_VALUE);
		mSpeedX = PUSH_X_SPEED
			* Utils.boundTheValue(MAXIMUM_SPEED_X_VALUE, Math.abs(x - getCenterX()) / Top.gameWidth(), MINIMUM_SPEED_X_VALUE);

		if (x - getCenterX() > 0) {
			mSpeedXDirection = 1;
			setFlip(false, false);
		} else {
			mSpeedXDirection = -1;
			setFlip(true, false);
		}
// if (y < getCenterY()) mSpeedXDirection *= -1;
		return false;
	}

	@Override
	public boolean onTouchDragged (float x, float y, int pointer) {
		return false;
	}

	@Override
	public boolean onTouchMoved (float x, float y) {
		return false;
	}

}
