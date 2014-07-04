/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.tntstudio.core;

import org.tntstudio.Const.ScreenState;
import org.tntstudio.core.Input.Callback;
import org.tntstudio.core.Input.InputCallback;
import org.tntstudio.core.Input.KeyCallback;
import org.tntstudio.core.Input.MotionCallback;
import org.tntstudio.interfaces.Identifiable;
import org.tntstudio.interfaces.ScreenStateListener;
import org.tntstudio.interfaces.Viewport.Viewport2D;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/** <p>
 * Represents one of many application screens, such as a main menu, a settings menu, the game screen and so on.
 * </p>
 * <p>
 * Note that {@link #dispose()} is not called automatically.
 * </p>
 * @see BaseScreen, {@link PopUpScreen}
 * @author trungnt13 */
public abstract class Screen implements Identifiable, Viewport2D {
	/** List for {@link InputCallback}, {@link MotionCallback}, {@link KeyCallback} */
	private final Array<Callback> mInputCallbackList = new Array<Callback>();

	/*-------- static resources --------*/
	protected static SpriteBatch batch = null;
	private static SpriteCache cache = null;
	private static ShapeRenderer shape = null;
	protected static Frame frame = null;
	private Panel mPanel;

	/*-------- flags --------*/
	/** Current {@link ScreenState} use for supervision screen state at pause and resume */
	private ScreenState mCurrentState = ScreenState.NotDefined;
	/** Current {@link ScreenState} use for supervision screen state at pause and resume */
	private ScreenState mLastState = null;
	/** Screen will distribute StateChanged event using all these listener */
	private final Array<ScreenStateListener> mStateListeners = new Array<ScreenStateListener>();

	protected final Matrix4 projection = new Matrix4().idt();
	protected final Matrix4 transform = new Matrix4().idt();
	private final Matrix4 combined = new Matrix4().idt();

	private float zoom = 1f;

	/** left postion of viewport on this screen */
	private float left;
	/** right postion of viewport on this screen */
	private float right;
	/** top postion of viewport on this screen */
	private float top;
	/** bottom postion of viewport on this screen */
	private float bottom;

	public Screen () {
	}

	/** Init Screen with getting Camera & SpriteBatch */
	static void init () {
		batch = Top.tres.gSpriteBatch();
		frame = Top.tres.gFrame();
	}

	@Override
	public final String Name () {
		return getClass().getSimpleName();
	}

	public ShapeRenderer shapeRenderer () {
		if (shape == null) shape = Top.tres.gShapeRenderer();
		return shape;
	}

	public SpriteCache spriteCache () {
		if (cache == null) cache = Top.tres.gSpriteCache();
		return cache;
	}

	// ///////////////////////////////////////////////////////////////
	// getter setter for flags
	// ///////////////////////////////////////////////////////////////
	/** If onCreated is called, this methods return true, otherwise false */
	public boolean isCreated () {
		return mCurrentState != ScreenState.NotDefined;
	}

	/** Query for current state of screen */
	public ScreenState getCurrentScreenState () {
		return mCurrentState;
	}

	/** Query for Last State of Screen, extremely useful use in methods resume(), only when last state is Hided screen will call
	 * onShow */
	public ScreenState getLastScreenState () {
		return mLastState;
	}

	/** Add listener */
	public void registerStateChangedListener (ScreenStateListener... listener) {
		mStateListeners.addAll(listener);
	}

	/** Remove listener */
	public void removeStateChangedListener (ScreenStateListener... listener) {
		for (ScreenStateListener screenStateListener : listener) {
			mStateListeners.removeValue(screenStateListener, true);
		}
	}

	/** Set curretn state, Save last state and fire onStatedChanged event */
	void setScreenState (ScreenState state) {
		if (mCurrentState == state) return;

		mLastState = mCurrentState;
		mCurrentState = state;
		for (ScreenStateListener s : mStateListeners)
			s.onStateChanged(this, mCurrentState, mLastState);
	}

	/** if screen was hided, return true, otherwise false */
	public boolean isHided () {
		return mCurrentState == ScreenState.Hided;
	}

	/** if screen was disposed, return true, otherwise false */
	public boolean isDisposed () {
		return mCurrentState == ScreenState.Disposed;
	}

