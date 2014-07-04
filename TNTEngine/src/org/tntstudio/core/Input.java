
package org.tntstudio.core;

import org.tntstudio.core.CustomerServices.CustomerServicesCreator;
import org.tntstudio.interfaces.LifecycleListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Input implements InputProcessor, LifecycleListener {
	private final Array<MotionCallback> mMotionCallbackList = new Array<MotionCallback>(true, 16);
	private final Array<KeyCallback> mKeyCallbackList = new Array<KeyCallback>(true, 16);
	private final Array<InputCallback> mInputCallbackList = new Array<InputCallback>(true, 16);
	private final Array<InputProcessor> mInputList = new Array<InputProcessor>(true, 16);

	private final Vector2 projected = new Vector2();
	private int mLastTouchedPointer = 0;
	private float tmp;

	public final InputCreator Creator = new InputCreator();

	Input () {
		Top.tgame.registerLifecycleListener(this);
	}

	public class InputCreator {
		private boolean isInited = false;

		private InputCreator () {
		}

		public CustomerServicesCreator initInput () {
			if (isInited) return Top.tcus.Creator;
			isInited = true;

			return Top.tcus.Creator;
		}
	}

	// ///////////////////////////////////////////////////////////////
	// getter
	// ///////////////////////////////////////////////////////////////
	public boolean isTouched () {
		return Gdx.input.isTouched();
	}

	public boolean justTouched () {
		return Gdx.input.justTouched();
	}

	public boolean isKeyPressed (int key) {
		return Gdx.input.isKeyPressed(key);
	}

	public int getLastTouchedPointer () {
		return mLastTouchedPointer;
	}

	@SuppressWarnings("rawtypes")
	public int size (Class type) {
		if (type == MotionCallback.class) return mMotionCallbackList.size;
		if (type == KeyCallback.class) return mKeyCallbackList.size;
		if (type == InputCallback.class) return mInputCallbackList.size;
		if (type == InputProcessor.class) return mInputList.size;
		return mMotionCallbackList.size + mKeyCallbackList.size + mInputCallbackList.size + mInputList.size;
	}

	// ///////////////////////////////////////////////////////////////
	// setter & callback manager
	// ///////////////////////////////////////////////////////////////
	void addCallback (Callback callback) {
		if (callback instanceof MotionCallback && !mMotionCallbackList.contains((MotionCallback)callback, true))
			mMotionCallbackList.add((MotionCallback)callback);
		else if (callback instanceof KeyCallback && !mKeyCallbackList.contains((KeyCallback)callback, true))
			mKeyCallbackList.add((KeyCallback)callback);
		else if (callback instanceof InputCallback && !mInputCallbackList.contains((InputCallback)callback, true))
			mInputCallbackList.add((InputCallback)callback);
	}

	void removeCallback (Callback callback) {
		if (callback instanceof MotionCallback)
			mMotionCallbackList.removeValue((MotionCallback)callback, true);
		else if (callback instanceof KeyCallback)
			mKeyCallbackList.removeValue((KeyCallback)callback, true);
		else if (callback instanceof InputCallback) mInputCallbackList.removeValue((InputCallback)callback, true);
	}

	public void addInput (InputProcessor input) {
		if (!mInputList.contains(input, true)) mInputList.add(input);
	}

	public void addInput (InputAdapter input) {
		if (!mInputList.contains(input, true)) mInputList.add(input);
	}

	public void removeInput (InputProcessor in) {
		mInputList.removeValue(in, true);
	}

	// ///////////////////////////////////////////////////////////////
	// override methods
	// ///////////////////////////////////////////////////////////////

	@Override
	public boolean keyDown (int keycode) {
		for (InputProcessor in : mInputList) {
			if (in.keyDown(keycode)) return true;
		}

		for (KeyCallback k : mKeyCallbackList) {
			if (k.onKeyDown(keycode)) return true;
		}

		for (InputCallback k : mInputCallbackList) {
			if (k.onKeyDown(keycode)) return true;
		}

		return false;
	}

	@Override
	public boolean keyUp (int keycode) {
		for (InputProcessor in : mInputList) {
			if (in.keyUp(keycode)) return true;
		}

		for (KeyCallback k : mKeyCallbackList) {
			if (k.onKeyUp(keycode)) return true;
		}

		for (InputCallback k : mInputCallbackList) {
			if (k.onKeyUp(keycode)) return true;
		}

		return false;
	}

	@Override
	public boolean keyTyped (char character) {
		for (InputProcessor in : mInputList) {
			if (in.keyTyped(character)) return true;
		}

		for (KeyCallback k : mKeyCallbackList) {
			if (k.onKeyTyped(character)) return true;
		}

		for (InputCallback k : mInputCallbackList) {
			if (k.onKeyTyped(character)) return true;
		}

		return false;
	}

	private final void projectToCurrentScreen (float x, float y) {
		tmp = Gdx.graphics.getHeight() - y;

		projected.x = (int)(((float)x / (float)Gdx.graphics.getWidth()) * Top.gameWidth());
		projected.y = (int)(((float)tmp / (float)Gdx.graphics.getHeight()) * Top.gameHeight());

	}

	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		mLastTouchedPointer = pointer;

		// ============= calculate projected input =============
		projectToCurrentScreen(screenX, screenY);

		for (InputProcessor i : mInputList) {
			if (i.touchDown(screenX, screenY, pointer, button)) return true;
		}

		for (MotionCallback m : mMotionCallbackList) {
			if (m.onTouchDown(projected.x, projected.y, pointer, button)) return true;
		}

		for (InputCallback m : mInputCallbackList) {
			if (m.onTouchDown(projected.x, projected.y, pointer, button)) return true;
		}

		return false;
	}

	@Override
	public boolean touchUp (int screenX, int screenY, int pointer, int button) {
		// ============= calculate projected input =============
		projectToCurrentScreen(screenX, screenY);

		for (InputProcessor i : mInputList) {
			if (i.touchUp(screenX, screenY, pointer, button)) return true;
		}

		for (MotionCallback m : mMotionCallbackList) {
			if (m.onTouchUp(projected.x, projected.y, pointer, button)) return true;
		}

		for (InputCallback m : mInputCallbackList) {
			if (m.onTouchUp(projected.x, projected.y, pointer, button)) return true;
		}

		return false;
	}

	@Override
	public boolean touchDragged (int screenX, int screenY, int pointer) {
		// ============= calculate projected input =============
		projectToCurrentScreen(screenX, screenY);

		for (InputProcessor i : mInputList) {
			if (i.touchDragged(screenX, screenY, pointer)) return true;
		}

		for (MotionCallback m : mMotionCallbackList) {
			if (m.onTouchDragged(projected.x, projected.y, pointer)) return true;
		}

		for (InputCallback m : mInputCallbackList) {
			if (m.onTouchDragged(projected.x, projected.y, pointer)) return true;
		}

		return false;

	}

	@Override
	public boolean mouseMoved (int screenX, int screenY) {
		// ============= calculate projected input =============
		projectToCurrentScreen(screenX, screenY);

		for (InputProcessor i : mInputList) {
			if (i.mouseMoved(screenX, screenY)) return true;
		}

		for (MotionCallback m : mMotionCallbackList) {
			if (m.onTouchMoved(projected.x, projected.y)) return true;
		}

		for (InputCallback m : mInputCallbackList) {
			if (m.onTouchMoved(projected.x, projected.y)) return true;
		}
		return false;
	}

	@Override
	public boolean scrolled (int amount) {
		for (InputProcessor i : mInputList) {
			if (i.scrolled(amount)) return true;
		}

		return false;
	}

	// ///////////////////////////////////////////////////////////////
	// life cycle
	// ///////////////////////////////////////////////////////////////
	@Override
	public void dispose () {
		mInputCallbackList.clear();
		mInputList.clear();
		mKeyCallbackList.clear();
		mMotionCallbackList.clear();
	}

	@Override
	public void resume () {

	}

	@Override
	public void pause () {

	}

	// ///////////////////////////////////////////////////////////////
	// helper interface
	// ///////////////////////////////////////////////////////////////

	public interface Callback {
	}

	public interface KeyCallback extends Callback {

		/** Called when a key was pressed
		 * 
		 * @param keycode one of the constants in {@link Input.Keys}
		 * @return whether the input was processed */
		public boolean onKeyDown (int keycode);

		/** Called when a key was released
		 * 
		 * @param keycode one of the constants in {@link Input.Keys}
		 * @return whether the input was processed */
		public boolean onKeyUp (int keycode);

		/** Called when a key was typed
		 * 
		 * @param character The character
		 * @return whether the input was processed */
		public boolean onKeyTyped (char character);
	}

	public interface MotionCallback extends Callback {

		/** Called when the screen was touched or a mouse button was pressed. The button parameter will be {@link Buttons#LEFT} on
		 * Android.
		 * 
		 * @param x The x coordinate, origin is in the upper left corner
		 * @param y The y coordinate, origin is in the upper left corner
		 * @param pointer the pointer for the event.
		 * @param button the button
		 * @return whether the input was processed */
		public boolean onTouchDown (float x, float y, int pointer, int button);

		/** Called when a finger was lifted or a mouse button was released. The button parameter will be {@link Buttons#LEFT} on
		 * Android.
		 * 
		 * @param x The x coordinate
		 * @param y The y coordinate
		 * @param pointer the pointer for the event.
		 * @param button the button
		 * @return whether the input was processed */
		public boolean onTouchUp (float x, float y, int pointer, int button);

		/** Called when a finger or the mouse was dragged.
		 * 
		 * @param x The x coordinate
		 * @param y The y coordinate
		 * @param pointer the pointer for the event.
		 * @return whether the input was processed */
		public boolean onTouchDragged (float x, float y, int pointer);

		/** Called when the mouse was moved without any buttons being pressed. Will not be called on Android.
		 * 
		 * @param x The x coordinate
		 * @param y The y coordinate
		 * @return whether the input was processed */
		public boolean onTouchMoved (float x, float y);
	}

	public interface InputCallback extends MotionCallback, KeyCallback {

	}

}
