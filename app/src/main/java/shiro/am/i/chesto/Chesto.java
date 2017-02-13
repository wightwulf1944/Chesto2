package shiro.am.i.chesto;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import shiro.am.i.chesto.retrofitDanbooru.Danbooru;
import timber.log.Timber;

/**
 * Created by Shiro on 7/29/2016.
 */
public final class Chesto extends Application {

    private static Chesto instance;
    private static SharedPreferences sharedPreferences;
    private static Danbooru danbooru;
    private static Danbooru safebooru;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());

            StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build();
            StrictMode.setThreadPolicy(threadPolicy);

            StrictMode.VmPolicy vmPolicy = new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build();
            StrictMode.setVmPolicy(vmPolicy);
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        RealmConfiguration defaultConfig = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(defaultConfig);

    }

    public static Chesto getInstance() {
        return instance;
    }

    public static SharedPreferences getPreferences() {
        return sharedPreferences;
    }

    public static Danbooru getDanbooru() {
        if (danbooru == null) {
            danbooru = new Retrofit.Builder()
                    .baseUrl("http://danbooru.donmai.us/")
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(Danbooru.class);
        }
        return danbooru;
    }

    public static Danbooru getSafebooru() {
        if (safebooru == null) {
            safebooru = new Retrofit.Builder()
                    .baseUrl("http://safebooru.donmai.us/")
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(Danbooru.class);
        }
        return safebooru;
    }
}
