
package org.tntstudio.interfaces;

import org.tntstudio.Const.ScreenState;
import org.tntstudio.core.Screen;

/** Listener to {@link Screen} state
 * @author trungnt13 */
public interface ScreenStateListener {
	public void onStateChanged (Screen screen, ScreenState currentState, ScreenState lastState);
}
