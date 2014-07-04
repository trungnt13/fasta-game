
package org.tntstudio.utils;

import org.tntstudio.Debug;

import com.badlogic.gdx.Gdx;

public class UnitEvaluation {
	public static final class TimeRecord {
		long startTime = 0;

		public void start () {
			startTime = System.currentTimeMillis();
		}

		public void end () {
			long delta = System.currentTimeMillis() - startTime;
			if (Gdx.app != null)
				Debug.out(delta);
			else
				System.out.println(delta);
			startTime = 0;
		}

		public void end (String tag) {
			long delta = System.currentTimeMillis() - startTime;
			if (Gdx.app != null)
				Debug.out(tag + "  :  " + delta);
			else
				System.out.println(tag + "  :  " + delta);
			startTime = 0;
		}
	}
}
