
package com.ict;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

/** <p>
 * An {@link ApplicationListener} that delegates to a {@link Screen}. This allows an application to easily have multiple screens.
 * </p>
 * <p>
 * Screens are not disposed automatically. You must handle whether you want to keep screens around or dispose of them when another
 * screen is set.
 * </p> */
public abstract class Game implements ApplicationListener {
	private Screen screen;

	@Override
	public void dispose () {
		if (screen != null) {
			screen.hide();
			screen.dispose();
		}
	}

	@Override
	public void pause () {
		if (screen != null) screen.pause();
	}

	@Override
	public void resume () {
		if (screen != null) screen.resume();
	}

	@Override
	public void render () {
		if (screen != null) screen.render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void resize (int width, int height) {
		if (screen != null) screen.resize(width, height);
	}

	/** Sets the current screen. {@link Screen#hide()} is called on any old screen, and {@link Screen#show()} is called on the new
	 * screen, if any.
	 * @param screen may be {@code null}
	 * @return old screen (if available) */
	public Screen setScreen (Screen screen) {
		final Screen oldScreen = this.screen;
		if (oldScreen != null) oldScreen.hide();
		this.screen = screen;
		if (this.screen != null) {
			this.screen.show();
			this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
		return oldScreen;
	}

	/** @return the currently active {@link Screen}. */
	public Screen getScreen () {
		return screen;
	}
}
