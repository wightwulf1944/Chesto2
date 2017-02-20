package shiro.am.i.chesto.activitySearch;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import shiro.am.i.chesto.Chesto;
import shiro.am.i.chesto.retrofitDanbooru.Tag;
import timber.log.Timber;

/**
 * Created by Shiro on 30/01/2017.
 */

final class TagRepository {

    interface OnTagRepoUpdateListener {
        void onTagRepoUpdate(List<Tag> results);
    }

    private final PublishSubject<String> publishSubject;
    private RealmResults<Tag> results;
    private OnTagRepoUpdateListener onTagRepoUpdateListener;

    TagRepository() {
        publishSubject = PublishSubject.create();
        publishSubject.observeOn(AndroidSchedulers.mainThread())
                .doOnNext(s -> {
                    results = Realm.getDefaultInstance()
                            .where(Tag.class)
                            .contains("name", s, Case.INSENSITIVE)
                            .findAllSorted("postCount", Sort.DESCENDING);
                    onTagRepoUpdateListener.onTagRepoUpdate(results);
                })
                .debounce(300, TimeUnit.MILLISECONDS)
                .flatMap(s -> Chesto.getDanbooru().searchTags(s + "*"))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        tags -> {
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(tags);
                            realm.commitTransaction();
                            onTagRepoUpdateListener.onTagRepoUpdate(results);
                        },
                        throwable -> Timber.e(throwable, "Error fetching tag suggestions")
                );
    }

    void setOnTagRepoUpdateListener(OnTagRepoUpdateListener l) {
        onTagRepoUpdateListener = l;
    }

    void setQuery(String s) {
        publishSubject.onNext(s);
    }
}
