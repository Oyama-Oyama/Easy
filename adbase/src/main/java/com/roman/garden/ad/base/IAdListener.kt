package com.roman.garden.ad.base

interface IAdListener {

    fun onAdShow(adUnit: AdUnit)

    fun onAdShowFail(adUnit: AdUnit, reason: AdStatus)

    fun onAdClicked(adUnit: AdUnit)

    fun onAdClosed(adUnit: AdUnit)

    fun onUserRewarded(adUnit: AdUnit)

    fun onRequestShow(adUnit: AdUnit)

}