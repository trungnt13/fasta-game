
package org.tntstudio.utils.encode;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;

public class CryptoManager {
	private static Crypto crypto = null;

	public static Crypto newInstance () {
		if (crypto == null) {
			if (Gdx.app.getType() == ApplicationType.iOS) {
				crypto = new IOSCrypto();
			} else
				crypto = new AndroidCrypto();
		}
		return crypto;
	}
}
