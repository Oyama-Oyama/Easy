package com.roman.garden.core.google

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

interface IGoogleSignListener {
    fun onSignInSuccess(id: String?, displayName: String?, email: String?, avatar: Uri?)
    fun onSignInFail(e: java.lang.Exception?)
}

interface IGoogleSignOutListener {
    fun onSignOutSuccess()
    fun onSignOutFail(e: java.lang.Exception?)
}

class GoogleSignUtil {
    val RC_SIGN_IN: Int = 9001
    var mGoogleSignInClient: GoogleSignInClient? = null
    var signListener: IGoogleSignListener? = null

    private fun buildSignClient(activity: Activity): GoogleSignInClient? {
        if (mGoogleSignInClient == null) {
            mGoogleSignInClient = GoogleSignIn.getClient(
                activity, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build()
            )
        }
        return mGoogleSignInClient
    }

    /**
     *  静默登陆
     */
    fun signInSilently(activity: Activity, listener: IGoogleSignListener? = null) {
        val mGoogleSignInClient = buildSignClient(activity)
        mGoogleSignInClient?.silentSignIn()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                listener?.onSignInSuccess(
                    task.result.id, task.result.displayName, task.result.email, task.result.photoUrl
                )
            } else {
                listener?.onSignInFail(task.exception)
            }
        } ?: listener?.onSignInFail(Exception("GoogleSignInClient is null"))
    }

    /**
     *  正常登陆，需要在Activity内主动调用
     *  @{onActivityResult(requestCode: Int, resultCode: Int, intent: Intent)} 方法
     */
    fun startSignIn(activity: Activity, listener: IGoogleSignListener? = null) {
        val mGoogleSignInClient = buildSignClient(activity)
        mGoogleSignInClient?.apply {
            this@GoogleSignUtil.signListener = listener
            activity.startActivityIfNeeded(this.signInIntent, RC_SIGN_IN)
        } ?: signListener?.onSignInFail(Exception("GoogleSignInClient is null"))
    }

    /**
     *  解析登陆结果
     *  正常登陆时，需要被Activity主动调用
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignIn.getSignedInAccountFromIntent(intent).let { task ->
                    signListener?.onSignInSuccess(
                        task.result.id,
                        task.result.displayName,
                        task.result.email,
                        task.result.photoUrl
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                signListener?.onSignInFail(e)
            }
        }
    }

    /**
     * 退出登陆
     */
    fun signOut(activity: Activity, listener: IGoogleSignOutListener? = null) {
        val mGoogleSignInClient = buildSignClient(activity)
        mGoogleSignInClient?.signOut()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                listener?.onSignOutSuccess()
            } else {
                listener?.onSignOutFail(task.exception)
            }
        } ?: listener?.onSignOutFail(Exception("GoogleSignInClient is null"))
    }

    /**
     * 当前登陆账号
     */
    fun getSignInAccount(context: Context): GoogleSignInAccount? =
        GoogleSignIn.getLastSignedInAccount(context)

    /**
     * 登陆状态
     */
    fun isSignIn(context: Context): Boolean = !((getSignInAccount(context)?.isExpired) ?: true)

}