
package org.tntstudio.graphics.objs;

import org.tntstudio.Const.UpdaterExecuteMode;
import org.tntstudio.graphics.Updateable;
import org.tntstudio.interfaces.SpriteStateListener;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Pool.Poolable;

/** @author trungnt13 */
public interface Sprite extends Updateable, Disposable, Poolable {

	// =================================================
	// setter

	public void setBounds (float x, float y, float width, float height);

	public void setSize (float width, float height);

	public void setPosition (float x, float y);

	public void setX (float x);

	public void setY (float y);

	public void translate (float xAmount, float yAmount);

	public void translateX (float xAmount);

	public void translateY (float yAmount);

	public void setOrigin (float originX, float originY);

	public void setRotation (float degree);

	public void rotate (float degree);

	public void setScale (float scaleXY);

	public void setScale (float scaleX, float scaleY);

	public void scale (float amount);

	public void setColor (float r, float g, float b, float a);

	public void setColor (float a);

	public void setColor (Color color);

	public void flip (boolean flipX, boolean flipY);

	public void setFlip (boolean flipX, boolean flipY);

	// =================================================
	// state control
	public void setVisible (boolean isvisible);

	public void setUpdateable (boolean isupdate);

	public boolean isVisible ();

	public boolean isUpdateable ();

	// =================================================
	// getter

	public float[] getVertices ();

	public Array<FloatArray> getBounding ();

	public float getX ();

	public float getCenterX ();

	public float getY ();

	public float getCenterY ();

	public float getWidth ();

	public float getHeight ();

	public float getOriginX ();

	public float getOriginY ();

	public float getRotation ();

	public float getScaleX ();

	public float getScaleY ();

	public Color getColor ();

	public boolean isFlipX ();

	public boolean isFlipY ();

	// =================================================
	// processor
	public void postUpdater (Updater... name);


	public void setUpdaterExecuteMode (UpdaterExecuteMode mode);

	public UpdaterExecuteMode getUpdaterExecuteMode ();

	public void clearUpdater ();

	public int sizeUpdater ();

	public void removeUpdater (Updater... name);

	public void draw (Batch batch);

	// =================================================
	// sprite state

	public void registerStateListener (SpriteStateListener... listeners);

	public void unregisterStateListener (SpriteStateListener... listeners);
}
