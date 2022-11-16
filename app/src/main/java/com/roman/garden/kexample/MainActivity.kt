package com.roman.garden.kexample

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.roman.garden.ad.base.AdStatus
import com.roman.garden.ad.base.AdUnit
import com.roman.garden.core.Easy
import com.roman.garden.core.ad.AdListener
import com.roman.garden.core.ad.EventListener

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Easy.setGlobalAdEventListener(object : EventListener() {
            override fun onAdClicked(adUnit: AdUnit) {
                super.onAdClicked(adUnit)
                Log.e("AdEvent", "ad clicked: $adUnit")
            }

            override fun onAdShowFail(adUnit: AdUnit, reason: AdStatus) {
                super.onAdShowFail(adUnit, reason)
                Log.e("AdEvent", "ad show fail: $adUnit, reason: $reason")
            }

            override fun onAdShow(adUnit: AdUnit) {
                super.onAdShow(adUnit)
                Log.e("AdEvent", "ad show: $adUnit")
            }

            override fun onAdClosed(adUnit: AdUnit) {
                super.onAdClosed(adUnit)
                Log.e("AdEvent", "ad close: $adUnit")
            }

            override fun onLoadFail(adUnit: AdUnit, reason: AdStatus) {
                super.onLoadFail(adUnit, reason)
                Log.e("AdEvent", "ad load fail: $adUnit, reason: $reason")
            }

            override fun onLoadSuccess(adUnit: AdUnit) {
                super.onLoadSuccess(adUnit)
                Log.e("AdEvent", "ad load success: $adUnit")
            }

            override fun onRequestLoad(adUnit: AdUnit) {
                super.onRequestLoad(adUnit)
                Log.e("AdEvent", "ad request load: $adUnit")
            }

            override fun onRequestShow(adUnit: AdUnit) {
                super.onRequestShow(adUnit)
                Log.e("AdEvent", "ad request show: $adUnit")
            }

            override fun onUserRewarded(adUnit: AdUnit) {
                super.onUserRewarded(adUnit)
                Log.e("AdEvent", "ad user rewarded: $adUnit")
            }

        })

    }

    fun showInterstitial(view: View) {
        Easy.showInterstitial()
    }

    fun showAppOpen(view: View) {
        Easy.showAppOpenAd()
    }

    fun showRewarded(view: View) {
        Easy.showRewardedVideo()
    }

    fun showBanner(view: View) {
        Easy.showBanner()
    }

    fun closeBanner(view: View) {
        Easy.hideBanner()
    }

    fun showNative(view: View) {
        var place = findViewById<ViewGroup>(R.id.place)
        Easy.showNativeAd(place)
    }

    fun closeNative(view: View) {
        Easy.hideNativeAd()
    }

}

