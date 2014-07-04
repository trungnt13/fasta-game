
package org.tntstudio.interfaces;

import org.tntstudio.Const.SpriteState;
import org.tntstudio.graphics.objs.Sprite;

/** This class for handling state changed event of a sprite */
public interface SpriteStateListener {
	public void stateChanged (Sprite sprite, SpriteState currentState, SpriteState lastState);
}
