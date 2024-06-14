package com.roman.gurdan.sudo.pro

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.roman.garden.base.log.Logger
import com.roman.garden.core.Easy
import com.roman.garden.core.google.IGoogleSignListener
import com.roman.garden.core.listener.IAdListener
import java.lang.Exception

class MainActivity : Base() {

    var time: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val frameLayout = findViewById<FrameLayout>(R.id.nativeContainer)
        val duration = findViewById<TextView>(R.id.duration)

        findViewById<MaterialButton>(R.id.showBanner).setOnClickListener { _ ->
            Easy.instance.showBanner()
        }
        findViewById<MaterialButton>(R.id.closeBanner).setOnClickListener { _ ->
//            Easy.instance.closeBanner()
            Intent().apply {
                this.action = "test.action.banner"
                startActivity(this)
            }
        }
        findViewById<MaterialButton>(R.id.showRewarded).setOnClickListener { _ ->
            if (Easy.instance.hasRewarded()) {
                Easy.instance.setRewardedListener(object : IAdListener {

                    override fun onShow() {
                        super.onShow()
                        showToast("rewarded show")
                    }

                    override fun onClicked() {
                        super.onClicked()
                        showToast("rewarded clicked")
                    }

                    override fun onShowFail(reason: String?) {
                        super.onShowFail(reason)
                        showToast("rewarded show fail")
                    }

                    override fun onClosed(rewarded: Boolean) {
                        super.onClosed(rewarded)
                        showToast("rewarded close:$rewarded")
                    }

                    override fun onUserRewarded() {
                        super.onUserRewarded()
                        showToast("rewarded rewarded user")
                    }
                })
                Easy.instance.showRewarded()
            } else showToast("没有激励视频")
        }
        findViewById<MaterialButton>(R.id.showInterstitial).setOnClickListener { _ ->
            if (Easy.instance.hasInterstitial()) {
                Easy.instance.setInterstitialListener(object : IAdListener {

                    override fun onShow() {
                        super.onShow()
                        showToast("Interstitial show")
                    }

                    override fun onClicked() {
                        super.onClicked()
                        showToast("Interstitial clicked")
                    }

                    override fun onShowFail(reason: String?) {
                        super.onShowFail(reason)
                        showToast("Interstitial show fail")
                    }

                    override fun onClosed(rewarded: Boolean) {
                        super.onClosed(rewarded)
                        showToast("rewarded close:$rewarded")
                    }
                })
                Easy.instance.showInterstitial()
            } else showToast("没有插屏")
        }
        findViewById<MaterialButton>(R.id.showNative).setOnClickListener { _ ->
            if (Easy.instance.hasNative())
                Easy.instance.showNative(frameLayout)
            else showToast("没有native广告")
        }
        findViewById<MaterialButton>(R.id.closeNative).setOnClickListener { _ ->
//            Easy.instance.closeNative(frameLayout)
            Intent().apply {
                this.action = "test.action.native"
                startActivity(this)
            }
        }
        findViewById<MaterialButton>(R.id.silentSignIn).setOnClickListener {
            Easy.instance.signInSilently(this@MainActivity, object : IGoogleSignListener{
                override fun onSignInSuccess(
                    id: String?,
                    displayName: String?,
                    email: String?,
                    avatar: Uri?
                ) {
                    showToast("登陆成功：$displayName, $email")
                }

                override fun onSignInFail(e: Exception?) {
                    e?.printStackTrace()
                    showToast("登陆失败: ${e?.message}")
                }

            })
        }
        findViewById<MaterialButton>(R.id.signIn).setOnClickListener {
            Easy.instance.signIn(this@MainActivity, object : IGoogleSignListener{
                override fun onSignInSuccess(
                    id: String?,
                    displayName: String?,
                    email: String?,
                    avatar: Uri?
                ) {
                    showToast("登陆成功：$displayName, $email")

                }

                override fun onSignInFail(e: Exception?) {
                    e?.printStackTrace()
                    showToast("登陆失败: ${e?.message}")
                }

            })
        }
        findViewById<MaterialButton>(R.id.signOut).setOnClickListener {
            Easy.instance.signOut(this@MainActivity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Easy.instance.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        Easy.instance.showBanner()
    }

    override fun onStop() {
        super.onStop()
        // Easy.instance.closeBanner()
    }

    fun showToast(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
        Logger.e("from listener:$str")
    }

}