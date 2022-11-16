package com.roman.garden.ad.base

class AdUnitManager {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
            AdUnitManager()
        }
    }

    private var interstitialGroups: Array<AdGroup>? = null
    private var rewardedGroups: Array<AdGroup>? = null
    private var bannerGroups: Array<AdGroup>? = null
    private var nativeGroups: Array<AdGroup>? = null
    private var appOpenAdGroups: Array<AdGroup>? = null
    private var configAdTypes: Set<AdType> = setOf()
    private var bannerRefreshDuration: Long = 30

    fun addAdUnit(adUnit: AdUnit){
        if (!adUnit.isValid()){
            Logger.w("AdUnit is invalid:$adUnit")
            return
        }
        configAdTypes = configAdTypes.plus(adUnit.adType)
        when(adUnit.adType){
            AdType.INTERSTITIAL -> interstitialGroups = plusAdUnit(interstitialGroups, adUnit)
            AdType.REWARDED_VIDEO -> rewardedGroups = plusAdUnit(rewardedGroups, adUnit)
            AdType.BANNER -> bannerGroups = plusAdUnit(bannerGroups, adUnit)
            AdType.NATIVE_AD -> nativeGroups = plusAdUnit(nativeGroups, adUnit)
            AdType.APP_OPEN_AD -> appOpenAdGroups = plusAdUnit(appOpenAdGroups, adUnit)
        }
    }

    private fun plusAdUnit(array: Array<AdGroup>?, adUnit: AdUnit) : Array<AdGroup> {
        var array1 = array ?: arrayOf()
        val group = adUnit.group
        val adGroups = array1.filter { adGroup ->
            adGroup.group == group
        }
        if (adGroups.isNotEmpty()){
            adGroups[0].add(adUnit)
            return array1
        }
        return AdGroup().let {
            it.group = group
            it.add(adUnit)
            array1.plus(it)
        }
    }

    fun findNextAdUnit(adUnit: AdUnit?, adType: AdType) : Array<AdUnit> {
        when(adType){
            AdType.INTERSTITIAL -> return realFindNextAdUnit(interstitialGroups, adUnit)
            AdType.REWARDED_VIDEO -> return realFindNextAdUnit(rewardedGroups, adUnit)
            AdType.BANNER -> return realFindNextAdUnit(bannerGroups, adUnit)
            AdType.NATIVE_AD -> return realFindNextAdUnit(nativeGroups, adUnit)
            AdType.APP_OPEN_AD -> return realFindNextAdUnit(appOpenAdGroups, adUnit)
        }
        return arrayOf()
    }

    private fun realFindNextAdUnit(array: Array<AdGroup>?, adUnit: AdUnit?) : Array<AdUnit> {
        var _result: Array<AdUnit> = arrayOf()
        if (array == null)
            return arrayOf()
        return _result.apply {
            array.forEach { adGroup ->
                adGroup.realFindNextAdUnit(adUnit)?.let {
                    _result = _result.plus(it)
                }
            }
            return _result
        }
    }

    fun sortAdUnits(){
        interstitialGroups?.map { adGroup -> adGroup.realSortAdUnits() }
        rewardedGroups?.map { adGroup -> adGroup.realSortAdUnits() }
        bannerGroups?.map { adGroup -> adGroup.realSortAdUnits() }
        nativeGroups?.map { adGroup -> adGroup.realSortAdUnits() }
        appOpenAdGroups?.map { adGroup -> adGroup.realSortAdUnits() }
    }

    fun setBannerRefreshDuration(duration: Long = 30){
        this.bannerRefreshDuration = duration
    }

    fun getBannerRefreshDuration() : Long {
        return this.bannerRefreshDuration
    }

    fun getConfigAdTypes() : Set<AdType> {
        return configAdTypes
    }

    fun hasSetAdType(adType: AdType) : Boolean {
        configAdTypes.forEach { item ->
            if (adType == item)
                return true
        }
        return false
    }

}