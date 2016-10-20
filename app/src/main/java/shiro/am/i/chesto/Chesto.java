package shiro.am.i.chesto;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import shiro.am.i.chesto.retrofitDanbooru.Danbooru;
import timber.log.Timber;

/**
 * Created by Shiro on 7/29/2016.
 */
public final class Chesto extends Application {

    private static Chesto instance;
    private static Handler mainHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Realm.setDefaultConfiguration(
                new RealmConfiguration.Builder(this).build()
        );

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        mainHandler = new Handler(Looper.getMainLooper());
    }

    public static Chesto getInstance() {
        return instance;
    }

    public static Handler getMainHandler() {
        return mainHandler;
    }
}
