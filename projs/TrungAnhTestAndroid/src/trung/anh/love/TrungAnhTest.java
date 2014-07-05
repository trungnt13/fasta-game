
package trung.anh.love;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;

public class TrungAnhTest extends AndroidApplication{

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize(new TrungAnhMain());
	}
}
