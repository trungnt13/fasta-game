
package com.topvn.fappybird;

import org.tntstudio.Debug;
import org.tntstudio.core.GameCore;
import org.tntstudio.core.Top;
import org.tntstudio.services.PrefCallback;
import org.tntstudio.services.TNTPreference;

public class FAPpyBirdCore extends GameCore {
	@Override
	protected void onGameCreate () {
		Top.tcus.pref().load(new PrefCallback() {
			@Override
			public void prefLoaded (TNTPreference pref) {
				String val1 = pref.getString("Trung1"); 
				String val2 = pref.getString("Trung"); 
				
				Debug.out(val1);
				Debug.out(val2);
				
				pref.setEncryptData(true);
				pref.putString("Trung1", "Love Anh");
				pref.putString("Trung", "Kiss Anh");
				pref.flush();
			}
		});
	}
	
	@Override
	public String getCompanyName () {
		return "TrungAnh";
	}
}
