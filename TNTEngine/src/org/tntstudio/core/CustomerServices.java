
package org.tntstudio.core;

import org.tntstudio.app.Game;
import org.tntstudio.interfaces.LifecycleListener;
import org.tntstudio.services.PreferencesManager;
import org.tntstudio.services.payment.PaymentCallback;
import org.tntstudio.services.payment.PaymentListener;

/** Customer services for handling following stuffs:
 * <nl>
 * {@link PreferencesManager}
 * <nl>
 * @author trungnt13
 * @modify ManhPhi */

public class CustomerServices implements LifecycleListener, PaymentCallback {

	CustomerServicesCreator Creator = new CustomerServicesCreator();
	private final PreferencesManager mPreferencesManager;

	CustomerServices () {
		mPreferencesManager = new PreferencesManager();
		Top.tgame.registerLifecycleListener(this);
	}

	public class CustomerServicesCreator {
		private boolean isInited = false;

		private CustomerServicesCreator () {

		}

		public void initCustomerServices () {
			if (isInited) return;
			isInited = true;

		}
	}

	// ///////////////////////////////////////////////////////////////
	// get set
	// ///////////////////////////////////////////////////////////////
	public PreferencesManager pref () {
		return mPreferencesManager;
	}

	// ///////////////////////////////////////////////////////////////
	// life cycle
	// ///////////////////////////////////////////////////////////////

	@Override
	public void dispose () {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume () {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause () {
		// TODO Auto-generated method stub

	}

	// ///////////////////////////////////////////////////////////////
	// payment methods
	// ///////////////////////////////////////////////////////////////

	@Override
	public void onToast (String text, int duration) {
		Top.tgame.getGameApplication().sendMessages(Game.PAY_TOAST, text, duration);
	}

	@Override
	public void onShare () {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDownload () {
		// TODO Auto-generated method stub

	}

	/*-------- adview --------*/

	@Override
	public void onShowAdview (boolean isShow, String... pos) {
		if (isShow) {
			Top.tgame.getGameApplication().sendMessages(Game.PAY_ADVIEW_SHOW, pos);
		} else {
			Top.tgame.getGameApplication().sendMessages(Game.PAY_ADVIEW_HIDE);
		}
	}

	@Override
	public void onRefreshAdview () {
		Top.tgame.getGameApplication().sendMessages(Game.PAY_ADVIEW_REFRESH);
	}

	/*-------- analytics --------*/

	@Override
	public void onAnalyticOptOut (boolean isEnable) {
		Top.tgame.getGameApplication().sendMessages(Game.PAY_ANALYTIC_OptOut, isEnable);
	}

	@Override
	public void onAnalyticDispatchLocalHits () {
		Top.tgame.getGameApplication().sendMessages(Game.PAY_ANALYTIC_DISPATCH);
	}

	@Override
	public void onAnalyticSendEvent (String category, String action, String label, Long value) {
		Top.tgame.getGameApplication().sendMessages(Game.PAY_ANALYTIC_EVENT, category, action, label, value);
	}

	@Override
	public void onAnalyticSendTransaction (String id, String affiliation, Double revenue, Double tax, Double shipingCost,
		String currency) {
		Top.tgame.getGameApplication().sendMessages(Game.PAY_ANALYTIC_TRANSACTION, id, affiliation, revenue, tax, shipingCost,
			currency);
	}

	@Override
	public void onAnalyticSendTiming (String category, Long interval, String name, String label) {
		Top.tgame.getGameApplication().sendMessages(Game.PAY_ANALYTIC_TIMING, category, interval, name, label);
	}

	@Override
	public void onAnalyticSendSocial (String socialNetwork, String action, String target) {
		Top.tgame.getGameApplication().sendMessages(Game.PAY_ANALYTIC_SOCIAL, socialNetwork, action, target);
	}

	@Override
	public void onAnalyticSendScreen (String screenName) {
		Top.tgame.getGameApplication().sendMessages(Game.PAY_ANALYTIC_SCREEN, screenName);
	}

	@Override
	public void onAnalyticSessionStart () {
		Top.tgame.getGameApplication().sendMessages(Game.PAY_ANALYTIC_SESSION_START);
	}

	@Override
	public void onAnalyticSessionEnd () {
		Top.tgame.getGameApplication().sendMessages(Game.PAY_ANALYTIC_SESSION_END);
	}

	@Override
	public void onAnalyticSendException (Exception exception, Boolean fata) {
		Top.tgame.getGameApplication().sendMessages(Game.PAY_ANALYTIC_EXCEPTION, exception, fata);
	}

	/*-------- facebook --------*/

	@Override
	public void onSignFacebook (boolean isSignIn) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSignGoogle (boolean isSignIn) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onShowPaymentDialog () {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPaymentListener (PaymentListener onPaymentListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSendHighScore (long score) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onShowHighScore () {
		// TODO Auto-generated method stub

	}

	@Override
	public void onShowAchievement () {
		// TODO Auto-generated method stub

	}

	@Override
	public void onShowHighScoreView () {
		// TODO Auto-generated method stub

	}

}
