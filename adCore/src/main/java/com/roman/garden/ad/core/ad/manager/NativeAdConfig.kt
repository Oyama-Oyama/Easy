package com.roman.garden.ad.core.ad.manager

import com.roman.garden.adbase.AdGroup
import com.roman.garden.adbase.AdUnit

internal class NativeAdConfig : IAdConfig {

    override val adGroups: MutableList<AdGroup> = mutableListOf()
    override var refreshDuration: Int = 20
    override val adUnits: MutableList<AdUnit> = mutableListOf()
}