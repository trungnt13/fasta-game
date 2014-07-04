
package org.tntstudio.graphics.objs;

import org.tntstudio.Const.SpriteState;
import org.tntstudio.TNTRuntimeException;
import org.tntstudio.Utils;
import org.tntstudio.interfaces.Identifiable;
import org.tntstudio.interfaces.SpriteStateListener;
import org.tntstudio.utils.AdvPool;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;

/** SPriteManager
 * @author trungnt13 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class SpriteManager extends SpriteBase implements SpriteStateListener, Identifiable {
	// ///////////////////////////////////////////////////////////////
	// pooling sprite
	// ///////////////////////////////////////////////////////////////

	static final ObjectMap<Class, AdvPool<? extends Sprite>> mSpritePoolMap = new ObjectMap<Class, AdvPool<? extends Sprite>>();

	protected static final <T extends Sprite> T obtainNewSprite (final Class<T> type) {
		AdvPool pool = mSpritePoolMap.get(type);
		// if null create new pool
		if (pool == null) {
			pool = new AdvPool<T>() {
				@Override
				protected T newObject () {
					try {
						return type.newInstance();
					} catch (InstantiationException e) {
					} catch (IllegalAccessException e) {
					}
					return null;
				}

				@Override
				protected T newObject (Object... objects) {
					return newObject();
				}
			};
			mSpritePoolMap.put(type, pool);
		}
		return (T)pool.obtain();
	}

	protected static final void freeSprite (Sprite sprite) {
		AdvPool pool = mSpritePoolMap.get(sprite.getClass());
		if (pool == null) {
			throw new TNTRuntimeException("No instance of this sprite exist, we cant free it");
		}
		pool.free(sprite);
	}

	static void ClearOldData () {
		final Values<AdvPool<? extends Sprite>> pools = mSpritePoolMap.values();
		for (AdvPool<? extends Sprite> p : pools)
			p.clear();
	}

	// ///////////////////////////////////////////////////////////////
	// manager params
	// ///////////////////////////////////////////////////////////////

	protected final Array<Sprite> mSpriteList = new Array<Sprite>(13);
	// ============= for safe traverse =============
	private final Array<Sprite> mCloneList = new Array<Sprite>(100);

	private final Array<FloatArray> mBoundList = new Array<FloatArray>();

	private String Name = null;

	/*-------- sprite params --------*/

	// ///////////////////////////////////////////////////////////////
	// main
	// ///////////////////////////////////////////////////////////////

	protected SpriteManager () {
		active();
	}

	protected SpriteManager bind (String name) {
		Name = name;
		return this;
	}

	public <T extends Sprite> T newSprite (Class<T> spriteType) {
		T sprite = obtainNewSprite(spriteType);
		sprite.registerStateListener(this);
		mSpriteList.add(sprite);
		return sprite;
	}

	/** Simply add sprite to list */
	final void addSingle (Sprite sprites) {
		mSpriteList.add(sprites);
	}

	public SpriteManager remove (Sprite... sprites) {
		for (Sprite s : sprites) {
			s.reset();
		}
		return this;
	}

	public void clearSpriteList () {
		mSpriteList.clear();
	}

	@Override
	public void stateChanged (Sprite sprite, SpriteState currentState, SpriteState lastState) {
		// disposed
		if (currentState == SpriteState.Disposed) {
			mSpriteList.removeValue(sprite, true);
			// check remove from pool
			AdvPool pool = mSpritePoolMap.get(sprite.getClass());
			pool.delete(sprite);
		}
		// reseted
		else if (currentState == SpriteState.Reseted) {
			mSpriteList.removeValue(sprite, true);
			freeSprite(sprite);
		}
	}

	public final int size () {
		return mSpriteList.size;
	}

	public final Array<Sprite> getSpriteList () {
		return mSpriteList;
	}

	/** collect all sprite from this manager except SpriteManager */
	public final void gatherAllSprite (Array<Sprite> list) {
		for (Sprite s : mSpriteList) {
			if (Utils.isSuperAndChild(SpriteManager.class, s.getClass())) {
				((SpriteManager)s).gatherAllSprite(list);
			} else
				list.add(s);
		}
	}

	private final void cloneList () {
		mCloneList.clear();
		mCloneList.addAll(mSpriteList);
	}

	// ///////////////////////////////////////////////////////////////
	// setter
	// ///////////////////////////////////////////////////////////////

	@Override
	public void setBounds (float x, float y, float width, float height) {
		cloneList();
		for (Sprite s : mCloneList) {
			s.setBounds(x, y, width, height);
		}
	}

	@Override
	public void setSize (float width, float height) {
		cloneList();
		for (Sprite s : mCloneList) {
			s.setSize(width, height);
		}
	}

	@Override
	public void setPosition (float x, float y) {
		cloneList();
		for (Sprite s : mCloneList) {
			s.setPosition(x, y);
		}
	}

	@Override
	public void setX (float x) {
		cloneList();
		for (Sprite s : mCloneList) {
			s.setX(x);
		}
	}

	@Override
	public void setY (float y) {
		cloneList();
		for (Sprite s : mCloneList) {
			s.setY(y);
		}
	}

	@Override
	public void translate (float xAmount, float yAmount) {
		cloneList();
		for (Sprite s : mCloneList) {
			s.translate(xAmount, yAmount);
		}
	}

	@Override
	public void translateX (float xAmount) {
		cloneList();
		for (Sprite s : mCloneList) {
			s.translateX(xAmount);
		}
	}

	@Override
	public void translateY (float yAmount) {
		cloneList();
		for (Sprite s : mCloneList) {
			s.translateY(yAmount);
		}
	}

	@Override
	public void setOrigin (float originX, float originY) {
		cloneList();
		for (Sprite s : mCloneList) {
			s.setOrigin(originX, originY);
		}
	}

	@Override
	public void setRotation (float degree) {
		cloneList();
		for (Sprite s : mCloneList) {
			s.setRotation(degree);
		}
	}

	@Override
	public void rotate (float degree) {
		cloneList();
		for (Sprite s : mCloneList) {
			s.rotate(degree);
		}
	}

	@Override
	public void setScale (float scaleXY) {
		cloneList();
		for (Sprite s : mCloneList) {
			s.setScale(scaleXY);
		}
	}

	@Override
	public void setScale (float scaleX, float scaleY) {
		cloneList();
		for (Sprite s : mCloneList) {
			s.setScale(scaleX, scaleY);
		}
	}

	@Override
	public void scale (float amount) {
		cloneList();
		for (Sprite s : mCloneList) {
			s.scale(amount);
		}
	}

	@Override
	public void setColor (float r, float g, float b, float a) {
		cloneList();
		for (Sprite s : mCloneList) {
			s.setColor(r, g, b, a);
		}
	}

	@Override
	public void setColor (float a) {
		cloneList();
		for (Sprite s : mCloneList) {
			s.setColor(a);
		}
	}

	@Override
	public void setColor (Color color) {
		color.set(color);

		cloneList();
		for (Sprite s : mCloneList) {
			s.setColor(color);
		}
	}

	@Override
	public void flip (boolean flipX, boolean flipY) {
		super.flip(flipX, flipY);
		cloneList();
		for (Sprite s : mCloneList) {
			s.flip(flipX, flipY);
		}
	}

	// ///////////////////////////////////////////////////////////////
	// getter
	// ///////////////////////////////////////////////////////////////

	@Override
	public float[] getVertices () {
		if (mSpriteList.size == 0) return null;
		return mSpriteList.get(0).getVertices();
	}

	@Override
	public Array<FloatArray> getBounding () {
		mBoundList.clear();
		cloneList();

		for (Sprite s : mCloneList) {
			mBoundList.addAll(s.getBounding());
		}
		return mBoundList;
	}

	@Override
	public float getX () {
		if (mSpriteList.size == 0) return 0;
		return mSpriteList.get(0).getX();
	}

	@Override
	public float getCenterX () {
		float centerX = 0;
		for (Sprite s : mSpriteList) {
			centerX += s.getCenterX();
		}
		return centerX / mSpriteList.size;
	}

	@Override
	public float getY () {
		if (mSpriteList.size == 0) return 0;
		return mSpriteList.get(0).getY();
	}

	@Override
	public float getCenterY () {
		float centerY = 0;
		for (Sprite s : mSpriteList) {
			centerY += s.getCenterY();
		}
		return centerY / mSpriteList.size;
	}

	@Override
	public float getWidth () {
		if (mSpriteList.size == 0) return 0;
		return mSpriteList.get(0).getWidth();
	}

	@Override
	public float getHeight () {
		if (mSpriteList.size == 0) return 0;
		return mSpriteList.get(0).getHeight();
	}

	@Override
	public float getOriginX () {
		if (mSpriteList.size == 0) return 0;
		return mSpriteList.get(0).getOriginX();
	}

	@Override
	public float getOriginY () {
		if (mSpriteList.size == 0) return 0;
		return mSpriteList.get(0).getOriginY();
	}

	@Override
	public float getRotation () {
		if (mSpriteList.size == 0) return 0;
		return mSpriteList.get(0).getRotation();
	}

	@Override
	public float getScaleX () {
		if (mSpriteList.size == 0) return 0;
		return mSpriteList.get(0).getScaleX();
	}

	@Override
	public float getScaleY () {
		if (mSpriteList.size == 0) return 0;
		return mSpriteList.get(0).getScaleY();
	}

	@Override
	public Color getColor () {
		if (mSpriteList.size == 0) return null;
		return mSpriteList.get(0).getColor();
	}

	// ///////////////////////////////////////////////////////////////
	// processor
	// ///////////////////////////////////////////////////////////////

	@Override
	protected void updateInternal (float delta) {
		cloneList();
		for (Sprite s : mCloneList) {
			s.update(delta);
		}
	}

	@Override
	protected void drawInternal (Batch batch) {
		cloneList();
		for (Sprite s : mCloneList)
			s.draw(batch);
	}

	@Override
	protected void resetInternal () {
		for (Sprite s : mSpriteList)
			s.reset();
		mSpriteList.clear();
		mCloneList.clear();
	}

	@Override
	protected void disposeInternal () {
		for (Sprite s : mSpriteList)
			s.dispose();
		super.dispose();
	}

	@Override
	public String Name () {
		return Name;
	}

	@Override
	public String toString () {
		return Name + " Size: " + mSpriteList.size;
	}
}
