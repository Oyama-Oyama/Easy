package com.roman.garden.ad.core.ad.loader

import android.view.ViewGroup
import com.roman.garden.ad.core.ad.manager.AdConfigManager
import com.roman.garden.adbase.AdType
import com.roman.garden.adbase.AdUnit
import com.roman.garden.base.log.Logger
import java.lang.ref.WeakReference

internal class BannerProxy : BaseProxy() {

    var container: WeakReference<ViewGroup>? = null
    var destroy: Boolean = false

    override fun getType(): AdType = AdType.BANNER

    override fun isComparePrice(): Boolean = false

    private val repeatShowBannerRunnable: Runnable = Runnable {
        if (destroy) return@Runnable
        container?.let {
            Logger.e("repeat show banner task")
            show(it.get())
        }
    }

    private fun repeatShowBanner() {
        handler.removeCallbacks(repeatShowBannerRunnable)
        handler.postDelayed(
            repeatShowBannerRunnable,
            AdConfigManager.instance.getBannerRefreshDuration() * 1000L
        )
    }

    override fun destroy() {
        destroy = true
        handler.removeCallbacks(repeatShowBannerRunnable)
        super.destroy()
    }

    /**
     *  同native 会有同步竞争问题
     */
    override fun show(viewGroup: ViewGroup?) {
        // TODO: bug need fix
        if (viewGroup != null && container?.get() != null && container?.get() == viewGroup) {
            if (System.currentTimeMillis() - showStartTime < AdConfigManager.instance.getBannerRefreshDuration() * 920L) {
                Logger.e("last banner not show enough time")
                return
            }
        } else {
            closeBanner()
        }
        viewGroup?.let {
            container = WeakReference(it)
            super.show(viewGroup)
            repeatShowBanner()
            realLoad(null)
        } ?: Logger.e("show ${getType().value} error, ViewGroup can't be null")
    }

    override fun onLoadSuccess(adUnit: AdUnit) {
        super.onLoadSuccess(adUnit)
        container?.let {
            Logger.e("banner load success and call show")
            show(it.get())
        }
    }

    /**
     *  可以在应用退出时关闭
     *  如果在页面切换间关闭和展示，要注意生命周期问题
     */
    override fun closeBanner() {
        super.closeBanner()
        // TODO: bug need fix
        container?.get()?.removeAllViews()
        handler.removeCallbacks(repeatShowBannerRunnable)
        container = null
        showStartTime = -1L
    }


}