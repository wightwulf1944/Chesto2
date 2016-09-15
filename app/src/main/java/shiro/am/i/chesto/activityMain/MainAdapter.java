package shiro.am.i.chesto.activityMain;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import shiro.am.i.chesto.PostList;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.activityPost.PostActivity;

/**
 * Created by UGZ on 8/4/2016.
 */
public final class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private static final PostList postList = PostList.getInstance();
    private final AppCompatActivity mParent;

    MainAdapter(AppCompatActivity parent) {
        mParent = parent;
        postList.setAdapter(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new ImageView(mParent));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position >= postList.size() - 5) {
            postList.requestMorePosts();
        }

        Glide.with(mParent)
                .load(postList.get(position).getPreviewFileUrl())
                .error(R.drawable.ic_image_broken)
                .placeholder(R.drawable.ic_image_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return postList.size();
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
