package com.roman.garden.base.interfaces

import android.app.Application
import android.view.Gravity
import android.view.ViewGroup
import androidx.annotation.LayoutRes

interface IAdCore {

    fun init(application: Application, eventCallback: IEvent, testMode: Boolean = false)

    fun destroy(application: Application)

    fun hasBanner(): Boolean

    fun showBanner(
        viewGroup: ViewGroup?,
        gravity: Int
    )

    fun closeBanner()

    fun hasInterstitial(): Boolean

    fun showInterstitial()

    fun hasRewarded(): Boolean

    fun showRewarded()

    fun hasAppOpenAd(): Boolean

    fun showAppOpen()

    fun hasNative(): Boolean

    fun showNative(viewGroup: ViewGroup?, size: Int = 1, @LayoutRes templateLayoutId: Int = -1)

    fun closeNative(viewGroup: ViewGroup?)

}