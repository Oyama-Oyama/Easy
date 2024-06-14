package com.roman.garden.ad.core.ad.factory

import com.roman.garden.ad.core.ad.loader.*
import com.roman.garden.adbase.AdType


internal class AdProxyFactory {

    companion object {

        fun getAdLoader(adType: AdType): IAdProxy? {
            return when (adType) {
                AdType.APP_OPEN_AD -> AppOpenProxy()
                AdType.INTERSTITIAL -> InterstitialProxy()
                AdType.REWARDED_VIDEO -> RewardedProxy()
                AdType.BANNER -> BannerProxy()
                AdType.NATIVE_AD -> NativeProxy()
                else -> null
            }
        }

    }

}