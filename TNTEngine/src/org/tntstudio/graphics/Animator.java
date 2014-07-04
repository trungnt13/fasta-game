
package org.tntstudio.graphics;

/** @author trungnt13 */
public interface Animator {
	/*-------- state control --------*/

	public Animator setPlaybackSpeed (float playback);

	public Animator setLoop (boolean loop);

	public Animator start (float statetime, boolean reversed);

	public Animator start (float statetime);

	public Animator start ();

	public Animator stop ();

	public Animator pause ();

	/*-------- updater --------*/

	public void update (float delta);

	/*-------- getter --------*/

	public boolean isRunning ();

	public boolean isLooping ();

	public boolean isReversed ();

	public float getPlaybackSpeed ();

	public float getStateTime ();

	public float getTotalDuration ();
}
