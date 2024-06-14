package com.roman.garden.base.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri

open class GpUtil {

    fun isInstalled(context: Context, pkgName: String?): Boolean {
        try {
            if (context.packageManager.getPackageInfo(pkgName!!, 0) != null) return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun openPlayStore(context: Context, pkg: String) {
        val i = Intent("android.intent.action.VIEW")
        val url = fixUrl(pkg)
        val isGooglePlay = isPlayStoreUrl(url)
        if (isGooglePlay) {
            if (hasPlayStore(context)) {
                launchPlayStore(context, url, i)
            } else {
                launchBrowser(context, url, i)
            }
        } else {
            launchBrowser(context, url, i)
        }
    }

    private fun fixUrl(url: String): String {
        return if (url.startsWith("http")) url else "https://play.google.com/store/apps/details?id=$url"
    }

    private fun isPlayStoreUrl(url: String): Boolean {
        return url.startsWith("https://play.google.com/store/apps/details?id=")
    }

    private fun hasPlayStore(context: Context): Boolean {
        return try {
            val var2 = context.packageManager.getApplicationEnabledSetting("com.android.vending")
            var2 == 0 || var2 == 1
        } catch (var21: Exception) {
            false
        }
    }

    private fun launchPlayStore(context: Context, url: String, i: Intent) {
        var url = url
        val marketUrl = "market://details?id="
        url = url.replace("https://play.google.com/store/apps/details?id=", "market://details?id=")
        i.setPackage("com.android.vending")
        i.data = Uri.parse(url)
        launchApp(context, i)
    }

    private fun launchApp(context: Context, i: Intent) {
        try {
            if (context is Activity) {
                context.startActivity(i)
            } else {
                context.startActivity(i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        } catch (var3: Exception) {
            var3.printStackTrace()
        }
    }

    private fun launchBrowser(context: Context, url: String?, i: Intent) {
        i.data = Uri.parse(url)
        val browserPackageName = getDefaultBrowserPackageName(context, i)
        if (browserPackageName != null) {
            i.setPackage(browserPackageName)
        }
        launchApp(context, i)
    }

    private fun getDefaultBrowserPackageName(context: Context, intent: Intent?): String? {
        val packageManager = context.packageManager
        val resolveInfos: List<*> = packageManager.queryIntentActivities(intent!!, 0)
        return if (resolveInfos.size > 0) {
            val info = resolveInfos[0] as ResolveInfo
            info.activityInfo.packageName
        } else {
            null
        }
    }


}