package shiro.am.i.chesto.activitySearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import shiro.am.i.chesto.Chesto;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.retrofitDanbooru.Danbooru;
import shiro.am.i.chesto.retrofitDanbooru.Tag;

/**
 * Created by Shiro on 7/29/2016.
 */
public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsAdapter.ViewHolder>
        implements RealmChangeListener<Realm>, Callback<List<Tag>> {

    private static final String TAG = SuggestionsAdapter.class.getSimpleName();
    private static final Danbooru danbooru = Chesto.getDanbooru();
    private static final Realm realm = Realm.getDefaultInstance();

    private final LayoutInflater inflater;
    private final SearchView mSearchView;
    private RealmResults<Tag> suggestionsList;

    SuggestionsAdapter(Context context, SearchView searchView) {
        inflater = LayoutInflater.from(context);
        mSearchView = searchView;

        suggestionsList = realm.where(Tag.class)
                .findAllSorted("postCount", Sort.DESCENDING);

        realm.addChangeListener(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_tag, parent, false));
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
            final String currentQ = mSearchView.getQuery().toString();
            mSearchView.setQuery(currentQ + name.getText(), false);
        }
    }

    @Override
    public void onChange(Realm element) {
        notifyDataSetChanged();
    }

    void setQuery(String s) {
        //  get the last word
        final int wordIndex = s.lastIndexOf(" ");
        if (wordIndex >= 0) {
            s = s.substring(wordIndex + 1);
        }

        suggestionsList = realm.where(Tag.class)
                .beginsWith("name", s, Case.INSENSITIVE)
                .findAllSorted("postCount", Sort.DESCENDING);
        notifyDataSetChanged();
        danbooru.searchTags(s + "*").enqueue(this);
    }

    @Override
    public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(response.body());
        realm.commitTransaction();
        // above lines triggers onChange()
    }

    @Override
    public void onFailure(Call<List<Tag>> call, Throwable t) {
        //TODO:
    }
}
