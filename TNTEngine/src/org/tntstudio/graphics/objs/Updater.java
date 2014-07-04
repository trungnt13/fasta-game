
package org.tntstudio.graphics.objs;

import org.tntstudio.core.Top;
import org.tntstudio.interfaces.Updating;

import com.badlogic.gdx.utils.Pool.Poolable;

/** @author trungnt13 */
public class Updater implements Poolable, Updating {

	public static final Updater instance = new Updater() {
		@Override
		public boolean update (Sprite sprite, float delta) {
			if (sprite == null) return true;
			return false;
		}
	};

	// ///////////////////////////////////////////////////////////////
	// local code
	// ///////////////////////////////////////////////////////////////

	Updating mUpdating;
	private boolean isStopped = false;
	private boolean isLuaSetted = false;

	public Updater () {
	}

	public Updater setUpdating (Updating updating) {
		mUpdating = updating;
		isLuaSetted = false;
		return this;
	}

	public Updater setLuaUpdating (String luaProxyTable) {
		if (Top.lua != null) {
			mUpdating = Top.lua.getProxy(luaProxyTable, Updating.class);
			isLuaSetted = true;
		} 
		return this;
	}

	private Updater (Updater updater) {
		mUpdating = updater.mUpdating;
	}

	public void start () {
		isStopped = false;
	}

	public void stop () {
		isStopped = true;
	}

	public boolean isStoped () {
		return isStopped;
	}

	public final boolean updateInternal (Sprite core, float delta) {
		if (isStopped) return false;

		boolean isDead = false;
		isDead = update(core, delta);
		if (isDead) reset();
		return isDead;
	}

	@Override
	public boolean update (Sprite sprite, float delta) {
		if (mUpdating != null || isLuaSetted) return mUpdating.update(sprite, delta);
		return false;
	}

	public Updating getUpdate () {
		return mUpdating;
	}

	@Override
	public void reset () {
		isStopped = false;
	}

	@Override
	public String toString () {
		return this + " " + mUpdating;
	}
}
