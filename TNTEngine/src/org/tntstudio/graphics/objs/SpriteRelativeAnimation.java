
package org.tntstudio.graphics.objs;

public class SpriteRelativeAnimation extends SpriteAnimation {

	public String name;
	float distanceOriginX, distanceOriginY;
	float distanceX, distanceY;
	private boolean isActive = false; // true : independent Updater with Root

	public SpriteRelativeAnimation (String animationName) {
		name = animationName;
	}

	public void bindOriginRoot (SpriteAnimation root, boolean active, float dOX, float dOY) {
		setPosition(root.getX() + root.getOriginX() + dOX - getOriginX(), root.getY() + root.getOriginY() + dOY - getOriginY());
		setOrigin(getOriginX() - dOX, getOriginY() - dOY);

		if (active) {
			setScale(root.getScaleX(), root.getScaleY());
			setRotation(root.getRotation());
		}

		isActive = active;
		distanceX = root.getX() + root.getOriginX() + dOX - getOriginX();
		distanceY = root.getY() + root.getOriginY() + dOY - getOriginY();
		distanceOriginX = dOX;
		distanceOriginY = dOY;
	}

	public boolean isActive () {
		return isActive;
	}

	public void setActive (boolean isAcive) {
		this.isActive = isAcive;
	}

	public float getRelativeOX () {
		return distanceOriginX;
	}

	public float getRelativeOY () {
		return distanceOriginY;
	}

	public float getRelativeX () {
		return distanceX;
	}

	public float getRelativeY () {
		return distanceY;
	}
}
