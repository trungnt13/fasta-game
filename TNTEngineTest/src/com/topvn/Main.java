
package com.topvn;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.tntstudio.core.GameCore;

public class Main {
	public static void main (String[] args) {
//		Security.addProvider(new BouncyCastleProvider());

// LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
// cfg.title = "PTZ";
// cfg.useGL20 = true;
// cfg.width = 800;
// cfg.height = 480;
//
// new LwjglApplication(new TestCore(), cfg);
		// LuaScript script = new LuaScript("hellolua.lua");
		// script.runScriptFunction("println","abc");
		GameCore.startNewLwjglGame();
	}
}
