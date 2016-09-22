package shiro.am.i.chesto;

import android.app.Application;

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

    private static Danbooru danbooru;
    private static Chesto instance;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.setDefaultConfiguration(
                new RealmConfiguration.Builder(this).build()
        );

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        danbooru = new Retrofit.Builder()
                .baseUrl("http://danbooru.donmai.us/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Danbooru.class);

        instance = this;
    }

    public static Danbooru getDanbooru() {
        return danbooru;
    }

    public static Chesto getInstance() {
        return instance;
    }
}
