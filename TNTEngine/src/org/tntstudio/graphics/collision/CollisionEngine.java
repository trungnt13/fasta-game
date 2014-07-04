
package org.tntstudio.graphics.collision;

import org.tntstudio.graphics.objs.SpriteManager;

/** Interface to process collision between sprite
 * @author trungnt13 */
public interface CollisionEngine {
	public void setMultiThread (boolean isMultiThread);

	public void setCollisionSensity (float sensity);

	/** Normal engine will stop when it detects a <b>single</b> collision between two sprites, but if you set <b>Forawrd Mode</b> to
	 * true, it wont stop until it traverses to the end of bounding list */
	public void setForwardMode (boolean isForward);

	public void processCollision (SpriteManager m1, SpriteManager m2, CollisionListener listener);

	public void processCollision (SpriteManager m1, CollisionListener listener);

	public CollisionEngine resetCollision ();

	public CollisionEngine queueCollision (SpriteManager n, CollisionListener listener);

	public CollisionEngine queueCollision (SpriteManager n1, SpriteManager n2, CollisionListener listener);

	public void start (float delay);

	public void start (float delay, float interval);

	public void start (float delay, float interval, int repeatCount);

	public void startDuration (float delay, float interval, float duration);
}
