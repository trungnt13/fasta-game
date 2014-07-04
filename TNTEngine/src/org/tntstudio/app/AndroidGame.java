
package org.tntstudio.app;

import java.io.IOException;

import org.tntstudio.Const;
import org.tntstudio.Const.ScreenChangeMode;
import org.tntstudio.core.GameCore;
import org.tntstudio.core.Top;
import org.tntstudio.resources.GameConfiguration;
import org.tntstudio.utils.io.IOUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Logger.LogLevel;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.StandardExceptionParser;
import com.google.analytics.tracking.android.Tracker;

/** Asbtract backend class Android add Admob
 * @author ManhPhi
 * @author TrungNT */
@SuppressWarnings("unchecked")
public abstract class AndroidGame extends AndroidApplication implements Game {
	// ///////////////////////////////////////////////////////////////
	// other params
	// ///////////////////////////////////////////////////////////////
	public static final long ADVIEW_ALPHA_CHANGE_DELAY = 500;

	private boolean isCancelAplphaTask = false;
	private final Runnable mAdViewAlphaTask = new Runnable() {
		@Override
		public void run () {
			if (adview == null || adview.getVisibility() != View.VISIBLE) {
				mHandle.postDelayed(mAdViewAlphaTask, ADVIEW_ALPHA_CHANGE_DELAY);
				return;
			}

			if (adview.getAlpha() <= 0.99f)
				adview.setAlpha(1f);
			else
				adview.setAlpha(0.85f);

			if (!isCancelAplphaTask) mHandle.postDelayed(mAdViewAlphaTask, ADVIEW_ALPHA_CHANGE_DELAY);
		}
	};

	// ///////////////////////////////////////////////////////////////
	// payment params
	// ///////////////////////////////////////////////////////////////

	/*-------- simple handler --------*/

	private final Handler mHandle = new Handler() {
		@Override
		public void handleMessage (Message msg) {
			Object[] objs = (Object[])msg.obj;

			switch (msg.what) {
			case Game.PAY_DOWNLOAD:
				break;
			case Game.PAY_SHARE:
				break;
			case Game.PAY_HIGHSCORE:
				break;
			/*-------- admob --------*/
			case Game.PAY_ADVIEW_SHOW:
				showAdView((Object[])msg.obj);
				break;
			case Game.PAY_ADVIEW_HIDE:
				hideAdView();
				break;
			case Game.PAY_ADVIEW_REFRESH:
				refreshAdView();
				break;
			/*-------- google account--------*/
			case Game.PAY_GOOGLE_SIGN_IN:
				break;
			case Game.PAY_GOOGLE_SIGN_OUT:
				break;
			/*-------- google account--------*/
			case Game.PAY_FACEBOOK_SHOW:
				break;
			case Game.PAY_FACEBOOK_HIDE:
				break;
			/*-------- show toast --------*/
			case Game.PAY_TOAST:
				showToast((String)objs[0], (Integer)objs[1]);
				break;
			/*-------- google analytic --------*/
			case Game.PAY_ANALYTIC_OptOut:
				setAptOptOut((Boolean)objs[0]);
				break;
			case Game.PAY_ANALYTIC_EVENT:
				sendGAEvent((String)objs[0], (String)objs[1], (String)objs[2], (Long)objs[3]);
				break;
			case Game.PAY_ANALYTIC_DISPATCH:
				sendGADispatchLocalHits();
				break;
			case Game.PAY_ANALYTIC_SCREEN:
				sendGAScreen((String)objs[0]);
				break;
			case Game.PAY_ANALYTIC_SESSION_END:
				sendGASessionEnd();
				break;
			case Game.PAY_ANALYTIC_SESSION_START:
				sendGASessionStart();
				break;
			case Game.PAY_ANALYTIC_EXCEPTION:
				sendGAException((Exception)objs[0], (Boolean)objs[1]);
				break;
			case Game.PAY_ANALYTIC_SOCIAL:
				sendGASocial((String)objs[0], (String)objs[1], (String)objs[2]);
				break;
			case Game.PAY_ANALYTIC_TIMING:
				sendGATiming((String)objs[0], (Long)objs[1], (String)objs[2], (String)objs[3]);
				break;
			case Game.PAY_ANALYTIC_TRANSACTION:
				sendGATransaction((String)objs[0], (String)objs[1], (Double)objs[2], (Double)objs[3], (Double)objs[4],
					(String)objs[5]);
				break;
			}
		}
	};

	public void sendMessages (int gameMessageID, Object... params) {
		Message mess = new Message();
		mess.what = gameMessageID;
		mess.obj = params;
		mHandle.sendMessage(mess);
	};

	private void showToast (final String text, final int duration) {
		runOnUiThread(new Runnable() {
			public void run () {
				Toast.makeText(getApplicationContext(), text, duration);
			}
		});
	}

