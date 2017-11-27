package shiro.am.i.chesto;

import android.app.Application;
import android.os.StrictMode;

import com.squareup.otto.Bus;

import io.realm.Realm;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;
import shiro.am.i.chesto.model.Danbooru;
import timber.log.Timber;

/**
 * Created by Shiro on 7/29/2016.
 */
public final class Chesto extends Application {

    private static Chesto instance;
    private static Danbooru danbooru;
    private static Danbooru safebooru;
    private static Bus eventBus;

    @Override
    public void onCreate() {
        super.onCreate();

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

        instance = this;

        eventBus = new Bus();

        Settings.init(this);

        RxJavaCallAdapterFactory callAdapterFactory = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
        GsonConverterFactory converterFactory = GsonConverterFactory.create();

        danbooru = new Retrofit.Builder()
                .baseUrl("http://danbooru.donmai.us/")
                .addCallAdapterFactory(callAdapterFactory)
                .addConverterFactory(converterFactory)
                .build()
                .create(Danbooru.class);

        safebooru = new Retrofit.Builder()
                .baseUrl("http://safebooru.donmai.us/")
                .addCallAdapterFactory(callAdapterFactory)
                .addConverterFactory(converterFactory)
                .build()
                .create(Danbooru.class);

        Realm.init(this);
    }

    public static Chesto getInstance() {
        return instance;
    }

    public static Danbooru getDanbooru() {
        if (Settings.hideNsfw()) {
            return safebooru;
        } else {
            return danbooru;
        }
    }

    public static Bus getEventBus() {
        return eventBus;
    }
}