	/** if screen was Paused, return true, otherwise false */
	public boolean isPaused () {
		return mCurrentState == ScreenState.Paused;
	}

	/** Create new Panel to cache all UI of this screen */
	protected Panel getPanel () {
		if (mPanel != null) return mPanel;
		mPanel = Top.tres.gFrame().createNewPanel(Name());
		return mPanel;
	}

	/** Call by {@link Panel} whhen it is disposed */
	void nullizePanel () {
		mPanel = null;
	}

	// ///////////////////////////////////////////////////////////////
	// input control methods
	// ///////////////////////////////////////////////////////////////

	/** {@link Callback} for handle Key Event, Motion Event or both */
	public void bindInputCallback (Callback... callback) {
		for (Callback c : callback) {
			_bindInputCallback(c);
		}
	}

	private void _bindInputCallback (Callback callback) {
		if (mInputCallbackList.contains(callback, true)) return;

		mInputCallbackList.add(callback);
		Top.tinp.addCallback(callback);
	}

	/** {@link Callback} for handle Key Event, Motion Event or both */
	public void removeInputCallback (Callback... callback) {
		for (Callback c : callback) {
			_removeInputCallback(c);
		}
	}

	private void _removeInputCallback (Callback callback) {
		mInputCallbackList.removeValue(callback, true);
		Top.tinp.removeCallback(callback);
	}

	/** Clear all input callback from input list and they can't be restored */
	public void clearAllInputCallback () {
		for (Callback c : mInputCallbackList) {
			Top.tinp.removeCallback(c);
		}
		mInputCallbackList.clear();
	}

	/** Call on resume() to add all Callback to Top.Input */
	void startReceiveInput () {
		for (Callback c : mInputCallbackList) {
			Top.tinp.addCallback(c);
		}
	}

	/** Call on pause(), dispose(), hide() to remove all Callback from Top.Input */
	void stopReceiveInput () {
		for (Callback c : mInputCallbackList) {
			Top.tinp.removeCallback(c);
		}
	}

	/** apply projection and transformation to all drawing components */
	void prepareGraphics () {
		vpApply(batch);
		vpApply(frame);
		vpApply(shape);
		vpApply(cache);
	}

	void drawLayout () {
		frame.draw();
	}

	void flushAndResetGraphics () {
		batch.setColor(Color.WHITE);
		batch.flush();
		frame.getSpriteBatch().flush();
		if (shape != null) {
			try {
				shape.flush();
			} catch (NullPointerException e) {
			}
		}
	}

	public TweenManager getTweenManager () {
		return Top.tgame.mTween;
	}

	// ///////////////////////////////////////////////////////////////
	// abstract methods
	// ///////////////////////////////////////////////////////////////

	public abstract boolean isGamePlayScreen ();

	/** call for updating Game Components before render() them */
	abstract void update (float delta);

	/** Called when the screen should render itself.
	 * @param delta The time in seconds since the last render. */
	abstract void render (float delta);

	/** @see ApplicationListener#resize(int, int) */
	abstract void resize (int width, int height);

	/** Called when this screen becomes the current screen for a {@link Game}. */
	abstract void show ();

	/** Called when this screen is no longer the current screen for a {@link Game}. */
	abstract void hide ();

	/** @see ApplicationListener#pause() */
	abstract void pause ();

	/** @see ApplicationListener#resume() */
	abstract void resume ();

	/** Called when this screen should release all resources. */
	abstract void dispose ();

	// ///////////////////////////////////////////////////////////////
	// viewport 2D
	// ///////////////////////////////////////////////////////////////

	@Override
	public void vpApply (SpriteCache cache) {
		if (cache == null) return;

		cache.setProjectionMatrix(projection);
		cache.setTransformMatrix(transform);
	}

	@Override
	public void vpApply (SpriteBatch batch) {
		if (batch == null) return;

		batch.setProjectionMatrix(projection);
		batch.setTransformMatrix(transform);
	}

	@Override
	public void vpApply (ShapeRenderer shape) {
		if (shape == null) return;

		shape.setProjectionMatrix(projection);
		shape.setTransformMatrix(transform);
	}

