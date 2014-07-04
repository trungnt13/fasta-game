
package org.tntstudio.graphics.collision;

import java.util.ArrayDeque;
import java.util.ArrayList;

import org.tntstudio.graphics.objs.Sprite;
import org.tntstudio.graphics.objs.SpriteManager;
import org.tntstudio.utils.AsyncWork;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Timer;

/** Simplest collision detection algorithm */
public class NormalCollisionEngine implements CollisionEngine {
	// ///////////////////////////////////////////////////////////////
	// basic params
	// ///////////////////////////////////////////////////////////////

	private boolean isMultiThread = true;
	private float minimumDepthAmount = 0;
	private boolean isInForwardMode = false;

	public void setMultiThread (boolean isMultiThread) {
		this.isMultiThread = isMultiThread;
	}

	@Override
	public void setCollisionSensity (float sensity) {
		minimumDepthAmount = sensity;
	}

	@Override
	public void setForwardMode (boolean isForward) {
		isInForwardMode = isForward;
	}

	private final void processMulti (SpriteManager m1, SpriteManager m2, Array<Sprite> list1, Array<Sprite> list2,
		CollisionInfo info, CollisionListener listener) {
		/*-------- prepare --------*/

		list1.clear();
		list2.clear();

		m1.gatherAllSprite(list1);
		m2.gatherAllSprite(list2);

		info.Manager1 = m1;
		info.Manager2 = m2;

		/*-------- process --------*/

		boolean isCollided = false;
		// traverse list1
		for (Sprite s1 : list1) {
			// traverse list2
			for (Sprite s2 : list2) {
				isCollided = checkCollision(s1, s2, info);
				// collision happed
				if (isCollided) {
					mDirectlyInfo.Sprite1 = s1;
					mDirectlyInfo.Sprite2 = s2;
					listener.collided(info);
				}
			}
		}
	}

	private final void processSingle (SpriteManager m1, Array<Sprite> list, CollisionInfo info, CollisionListener listener) {
		/*-------- prepare --------*/
		list.clear();

		m1.gatherAllSprite(list);

		info.Manager1 = m1;
		info.Manager2 = m1;

		/*-------- process --------*/

		boolean isCollided = false;
		final int size = list.size;
		// from 0 to the end
		for (int i = 0; i < size; i++) {
			final Sprite s1 = list.get(i);
			// from 0 to the end
			for (int j = i + 1; j < size; j++) {
				final Sprite s2 = list.get(j);
				isCollided = checkCollision(s1, s2, info);
				// collision happedn
				if (isCollided) {
					info.Sprite1 = s1;
					info.Sprite2 = s2;
					listener.collided(info);
				}
			}
		}
	}

	private final boolean checkCollision (Sprite s1, Sprite s2, CollisionInfo info) {
		final MinimumTranslationVector mDepth = new MinimumTranslationVector();
		boolean isOverlap = false;
		boolean isCollided = false;

		info.Index1.clear();
		info.Index2.clear();

		final Array<FloatArray> bound1 = s1.getBounding();
		final Array<FloatArray> bound2 = s2.getBounding();

		for (int i = 0; i < bound1.size; i++) {
			final float[] b1 = bound1.get(i).items;
			for (int j = 0; j < bound2.size; j++) {
				final float[] b2 = bound2.get(j).items;
				isOverlap = Intersector.overlapConvexPolygons(b1, 0, b1.length, b2, 0, b2.length, mDepth);
				// collision happened
				if (isOverlap && mDepth.depth > minimumDepthAmount) {
					info.Index1.add(i);
					info.Index2.add(j);
					isCollided = true;
					if (!isInForwardMode) return true;
				}
			}
		}
		return isCollided;
	}

	// ///////////////////////////////////////////////////////////////
	// directly methods
	// ///////////////////////////////////////////////////////////////

	private final Array<Sprite> mDirectlySpriteList1 = new Array<Sprite>();
	private final Array<Sprite> mDirectlySpriteList2 = new Array<Sprite>();
	private final ArrayDeque<Object> mCachedCollision = new ArrayDeque<Object>();
	private final CollisionInfo mDirectlyInfo = CollisionInfo.newCollisionInfo();
	private boolean isProcessing = false;
	public Runnable collisionProcessor = new Runnable() {
		@Override
		public void run () {
			while (mCachedCollision.size() > 0) {
				int paramCount = (Integer)mCloneQueuedCollision.removeFirst();
				if (paramCount == 2) {
					SpriteManager m1 = (SpriteManager)mCachedCollision.removeFirst();
					SpriteManager m2 = (SpriteManager)mCachedCollision.removeFirst();
					CollisionListener listener = (CollisionListener)mCachedCollision.removeFirst();
					processMulti(m1, m2, mDirectlySpriteList1, mDirectlySpriteList2, mDirectlyInfo, listener);
				} else if (paramCount == 1) {
					SpriteManager m1 = (SpriteManager)mCachedCollision.removeFirst();
					CollisionListener listener = (CollisionListener)mCachedCollision.removeFirst();
					processSingle(m1, mDirectlySpriteList1, mDirectlyInfo, listener);
				}
			}
			isProcessing = false;
		}
	};

