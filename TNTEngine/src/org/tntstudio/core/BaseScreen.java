
package org.tntstudio.core;

import org.tntstudio.Const.ScreenState;
import org.tntstudio.TNTRuntimeException;
import org.tntstudio.interfaces.Identifiable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** A Base Screen of Game. Draw with {@value batch} This screen init every thing coordinate to Screen
 * @author ManhPhi */
public abstract class BaseScreen extends Screen implements Identifiable {
	private int mPopupScreenCount = 0;

	// ///////////////////////////////////////////////////////////////
	// abstract
	// ///////////////////////////////////////////////////////////////

	/*-------- init state --------*/

	/** Call only once, when Screen first create */
	public abstract void onCreate ();

	/** This method is call when ApplicationListern.resize() or screen comeback from Hiding State */
	public abstract void onShow (int screenWidth, int screenHeight);

	/*-------- running state --------*/

	/** Update Game Components on main thread of Screen */
	public abstract void onUpdate (float delta);

	/** Draw Drawable Object to Screen use {@value batch} */
	public abstract void onRender (SpriteBatch batch);

	/*-------- release state --------*/

	/** Call when screen is hided or becomes paused and invisible but not be disposed
	 * @param mode {@link ScreenState} */
	public abstract void onHide (ScreenState mode);

	/** Dispose everythiing on this screen */
	public abstract void onDisposed ();

	// ///////////////////////////////////////////////////////////////
	// override methods
	// ///////////////////////////////////////////////////////////////

	/** Logic of Show: <li>
	 * Check if Screen is Disposed, yes - throw Exception, otherwise we continue <li> */
	void show () {
		if (isDisposed()) throw new TNTRuntimeException("The Screen:" + Name()+ "," + this + " is Disposed and can't be use again!");

		/*-------- show panel --------*/
		if (getPanel() != null) getPanel().show();

		/*-------- do NOT create 2 times --------*/
		if (!isCreated()) {
			// init screen size
			vpSetOrtho(0, Top.gameWidth(), 0, Top.gameHeight(), 0, 1);
			vpResetTransform();

			setScreenState(ScreenState.Showed);
			onCreate();
			// onShow will be called on GameCore#setScreen()
		} else
			resume();
	}

	@Override
	void hide () {
		setScreenState(ScreenState.Hided);
		if (getPanel() != null) getPanel().hide();

		stopReceiveInput();
		onHide(getCurrentScreenState());
	}

	@Override
	void resize (int width, int height) {
		onShow(width, height);
	}

	@Override
	void resume () {
		if (isDisposed()) return;
		setScreenState(ScreenState.Showed);

		startReceiveInput();

		if (getLastScreenState() == ScreenState.Hided) onShow(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	void pause () {
		/*-------- never pause twice at one --------*/
		if (isPaused()) return;

		setScreenState(ScreenState.Paused);

		stopReceiveInput();
		onHide(getCurrentScreenState());
	}

	@Override
	void dispose () {
		setScreenState(ScreenState.Disposed);

		if (getPanel() != null) getPanel().dispose();
		stopReceiveInput();
		onDisposed();
	}

	@Override
	void update (float delta) {
		if (getCurrentScreenState() == ScreenState.Showed) onUpdate(delta);
		frame.act(delta);
	}

	@Override
	void render (float delta) {
		if (getCurrentScreenState() != ScreenState.Showed && getCurrentScreenState() != ScreenState.Paused) return;

		prepareGraphics();
		onRender(batch);
		drawLayout();
		flushAndResetGraphics();
	}

	// ///////////////////////////////////////////////////////////////
	// popup screen methods
	// ///////////////////////////////////////////////////////////////
	void popupScreenShowed () {
		mPopupScreenCount++;
		pause();
	}

	void popupScreenHide () {
		mPopupScreenCount--;
		if (mPopupScreenCount <= 0) resume();
	}

	public void newPopupScreen (Class<? extends PopUpScreen> screenType) {
		Top.tgame.newPopupScreen(screenType.getSimpleName());
	}
}
