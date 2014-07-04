
package org.tntstudio.graphics.objs;

import org.tntstudio.Const.SpriteState;
import org.tntstudio.Const.UpdaterExecuteMode;
import org.tntstudio.interfaces.SpriteStateListener;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;

public abstract class SpriteBase implements Sprite {
	private SpriteState mLastState;
	private SpriteState mSpriteState;

	private UpdaterExecuteMode mUpdaterExecuteMode;

	// ---------------------------------------------------------
	private final Array<Updater> mUpdaters = new Array<Updater>(true, 3);

	private boolean isVisible = true;
	private boolean isUpdateable = true;

	// ///////////////////////////////////////////////////////////////
	// getter setter
	// ///////////////////////////////////////////////////////////////

	public SpriteState getState () {
		return mSpriteState;
	}

	public SpriteState getLastState () {
		return mLastState;
	}

	protected void active () {
		setSpriteSate(SpriteState.Activated);
	}

	private final void setSpriteSate (SpriteState state) {
		mLastState = mSpriteState;
		mSpriteState = state;

		for (SpriteStateListener listener : mStateListener)
			listener.stateChanged(this, mSpriteState, mLastState);
	}

	// ///////////////////////////////////////////////////////////////
	// processor
	// ///////////////////////////////////////////////////////////////
	public void postUpdater (Updater... ups) {
		for (Updater updater : ups) {
			if (mUpdaters.contains(updater, false) || updater == null) continue;

			updater.start();
			this.mUpdaters.add(updater);
		}
	}

	@Override
	public void setUpdaterExecuteMode (UpdaterExecuteMode mode) {
		mUpdaterExecuteMode = mode;
	}

	@Override
	public UpdaterExecuteMode getUpdaterExecuteMode () {
		return mUpdaterExecuteMode;
	}

	public int sizeUpdater () {
		return mUpdaters.size;
	}

	@Override
	public void removeUpdater (Updater... ups) {
		for (Updater u : ups) {
			mUpdaters.removeValue(u, true);
		}
	}

	public void clearUpdater () {
		this.mUpdaters.clear();
	}

	@Override
	public final void update (float delta) {
		if (mSpriteState != SpriteState.Activated || !isUpdateable) return;

		for (int i = 0, n = mUpdaters.size; i < n; i++) {
			final Updater tmp = mUpdaters.get(i);
			// if not stop update now
			if (!tmp.isStoped()) {
				// if update return true, remove it completely
				if (tmp.updateInternal(this, delta)) {
					mUpdaters.removeValue(tmp, true);
					--i;
					--n;
				} else if (mUpdaterExecuteMode == UpdaterExecuteMode.Sequence) break;
			}
		}

		updateInternal(delta);
	}

	protected void updateInternal (float delta) {
	}

	@Override
	public final void draw (Batch batch) {
		if (mSpriteState != SpriteState.Activated || !isVisible) return;
		drawInternal(batch);
	}

	protected void drawInternal (Batch batch) {
	}

	/*-------- state control --------*/
	@Override
	public void setVisible (boolean isvisible) {
		isVisible = isvisible;
	}

	@Override
	public void setUpdateable (boolean isupdate) {
		isUpdateable = isupdate;
	}

	@Override
	public boolean isVisible () {
		return isVisible;
	}

	@Override
	public boolean isUpdateable () {
		return isUpdateable;
	}

	// ///////////////////////////////////////////////////////////////
	// state controller
	// ///////////////////////////////////////////////////////////////

	@Override
	public final void reset () {
		if (mSpriteState == SpriteState.Reseted) return;
		if (mSpriteState != SpriteState.Disposed) setSpriteSate(SpriteState.Reseted);
		mStateListener.clear();

		resetInternal();
	}

	protected void resetInternal () {
	}

	@Override
	public final void dispose () {
		if (mSpriteState == SpriteState.Disposed) return;
		setSpriteSate(SpriteState.Disposed);
		reset();

		disposeInternal();
	}

	protected void disposeInternal () {
	}

	// ///////////////////////////////////////////////////////////////
	// state manager
	// ///////////////////////////////////////////////////////////////
	private final Array<SpriteStateListener> mStateListener = new Array<SpriteStateListener>();

	@Override
	public final void registerStateListener (SpriteStateListener... listeners) {
		for (SpriteStateListener l : listeners) {
			if (!mStateListener.contains(l, true)) mStateListener.add(l);
		}
	}

	@Override
	public final void unregisterStateListener (SpriteStateListener... listeners) {
		for (SpriteStateListener l : listeners) {
			mStateListener.removeValue(l, true);
		}
	}

	// ///////////////////////////////////////////////////////////////
	// flip methods
	// ///////////////////////////////////////////////////////////////
	private int isFlipX = 1;
	private int isFlipY = 1;

	@Override
	public void flip (boolean flipX, boolean flipY) {
		if (flipX) isFlipX *= -1;
		if (flipY) isFlipY *= -1;
	}

	public final void setFlip (boolean x, boolean y) {
		boolean performX = false;
		boolean performY = false;
		if (isFlipX() != x) {
			performX = true;
		}
		if (isFlipY() != y) {
			performY = true;
		}
		flip(performX, performY);
	}

	@Override
	public final boolean isFlipX () {
		return isFlipX == -1;
	}

	@Override
	public final boolean isFlipY () {
		return isFlipY == -1;
	}
}
