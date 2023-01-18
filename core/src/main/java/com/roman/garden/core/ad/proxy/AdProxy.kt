package com.roman.garden.core.ad.proxy

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.roman.garden.ad.base.AdStatus
import com.roman.garden.ad.base.AdType
import com.roman.garden.ad.base.AdUnit
import com.roman.garden.ad.base.AdUnitManager
import com.roman.garden.core.ad.AdEasy
import com.roman.garden.core.ad.AdListener
import com.roman.garden.core.ad.EventListener
import com.roman.garden.core.ad.platform.PlatformManager


internal abstract class AdProxy : AdapterListener {

    var adListener: AdListener? = null
    var eventListener: EventListener? = null
    private var loadedAds: MutableList<AdUnit?> = mutableListOf()
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var isInit: Boolean = false

    private var runnable: Runnable = Runnable {
        run {
            if (getAdType() == AdType.INTERSTITIAL || getAdType() == AdType.REWARDED_VIDEO){
                return@Runnable
            }
            findHighPriceAdUnit().let {
                if (it == null)
                    load(AdEasy.instance.getContext(), null)
            }
            handler.postDelayed(runnable, 60 * 1000)
        }
    }

    protected abstract fun getAdType(): AdType
    protected abstract fun getPartListener(): AdListener?
    abstract fun setPartListener(adListener: AdListener?)

    private fun init() {
        if (!isInit) {
            isInit = true
            AdapterListenerProxy.instance.setListener(getAdType(), this)
            handler.postDelayed(runnable, 60 * 1000)
        }
    }

    fun hasValidAd(): Boolean {
        loadedAds.iterator().let {
            while (it.hasNext()) {
                it.next()?.let { adUnit ->
                    PlatformManager.instance.findAdapter(adUnit)?.let { adapter ->
                        if (adapter.isValidAd(adUnit))
                            return true
                    }
                }
            }
        }
        return false
    }

    fun load(context: Context, adUnit: AdUnit?) {
        init()
        AdUnitManager.instance.findNextAdUnit(adUnit, getAdType()).let {
            var canLoad = true
            for (item in it) {
                item.let {
                    canLoad = true
                    findHighPriceAdUnit()?.let { high ->
                        if (it.price < high.price)
                            canLoad = false
                    }
                    if (canLoad) {
                        PlatformManager.instance.findAdapter(item)?.let { adapter ->
                            adapter.load(context, it)
                        }
                    }
                }
            }
        }
    }

    fun getCanShowAdUnit(): AdUnit? {
        if (loadedAds.isEmpty())
            return null
        return loadedAds.removeAt(0)
    }

    private fun findHighPriceAdUnit(): AdUnit? {
        if (loadedAds.isEmpty())
            return null
        return loadedAds[0]
    }

    override fun onAdShow(adUnit: AdUnit) {
        adListener?.let { it.onAdShow() }
        eventListener?.let { it.onAdShow(adUnit) }
        getPartListener()?.let { it.onAdShow() }
    }

    override fun onAdShowFail(adUnit: AdUnit, reason: AdStatus) {
        adListener?.let { it.onAdShowFail(reason) }
        eventListener?.let { it.onAdShowFail(adUnit, reason) }
        getPartListener()?.let { it.onAdShowFail(reason) }
    }

    override fun onAdClicked(adUnit: AdUnit) {
        adListener?.let { it.onAdClick() }
        eventListener?.let { it.onAdClicked(adUnit) }
        getPartListener()?.let { it.onAdClick() }
    }

    override fun onAdClosed(adUnit: AdUnit) {
        adListener?.let { it.onAdClose() }
        eventListener?.let { it.onAdClosed(adUnit) }
        getPartListener()?.let { it.onAdClose() }
    }

    override fun onUserRewarded(adUnit: AdUnit) {
        adListener?.let { it.onUserRewarded() }
        eventListener?.let { it.onUserRewarded(adUnit) }
        getPartListener()?.let { it.onUserRewarded() }
    }

    override fun onRequestShow(adUnit: AdUnit) {
        eventListener?.let { it.onRequestShow(adUnit) }
    }

    override fun onLoadSuccess(adUnit: AdUnit) {
        adUnit.let {
            loadedAds.apply {
                loadedAds.add(it)
                loadedAds.sortWith() { o1, o2 ->
                    return@sortWith AdUnit.compare(o1!!, o2!!)
                }
            }
        }
        adListener?.let { it.onAdLoaded() }
        eventListener?.let { it.onLoadSuccess(adUnit) }
    }

    override fun onLoadFail(adUnit: AdUnit, reason: AdStatus) {
        adListener?.let { it.onAdLoadFail(reason) }
        eventListener?.let { it.onLoadFail(adUnit, reason) }
    }

    override fun onRequestLoad(adUnit: AdUnit) {
        eventListener?.let { it.onRequestLoad(adUnit) }
    }


}