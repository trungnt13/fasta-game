
package org.tntstudio.app;

public interface Game {
	public static final int PAY_SHARE = 1;
	public static final int PAY_DOWNLOAD = 2;

	public static final int PAY_HIGHSCORE = 3;

	public static final int PAY_ANALYTIC_OptOut = 21;
	public static final int PAY_ANALYTIC_EVENT = 12;
	public static final int PAY_ANALYTIC_DISPATCH = 13;
	public static final int PAY_ANALYTIC_TRANSACTION = 14;
	public static final int PAY_ANALYTIC_TIMING = 15;
	public static final int PAY_ANALYTIC_SOCIAL = 16;
	public static final int PAY_ANALYTIC_SCREEN = 17;
	public static final int PAY_ANALYTIC_SESSION_START = 18;
	public static final int PAY_ANALYTIC_SESSION_END = 19;
	public static final int PAY_ANALYTIC_EXCEPTION = 20;

	public static final int PAY_GOOGLE_SIGN_IN = 4;
	public static final int PAY_GOOGLE_SIGN_OUT = 5;

	public static final int PAY_ADVIEW_SHOW = 6;
	public static final int PAY_ADVIEW_HIDE = 7;
	public static final int PAY_ADVIEW_REFRESH = 11;

	public static final int PAY_FACEBOOK_SHOW = 8;
	public static final int PAY_FACEBOOK_HIDE = 9;

	public static final int PAY_TOAST = 10;

	public void sendMessages (int gameMessageID, Object... params);
}
