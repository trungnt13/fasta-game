
package org.tntstudio.services.payment;

public interface PaymentCallback {
	// ///////////////////////////////////////////////////////////////
	// toast
	// ///////////////////////////////////////////////////////////////

	/** Create default android toast
	 * 
	 * @param text
	 * @param duration */
	public void onToast (String text, int duration);

	// ///////////////////////////////////////////////////////////////
	// basic function
	// ///////////////////////////////////////////////////////////////

	/** Request for share */
	public void onShare ();

	/** request for download */
	public void onDownload ();

	public void onSignFacebook (boolean isSignIn);

	public void onSignGoogle (boolean isSignIn);

	// ///////////////////////////////////////////////////////////////
	// google analytics
	// ///////////////////////////////////////////////////////////////
	/** Default value is <b>false</b> */
	public void onAnalyticOptOut (boolean isEnable);

	public void onAnalyticDispatchLocalHits ();

	public void onAnalyticSendEvent (String category, String action, String label, Long value);

	public void onAnalyticSendTransaction (String id, String affiliation, Double revenue, Double tax, Double shipingCost,
		String currency);

	public void onAnalyticSendTiming (String category, Long interval, String name, String label);

	public void onAnalyticSendSocial (String socialNetwork, String action, String target);

	public void onAnalyticSendScreen (String screenName);

	/*-------- session timeout --------*/

	public void onAnalyticSessionStart ();

	public void onAnalyticSessionEnd ();

	/*-------- exception --------*/
	public void onAnalyticSendException (Exception exception, Boolean fata);

	// ///////////////////////////////////////////////////////////////
	// admob
	// ///////////////////////////////////////////////////////////////

	public void onShowAdview (boolean isShow, String... pos);

	public void onRefreshAdview ();

	// ///////////////////////////////////////////////////////////////
	// payment
	// ///////////////////////////////////////////////////////////////

	/** Request for payment dialog */
	public void onShowPaymentDialog ();

	/** Set payment listener
	 * 
	 * @param onPaymentListener {@link OnPaymentListener} */
	public void setPaymentListener (PaymentListener onPaymentListener);

	// ///////////////////////////////////////////////////////////////
	// highscore
	// ///////////////////////////////////////////////////////////////

	/** send high score online
	 * @param score */
	public void onSendHighScore (long score);

	/** send high score online
	 * @param score */
	public void onShowHighScore ();

	public void onShowAchievement ();

	public void onShowHighScoreView ();

	/**************************************************
	 * 
	 **************************************************/

	/** @FileName: IActivityHandler.java
	 * @CreateOn: 7/12/2012
	 * @Author: ManhPhi */
	public static enum PaymentType {
		// tin nhan
		PAYMENT_TYPE_SMS,
		// the cao
		PAYMENT_TYPE_SCRATCH,
		// coupon
		PAYMENT_TYPE_COUPON,
		// wap charing
		PAYMENT_WAP_CHARGING,
		// wap charing
		PAYMENT_GOOGLE,
	}
}
