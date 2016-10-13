package cn.ucai.superwechat;

import android.app.Application;

import cn.ucai.superwechat.utils.I;
import cn.ucai.superwechat.utils.PreferencesUtil;

/**
 * Created by Shinelon on 2016/10/12.
 */

public class SWApplication extends Application {

    static SWApplication application;
    static boolean isLogined = false;

    public static SWApplication getInstance() {
        if (application == null) {
            application = new SWApplication();
        }
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        isLogined = hasLogined();
    }

    public static boolean hasLogined() {
        isLogined = PreferencesUtil.getBoolean(application, I.IS_LOGINED, false);
        return isLogined;
    }

    public static void setLogined(boolean isLogined) {
        PreferencesUtil.putBoolean(application, I.IS_LOGINED, isLogined);
    }
}
