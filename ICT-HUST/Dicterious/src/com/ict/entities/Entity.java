
package com.ict.entities;

import com.badlogic.gdx.graphics.g2d.Batch;

public abstract class Entity {
	// ///////////////////////////////////////////////////////////////
	// abstract methods
	// ///////////////////////////////////////////////////////////////

	public abstract void show (float viewWidth, float viewHeight);

	public abstract void render (Batch batch);

	public abstract void update (float delta);

	public abstract void postEvent (String event);

	// ///////////////////////////////////////////////////////////////
	// Main stuff
	// ///////////////////////////////////////////////////////////////

	private int mPriority;
	private PriorityChangeListener mPriorityListener;
	private String mName;

	public final String getName () {
		return mName;
	}

	public final void setName (String mName) {
		this.mName = mName;
	}

	public final void setPriority (int priority) {
		mPriority = priority;
		if (mPriorityListener != null) mPriorityListener.priorityChanged(this);
	}

	public final int getPriority () {
		return mPriority;
	}

	public final void setPriorityListener (PriorityChangeListener priorityChangeListener) {
		mPriorityListener = priorityChangeListener;
	}

	public final PriorityChangeListener getPriorityListener () {
		return mPriorityListener;
	}

	// ///////////////////////////////////////////////////////////////
	// add-on control interface
	// ///////////////////////////////////////////////////////////////

	public static interface PriorityChangeListener {
		public void priorityChanged (Entity entity);
	}
}
