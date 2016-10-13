package cn.ucai.superwechat;

import android.app.Application;

import cn.ucai.superwechat.bean.UserAvatar;
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

    public static void saveUserData(UserAvatar user) {
        if (user != null) {
            PreferencesUtil.putString(application, I.User.USER_NAME, user.getMUserName());
            PreferencesUtil.putString(application, I.User.NICK, user.getMUserNick());
            PreferencesUtil.putInt(application, I.Avatar.AVATAR_ID, user.getMAvatarId());
        }
    }

    public static String getUserName() {
        return PreferencesUtil.getString(application, I.User.USER_NAME);
    }

    public static String getNickName() {
        return PreferencesUtil.getString(application, I.User.NICK);
    }

    public static void saveAvatarPath(String path) {
        PreferencesUtil.putString(application, I.Avatar.AVATAR_PATH, path);
    }

    public static String getAavatarPath() {
        return PreferencesUtil.getString(application, I.Avatar.AVATAR_PATH);
    }
}
