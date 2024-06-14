package com.roman.garden.ad.core.ad.loader

import android.view.ViewGroup
import com.roman.garden.adbase.AdType

import com.roman.garden.base.log.Logger
import kotlinx.coroutines.launch

internal class AppOpenProxy : BaseProxy() {


    override fun getType(): AdType = AdType.APP_OPEN_AD

    override fun onClosed() {
        super.onClosed()
        realLoad(null)
    }

    override fun show(viewGroup: ViewGroup?) {
        launch {
            if (canShow()) {
                super.show(viewGroup)
            } else {
                Logger.e("no valid ${getType().value} to show, reload")
                realLoad(null)
            }
        }
    }

}