package com.roman.garden.ad.base

class AdGroup {

    var adUnits: Array<AdUnit> = arrayOf()
    var group: String? = null

    fun add(adUnit: AdUnit){
        adUnits = adUnits.plus(adUnit)
    }

    fun realFindNextAdUnit(adUnit: AdUnit?) : AdUnit? {
        if (adUnit == null){
            if (adUnits.size == 0)
                return null
            return adUnits[0]
        }
        var _index = adUnits.indexOf(adUnit) + 1
        if (_index < adUnits.size){
            return adUnits[_index]
        }
        return null
    }

    fun realSortAdUnits(){
        adUnits.sortWith { p0, p1 ->
            return@sortWith AdUnit.compare(p0, p1)
        }
    }

}