	// ///////////////////////////////////////////////////////////////
	// AdView
	// ///////////////////////////////////////////////////////////////

	protected AdView adview = null;

	private void refreshAdView () {
		if (adview.getVisibility() == View.VISIBLE) {
			adview.loadAd(new AdRequest());
		}
	}

	private void hideAdView () {
		runOnUiThread(new Runnable() {
			public void run () {
				adview.setVisibility(View.GONE);
			}
		});
	}

	private void showAdView (final Object[] obj) {
		runOnUiThread(new Runnable() {
			public void run () {
				adview.setVisibility(View.GONE);
				adview.loadAd(new AdRequest());

				if (obj.length > 0) {
					String pos = (String)obj[0];
					pos.toLowerCase();

					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)adview.getLayoutParams();
					clearRelativeParamsRules(layoutParams);
					applyRelativeParamsRules(layoutParams, pos);

					adview.setLayoutParams(layoutParams);
				}

				adview.setVisibility(View.VISIBLE);
			}
		});
	}

	// ///////////////////////////////////////////////////////////////
	// google analytic
	// ///////////////////////////////////////////////////////////////
	private static GoogleAnalytics mGoogleAnalystic;
	private static Tracker mTracker;

	private final void createGoogleAnalytics (String id, int sampleRate, boolean dryrun, LogLevel log) {
		mGoogleAnalystic = GoogleAnalytics.getInstance(this);
		mTracker = mGoogleAnalystic.getTracker(id);
		mTracker.set(Fields.SAMPLE_RATE, String.valueOf(sampleRate % 100));

		// Set dispatch period.
// GAServiceManager.getInstance().setLocalDispatchPeriod(GA_DISPATCH_PERIOD);
		// Set dryRun flag.
		mGoogleAnalystic.setDryRun(dryrun);

		// Set Logger verbosity.
		mGoogleAnalystic.getLogger().setLogLevel(log);

	}

	private final void setAptOptOut (final Boolean isOptOut) {
		if (mTracker == null) return;
		GoogleAnalytics.getInstance(getApplicationContext()).setAppOptOut(isOptOut);
	}

	private final void sendGAEvent (String category, String action, String label, Long value) {
		if (mTracker == null) return;
		mTracker.send(MapBuilder.createEvent(category, action, label, value).build());
	}

	private final void sendGATransaction (String id, String affiliation, Double revenue, Double tax, Double shipingCost,
		String currency) {
		if (mTracker == null) return;
		mTracker.send(MapBuilder.createTransaction(id, affiliation, revenue, tax, shipingCost, currency).build());
	}

	private final void sendGATiming (String category, Long interval, String name, String label) {
		if (mTracker == null) return;
		mTracker.send(MapBuilder.createTiming(category, interval, name, label).build());
	}

	private final void sendGASocial (String socialNetwork, String action, String target) {
		if (mTracker == null) return;
		mTracker.send(MapBuilder.createSocial(socialNetwork, action, target).build());
	}

	private final void sendGAScreen (String screenName) {
		if (mTracker == null) return;
		mTracker.set(Fields.SCREEN_NAME, screenName);
		mTracker.send(MapBuilder.createAppView().build());
	}

	private StandardExceptionParser mExceptionParser = null;

	private final void sendGAException (Exception exception, Boolean fata) {
		if (mTracker == null) return;
		if (mExceptionParser == null) mExceptionParser = new StandardExceptionParser(this, null);
		mTracker.send(MapBuilder
			.createException(mExceptionParser.getDescription(Thread.currentThread().getName(), exception), fata).build());
	}

	private final void sendGASessionStart () {
		if (mTracker == null) return;
		mTracker.set(Fields.SESSION_CONTROL, "start");
	}

	private final void sendGASessionEnd () {
		if (mTracker == null) return;
		mTracker.set(Fields.SESSION_CONTROL, "end");
	}

	private final void sendGADispatchLocalHits () {
		if (mTracker == null) return;
		GAServiceManager.getInstance().dispatchLocalHits();
	}

	// ///////////////////////////////////////////////////////////////
	// Override methods
	// ///////////////////////////////////////////////////////////////

	@Override
	protected final void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*-------- windows feature --------*/
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		/*-------- call main methods --------*/
		main(savedInstanceState);

		/*-------- schedule adview change alpha task --------*/
		mHandle.postDelayed(mAdViewAlphaTask, ADVIEW_ALPHA_CHANGE_DELAY);
	}

	@Override
	protected void onDestroy () {
		isCancelAplphaTask = true;
		super.onDestroy();
	}

	/** you can call {@link AndroidGame#startNewAndroidGame(AndroidGame)} here */
	public abstract void main (Bundle savedInstanceState);

	// ///////////////////////////////////////////////////////////////
	// init methods
	// ///////////////////////////////////////////////////////////////

	public static final void startNewAndroidGame (AndroidGame app) {
		/*-------- init Top --------*/
		Top.init();

		String data = null;
		try {
			data = IOUtils.toString(app.getAssets().open(Const.ManifestFilePath));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		final GameConfiguration gameConfig = Top.json.fromJson(GameConfiguration.class, data);
		AndroidApplicationConfiguration appConfig = gameConfig.getAndroidConfiguration();

		/*-------- game core --------*/
		GameCore gameCore = null;
		try {
			gameCore = (GameCore)gameConfig.gameCore.newInstance();
			// set GameApplication to be manipulate at CustomerServices
			gameCore.setGameApplication(app);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		gameCore.mGameConfigurationRunnable = new Runnable() {
			@Override
			public void run () {
				Top.tgame
					.init(gameConfig.useLua, gameConfig.baseScreenList, gameConfig.popupScreenList, gameConfig.loadingScreenList)
					.initResources()
					.initAssets(gameConfig.resourceType, gameConfig.resourceParams, gameConfig.listOfResourceFile)
					.initGraphics(gameConfig.gameWidth, gameConfig.gameHeight, gameConfig.spriteBatchSize, gameConfig.isUseFrame,
						gameConfig.frameWidth, gameConfig.frameHeight, gameConfig.isKeepAspectRatio, gameConfig.spriteCacheSize,
						gameConfig.shapeRendererSize, gameConfig.shader, gameConfig.spriteCacheShader)
					.initAudio(gameConfig.isEnableAudio, gameConfig.musicVolume, gameConfig.soundVolume).initInput()
					.initCustomerServices();
			}
		};
		if (gameConfig.firstScreen != null) {
			gameCore.mSetFirstScreenRunnable = new Runnable() {
				@Override
				public void run () {
					Top.tgame.setScreen(gameConfig.firstScreen, ScreenChangeMode.Destroy);
				}
			};
		}

		/*-------- init gameview --------*/
		View gameView = app.initializeForView(gameCore, appConfig);
		/*-------- Create Relative Layout  --------*/
		RelativeLayout layout = new RelativeLayout(app);
		layout.addView(gameView);
		/*-------- Create Admob --------*/
		if (gameConfig.admobID != null) {
			AdView adview = new AdView(app, resolveAdSize(gameConfig.admobSize), gameConfig.admobID);
			app.adview = adview;
			adview.setAlpha(0.3f);
			adview.loadAd(new AdRequest());

			RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
			if (gameConfig.admobPos == null) {
				adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				adParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			} else {
				applyRelativeParamsRules(adParams, gameConfig.admobPos);
			}
			layout.addView(adview, adParams);
		}
		/*-------- set content view of Activity --------*/
		app.setContentView(layout);

		/*-------- create analytic --------*/
		if (gameConfig.analytic != null) {
			LogLevel log = LogLevel.INFO;
			if (gameConfig.analyticLoglevel.contains("error"))
				log = LogLevel.ERROR;
			else if (gameConfig.analyticLoglevel.contains("verbose"))
				log = LogLevel.VERBOSE;
			else if (gameConfig.analyticLoglevel.contains("warning")) log = LogLevel.WARNING;

			app.createGoogleAnalytics(gameConfig.analytic, gameConfig.analyticRate, gameConfig.analyticDryrun, log);
		}
	}

	// ///////////////////////////////////////////////////////////////
	// private helper
	// ///////////////////////////////////////////////////////////////

	private static AdSize resolveAdSize (String size) {
		size = size.toLowerCase();

		if (size.contains("smart")) {
			return AdSize.SMART_BANNER;
		} else if (size.contains("iab")) {
			if (size.contains("mrect"))
				return AdSize.IAB_MRECT;
			else if (size.contains("banner"))
				return AdSize.IAB_BANNER;
			else if (size.contains("leaderboard"))
				return AdSize.IAB_LEADERBOARD;
			else if (size.contains("wide")) return AdSize.IAB_WIDE_SKYSCRAPER;
		}
		return AdSize.BANNER;
	}

	private static void clearRelativeParamsRules (RelativeLayout.LayoutParams layoutParams) {
		layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
		layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
		layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_END);
		layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_START);
	}

	private static void applyRelativeParamsRules (RelativeLayout.LayoutParams layoutParams, String pos) {
		pos = pos.toLowerCase();
		if (pos.contains("top")) layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		if (pos.contains("bottom")) layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

		if (pos.contains("left")) layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		if (pos.contains("right")) layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

		if (pos.contains("end")) layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
		if (pos.contains("start")) layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
	}

}
