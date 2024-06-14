package com.roman.garden.ad.core.ad.manager

import com.roman.garden.adbase.AdGroup
import com.roman.garden.adbase.AdUnit

internal class BannerAdConfig : IAdConfig {

    override val adGroups: MutableList<AdGroup> = mutableListOf()
    override val adUnits: MutableList<AdUnit> = mutableListOf()
    override var refreshDuration: Int = 20

}