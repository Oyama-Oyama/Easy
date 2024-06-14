package com.roman.garden.ad.core.ad.manager

import com.roman.garden.adbase.AdGroup
import com.roman.garden.adbase.AdUnit

internal interface IAdConfig {

    val adGroups: MutableList<AdGroup>
    val adUnits: MutableList<AdUnit>
    var refreshDuration: Int

    fun addAdUnit(adUnit: AdUnit) {
        adUnits.add(adUnit)
        adGroups.find { group -> group.let { it.group.equals(adUnit.group) } }?.add(adUnit)
            ?: adGroups.also {
                val group = AdGroup().apply {
                    this.add(adUnit)
                    this.group = adUnit.group
                }
                it.add(group)
            }
    }

    fun findNextAdUnits(adUnit: AdUnit?): List<AdUnit> {
        return adUnit?.let {
            adGroups.find { group ->
                group.let { g ->
                    g.group.equals(it.group)
                }
            }?.realFindNextAdUnit(it)?.let { ad ->
                mutableListOf(ad)
            } ?: emptyList<AdUnit>()
        } ?: mutableListOf<AdUnit>().apply {
            adGroups.forEach { adGroup ->
                adGroup.realFindNextAdUnit(null)?.let { adUnit ->
                    add(adUnit)
                }
            }
        }
    }

    fun sortAdUnits() = adGroups.map { group -> group.realSortAdUnits() }


}