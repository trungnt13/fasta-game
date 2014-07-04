
package org.tntstudio.graphics.objs;

import org.tntstudio.Const;
import org.tntstudio.Const.SpriteState;
import org.tntstudio.TNTRuntimeException;
import org.tntstudio.core.Top;
import org.tntstudio.graphics.collision.CollisionEngine;
import org.tntstudio.graphics.collision.CollisionInfo;
import org.tntstudio.graphics.collision.CollisionListener;
import org.tntstudio.interfaces.LifeCycleAdapter;
import org.tntstudio.interfaces.LifecycleListener;
import org.tntstudio.utils.math.PolygonList;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;

/** Manage many sprite manager
 * @author trungnt13 */
@SuppressWarnings("unchecked")
public class SpriteWorld extends SpriteManager implements LifecycleListener, CollisionEngine {
	// ///////////////////////////////////////////////////////////////
	// singleton methods
	// ///////////////////////////////////////////////////////////////
	private static ObjectMap<String, SpriteWorld> mSpriteWorlds = new ObjectMap<String, SpriteWorld>();
	private static boolean isBindedSpriteLifeCycleListener = false;

	public static final SpriteWorld newWorld (String name) {
		// bind lifecycle listener for sprite to reset game when exist
		if (Top.tgame != null && !isBindedSpriteLifeCycleListener) {
			Top.tgame.registerLifecycleListener(SpriteLifeCycleListener);
			isBindedSpriteLifeCycleListener = true;
		}

		// create new world
		SpriteWorld world = mSpriteWorlds.get(name);
		if (world == null) {
			world = new SpriteWorld();
			world.bind(name);
			mSpriteWorlds.put(name, world);
		}
		return world;
	}

