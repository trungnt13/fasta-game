
package org.tntstudio.utils.encode;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;

public class CryptoManager {
	private static Crypto crypto = null;

	public static Crypto newInstance () {
		if (crypto == null) {
			if (Gdx.app.getType() == ApplicationType.iOS) {
				crypto = new IOSCrypto();
			} else if (Gdx.app.getType() == ApplicationType.Android) {
				crypto = new AndroidCrypto();
			} else {
				crypto = new DesktopCrypto();
			}
		}
		return crypto;
	}
	
	public static Crypto newInstance (int cryptoAlgorithm) {
		if (crypto == null) {
			if (Gdx.app.getType() == ApplicationType.iOS) {
				crypto = new IOSCrypto(cryptoAlgorithm);
			} else if (Gdx.app.getType() == ApplicationType.Android) {
				crypto = new AndroidCrypto(cryptoAlgorithm);
			} else {
				crypto = new DesktopCrypto(cryptoAlgorithm);
			}
		}
		return crypto;
	}

}
