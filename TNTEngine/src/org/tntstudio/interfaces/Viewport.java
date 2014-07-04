
package org.tntstudio.interfaces;

import org.tntstudio.core.Frame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/** The interface to setup viewport
 * @author trungnt13 */
public interface Viewport {
	public void vpApply (SpriteCache cache);

	public void vpApply (SpriteBatch batch);

	public void vpApply (ShapeRenderer batch);

	public void vpApply (Frame layout);

	public Matrix4 vpCombined ();

	public void vpResetTransform ();

	public static interface Viewport2D extends Viewport {
		public Viewport2D vpSetOrtho2D (float x, float y, float width, float height, boolean yDown);

		public Viewport2D vpSetOrtho (float left, float right, float bottom, float top, float near, float far);

		public Viewport2D vpSetOrtho2D (float width, float height, boolean yDown, boolean lookAtCenter);

		public Viewport2D vpTranslate (float x, float y);

		public Viewport2D vpTranslate (Vector2 vec2);

		public Viewport2D vpToTranslatation (float x, float y);

		public Viewport2D vpToTranslatation (Vector2 vec2);

		public Viewport2D vpRotate (float angle);

		public Viewport2D vpToRotation (float angle);

		public Viewport2D vpZoom (float zoom);

		public Viewport2D vpProject (Vector2 vec);

		public Viewport2D vpUnproject (Vector2 vec);

		public float vpWidth ();

		public float vpHeight ();
	}

	public static interface Viewport3D extends Viewport {
		public Viewport3D vpSet (float width, float height, float fieldOfView);

		public Viewport3D vpTranslate (float x, float y, float z);

		public Viewport3D vpTranslate (Vector3 vec);

		public Viewport3D vpRotate (Vector3 vec, float angle);

		public Viewport3D vpRotate (float angle, float x, float y, float z);

		public Viewport3D vpRotate (final Matrix4 transform);

		public Viewport3D vpRotate (Quaternion quat);

		public Viewport3D vpZoom (float zoom);

		public Viewport3D vpProject (Vector3 vec);

		public Viewport3D vpUnproject (Vector3 vec);

		public Viewport3D vpLookAt (float x, float y, float z);

		public Viewport3D vpLookAt (Vector3 vec);
	}
}
