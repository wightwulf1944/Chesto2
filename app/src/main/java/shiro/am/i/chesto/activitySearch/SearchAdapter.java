package shiro.am.i.chesto.activitySearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import shiro.am.i.chesto.R;
import shiro.am.i.chesto.retrofitDanbooru.Tag;

/**
 * Created by Shiro on 7/29/2016.
 */
final class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private List<Tag> data;
    private OnItemClickListener onItemClickListener;

    SearchAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        data = Collections.emptyList();
    }

    void setData(List<Tag> l) {
        data = l;
    }

    void setOnItemClickListener(OnItemClickListener l) {
        onItemClickListener = l;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_tag_searchsuggestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Tag tag = data.get(position);
        holder.postCount.setText(tag.getPostCountStr());
        holder.name.setText(tag.getName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView postCount;
        private final TextView name;

        private ViewHolder(View v) {
            super(v);
            postCount = (TextView) v.findViewById(R.id.postCount);
            name = (TextView) v.findViewById(R.id.name);
            v.setOnClickListener(v1 -> onClick());
        }

        private void onClick() {
            String itemName = data.get(getAdapterPosition()).getName();
            onItemClickListener.onItemClick(itemName);
        }
    }

    interface OnItemClickListener {
        void onItemClick(String itemName);
    }
}
