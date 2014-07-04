
package org.tntstudio.interfaces;

import com.badlogic.gdx.utils.Disposable;

public interface LifecycleListener extends Disposable {
	public void resume ();

	public void pause ();
}
