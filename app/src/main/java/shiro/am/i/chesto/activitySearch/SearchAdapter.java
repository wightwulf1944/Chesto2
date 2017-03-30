package shiro.am.i.chesto.activitysearch;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import shiro.am.i.chesto.R;
import shiro.am.i.chesto.models.Tag;

/**
 * Created by Shiro on 3/20/2017.
 */
final class SearchAdapter extends Adapter<ViewHolder> {

    private final int HEADER = 0;
    private final int TAG = 1;

    private final LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;
    private SortedList<Tag> items;

    SearchAdapter(Context c) {
        inflater = LayoutInflater.from(c);
    }

    void setData(SortedList<Tag> data) {
        items = data;
    }

    void setOnItemClickListener(OnItemClickListener l) {
        onItemClickListener = l;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object data = items.get(position);

        if (data instanceof String) {
            return HEADER;
        } else if (data instanceof Tag) {
            return TAG;
        }

        throw new RuntimeException("Could not figure out item view type at position: " + position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == HEADER) {
            View view = inflater.inflate(R.layout.search_item_header, parent, false);
            return new HeaderViewHolder(view);

        } else if (viewType == TAG) {
            View view = inflater.inflate(R.layout.search_item_tag, parent, false);
            return new TagViewHolder(view);

        }
        throw new RuntimeException("Unexpected view type received: " + viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
        Object item = items.get(position);

        if (viewType == HEADER) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            String labelStr = (String) item;
            headerViewHolder.label.setText(labelStr);

        } else if (viewType == TAG) {
            TagViewHolder tagViewHolder = (TagViewHolder) holder;
            Tag tag = (Tag) item;
            tagViewHolder.postCount.setText(tag.getPostCountStr());
            tagViewHolder.name.setText(tag.getName());

        }
    }

    interface OnItemClickListener {
        void onItemClick(String itemName);
    }

    private static final class HeaderViewHolder extends ViewHolder {

        private final TextView label;

        HeaderViewHolder(View v) {
            super(v);
            label = (TextView) v;
        }
    }

    private final class TagViewHolder extends ViewHolder {

        private final TextView postCount;
        private final TextView name;

        TagViewHolder(View v) {
            super(v);
            postCount = (TextView) v.findViewById(R.id.postCount);
            name = (TextView) v.findViewById(R.id.name);
            v.setOnClickListener(v1 -> onClick());
        }

        private void onClick() {
            String itemName = name.getText().toString();
            onItemClickListener.onItemClick(itemName);
        }
    }
}