	@Override
	public void vpApply (Frame frame) {
		if (frame == null) return;

		frame.getSpriteBatch().setProjectionMatrix(projection);
		frame.getSpriteBatch().setTransformMatrix(transform);
	}

	@Override
	public Matrix4 vpCombined () {
		combined.set(projection).mul(transform);
		return combined;
	}

	@Override
	public Viewport2D vpSetOrtho (float left, float right, float bottom, float top, float near, float far) {
		this.left = left;
		this.right = zoom * right;
		this.bottom = bottom;
		this.top = zoom * top;

		projection.setToOrtho(this.left, this.right, this.bottom, this.top, 0, 1);
		return this;
	}

	@Override
	public Viewport2D vpSetOrtho2D (float x, float y, float width, float height, boolean yDown) {
		if (!yDown) {
			this.left = x;
			this.right = zoom * width;
			this.bottom = y;
			this.top = zoom * height;
		} else {
			this.left = 0;
			this.right = zoom * width;
			this.bottom = zoom * height;
			this.top = 0;
		}
		projection.setToOrtho(this.left, this.right, this.bottom, this.top, 0, 1);
		return this;
	}

	@Override
	public Viewport2D vpSetOrtho2D (float width, float height, boolean yDown, boolean lookAtCenter) {
		if (!yDown) {
			if (!lookAtCenter) {
				this.left = 0;
				this.right = zoom * width;
				this.bottom = 0;
				this.top = zoom * height;
			} else {
				this.left = zoom * -(width / 2);
				this.right = zoom * (width / 2);
				this.bottom = zoom * (-height / 2);
				this.top = zoom * (height / 2);
			}
		} else {
			if (!lookAtCenter) {
				this.left = 0;
				this.right = zoom * width;
				this.bottom = zoom * height;
				this.top = 0;
			} else {
				this.left = zoom * -(width / 2);
				this.right = zoom * (width / 2);
				this.bottom = zoom * (height / 2);
				this.top = zoom * (-height / 2);
			}
		}
		projection.setToOrtho(this.left, this.right, this.bottom, this.top, 0, 1);
		return this;
	}

	@Override
	public Viewport2D vpTranslate (float x, float y) {
		transform.translate(x, y, 0);
		return this;
	}

	@Override
	public Viewport2D vpTranslate (Vector2 vec2) {
		transform.translate(vec2.x, vec2.y, 0);
		return this;
	}

	@Override
	public Viewport2D vpToTranslatation (float x, float y) {
		transform.setToTranslation(x, y, 0);
		return this;
	}

	@Override
	public Viewport2D vpToTranslatation (Vector2 vec2) {
		transform.setToTranslation(vec2.x, vec2.y, 0);
		return this;
	}

	@Override
	public Viewport2D vpRotate (float angle) {
		transform.rotate(0, 0, 1, angle);
		return this;
	}

	@Override
	public Viewport2D vpZoom (float zoom) {
		this.zoom = 1 / zoom;

		this.left *= zoom;
		this.right *= zoom;
		this.bottom *= zoom;
		this.top *= zoom;
		return this;
	}

	@Override
	public void vpResetTransform () {
		transform.idt();
	}

	@Override
	public Viewport2D vpToRotation (float angle) {
		transform.setToRotation(0, 0, 1, angle);
		return this;
	}

	@Override
	public Viewport2D vpProject (Vector2 vec) {
		vec.x = (((float)vec.x / (right - left)) * (float)Gdx.graphics.getWidth());
		vec.y = (float)Gdx.graphics.getHeight() - (((float)vec.y / (top - bottom)) * (float)Gdx.graphics.getHeight());
		return this;
	}

	@Override
	public Viewport2D vpUnproject (Vector2 vec) {
		vec.x = (((float)vec.x / (float)Gdx.graphics.getWidth()) * (right - left));
		vec.y = (((float)(Gdx.graphics.getHeight() - vec.y) / (float)Gdx.graphics.getHeight()) * (top - bottom));
		return this;
	}

	@Override
	public float vpHeight () {
		return Math.abs(top - bottom);
	}

	@Override
	public float vpWidth () {
		return Math.abs(left - right);
	}
}
