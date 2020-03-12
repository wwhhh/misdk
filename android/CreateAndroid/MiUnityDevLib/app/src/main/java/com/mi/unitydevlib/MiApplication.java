package com.mi.unitydevlib;

import android.app.Application;
import android.util.Log;

import com.xiaomi.gamecenter.sdk.MiCommplatform;
import com.xiaomi.gamecenter.sdk.OnInitProcessListener;
import com.xiaomi.gamecenter.sdk.entry.MiAppInfo;

import java.util.List;

public class MiApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /** SDK initialize */
        MiAppInfo appInfo = new MiAppInfo();
        appInfo.setAppId("2882303761517239138");
        appInfo.setAppKey("5691723970138");
        MiCommplatform.Init(this, appInfo, new OnInitProcessListener() {
            @Override
            public void finishInitProcess(List<String> loginMethod, int gameConfig) {
                Log.i("Demo", "Init success");
            }

            @Override
            public void onMiSplashEnd() {//小米闪屏页结束回调，小米闪屏可配，无闪屏也会返回此回调，游戏的闪屏应当在收到此回调之后开始。
                Log.i("Demo", "onMiSplashEnd");
            }
        });
    }
}
