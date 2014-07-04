package org.tntstudio.box2d;

import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;

/** Use like simple Joint of Box2D
 * @author trungnt13 */
public final class LuaJoint extends Joint{

	protected LuaJoint (World world, long addr) {
		super(world, addr);
	}

}
