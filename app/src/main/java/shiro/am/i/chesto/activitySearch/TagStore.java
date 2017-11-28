package shiro.am.i.chesto.activitysearch;

import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import shiro.am.i.chesto.Chesto;
import shiro.am.i.chesto.listener.Listener1;
import shiro.am.i.chesto.model.Tag;
import shiro.am.i.chesto.notifier.Notifier1;
import shiro.am.i.chesto.subscription.Subscription;

import static io.realm.Sort.DESCENDING;

/**
 * Created by Shiro on 3/21/2017.
 */

final class TagStore {

    private final Notifier1<List<Tag>> onDatasetChangedNotifier = new Notifier1<>();

    private final Realm realm;

    private RealmResults<Tag> results;

    TagStore(Realm realm) {
        this.realm = realm;
        this.results = realm.where(Tag.class)
                .findAllSorted("postCount", DESCENDING);
    }

    List<Tag> getResults() {
        return results;
    }

    void searchTags(String tagName) {
        results.removeAllChangeListeners();

        results = realm.where(Tag.class)
                .contains("name", tagName, Case.INSENSITIVE)
                .findAllSorted("postCount", DESCENDING);
        onDatasetChangedNotifier.fireEvent(results);

        results.addChangeListener(onDatasetChangedNotifier::fireEvent);

        Chesto.getDanbooru()
                .searchTags('*' + tagName + '*')
                .flatMap(Observable::from)
                .map(Tag::new)
                .toList()
                .subscribe(
                        tags -> {
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            realm.insertOrUpdate(tags);
                            realm.commitTransaction();
                            realm.close();
                        },
                        Throwable::printStackTrace
                );
    }

    Subscription addOnDatasetChangedListener(Listener1<List<Tag>> listener) {
        return onDatasetChangedNotifier.addListener(listener);
    }
}
