package org.zhangge.rbplayer.utils;

import org.zhangge.almightyzgbox_android.utils.ZGPreference;
import org.zhangge.rbplayer.R;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class AdUtils {

	public static String ADMOD_BANNER_ID = "ca-app-pub-4037469062479023/2113458194";
	public static String ADMOD_InterstitialAd_ID = "ca-app-pub-4037469062479023/9636724994";
	public static String ADMOD_INTER_COUNT = "ADMOD_INTER_COUNT";
	private static AdView adView;
	private static InterstitialAd interstitial;

	public static void addAdModBanner(Context context, View rootView) {
		adView = new AdView(context);
		adView.setAdUnitId(AdUtils.ADMOD_BANNER_ID);
		adView.setAdSize(AdSize.BANNER);

		LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.mainLayout);
		layout.addView(adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	}

	public static void addInterstitialAd(Context context) {
		interstitial = new InterstitialAd(context);
		interstitial.setAdUnitId(ADMOD_InterstitialAd_ID);

		AdRequest adRequest = new AdRequest.Builder().build();

		interstitial.loadAd(adRequest);
	}

	public static void displayInterstitial() {
		int count = ZGPreference.getInstance().getInt(ADMOD_INTER_COUNT, 0);
		if (interstitial.isLoaded() && count % 2 == 0) {
			interstitial.show();
			ZGPreference.getInstance().putInt(ADMOD_INTER_COUNT, count+1);
		}
	}

	public static void onResume() {
		if (adView != null) {
			adView.resume();
		}
	}

	public static void onPause() {
		if (adView != null) {
			adView.pause();
		}
	}

	public static void onDestory() {
		if (adView != null) {
			adView.destroy();
		}
	}

}
