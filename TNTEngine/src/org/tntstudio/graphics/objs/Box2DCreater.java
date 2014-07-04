package org.tntstudio.graphics.objs;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Creater to creater a Body for Box2D!
 * @author ManhPhi
 *
 */
public interface Box2DCreater {

	/** @param rX : reference X, center of Body in world
	 * @param rY : reference Y
	 * @return BodyDef init Body */
	public BodyDef initBodyDef (float rX, float rY);

	public Shape initShape (float width, float height);

	public FixtureDef initFixtureDef (Shape shape);

	public Body attachFixture (World world, Body body, FixtureDef fixtureDef);

}
