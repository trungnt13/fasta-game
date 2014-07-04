
package org.tntstudio.services;

import static org.tntstudio.services.PreferencesManager.KEY_COMPANY_NAME;
import static org.tntstudio.services.PreferencesManager.KEY_GAME_DATA;
import static org.tntstudio.services.PreferencesManager.KEY_GAME_NAME;
import static org.tntstudio.services.PreferencesManager.KEY_GAME_SAFE;
import static org.tntstudio.services.PreferencesManager.KEY_GAME_WARNING;

import java.util.HashMap;
import java.util.Map;

import org.tntstudio.core.Top;
import org.tntstudio.utils.AsyncWork;
import org.tntstudio.utils.encode.AndroidCrypto;
import org.tntstudio.utils.encode.CryptoManager;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;

@SuppressWarnings("unchecked")
public class TNTPreference implements Preferences {
/*-------- static param --------*/
	private static final String GameWarningVN = "Bạn không nên sửa file này, chúng tôi biết bạn muốn làm gì !";
	private static final String GameWarningEN = "Please don't modify this file, we know what you want to do !";

	/*-------- local param --------*/
	private final HashMap<String, Object> mPrefMap;
	private final Preferences mGdxPref;
	private boolean isEncrypt = false;

	TNTPreference (Preferences gdx, String data) {
		mGdxPref = gdx;

		if (data == null)
			mPrefMap = new HashMap<String, Object>();
		else
			mPrefMap = Top.json.fromJson(HashMap.class, data);
	}

	public TNTPreference setEncryptData (boolean isEncrypt) {
		this.isEncrypt = isEncrypt;
		return this;
	}

	public boolean isEncryptData () {
		return isEncrypt;
	}

	@Override
	public void putBoolean (String key, boolean val) {
		mPrefMap.put(key, val);
	}

	@Override
	public void putInteger (String key, int val) {
		mPrefMap.put(key, val);
	}

	@Override
	public void putLong (String key, long val) {
		mPrefMap.put(key, val);
	}

	@Override
	public void putFloat (String key, float val) {
		mPrefMap.put(key, val);
	}

	@Override
	public void putString (String key, String val) {
		mPrefMap.put(key, val);
	}

	@Override
	public void put (Map<String, ?> vals) {
		mPrefMap.putAll(vals);
	}

	public boolean getBoolean (String key) {
		return getBoolean(key, false);
	}

	public int getInteger (String key) {
		return getInteger(key, 0);
	}

	public long getLong (String key) {
		return getLong(key, 0L);
	}

	public float getFloat (String key) {
		return getFloat(key, 0f);
	}

	public String getString (String key) {
		return getString(key, null);
	}

	public boolean getBoolean (String key, boolean defValue) {
		if (!mPrefMap.containsKey(key)) return defValue;
		return (Boolean)mPrefMap.get(key);
	}

	public int getInteger (String key, int defValue) {
		if (!mPrefMap.containsKey(key)) return defValue;
		return (Integer)mPrefMap.get(key);
	}

	public long getLong (String key, long defValue) {
		if (!mPrefMap.containsKey(key)) return defValue;
		return (Long)mPrefMap.get(key);
	}

	public float getFloat (String key, float defValue) {
		if (!mPrefMap.containsKey(key)) return defValue;
		return (Float)mPrefMap.get(key);
	}

	public String getString (String key, String defValue) {
		if (!mPrefMap.containsKey(key)) return defValue;
		return (String)mPrefMap.get(key);
	}

	@Override
	public Map<String, ?> get () {
		return mPrefMap;
	}

	@Override
	public boolean contains (String key) {
		return mPrefMap.containsKey(key);
	}

	@Override
	public void clear () {
		mPrefMap.clear();
	}

	@Override
	public void remove (String key) {
		mPrefMap.remove(key);
	}

	@Override
	public void flush () {
		AsyncWork.execute(new Runnable() {
			@Override
			public void run () {
				/*-------- gson --------*/
				final Json gson = Top.json;
				String data = null;
				String tmpData = gson.toJson(mPrefMap, HashMap.class);
				String salt = null;
				String oldData = null;
				if ((oldData = mGdxPref.getString(KEY_GAME_DATA, null)) == null) {
					salt = CryptoManager.newInstance().generateSalt();
				} else {
					salt = oldData.substring(0, AndroidCrypto.SALT_LENGTH * 2);
				}

				/*-------- encrypt data --------*/
				// encrypted
				if (isEncrypt) {
					CryptoManager.newInstance().generatedSecretKey(Top.tcus.pref().PASSWORDS, salt);
					data = salt + CryptoManager.newInstance().encrypt(tmpData);
				}
				// not encrypted
				else
					data = salt + tmpData;

				/*-------- put data --------*/
				mGdxPref.putString(KEY_COMPANY_NAME, Top.tgame.getCompanyName());
				mGdxPref.putString(KEY_GAME_NAME, Top.tgame.getGameName());
				mGdxPref.putString(KEY_GAME_WARNING, GameWarningVN + " - " + GameWarningEN);
				mGdxPref.putString(KEY_GAME_DATA, data);
				mGdxPref.putBoolean(KEY_GAME_SAFE, isEncrypt);
				mGdxPref.flush();
			}
		});
	}
}
