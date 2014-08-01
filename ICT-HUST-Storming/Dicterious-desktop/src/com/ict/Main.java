
package com.ict;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main (String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Dicterious";
		cfg.width = 360;
		cfg.height = 640;

		new LwjglApplication(new DicteriousGame(), cfg);
	}
}