	public static final SpriteWorld newWorld (String name, Class<? extends SpriteWorld> type) {
		// bind lifecycle listener for sprite to reset game when exist
		if (Top.tgame != null && !isBindedSpriteLifeCycleListener) {
			Top.tgame.registerLifecycleListener(SpriteLifeCycleListener);
			isBindedSpriteLifeCycleListener = true;
		}

		// create new world
		SpriteWorld world = mSpriteWorlds.get(name);
		try {
			if (world == null) {
				world = type.newInstance();
				world.bind(name);
				mSpriteWorlds.put(name, world);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return world;
	}

	private static final LifeCycleAdapter SpriteLifeCycleListener = new LifeCycleAdapter() {
		@Override
		public void dispose () {
			CollisionInfo.ClearOldData();
			SpriteParticle.ClearOldData();
			SpriteManager.ClearOldData();

			final Values<PolygonList> list = mSpriteBound.values();
			for (PolygonList l : list) {
				l.dispose();
			}
			mSpriteBound.clear();

			final Values<SpriteWorld> listWorld = mSpriteWorlds.values();
			for (SpriteWorld s : listWorld) {
				s.dispose();
			}
			mSpriteWorlds.clear();
			isBindedSpriteLifeCycleListener = false;
		}
	};

	@Override
	public void resume () {
	}

	@Override
	public void pause () {
	}

	@Override
	protected void disposeInternal () {
		super.dispose();
		mCollisionProcessMap.clear();
		Top.tgame.unregisterLifecycleListener(this);
	}

	// ///////////////////////////////////////////////////////////////
	// polygon manage
	// ///////////////////////////////////////////////////////////////
	private static final ObjectMap<String, PolygonList> mSpriteBound = new ObjectMap<String, PolygonList>();

	public static final PolygonList getPolygonForSprite (String spritename, float width, float height) {
		PolygonList pol = mSpriteBound.get(spritename);
		if (pol == null) {
			pol = new PolygonList();
			mSpriteBound.put(spritename, pol);
		}
		float[] vertices = new float[] {0, 0, width, 0, width, height, 0, height};
		pol.add(vertices);
		return pol;
	}

	public static final PolygonList putPolygonForSprite (String spritename, float[] vertices, float width, float height) {
		PolygonList pol = mSpriteBound.get(spritename);
		if (pol == null) {
			pol = new PolygonList();
			mSpriteBound.put(spritename, pol);
		}
		pol.add(vertices, width, height);
		return pol;
	}

	public static final PolygonList putPolygonForSprite (String spritename, float[] vertices) {
		PolygonList pol = mSpriteBound.get(spritename);
		if (pol == null) {
			pol = new PolygonList();
			mSpriteBound.put(spritename, pol);
		}
		pol.add(vertices);
		return pol;
	}

	// ///////////////////////////////////////////////////////////////
	// manager
	// ///////////////////////////////////////////////////////////////
	private final SpriteManager mMainManager;
	private final ObjectMap<String, SpriteManager> mManagerMap = new ObjectMap<String, SpriteManager>();

	/*-------- constructor --------*/
	protected SpriteWorld () {
		mMainManager = new SpriteManager();
		mMainManager.bind(getClass() + "@" + Integer.toHexString(hashCode()));
		addSingle(mMainManager);
		Top.tgame.registerLifecycleListener(this);
	}

	/*-------- manager --------*/
	@Override
	public <T extends Sprite> T newSprite (Class<T> spriteType) {
		if (SpriteWorld.class.isAssignableFrom(spriteType))
			throw new TNTRuntimeException("You can NOT create new SpriteWorld by this way");

		return mMainManager.newSprite(spriteType);
	}

	public <T extends SpriteManager> T newManager (String name, Class<T> type) {
		// check if exist sprite
		T manager = (T)mManagerMap.get(name);
		if (manager != null) return manager;

		// init sprite manager
		T sprite = obtainNewSprite(type);
		((SpriteManager)sprite).bind(name);

		// add to this world
		sprite.registerStateListener(this);
		addSingle(sprite);
		mManagerMap.put(name, sprite);

		return sprite;
	}

	public SpriteWorld removeManager (String name) {
		final SpriteManager manager = mManagerMap.get(name);
		if (manager == null) return this;
		removeManager(manager);
		return this;
	}

	private final void removeManager (SpriteManager manager) {
		manager.reset();
	}

	public SpriteManager findManager (String managerName) {

		return mManagerMap.get(managerName);
	}

	@Override
	public SpriteManager remove (Sprite... sprites) {
		for (Sprite s : sprites) {
			if (s instanceof SpriteManager) {
				removeManager((SpriteManager)s);
			} else {
				mMainManager.remove(s);
			}
		}
		return this;
	}

	@Override
	public void stateChanged (Sprite sprite, SpriteState currentState, SpriteState lastState) {
		// if sprite is SpriteManager and State != Activated
		if (SpriteManager.class.isAssignableFrom(sprite.getClass())) {
			if (currentState != SpriteState.Activated) mManagerMap.remove(((SpriteManager)sprite).Name());
		}
		super.stateChanged(sprite, currentState, lastState);
	}

	// ///////////////////////////////////////////////////////////////
	// Collision
	// ///////////////////////////////////////////////////////////////
	private final ObjectMap<String, Array<CollisionListener>> mCollisionProcessMap = new ObjectMap<String, Array<CollisionListener>>();
	private CollisionEngine mCollisionEngine;

	public final void addCollisionListener (SpriteManager m1, SpriteManager m2, CollisionListener... listeners) {
		// now return CollisionListener
		String key = getCollisionKey(m1, m2);
		Array<CollisionListener> list = mCollisionProcessMap.get(key);
		if (list == null) {
			list = new Array<CollisionListener>();
			mCollisionProcessMap.put(key, list);
		}
		for (CollisionListener cl : listeners) {
			if (!list.contains(cl, true)) continue;
			list.add(cl);
		}
	}

	public final void removeCollisionListener (SpriteManager m1, SpriteManager m2, CollisionListener... listeners) {
		String key = getCollisionKey(m1, m2);
		Array<CollisionListener> list = mCollisionProcessMap.get(key);
		if (list == null) return;

		for (CollisionListener cl : listeners) {
			list.removeValue(cl, true);
		}
	}

	/** Collision name is merge Name from <b>m1</b> and <b>m2</b> after sorting them by alphabet order */
	private final String getCollisionKey (SpriteManager m1, SpriteManager m2) {
		// get key name from two SpriteManager
		int compareResult = Const.Compare.ALPHABETICAL_ORDER.compare(m1.Name(), m2.Name());
		String first = null;
		String second = null;
		// first is higher
		if (compareResult > 0) {
			first = m1.Name();
			second = m2.Name();
		} else if (compareResult < 0) {
			first = m2.Name();
			second = m1.Name();
		} else {
			first = second = m1.Name();
		}
		return first + second;
	}

	/*-------- collision engine --------*/
	public void setCollisionEngine (CollisionEngine engine) {
		mCollisionEngine = engine;
	}

	@Override
	public void setMultiThread (boolean isMultiThread) {
		if (mCollisionEngine != null) mCollisionEngine.setMultiThread(isMultiThread);
	}

	@Override
	public void setCollisionSensity (float sensity) {
		if (mCollisionEngine != null) mCollisionEngine.setCollisionSensity(sensity);
	}

	@Override
	public void setForwardMode (boolean isForward) {
		if (mCollisionEngine != null) mCollisionEngine.setForwardMode(isForward);
	}

	@Override
	public void processCollision (SpriteManager m1, SpriteManager m2, CollisionListener listener) {
		if (mCollisionEngine != null) mCollisionEngine.processCollision(m1, m2, listener);
	}

	@Override
	public void processCollision (SpriteManager m1, CollisionListener listener) {
		if (mCollisionEngine != null) mCollisionEngine.processCollision(m1, listener);
	}

	public void processCollision (String m, CollisionListener listener) {
		SpriteManager m1 = findManager(m);
		if (m1 == null) return;
		if (mCollisionEngine != null) mCollisionEngine.processCollision(m1, listener);
	}

	public void processCollision (String n1, String n2, CollisionListener listener) {
		SpriteManager m1 = findManager(n1);
		SpriteManager m2 = findManager(n2);
		if (m1 == null || m2 == null) return;
		if (mCollisionEngine != null) mCollisionEngine.processCollision(m1, listener);
	}

	public CollisionEngine resetCollision () {
		if (mCollisionEngine != null) mCollisionEngine.resetCollision();
		return this;
	}

	public CollisionEngine queueCollision (String n, CollisionListener listener) {
		if (mCollisionEngine != null) mCollisionEngine.queueCollision(findManager(n), listener);
		return this;
	}

	public CollisionEngine queueCollision (String n1, String n2, CollisionListener listener) {
		if (mCollisionEngine != null) mCollisionEngine.queueCollision(findManager(n1), findManager(n2), listener);
		return this;
	}

	public void start (float delay) {
		if (mCollisionEngine != null) mCollisionEngine.start(delay);
	}

	public void start (float delay, float interval) {
		if (mCollisionEngine != null) mCollisionEngine.start(delay, interval);
	}

	public void start (float delay, float interval, int repeatCount) {
		if (mCollisionEngine != null) mCollisionEngine.start(delay, interval, repeatCount);
	}

	public void startDuration (float delay, float interval, float duration) {
		if (mCollisionEngine != null) mCollisionEngine.startDuration(delay, interval, duration);
	}

	@Override
	public CollisionEngine queueCollision (SpriteManager n, CollisionListener listener) {
		if (mCollisionEngine != null) mCollisionEngine.queueCollision(n, listener);
		return this;
	}

	@Override
	public CollisionEngine queueCollision (SpriteManager n1, SpriteManager n2, CollisionListener listener) {
		if (mCollisionEngine != null) mCollisionEngine.queueCollision(n1, n2, listener);
		return this;
	}
}
