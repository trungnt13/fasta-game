
package com.ict.entities;

import com.badlogic.gdx.graphics.g2d.Batch;

public abstract class Entity {
	// ///////////////////////////////////////////////////////////////
	// params and getter setter
	// ///////////////////////////////////////////////////////////////
	private int mPriority;
	private String mName;
	private PriorityListener mListener;

	public String getName () {
		return mName;
	}

	public void setName (String name) {
		mName = name;
	}

	public int getPriority () {
		return mPriority;
	}

	public void setPriority (int priority) {
		mPriority = priority;
		if (mListener != null) mListener.priorityChanged(this);
	}

	public void setPriorityListener (PriorityListener listener) {
		mListener = listener;
	}

	// ///////////////////////////////////////////////////////////////
	// abstract methods
	// ///////////////////////////////////////////////////////////////

	public abstract void show ();

	public abstract void update (float delta);

	public abstract void render (Batch batch);

	public abstract void postEvent (Object... params);

	// ///////////////////////////////////////////////////////////////
	// helper datatype
	// ///////////////////////////////////////////////////////////////

	public static interface PriorityListener {
		public void priorityChanged (Entity entity);
	}
}
