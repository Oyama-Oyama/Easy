package com.roman.garden.adbase

class AdManager private constructor(){

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { AdManager() }
    }




}