package shiro.am.i.chesto;

import android.app.Application;
import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import shiro.am.i.chesto.retrofitDanbooru.Danbooru;

/**
 * Created by Shiro on 7/29/2016.
 */
public final class Chesto extends Application {

    private static Danbooru danbooru;
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        final RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfig);

        danbooru = new Retrofit.Builder()
                .baseUrl("http://danbooru.donmai.us/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Danbooru.class);

        appContext = this;
    }

    public static Danbooru getDanbooru() {
        return danbooru;
    }

    public static Context getAppContext() {
        return appContext;
    }
}
