package com.roman.garden.adbase

interface IAdCallback {

    fun onAdShow(adUnit: AdUnit)

    fun onAdShowFail(adUnit: AdUnit, reason: AdError)

    fun onAdClicked(adUnit: AdUnit)

    fun onAdClosed(adUnit: AdUnit, rewarded:Boolean = false)

    fun onUserRewarded(adUnit: AdUnit)

    fun onRequestShow(adUnit: AdUnit)

}