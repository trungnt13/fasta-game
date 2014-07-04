
package org.tntstudio.utils.math;

import org.tntstudio.graphics.objs.Sprite;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FloatArray;

/** This class for handle list of Polygon
 * @author trungnt13 */
public final class PolygonList implements Disposable {
	public Array<Polygon> Polygons = new Array<Polygon>();

	private Array<Rectangle> mBounds = new Array<Rectangle>();
	private Array<FloatArray> mTransformedVertices = new Array<FloatArray>();

	/** Calculates and returns the vertices of the polygon after scaling, rotation, and positional translations have been applied,
	 * as they are position within the world.
	 * 
	 * @return vertices scaled, rotated, and offset by the polygon position. */
	public Array<FloatArray> getTransformedVertices () {
		mTransformedVertices.clear();
		for (Polygon p : Polygons) {
			mTransformedVertices.add(p.getTransformedVertices());
		}
		return mTransformedVertices;
	}

	/** Calculates and returns the vertices of the polygon after scaling, rotation, and positional translations have been applied,
	 * as they are position within the world.
	 * 
	 * @return vertices scaled, rotated, and offset by the polygon position. */
	public Array<FloatArray> getTransformedVertices (Sprite s) {
		mTransformedVertices.clear();
		for (Polygon p : Polygons) {
			p.apply(s);
			mTransformedVertices.add(p.getTransformedVertices());
		}
		return mTransformedVertices;
	}

	public PolygonList add (float[] vertices) {
		Polygons.add(new Polygon(vertices));
		return this;
	}

	public PolygonList add (float[] vertices, float width, float height) {
		Polygons.add(new Polygon(vertices, width, height));
		return this;
	}

	public PolygonList add (Polygon polygon) {
		Polygons.add(polygon);
		return this;
	}

	public void setOrigin (float originX, float originY) {
		for (Polygon p : Polygons) {
			p.setOrigin(originX, originY);
		}
	}

	public void setPosition (float x, float y) {
		for (Polygon p : Polygons) {
			p.setPosition(x, y);
		}
	}

	public void setSize (float width, float height) {
		for (Polygon p : Polygons) {
			p.setSize(width, height);
		}
	}

	public void setVertices (int index, float[] vertices) {
		if (index >= Polygons.size) return;
		Polygons.get(index).setVertices(vertices);
	}

	/** Translates the polygon's position by the specified horizontal and vertical amounts. */
	public void translate (float x, float y) {
		for (Polygon p : Polygons) {
			p.translate(x, y);
		}
	}

	/** Sets the polygon to be rotated by the supplied degrees. */
	public void setRotation (float degrees) {
		for (Polygon p : Polygons) {
			p.setRotation(degrees);
		}
	}

	/** Applies additional rotation to the polygon by the supplied degrees. */
	public void rotate (float degrees) {
		for (Polygon p : Polygons) {
			p.rotate(degrees);
		}
	}

	/** Sets the amount of scaling to be applied to the polygon. */
	public void setScale (float scaleX, float scaleY) {
		for (Polygon p : Polygons) {
			p.setScale(scaleX, scaleY);
		}
	}

	/** Applies additional scaling to the polygon by the supplied amount. */
	public void scale (float amount) {
		for (Polygon p : Polygons) {
			p.scale(amount);
		}
	}

	/** Sets the polygon's world vertices to be recalculated when calling {@link #getTransformedVertices() getTransformedVertices}. */
	public void dirty () {
		for (Polygon p : Polygons) {
			p.dirty();
		}
	}

	/** Returns the area contained within the polygon. */
	public float area () {
		float totalArea = 0;
		for (Polygon p : Polygons) {
			totalArea += p.area();
		}
		return totalArea;
	}

	/** Returns an axis-aligned bounding box of this polygon.
	 * 
	 * Note the returned Rectangle is cached in this polygon, and will be reused if this Polygon is changed.
	 * 
	 * @return this polygon's bounding box {@link Rectangle} */
	public Array<Rectangle> getBoundingRectangle () {
		mBounds.clear();
		for (Polygon p : Polygons) {
			mBounds.add(p.getBoundingRectangle());
		}
		return mBounds;
	}

	/** Returns whether an x, y pair is contained within the polygon. */
	public boolean contains (float x, float y) {
		for (Polygon p : Polygons) {
			if (p.contains(x, y)) return true;
		}
		return false;
	}

	/** Returns the x-coordinate of the polygon's position within the world. */
	public float getX () {
		final Polygon p = Polygons.get(0);
		return p == null ? 0 : p.getX();
	}

	/** Returns the y-coordinate of the polygon's position within the world. */
	public float getY () {
		final Polygon p = Polygons.get(0);
		return p == null ? 0 : p.getY();
	}

	/** Returns the x-coordinate of the polygon's origin point. */
	public float getOriginX () {
		final Polygon p = Polygons.get(0);
		return p == null ? 0 : p.getOriginX();
	}

	/** Returns the y-coordinate of the polygon's origin point. */
	public float getOriginY () {
		final Polygon p = Polygons.get(0);
		return p == null ? 0 : p.getOriginY();
	}

	/** Returns the total rotation applied to the polygon. */
	public float getRotation () {
		final Polygon p = Polygons.get(0);
		return p == null ? 0 : p.getRotation();
	}

	/** Returns the total horizontal scaling applied to the polygon. */
	public float getScaleX () {
		final Polygon p = Polygons.get(0);
		return p == null ? 0 : p.getScaleX();
	}

	/** Returns the total vertical scaling applied to the polygon. */
	public float getScaleY () {
		final Polygon p = Polygons.get(0);
		return p == null ? 0 : p.getScaleY();
	}

	public void apply (Sprite s) {
		for (Polygon p : Polygons) {
			p.apply(s);
		}
	}

	public void draw (ShapeRenderer renderer) {
		for (Polygon p : Polygons) {
			p.draw(renderer);
		}
	}

	@Override
	public void dispose () {
		Polygons = null;
		mBounds = null;
		mTransformedVertices = null;
	}
}
