package shiro.am.i.chesto;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Shiro on 4/3/2017.
 */

public final class Settings {

    private static final String KEY_HIDE_NSFW = "key_hide_nsfw";
    private static SharedPreferences sharedPreferences;

    static void init(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean hideNsfw() {
        return sharedPreferences.getBoolean(KEY_HIDE_NSFW, true);
    }

    public static void setHideNsfw(boolean hideNsfw) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_HIDE_NSFW, hideNsfw);
        editor.apply();
    }
}
