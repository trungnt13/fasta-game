
package com.ict.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.badlogic.gdx.graphics.g2d.Batch;

public abstract class EntityManager extends Entity {
	// ///////////////////////////////////////////////////////////////
	// static
	// ///////////////////////////////////////////////////////////////

	public static Comparator<Entity> EntityComparator = new Comparator<Entity>() {

		@Override
		public int compare (Entity o1, Entity o2) {
			if (o1.getPriority() > o2.getPriority())
				return 1;
			else if (o1.getPriority() == o2.getPriority()) return 0;
			return -1;
		}
	};

	final ArrayList<Entity> mEntities = new ArrayList<Entity>();
	final ArrayList<Entity> mTemps = new ArrayList<Entity>();

	// ///////////////////////////////////////////////////////////////
	// main
	// ///////////////////////////////////////////////////////////////
	private final void sort () {
		Collections.sort(mEntities, EntityComparator);
	}

	public void add (Entity entity) {
		mEntities.add(entity);
		sort();
	}

	public void remove (Entity entity) {
		mEntities.remove(entity);
	}

	public void clear () {
		mEntities.clear();
	}

	public ArrayList<Entity> find (String name) {
		mTemps.clear();
		for (Entity e : mEntities) {
			if (e.getName().equals(name)) mTemps.add(e);
		}
		return mTemps;
	}

	protected ArrayList<Entity> safeClone () {
		mTemps.clear();
		mTemps.addAll(mEntities);
		return mTemps;
	}

	// ///////////////////////////////////////////////////////////////
	// override methods
	// ///////////////////////////////////////////////////////////////

	@Override
	public void show () {
		for (Entity e : mEntities) {
			e.show();
		}
		sort();
	}

	@Override
	public void update (float delta) {
		mTemps.clear();
		mTemps.addAll(mEntities);
		for (Entity e : mTemps) {
			e.update(delta);
		}
	}

	@Override
	public void render (Batch batch) {
		mTemps.clear();
		mTemps.addAll(mEntities);
		for (Entity e : mTemps) {
			e.render(batch);
		}
	}
}
