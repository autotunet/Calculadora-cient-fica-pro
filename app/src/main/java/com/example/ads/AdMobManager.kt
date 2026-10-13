package com.example.ads

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdMobManager {
    private const val TAG = "AdMobManager"

    // Real Ad Unit IDs provided by the user
    const val AD_UNIT_APP_START = "ca-app-pub-1495262574338316/2803163788"
    const val AD_UNIT_BANNER = "ca-app-pub-1495262574338316/2903409205"
    const val AD_UNIT_TOOL_1 = "ca-app-pub-1495262574338316/2452733430"
    const val AD_UNIT_TOOL_2 = "ca-app-pub-1495262574338316/8970040322"
    const val AD_UNIT_TOOL_3 = "ca-app-pub-1495262574338316/1182179137"
    const val AD_UNIT_TOOL_4 = "ca-app-pub-1495262574338316/4962596335"
    const val AD_UNIT_TOOL_5 = "ca-app-pub-1495262574338316/2714752655"
    const val AD_UNIT_TOOL_6 = "ca-app-pub-1495262574338316/3503687777"

    // Official Google sample ad units for safe testing on emulators
    const val TEST_AD_UNIT_INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712"
    const val TEST_AD_UNIT_BANNER = "ca-app-pub-3940256099942544/6300978111"

    private val interstitialUnits = listOf(
        AD_UNIT_TOOL_1,
        AD_UNIT_TOOL_2,
        AD_UNIT_TOOL_3,
        AD_UNIT_TOOL_4,
        AD_UNIT_TOOL_5,
        AD_UNIT_TOOL_6
    )

    private var interstitialIndex = 0
    private var currentInterstitialAd: InterstitialAd? = null
    private var isAdLoading = false
    private var actionCounter = 0
    private var hasShownStartAd = false

    /**
     * Accurately detects whether the app is executing inside an Android emulator or container.
     */
    fun isEmulator(): Boolean {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("sdk_gphone64")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator")
    }

    fun initialize(context: Context) {
        try {
            val requestConfig = RequestConfiguration.Builder()
                .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR))
                .build()
            MobileAds.setRequestConfiguration(requestConfig)

            MobileAds.initialize(context) { status ->
                Log.d(TAG, "AdMob initialized: ${status.adapterStatusMap}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing MobileAds SDK", e)
        }
    }

    /**
     * Automatically loads and presents the startup interstitial ad.
     */
    fun showStartupAdIfAvailable(activity: Activity) {
        if (hasShownStartAd) return
        hasShownStartAd = true

        val adUnitId = if (isEmulator()) TEST_AD_UNIT_INTERSTITIAL else AD_UNIT_APP_START
        val request = AdRequest.Builder().build()

        InterstitialAd.load(
            activity,
            adUnitId,
            request,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            preloadNextInterstitial(activity)
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            preloadNextInterstitial(activity)
                        }
                    }
                    interstitialAd.show(activity)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.d(TAG, "Startup ad not loaded: ${loadAdError.message}")
                    preloadNextInterstitial(activity)
                }
            }
        )
    }

    /**
     * Preloads an interstitial ad for subsequent tool usage.
     */
    fun preloadNextInterstitial(context: Context) {
        if (isAdLoading || currentInterstitialAd != null) return

        isAdLoading = true
        val adUnitId = if (isEmulator()) {
            TEST_AD_UNIT_INTERSTITIAL
        } else {
            interstitialUnits[interstitialIndex % interstitialUnits.size]
        }
        interstitialIndex++

        val request = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            adUnitId,
            request,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    currentInterstitialAd = interstitialAd
                    isAdLoading = false
                    Log.d(TAG, "Interstitial loaded: $adUnitId")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    currentInterstitialAd = null
                    isAdLoading = false
                    Log.d(TAG, "Interstitial load failed: ${loadAdError.message}")
                }
            }
        )
    }

    /**
     * Triggers an automatic interstitial ad when tools are used.
     */
    fun onToolUsed(activity: Activity, force: Boolean = false) {
        actionCounter++
        if (force || actionCounter % 3 == 0) {
            val ad = currentInterstitialAd
            if (ad != null) {
                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        currentInterstitialAd = null
                        preloadNextInterstitial(activity)
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        currentInterstitialAd = null
                        preloadNextInterstitial(activity)
                    }
                }
                ad.show(activity)
                currentInterstitialAd = null
            } else {
                preloadNextInterstitial(activity)
            }
        }
    }
}

@Composable
fun AdBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = AdMobManager.AD_UNIT_BANNER
) {
    // Choose unit ID once before creating AdView to avoid 'ad unit ID can only be set once' IllegalStateException
    val targetUnitId = if (AdMobManager.isEmulator()) {
        AdMobManager.TEST_AD_UNIT_BANNER
    } else {
        adUnitId
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color(0xFF0B0E14)),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    this.adUnitId = targetUnitId
                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            Log.d("AdBanner", "AdMob banner loaded successfully ($targetUnitId)")
                        }

                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            Log.d("AdBanner", "AdMob banner failed: ${adError.message}")
                        }
                    }
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}
