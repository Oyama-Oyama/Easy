package com.roman.garden.core.google

import android.app.Activity
import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.games.Games
import com.google.android.gms.games.leaderboard.LeaderboardVariant

internal class PlayServicesUtil {

    companion object {
        val RC_LEADERBOARD_UI = 9004

        /**
         * 显示全部排行榜
         */
        fun showAllLeaderboards(
            activity: Activity,
            account: GoogleSignInAccount,
            listener: IGoogleListener? = null
        ) {
            Games.getLeaderboardsClient(
                activity.applicationContext,
                account
            ).allLeaderboardsIntent.addOnCompleteListener { task ->
                when (task.isSuccessful) {
                    true -> {
                        activity.startActivityForResult(task.result, RC_LEADERBOARD_UI)
                        listener?.onSuccess()
                    }
                    else -> listener?.onFail(task.exception)
                }
            }
        }

        /**
         * 显示指定名称排行榜
         */
        fun showLeaderboard(
            activity: Activity,
            account: GoogleSignInAccount,
            displayName: String?,
            listener: IGoogleListener? = null
        ) {
            if (displayName == null) {
                showAllLeaderboards(activity, account, listener)
                return
            }
            Games.getLeaderboardsClient(activity.applicationContext, account)
                .getLeaderboardIntent(displayName)
                .addOnCompleteListener { task ->
                    when (task.isSuccessful) {
                        true -> {
                            activity.startActivityForResult(task.result, RC_LEADERBOARD_UI)
                            listener?.onSuccess()
                        }
                        else -> listener?.onFail(task.exception)
                    }
                }
        }

        /**
         * 更新排行榜分数
         */
        fun submitScore(
            context: Context,
            account: GoogleSignInAccount,
            displayName: String,
            score: Long
        ) {
            Games.getLeaderboardsClient(context, account).apply {
                this.submitScore(displayName, score)
            }
        }

        fun loadCurrentPlayerLeaderboardScore(
            context: Context,
            account: GoogleSignInAccount,
            displayName: String,
            listener: (rank: String?, score: String?) -> Unit
        ) {
            Games.getLeaderboardsClient(context, account).loadCurrentPlayerLeaderboardScore(
                displayName,
                LeaderboardVariant.TIME_SPAN_ALL_TIME,
                LeaderboardVariant.COLLECTION_PUBLIC
            ).addOnSuccessListener {
                listener(it.get()?.displayRank, it.get()?.displayScore)
            }
        }

        fun showAllAchievements(
            activity: Activity,
            account: GoogleSignInAccount,
            listener: IGoogleListener? = null
        ) {
            Games.getAchievementsClient(
                activity,
                account
            ).achievementsIntent.addOnCompleteListener { task ->
                when (task.isSuccessful) {
                    true -> {
                        activity.startActivityForResult(task.result, RC_LEADERBOARD_UI)
                        listener?.onSuccess()
                    }
                    else -> listener?.onFail(task.exception)
                }
            }
        }

        fun unlockAchievement(
            context: Context,
            account: GoogleSignInAccount,
            achievement: String,
            immediate: Boolean = false,
            listener: IGoogleListener? = null
        ) {
            when (immediate) {
                false -> Games.getAchievementsClient(context, account).unlock(achievement)
                true -> {
                    Games.getAchievementsClient(context, account).unlockImmediate(achievement)
                        .addOnCompleteListener { task ->
                            when (task.isSuccessful) {
                                true -> listener?.onSuccess()
                                false -> listener?.onFail(task.exception)
                            }
                        }
                }
            }
        }

        fun incrementAchievement(
            context: Context,
            account: GoogleSignInAccount,
            achievement: String,
            progress: Int,
            immediate: Boolean = false,
            listener: IGoogleListener? = null
        ) {
            when (immediate) {
                false -> Games.getAchievementsClient(context, account)
                    .increment(achievement, progress)
                true -> {
                    Games.getAchievementsClient(context, account)
                        .incrementImmediate(achievement, progress).addOnCompleteListener { task ->
                            when (task.isSuccessful) {
                                true -> listener?.onSuccess()
                                false -> listener?.onFail(task.exception)
                            }
                        }
                }
            }
        }

        fun revealAchievement(
            context: Context,
            account: GoogleSignInAccount,
            achievement: String,
            immediate: Boolean = false,
            listener: IGoogleListener? = null
        ) {
            when (immediate) {
                false -> Games.getAchievementsClient(context, account).reveal(achievement)
                true -> {
                    Games.getAchievementsClient(context, account).revealImmediate(achievement)
                        .addOnCompleteListener { task ->
                            when (task.isSuccessful) {
                                true -> listener?.onSuccess()
                                false -> listener?.onFail(task.exception)
                            }
                        }
                }
            }
        }

        fun setStepsAchievement(
            context: Context,
            account: GoogleSignInAccount,
            achievement: String,
            step: Int,
            immediate: Boolean = false,
            listener: IGoogleListener? = null
        ) {
            when (immediate) {
                false -> Games.getAchievementsClient(context, account).setSteps(achievement, step)
                true -> {
                    Games.getAchievementsClient(context, account)
                        .setStepsImmediate(achievement, step)
                        .addOnCompleteListener { task ->
                            when (task.isSuccessful) {
                                true -> listener?.onSuccess()
                                false -> listener?.onFail(task.exception)
                            }
                        }
                }
            }
        }
    }

}