package shiro.am.i.chesto.activitySearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.retrofitDanbooru.Danbooru;
import shiro.am.i.chesto.retrofitDanbooru.Tag;
import timber.log.Timber;

/**
 * Created by Shiro on 7/29/2016.
 */
final class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private static final Realm realm = Realm.getDefaultInstance();

    private final LayoutInflater inflater;
    private final SearchView mSearchView;
    private RealmResults<Tag> suggestionsList;
    private String currentWord = "";

    SearchAdapter(Context context, SearchView searchView) {
        inflater = LayoutInflater.from(context);
        mSearchView = searchView;

        suggestionsList = realm.where(Tag.class)
                .findAllSorted("postCount", Sort.DESCENDING);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_tag_searchsuggestion, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Tag tag = suggestionsList.get(position);
        holder.postCount.setText(tag.getPostCountStr());
        holder.name.setText(tag.getName());
    }

    @Override
    public int getItemCount() {
        return suggestionsList.size();
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
                    .replaceFirst(currentWord, name.getText().toString());

            mSearchView.setQuery(text, false);
        }
    }

    void setQuery(String s) {
        currentWord = s;
        suggestionsList = realm.where(Tag.class)
                .contains("name", s, Case.INSENSITIVE)
                .findAllSorted("postCount", Sort.DESCENDING);
        notifyDataSetChanged();

        Danbooru.api.searchTags(s + "*")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        tags -> {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(tags);
                            realm.commitTransaction();
                            notifyDataSetChanged();
                        },
                        throwable -> Timber.e(throwable, "Error fetching tag suggestions")
                );
    }
}
