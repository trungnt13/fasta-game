
package org.tntstudio.core;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;

/** Ís a Group of Actor used to be represented on screen
 * @author trungnt13 */
public class Panel extends Group implements Disposable {
	/** Image Background for Panel */
	final Image mBackground;
	boolean isStarting;
	public final Screen mScreen;

	Panel (Screen screen) {
		super();
		mScreen = screen;

		mBackground = new Image();
		mBackground.setBounds(0, 0, getWidth(), getHeight());
		mBackground.setTouchable(Touchable.disabled);
		mBackground.setScaling(Scaling.stretch);
		mBackground.setZIndex(0);

		addActor(mBackground);
	}

	Panel (Screen screen, Drawable region) {
		super();
		mScreen = screen;

		mBackground = new Image(region);
		mBackground.setBounds(0, 0, getWidth(), getHeight());
		mBackground.setTouchable(Touchable.disabled);
		mBackground.setScaling(Scaling.stretch);
		mBackground.setZIndex(0);

		addActor(mBackground);
	}

	// ///////////////////////////////////////////////////////////////
	// override methods
	// ///////////////////////////////////////////////////////////////
	/** Make this panel become touchable */
	public void show () {
		setTouchable(Touchable.enabled);
		setVisible(true);
	}

	/** Make this panel become invisible and not touchable */
	public void hide () {
		setTouchable(Touchable.disabled);
		setVisible(false);
	}

	/**
	 * 
	 */
	public void dispose () {
		mScreen.nullizePanel();
		setTouchable(Touchable.disabled);
		setVisible(false);
		clear();
		Top.tres.gFrame().removeLowLevelUI(this);
	}

	@Override
	public void setWidth (float width) {
		super.setWidth(width);
		mBackground.setWidth(width);
	}

	@Override
	public void setHeight (float height) {
		super.setHeight(height);
		mBackground.setHeight(height);
	}

	@Override
	public void setSize (float width, float height) {
		super.setSize(width, height);
		mBackground.setSize(width, height);
	}

	@Override
	public void size (float size) {
		super.size(size);
		mBackground.size(size);
	}

	@Override
	public void size (float width, float height) {
		super.size(width, height);
		mBackground.size(width, height);
	}

	@Override
	public void setBounds (float x, float y, float width, float height) {
		super.setBounds(x, y, width, height);
		mBackground.setBounds(0, 0, width, height);
	}

	@Override
	public void setOrigin (float originX, float originY) {
		mBackground.setOrigin(originX, originY);
		super.setOrigin(originX, originY);
	}

	// ///////////////////////////////////////////////////////////////
	// getter setter
	// ///////////////////////////////////////////////////////////////

	public final void draw () {
		if (getStage() != null) Top.tres.gFrame().drawPanel(this);
	}

	public void setBackground (Drawable drawable) {
		mBackground.setDrawable(drawable);
	}

	public Image getBackground () {
		return mBackground;
	}

}
