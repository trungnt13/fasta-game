
package org.tntstudio.services;

import org.tntstudio.Const.ErrorCode;
import org.tntstudio.core.Top;
import org.tntstudio.utils.AsyncWork;
import org.tntstudio.utils.Error;
import org.tntstudio.utils.encode.AndroidCrypto;
import org.tntstudio.utils.encode.CryptoManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public final class PreferencesManager {
	final String PASSWORDS = "WHO_HACKS_THIS_GAME_IS_AN_IDIOT";

	public static final String KEY_COMPANY_NAME = "Company Name";
	public static final String KEY_GAME_NAME = "Game Name";
	public static final String KEY_GAME_WARNING = "Game Warning";
	public static final String KEY_GAME_IDENTIFIED = "Game Identified";
	public static final String KEY_GAME_DATA = "Game Components";
	public static final String KEY_GAME_SAFE = "Game Security";

	public PreferencesManager () {
	}

	// ///////////////////////////////////////////////////////////////
	// state control methods
	// ///////////////////////////////////////////////////////////////

	/** This is asynchronize methods use {@link AsyncWork} to execute a task that load the preference. {@link PrefCallback} is
	 * called when the preference done loading
	 * @param name use default pref name: Company Name + Game Name */
	public void load (final PrefCallback... callback) {
		AsyncWork.execute(new Runnable() {
			@Override
			public void run () {
				final TNTPreference pref = load(Top.tgame.getCompanyName() + "." + Top.tgame.getGameName());
				for (PrefCallback pc : callback) {
					pc.prefLoaded(pref);
				}
			}
		});
	}

	/** This is asynchronize methods use {@link AsyncWork} to execute a task that load the preference. {@link PrefCallback} is
	 * called when the preference done loading
	 * @param name name of preference */
	public void load (final String name, final PrefCallback... callback) {
		AsyncWork.execute(new Runnable() {
			@Override
			public void run () {
				final TNTPreference pref = load(Top.tgame.getCompanyName() + "." + Top.tgame.getGameName());
				for (PrefCallback pc : callback) {
					pc.prefLoaded(pref);
				}
			}
		});
	}

	/** This is synchronize methods
	 * @param name use default pref name: Company Name + Game Name */
	public TNTPreference load() {
		return load(Top.tgame.getCompanyName() + "." + Top.tgame.getGameName());
	}

	/** This is synchronize methods
	 * @param name name of preference */
	public TNTPreference load (String name) {
		Preferences pref = Gdx.app.getPreferences(name);
		/*-------- read text data from file --------*/
		String oldData = pref.getString(KEY_GAME_DATA, null);
		if (oldData == null) return new TNTPreference(pref, null);

		//now decrypt data
		String salt = null;
		String data = oldData;

		try {
			boolean isEncrypted = pref.getBoolean(KEY_GAME_SAFE);
			// if data is encrypted
			if (isEncrypted) {
				salt = oldData.substring(0, AndroidCrypto.SALT_LENGTH * 2);
				CryptoManager.newInstance().generatedSecretKey(PASSWORDS, salt);
				data = CryptoManager.newInstance().decrypt(oldData.replace(salt, ""));
			}
			// if data is not encrypted
			else {
				salt = oldData.substring(0, AndroidCrypto.SALT_LENGTH * 2);
				data = oldData.replace(salt, "");
			}
		} 
		//if user modify preference, encrypt or decrypt error, reset all data
		catch (Exception e) {
			pref.clear();
			data = null;
			Top.tgame.putError(new Error(ErrorCode.PREFERENCE_ERROR, pref, e, null));
		}
		return new TNTPreference(pref, data);
	}
}
