
package org.tntstudio.box2d;

import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;

/** Use like simple {@link FixtureDef} of Box2D
 * @author trungnt13 */
public final class LuaFixtureDef extends FixtureDef {
	public void setShape (Shape shape) {
		this.shape = shape;
	}

	public void setFriction (float friction) {
		this.friction = friction;
	}

	public void setRestitution (float restitution) {
		this.restitution = restitution;
	}

	public void setDensity (float density) {
		this.density = density;
	}

	public void setSensor (boolean isSensor) {
		this.isSensor = isSensor;
	}
}
