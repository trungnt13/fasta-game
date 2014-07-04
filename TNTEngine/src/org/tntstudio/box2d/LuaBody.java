
package org.tntstudio.box2d;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

/**
 * {@link Body} with setter support for Lua script
 * @author trungnt13
 *
 */
public final class LuaBody extends Body {
	protected LuaBody (World world, long addr) {
		super(world, addr);
	}
}
