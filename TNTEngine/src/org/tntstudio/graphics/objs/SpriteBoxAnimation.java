
package org.tntstudio.graphics.objs;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class SpriteBoxAnimation extends SpriteAnimation {
	private Body body;

	public SpriteBoxAnimation () {
	}

	public void bindBox (World world, Box2DCreater creater) {
		body = world.createBody(creater.initBodyDef(getX() + getOriginX(), getY() + getOriginY()));
		Shape shape = creater.initShape(getWidth(), getHeight());
		creater.attachFixture(world, body, creater.initFixtureDef(shape));
		shape.dispose();
		active();
	}

	public Body body () {
		return body;
	}

	@Override
	protected void updateInternal (float delta) {
		super.updateInternal(delta);
		Vector2 pos = body.getPosition();
		setPosition(pos.x - getOriginX(), pos.y - getOriginY());
		setRotation(body.getAngle() * MathUtils.radiansToDegrees);
	}
}
