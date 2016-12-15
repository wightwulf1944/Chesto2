package shiro.am.i.chesto.activitySearch;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import shiro.am.i.chesto.Chesto;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.U;
import shiro.am.i.chesto.retrofitDanbooru.Danbooru;
import shiro.am.i.chesto.retrofitDanbooru.Tag;
import timber.log.Timber;

/**
 * Created by Shiro on 7/29/2016.
 */
final class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final SearchView mSearchView;
    private final PublishSubject<String> subject;

    private RealmResults<Tag> list;
    private String currentQuery;

    SearchAdapter(SearchView searchView) {
        inflater = LayoutInflater.from(Chesto.getInstance());
        mSearchView = searchView;
        subject = PublishSubject.create();

        list = Realm.getDefaultInstance()
                .where(Tag.class)
                .findAllSorted("postCount", Sort.DESCENDING);

        subject.observeOn(AndroidSchedulers.mainThread())
                .map(CharSequence::toString)
                .map(U::getLastWord)
                .doOnNext(s -> {
                    currentQuery = s;
                    list = Realm.getDefaultInstance()
                            .where(Tag.class)
                            .contains("name", s, Case.INSENSITIVE)
                            .findAllSorted("postCount", Sort.DESCENDING);
                    notifyDataSetChanged();
                })
                .debounce(300, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .flatMap(s -> Danbooru.api.searchTags(s + "*"))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        tags -> {
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(tags);
                            realm.commitTransaction();
                            notifyDataSetChanged();
                        },
                        throwable -> Timber.e(throwable, "Error fetching tag suggestions")
                );
    }

    void setQuery(String query) {
        subject.onNext(query);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_tag_searchsuggestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Tag tag = list.get(position);
        Timber.d(tag.getName());
        holder.postCount.setText(tag.getPostCountStr());
        holder.name.setText(tag.getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView postCount;
        private final TextView name;

        private ViewHolder(View v) {
            super(v);
            postCount = (TextView) v.findViewById(R.id.postCount);
            name = (TextView) v.findViewById(R.id.name);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            String text = mSearchView.getQuery()
                    .toString()
                    .replaceFirst(currentQuery, name.getText().toString());

            mSearchView.setQuery(text, false);
        }
    }
}
