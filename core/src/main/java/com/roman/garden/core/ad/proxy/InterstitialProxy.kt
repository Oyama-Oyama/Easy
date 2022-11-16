package com.roman.garden.core.ad.proxy

import android.app.Activity
import com.roman.garden.ad.base.AdStatus
import com.roman.garden.ad.base.AdType
import com.roman.garden.ad.base.AdUnit
import com.roman.garden.core.ad.AdEasy
import com.roman.garden.core.ad.AdListener
import com.roman.garden.core.ad.platform.PlatformManager

internal class InterstitialProxy : AdProxy() {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { InterstitialProxy() }
    }

    private var partListener: AdListener? = null

    override fun getAdType(): AdType {
        return AdType.INTERSTITIAL
    }

    override fun getPartListener(): AdListener? {
        return partListener
    }

    override fun setPartListener(adListener: AdListener?) {
        partListener = adListener
    }

    fun show(activity: Activity) {
        getCanShowAdUnit().let {
            when (it) {
                null -> onAdShowFail(
                    AdUnit(),
                    AdStatus.AD_SHOW_FAIL.extra("can't find adUnit to Show:${AdType.INTERSTITIAL}")
                )
                else -> {
                    it.let { adUnit ->
                        PlatformManager.instance.findAdapter(adUnit).let { adapter ->
                            when (adapter) {
                                null -> onAdShowFail(
                                    it,
                                    AdStatus.AD_SHOW_FAIL.extra("can't find adapter to Show ad:${AdType.INTERSTITIAL}")
                                )
                                else -> adapter.showInterstitial(activity, adUnit)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onAdClosed(adUnit: AdUnit) {
        super.onAdClosed(adUnit)
        load(AdEasy.instance.getContext(), null)
    }

}