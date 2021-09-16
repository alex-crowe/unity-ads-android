package com.unity3d.scar.adapter.common;

/**
 * A listener for receiving notifications during the lifecycle of an ad.
 */
public interface IScarInterstitialAdListenerWrapper {

	/**
	 * Called when an ad is loaded.
	 */
	void onAdLoaded();

	/**
	 * Called when an ad request failed to load.
	 */
	void onAdFailedToLoad(int errorCode, String errorString);

	/**
	 * Called when an interstitial ad opens.
	 */
	void onAdOpened();

	/**
	 * Called when an ad failed to show on screen.
	 */
	void onAdFailedToShow(int errorCode, String errorString);

	/**
	 * Called when a click is recorded for an ad.
	 */
	void onAdClicked();

	/**
	 * Called when the user has left the app.
	 */
	void onAdLeftApplication();

	/**
	 * Called when the user is about to return to the application after clicking on an ad.
	 */
	void onAdClosed();

	/**
	 * Called when an impression is recorded for an ad.
	 */
	void onAdImpression();
}
