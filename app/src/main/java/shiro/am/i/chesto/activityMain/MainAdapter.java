package shiro.am.i.chesto.activityMain;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import shiro.am.i.chesto.databasePost.PostStore;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.activityPost.PostActivity;

/**
 * Created by Shiro on 8/4/2016.
 */
public final class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private static final PostStore POST_STORE = PostStore.getInstance();
    private final AppCompatActivity mParent;

    MainAdapter(AppCompatActivity parent) {
        mParent = parent;
        POST_STORE.setAdapter(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new ImageView(mParent));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position >= POST_STORE.size() - 5) {
            POST_STORE.requestMorePosts();
        }

        Picasso.with(mParent)
                .load(POST_STORE.get(position).getPreviewFileUrl())
                .error(R.drawable.ic_image_broken)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return POST_STORE.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView imageView;

        public ViewHolder(ImageView v) {
            super(v);
            imageView = v;
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mParent, PostActivity.class);
            intent.putExtra("default", getAdapterPosition());
            mParent.startActivity(intent);
        }
    }
}
