
package org.tntstudio.resources;

import com.badlogic.gdx.utils.Disposable;

/** Support <b>SAFE</b> interface for loading and unloading resource
 * @author trungnt13 */
public abstract class Loadable implements Disposable {
	private boolean isCallLoaded = false;

	public boolean isActivated () {
		return isCallLoaded;
	}

	/** This function only shuold be called once before unload() is called */
	void load () {
		if (isCallLoaded) return;
		isCallLoaded = true;

		loadInternal();
	}

	protected abstract void loadInternal ();

	/** This function only shuold be called once before load() is called */
	void unload () {
		if (!isCallLoaded) return;
		isCallLoaded = false;

		unloadInternal();
	}

	protected abstract void unloadInternal ();

	public abstract boolean isLoaded ();
}
