package shiro.am.i.chesto.activitypost;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;

import shiro.am.i.chesto.R;
import shiro.am.i.chesto.listener.Listener1;
import shiro.am.i.chesto.model.Post;

/**
 * Created by Subaru Tashiro on 7/19/2017.
 * TODO: identify why LayoutInflater views ignores some xml attributes such as layout_wrapBefore
 */

public class DetailTagAdapter extends RecyclerView.Adapter<DetailTagAdapter.ViewHolder> {

    private final ArrayList<Pair<Integer, String>> items = new ArrayList<>();

    private Listener1<String> onItemClickListener;

    void setOnItemClickListener(Listener1<String> listener) {
        onItemClickListener = listener;
    }

    void setCurrentPost(Post post) {
        items.clear();
        items.ensureCapacity(post.getTagCount());
        setCategoryTags("Copyrights:", R.layout.item_post_tag_copyright, post.getTagStringCopyright());
        setCategoryTags("Characters:", R.layout.item_post_tag_character, post.getTagStringCharacter());
        setCategoryTags("Artist:", R.layout.item_post_tag_artist, post.getTagStringArtist());
        setCategoryTags("Tags:", R.layout.item_post_tag_general, post.getTagStringGeneral());
        setCategoryTags("Meta:", R.layout.item_post_tag_meta, post.getTagStringMeta());
        notifyDataSetChanged();
    }

    private void setCategoryTags(String categoryLabel, int layout, String tags) {
        if (tags.isEmpty()) return;

        items.add(new Pair<>(R.layout.item_post_label, categoryLabel));
        for (String tag : tags.split(" ")) {
            items.add(new Pair<>(layout, tag));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).first;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(viewType, parent, false);
        ViewHolder vh = new ViewHolder(view);

        if (viewType == R.layout.item_post_label) {
            FlexboxLayoutManager.LayoutParams layoutParams = (FlexboxLayoutManager.LayoutParams) view.getLayoutParams();
            layoutParams.setWrapBefore(true);
        } else {
            view.setOnClickListener(v -> {
                int adapterPosition = vh.getAdapterPosition();
                String tagString = items.get(adapterPosition).second;
                onItemClickListener.onEvent(tagString);
            });
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String itemString = items.get(position).second;
        TextView textView = (TextView) holder.itemView;
        textView.setText(itemString);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
