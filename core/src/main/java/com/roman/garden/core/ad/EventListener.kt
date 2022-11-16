package com.roman.garden.core.ad

import com.roman.garden.ad.base.AdStatus
import com.roman.garden.ad.base.AdUnit
import com.roman.garden.core.ad.proxy.AdapterListener

open class EventListener : AdapterListener {

    override fun onAdShow(adUnit: AdUnit) {

    }

    override fun onAdShowFail(adUnit: AdUnit, reason: AdStatus) {

    }

    override fun onAdClicked(adUnit: AdUnit) {

    }

    override fun onAdClosed(adUnit: AdUnit) {

    }

    override fun onUserRewarded(adUnit: AdUnit) {

    }

    override fun onRequestShow(adUnit: AdUnit) {

    }

    override fun onLoadSuccess(adUnit: AdUnit) {

    }

    override fun onLoadFail(adUnit: AdUnit, reason: AdStatus) {

    }

    override fun onRequestLoad(adUnit: AdUnit) {

    }
}