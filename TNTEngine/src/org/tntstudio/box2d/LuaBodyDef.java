
package org.tntstudio.box2d;

import com.badlogic.gdx.physics.box2d.BodyDef;

/** {@link BodyDef} with setter support for Lua script
 * @author trungnt13 */
public final class LuaBodyDef extends BodyDef {
	public void setType (BodyType type) {
		this.type = type;
	}

	public void setAngle (float angle) {
		this.angle = angle;
	}

	public void setAngularVelocity (float angularVelocity) {
		this.angularVelocity = angularVelocity;
	}

	public void setLinearDamping (float linearDamping) {
		this.linearDamping = linearDamping;
	}

	public void setAngularDamping (float angularDamping) {
		this.angularDamping = angularDamping;
	}

	public void setAllowSleep (boolean allowSleep) {
		this.allowSleep = allowSleep;
	}

	public void setAwake (boolean awake) {
		this.awake = awake;
	}

	public void setFixedRotation (boolean fixedRotation) {
		this.fixedRotation = fixedRotation;
	}

	public void setBullet (boolean bullet) {
		this.bullet = bullet;
	}

	public void setActive (boolean active) {
		this.active = active;
	}

	public void setGravityScale (float gravityScale) {
		this.gravityScale = gravityScale;
	}
}
