package com.roman.garden.adbase

class AdGroup {

    private val adUnits = mutableListOf<AdUnit>()
    var group: String? = null

    fun add(adUnit: AdUnit) = adUnits.add(adUnit)

    fun realFindNextAdUnit(adUnit: AdUnit?): AdUnit? =
        adUnit?.let { adUnits.indexOf(it).let { index -> adUnits.elementAtOrNull(index + 1) } }
            ?: adUnits.firstOrNull()

    fun realSortAdUnits() = adUnits.sortedBy { it.price }


}