package com.roman.garden.proxy.unity;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;

import androidx.annotation.Nullable;

import com.roman.garden.base.BaseImpl;
import com.roman.garden.base.log.Logger;
import com.roman.garden.core.Easy;
import com.roman.garden.core.listener.IAdListener;
import com.unity3d.player.UnityPlayer;

public class Unity{

    private static void sendMessage(String methodName, String arg){
        try{
            UnityPlayer.UnitySendMessage("NativeSdkListener", methodName, arg);

        } catch (Exception e){
            Logger.Companion.e("send message error");
        }
    }

    private static Handler uiHandler;

    private static Handler getUIHandler(){
        if(uiHandler == null){
            uiHandler = new Handler(Looper.getMainLooper());
        }
        return uiHandler;
    }

    public static void onCreate(){
        Easy.Companion.getInstance().setAppOpenAdListener(new IAdListener() {
            @Override
            public void onUserRewarded() {
            }

            @Override
            public void onClosed(boolean rewarded) {
                sendMessage("AdClosed", "AppOpen|" + (rewarded ? "1" : "0"));
            }

            @Override
            public void onShowFail(@Nullable String reason) {
                sendMessage("AdShowFail", "AppOpen");
                Logger.Companion.w("AppOpenAd Show fail:" + reason);
            }

            @Override
            public void onClicked() {
                sendMessage("AdClicked", "AppOpen");
            }

            @Override
            public void onShow() {
                sendMessage("AdShown", "AppOpen");
            }
        });
        Easy.Companion.getInstance().setInterstitialListener(new IAdListener() {
            @Override
            public void onShow() {
                Logger.Companion.d("Interstitial shown");
                sendMessage("AdShown", "Interstitial");
            }

            @Override
            public void onClicked() {
                sendMessage("AdClicked", "Interstitial");
            }

            @Override
            public void onShowFail(@Nullable String reason) {
                sendMessage("AdShowFail", "Interstitial");
                Logger.Companion.w("Interstitial Show fail:" + reason);
            }

            @Override
            public void onClosed(boolean rewarded) {
                Logger.Companion.d("Interstitial close");
                sendMessage("AdClosed", "Interstitial|" + (rewarded ? "1" : "0"));
            }

            @Override
            public void onUserRewarded() {
            }
        });
        Easy.Companion.getInstance().setRewardedListener(new IAdListener() {
            @Override
            public void onShow() {
                sendMessage("AdShown", "Rewarded");
            }

            @Override
            public void onClicked() {
                sendMessage("AdClicked", "Rewarded");
            }

            @Override
            public void onShowFail(@Nullable String reason) {
                sendMessage("AdShowFail", "Rewarded");
                Logger.Companion.w("Rewarded ad Show fail:" + reason);
            }

            @Override
            public void onClosed(boolean rewarded) {
                sendMessage("AdClosed", "Rewarded|" + (rewarded ? "1" : "0"));
            }

            @Override
            public void onUserRewarded() {
            }
        });
    }

    public static void destroy(Application application){
        Easy.Companion.getInstance().destroy(application);
    }

    public static boolean hasBanner(){
        return Easy.Companion.getInstance().hasBanner();
    }

    public static void showBanner(){
        getUIHandler().post(new Runnable() {
            @Override
            public void run() {
                Easy.Companion.getInstance().showBanner(null, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
            }
        });
    }

    public static void showBanner(int gravity){
        getUIHandler().post(new Runnable() {
            @Override
            public void run() {
                Easy.Companion.getInstance().showBanner(null, gravity);
            }
        });
    }

    public static void closeBanner(){
        getUIHandler().post(new Runnable() {
            @Override
            public void run() {
                Easy.Companion.getInstance().closeBanner();
            }
        });
    }

    public static boolean hasInterstitial(){
        return Easy.Companion.getInstance().hasInterstitial();
    }

    public static void showInterstitial(){
        getUIHandler().post(new Runnable() {
            @Override
            public void run() {
                Easy.Companion.getInstance().showInterstitial();
            }
        });
    }

    public static boolean hasRewarded(){
        return Easy.Companion.getInstance().hasRewarded();
    }

    public static void showRewarded(){
        getUIHandler().post(new Runnable() {
            @Override
            public void run() {
                Easy.Companion.getInstance().showRewarded();
            }
        });
    }

    public static void toast(String msg){
        getUIHandler().post(new Runnable() {
            @Override
            public void run() {
                Easy.Companion.getInstance().toast(UnityPlayer.currentActivity, msg);
            }
        });
    }

    public static void rate(){
        getUIHandler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (UnityPlayer.currentActivity != null) {
                        BaseImpl.Companion.rate(UnityPlayer.currentActivity, UnityPlayer.currentActivity.getPackageName());
                    }
                } catch (Exception e){
                    Logger.Companion.e("rate err:" + e.getMessage());
                }
            }
        });
    }

}
