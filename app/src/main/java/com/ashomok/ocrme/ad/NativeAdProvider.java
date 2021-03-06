package com.ashomok.ocrme.ad;

import android.content.Context;

import androidx.annotation.StringRes;

import com.ashomok.ocrme.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.Single;

import com.ashomok.ocrme.utils.LogHelper;

public class NativeAdProvider {
    private Set<UnifiedNativeAd> nativeAds;
    private final Context context;
    private final int adid;

    private static final String TAG = LogHelper.makeLogTag(NativeAdProvider.class);

    public Set<UnifiedNativeAd> getNativeAds() {
        return nativeAds;
    }

    @Inject
    public NativeAdProvider(Context context, @StringRes int adid) {
        this.context = context;
        this.adid = adid;
        String appId = context.getResources().getString(R.string.app_id);
        MobileAds.initialize(context, appId);
        nativeAds = loadNativeAdsAsync();
    }

    private Set<UnifiedNativeAd> loadNativeAdsAsync() {

        Set<UnifiedNativeAd> result = new HashSet<>();

        AdLoader.Builder builder = new AdLoader.Builder(context, context.getString(adid));

        builder.forUnifiedNativeAd(unifiedNativeAd -> {
            result.add(unifiedNativeAd);
            LogHelper.d(TAG, "native add loaded, total set size = " + result.size());
        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);
        builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {

                LogHelper.e(TAG, "onAdFailedToLoad,error code " + errorCode);
            }
        });
        AdLoader adLoader = builder.build();
        adLoader.loadAds(new AdRequest.Builder().build(), 5);
        return result;
    }

    public Single<UnifiedNativeAd> loadNativeAd() {

        return Single.create(emitter -> {

            AdLoader.Builder builder = new AdLoader.Builder(context, context.getString(adid));

            builder.forUnifiedNativeAd(ad -> {
                        emitter.onSuccess(ad);
                        LogHelper.d(TAG, "Native ad loaded: " + ad.toString());
                    }
            );

            VideoOptions videoOptions = new VideoOptions.Builder()
                    .setStartMuted(true)
                    .build();

            NativeAdOptions adOptions = new NativeAdOptions.Builder()
                    .setVideoOptions(videoOptions)
                    .build();

            builder.withNativeAdOptions(adOptions);

            builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int errorCode) {
                    emitter.onError(new Throwable("onAdFailedToLoad,error code " + errorCode));
                    LogHelper.e(TAG, "onAdFailedToLoad,error code " + errorCode);
                }
            });
            AdLoader adLoader = builder.build();
            adLoader.loadAd(new AdRequest.Builder().build());
        });
    }
}
