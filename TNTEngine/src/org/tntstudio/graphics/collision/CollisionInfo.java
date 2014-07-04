
package org.tntstudio.graphics.collision;

import org.tntstudio.graphics.objs.Sprite;
import org.tntstudio.graphics.objs.SpriteManager;
import org.tntstudio.utils.AdvPool;

import com.badlogic.gdx.utils.Array;

public final class CollisionInfo {
	// ///////////////////////////////////////////////////////////////
	// pooling
	// ///////////////////////////////////////////////////////////////

	private static final AdvPool<CollisionInfo> ADV_COLLISION_POOL = new AdvPool<CollisionInfo>() {

		@Override
		protected CollisionInfo newObject (Object... objects) {
			return new CollisionInfo();
		}

		@Override
		protected CollisionInfo newObject () {
			return new CollisionInfo();
		}
	};

	public static CollisionInfo newCollisionInfo () {
		return ADV_COLLISION_POOL.obtain();
	}

	public static CollisionInfo newCollisionInfo (CollisionInfo info) {
		return ADV_COLLISION_POOL.obtain().set(info);
	}

	public static void ClearOldData () {
		ADV_COLLISION_POOL.clear();
	}

	// ///////////////////////////////////////////////////////////////
	// local
	// ///////////////////////////////////////////////////////////////

	public SpriteManager Manager1;
	public Sprite Sprite1;
	public final Array<Integer> Index1 = new Array<Integer>();

	public SpriteManager Manager2;
	public Sprite Sprite2;
	public final Array<Integer> Index2 = new Array<Integer>();

	private CollisionInfo () {
	}

	public CollisionInfo set (CollisionInfo info) {
		Sprite1 = info.Sprite1;
		Sprite2 = info.Sprite2;

		Index1.clear();
		Index1.addAll(info.Index1);

		Index1.clear();
		Index1.addAll(info.Index1);
		return this;
	}
}
