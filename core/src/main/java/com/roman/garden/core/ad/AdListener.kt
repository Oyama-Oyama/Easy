package com.roman.garden.core.ad

import com.roman.garden.ad.base.AdStatus

open abstract class AdListener {

    open fun onAdLoaded(){}

    open fun onAdLoadFail(reason: AdStatus){}

    open fun onAdShow(){}

    open fun onAdShowFail(reason: AdStatus){}

    open fun onAdClick(){}

    open fun onAdClose(){}

    open fun onUserRewarded(){}

}