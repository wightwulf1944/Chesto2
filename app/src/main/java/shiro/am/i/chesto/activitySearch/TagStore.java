package shiro.am.i.chesto.activitysearch;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.util.SortedListAdapterCallback;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import shiro.am.i.chesto.Chesto;
import shiro.am.i.chesto.model.Tag;

/**
 * Created by Shiro on 3/21/2017.
 */

final class TagStore {

    private final SortedList<Tag> store;
    private RealmResults<Tag> results;

    TagStore(SearchAdapter adapter) {
        store = new SortedList<>(Tag.class, new Callback(adapter));
        adapter.setData(store);
    }

    void getTags(String tagName) {
        if (results != null) {
            results.removeAllChangeListeners();
        }

        results = Realm.getDefaultInstance()
                .where(Tag.class)
                .contains("name", tagName, Case.INSENSITIVE)
                .findAllSorted("postCount", Sort.DESCENDING);

        results.addChangeListener(tags -> store.addAll(tags));

        //TODO: requires onError method
        Chesto.getDanbooru().searchTags('*' + tagName + '*')
                .subscribe(tags -> {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(tags);
                    realm.commitTransaction();
                });

        store.beginBatchedUpdates();
        store.clear();
        store.addAll(results);
        store.endBatchedUpdates();
    }

    private static class Callback extends SortedListAdapterCallback<Tag> {

        private Callback(Adapter adapter) {
            super(adapter);
        }

        @Override
        public int compare(Tag o1, Tag o2) {
            return o2.getPostCount() - o1.getPostCount();
        }

        @Override
        public boolean areContentsTheSame(Tag oldItem, Tag newItem) {
            boolean isNameTheSame = oldItem.getName().equals(newItem.getName());
            boolean isPostCountTheSame = oldItem.getPostCount() == newItem.getPostCount();
            return isNameTheSame && isPostCountTheSame;
        }

        @Override
        public boolean areItemsTheSame(Tag item1, Tag item2) {
            return item1.getId() == item2.getId();
        }
    }
}
