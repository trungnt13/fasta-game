
package org.tntstudio.lua;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;

public final class LuaManager {
	// ///////////////////////////////////////////////////////////////
	// singleton manager
	// ///////////////////////////////////////////////////////////////
	private static final String[] LuaInitFile = new String[] {"classpath:resources/script/init.lua"};

	private static LuaScript luaScript = null;

	public static LuaScript newScript () {
		if (luaScript == null || luaScript.isDisposed()) {
			if (Gdx.app.getType() == ApplicationType.Android || Gdx.app.getType() == ApplicationType.Desktop) {
				luaScript = new LuaAndroidScript();
			} else if (Gdx.app.getType() == ApplicationType.iOS) {
				luaScript = new LuaIOSScript();
			}
			luaScript.loadFile(LuaInitFile);
		}
		return luaScript;
	}
}
