
package org.tntstudio.graphics.objs;

import org.tntstudio.Utils;
import org.tntstudio.graphics.Animator;

/** This sprite can handle mult-animation at one position
 * @author trungnt13 */
public class SpriteMultiAnimation extends SpriteManager implements Animator {

	public void setVisibleSprite (SpriteAnimation... animations) {
		for (Sprite a : mSpriteList) {
			a.setVisible(false);
			a.setUpdateable(false);
		}

		for (SpriteAnimation s : animations) {
			if (!mSpriteList.contains(s, true)) continue;

			s.setVisible(true);
			s.setUpdateable(true);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Sprite> T newSprite (Class<T> spriteType) {
		if (Utils.isSuperAndChild(SpriteAnimation.class, spriteType)) {
			return super.newSprite(spriteType);
		}
		return (T)super.newSprite(SpriteAnimation.class);
	}

	// ///////////////////////////////////////////////////////////////
	// setter
	// ///////////////////////////////////////////////////////////////

	public Animator setPlaybackSpeed (float playback) {
		for (Sprite s : mSpriteList) {
			((SpriteAnimation)s).setPlaybackSpeed(playback);
		}
		return this;
	}

	@Override
	public Animator setLoop (boolean loop) {
		for (Sprite s : mSpriteList) {
			((SpriteAnimation)s).setLoop(loop);
		}
		return this;
	}

	@Override
	public Animator start (float statetime, boolean reversed) {
		for (Sprite s : mSpriteList) {
			((SpriteAnimation)s).start(statetime, reversed);
		}
		return this;
	}

	@Override
	public Animator start (float statetime) {
		for (Sprite s : mSpriteList) {
			((SpriteAnimation)s).start(statetime);
		}
		return this;
	}

	@Override
	public Animator start () {
		for (Sprite s : mSpriteList) {
			((SpriteAnimation)s).start();
		}
		return this;
	}

	@Override
	public Animator stop () {
		for (Sprite s : mSpriteList) {
			((SpriteAnimation)s).stop();
		}
		return this;
	}

	@Override
	public Animator pause () {
		for (Sprite s : mSpriteList) {
			((SpriteAnimation)s).pause();
		}
		return this;
	}

	@Override
	public boolean isRunning () {
		if (mSpriteList.size == 0) return false;

		final SpriteAnimation s = (SpriteAnimation)mSpriteList.get(0);
		if (s != null) return s.isRunning();
		return false;
	}

	@Override
	public boolean isLooping () {
		if (mSpriteList.size == 0) return false;

		final SpriteAnimation s = (SpriteAnimation)mSpriteList.get(0);
		if (s != null) return s.isLooping();
		return false;
	}

	@Override
	public boolean isReversed () {
		if (mSpriteList.size == 0) return false;

		final SpriteAnimation s = (SpriteAnimation)mSpriteList.get(0);
		if (s != null) return s.isReversed();
		return false;
	}

	@Override
	public float getPlaybackSpeed () {
		if (mSpriteList.size == 0) return 0;
		final SpriteAnimation s = (SpriteAnimation)mSpriteList.get(0);
		if (s != null) return s.getPlaybackSpeed();
		return 0;
	}

	@Override
	public float getStateTime () {
		if (mSpriteList.size == 0) return 0;
		final SpriteAnimation s = (SpriteAnimation)mSpriteList.get(0);
		if (s != null) return s.getStateTime();
		return 0;
	}

	@Override
	public float getTotalDuration () {
		if (mSpriteList.size == 0) return 0;
		final SpriteAnimation s = (SpriteAnimation)mSpriteList.get(0);
		if (s != null) return s.getTotalDuration();
		return 0;
	}
}
