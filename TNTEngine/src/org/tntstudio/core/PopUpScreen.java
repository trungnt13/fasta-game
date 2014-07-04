
package org.tntstudio.core;

import org.tntstudio.Const.ScreenChangeMode;
import org.tntstudio.Const.ScreenState;
import org.tntstudio.interfaces.Identifiable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class PopUpScreen extends BaseScreen implements Identifiable {
	private BaseScreen mParent;

	// ///////////////////////////////////////////////////////////////
	// abstract
	// ///////////////////////////////////////////////////////////////
	/*-------- init state --------*/
	public abstract void onCreate ();

	public abstract void onShow (int screenWidth, int screenHeight);

	/*-------- running state --------*/
	/** Update main thread of Screen */
	public abstract void onUpdate (float delta);

	/** Draw Drawable Object to Screen use {@value batch} */
	public abstract void onRender (SpriteBatch batch);

	/*-------- release state --------*/
	public abstract void onDisposed ();

	@Deprecated
	public void onHide (ScreenState mode) {
	}

	// ///////////////////////////////////////////////////////////////
	// override methods
	// ///////////////////////////////////////////////////////////////

	@Override
	void show () {
		super.show();

		if (mParent != null) mParent.popupScreenShowed();
	}

	@Override
	void hide () {
		super.hide();

		if (mParent != null) mParent.popupScreenHide();
		mParent = null;
	}

	@Override
	void resize (int width, int height) {
		onShow(width, height);
	}

	@Override
	public boolean isGamePlayScreen () {
		return false;
	}

	// ///////////////////////////////////////////////////////////////
	// util methods
	// ///////////////////////////////////////////////////////////////

	public void disappear () {
		Top.tgame.hidePopupScreen(this);
	}

	public void backToMainScreen (ScreenChangeMode transactionMode) {
		Top.tgame.setScreen(mParent, transactionMode);
	}

	void setParentScreen (BaseScreen parent) {
		this.mParent = parent;
	}

	public BaseScreen getParentScreen () {
		return mParent;
	}

	// ///////////////////////////////////////////////////////////////
	// override methods
	// ///////////////////////////////////////////////////////////////
	@Deprecated
	void popupScreenHide () {
	}

	@Deprecated
	void popupScreenShowed () {
	};

	@Deprecated
	public void newPopupScreen (Class<? extends PopUpScreen> screenType) {
	}
}