	@Override
	public void processCollision (SpriteManager m1, SpriteManager m2, CollisionListener listener) {
		// if the last proces not done yet
		mCachedCollision.add(2);
		mCachedCollision.add(m1);
		mCachedCollision.add(m2);
		mCachedCollision.add(listener);

		if (!isProcessing) {
			isProcessing = true;
			if (isMultiThread)
				AsyncWork.execute(collisionProcessor);
			else
				collisionProcessor.run();
		}
	}

	@Override
	public void processCollision (SpriteManager m1, CollisionListener listener) {
		// if the last proces not done yet
		mCachedCollision.add(1);
		mCachedCollision.add(m1);
		mCachedCollision.add(listener);

		if (!isProcessing) {
			isProcessing = true;
			if (isMultiThread)
				AsyncWork.execute(collisionProcessor);
			else
				collisionProcessor.run();
		}
	}

	// ///////////////////////////////////////////////////////////////
	// queue methods
	// ///////////////////////////////////////////////////////////////
	private final ArrayList<Object> mQueuedCollision = new ArrayList<Object>();
	private final ArrayDeque<Object> mCloneQueuedCollision = new ArrayDeque<Object>();

	private Timer.Task mLastCollisionTask;
	private Runnable mQueueCollisionProcessing = new Runnable() {
		private final Array<Sprite> mSpriteList1 = new Array<Sprite>();
		private final Array<Sprite> mSpriteList2 = new Array<Sprite>();
		private final CollisionInfo mInfo = CollisionInfo.newCollisionInfo();

		@Override
		public void run () {
			while (mCloneQueuedCollision.size() > 0) {
				int paramCount = (Integer)mCloneQueuedCollision.removeFirst();
				if (paramCount == 1) {
					SpriteManager m = (SpriteManager)mCloneQueuedCollision.removeFirst();
					CollisionListener l = (CollisionListener)mCloneQueuedCollision.removeFirst();
					processSingle(m, mSpriteList1, mInfo, l);
				} else if (paramCount == 2) {
					SpriteManager m1 = (SpriteManager)mCloneQueuedCollision.removeFirst();
					SpriteManager m2 = (SpriteManager)mCloneQueuedCollision.removeFirst();
					CollisionListener l = (CollisionListener)mCloneQueuedCollision.removeFirst();
					processMulti(m1, m2, mSpriteList1, mSpriteList2, mInfo, l);
				}
			}
		}
	};

	@Override
	public CollisionEngine resetCollision () {
		if (mLastCollisionTask != null) mLastCollisionTask.cancel();

		mQueuedCollision.clear();
		return null;
	}

	@Override
	public CollisionEngine queueCollision (SpriteManager n, CollisionListener listener) {
		if (mLastCollisionTask != null) mLastCollisionTask.cancel();

		mQueuedCollision.add(1);
		mQueuedCollision.add(n);
		mQueuedCollision.add(listener);
		return null;
	}

	@Override
	public CollisionEngine queueCollision (SpriteManager n1, SpriteManager n2, CollisionListener listener) {
		if (mLastCollisionTask != null) mLastCollisionTask.cancel();

		mQueuedCollision.add(2);
		mQueuedCollision.add(n1);
		mQueuedCollision.add(n2);
		mQueuedCollision.add(listener);
		return null;
	}

	@Override
	public void start (float delay) {
		mCloneQueuedCollision.clear();
		mCloneQueuedCollision.add(mQueuedCollision);

		if (mLastCollisionTask != null) mLastCollisionTask.cancel();
		mLastCollisionTask = new Timer.Task() {
			@Override
			public void run () {
				if (isMultiThread)
					AsyncWork.execute(mQueueCollisionProcessing);
				else
					mQueueCollisionProcessing.run();
			}
		};
		Timer.schedule(mLastCollisionTask, delay);
	}

	@Override
	public void start (float delay, float interval) {
		mCloneQueuedCollision.clear();
		mCloneQueuedCollision.add(mQueuedCollision);

		if (mLastCollisionTask != null) mLastCollisionTask.cancel();
		mLastCollisionTask = new Timer.Task() {
			@Override
			public void run () {
				if (isMultiThread)
					AsyncWork.execute(mQueueCollisionProcessing);
				else
					mQueueCollisionProcessing.run();
			}
		};
		Timer.schedule(mLastCollisionTask, delay, interval);
	}

	@Override
	public void start (float delay, float interval, int repeatCount) {
		mCloneQueuedCollision.clear();
		mCloneQueuedCollision.add(mQueuedCollision);

		if (mLastCollisionTask != null) mLastCollisionTask.cancel();
		mLastCollisionTask = new Timer.Task() {
			@Override
			public void run () {
				if (isMultiThread)
					AsyncWork.execute(mQueueCollisionProcessing);
				else
					mQueueCollisionProcessing.run();
			}
		};
		Timer.schedule(mLastCollisionTask, delay, interval, repeatCount);
	}

	@Override
	public void startDuration (float delay, float interval, float duration) {
		mCloneQueuedCollision.clear();
		mCloneQueuedCollision.add(mQueuedCollision);

		if (mLastCollisionTask != null) mLastCollisionTask.cancel();
		mLastCollisionTask = new Timer.Task() {
			@Override
			public void run () {
				if (isMultiThread)
					AsyncWork.execute(mQueueCollisionProcessing);
				else
					mQueueCollisionProcessing.run();
			}
		};
		Timer.schedule(mLastCollisionTask, delay, interval, (int)(duration / interval));
	}
}
