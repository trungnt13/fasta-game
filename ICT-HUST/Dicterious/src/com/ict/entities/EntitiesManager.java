
package com.ict.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.badlogic.gdx.graphics.g2d.Batch;

public class EntitiesManager extends Entity implements Entity.PriorityChangeListener {

	final ArrayList<Entity> mEntitiesList = new ArrayList<Entity>();
	private final ArrayList<Entity> mTmpList = new ArrayList<Entity>();
	
	private static final Comparator<Entity> EntityComparator = new Comparator<Entity>() {
		@Override
		public int compare (Entity o1, Entity o2) {
			if (o1.getPriority() > o2.getPriority()) return 1;
			if (o1.getPriority() < o2.getPriority()) return -1;
			return -1;
		}
	};

	// ///////////////////////////////////////////////////////////////
	// manager methods
	// ///////////////////////////////////////////////////////////////

	public void add (Entity entity) {
		mEntitiesList.add(entity);
		entity.setPriorityListener(this);
	}

	public void remove (Entity entity) {
		mEntitiesList.remove(entity);
		entity.setPriorityListener(null);
	}

	public void clear () {
		mEntitiesList.clear();
	}

	public Entity findEntityByName (String name) {
		for (Entity entity : mEntitiesList) {
			if (entity.getName().equals(name)) return entity;
		}
		return null;
	}

	private void sortByPriority () {
		Collections.sort(mEntitiesList, EntityComparator);
	}

	// ///////////////////////////////////////////////////////////////
	// entity methods
	// ///////////////////////////////////////////////////////////////


	@Override
	public void show (float viewWidth, float viewHeight) {
		sortByPriority();
		for (Entity entity : mEntitiesList) {
			entity.show(viewWidth, viewHeight);
		}
	}
	
	@Override
	public void render (Batch batch) {
		mTmpList.clear();
		mTmpList.addAll(mEntitiesList);
		for (Entity entity : mTmpList) {
			entity.render(batch);
		}
	}

	@Override
	public void update (float delta) {
		mTmpList.clear();
		mTmpList.addAll(mEntitiesList);
		for (Entity entity : mTmpList) {
			entity.update(delta);
		}
	}

	@Override
	public void priorityChanged (Entity entity) {
		sortByPriority();
	}

	@Override
	public void postEvent (Object...objects) {

	}
}
