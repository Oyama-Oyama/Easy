package com.roman.garden.ad.core.ad.factory

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.roman.garden.adbase.AdUnit

internal interface IAdProxy {

    fun startLoad()

    fun stopLoad()

    fun canShow(): Boolean

    fun show(viewGroup: ViewGroup? = null)

    fun showNativeAd(viewGroup: ViewGroup?, size: Int = 1, @LayoutRes templateLayoutId: Int = -1)

    suspend fun findCanShowAdUnit(): AdUnit?

    fun setListener()

    fun resume()

    fun pause()

    fun closeBanner()

    fun closeNative(viewGroup: ViewGroup?)

    fun destroy()

    fun onLoadSuccess(adUnit: AdUnit)

    fun onLoadFail(adUnit: AdUnit)

    fun onClosed()

}