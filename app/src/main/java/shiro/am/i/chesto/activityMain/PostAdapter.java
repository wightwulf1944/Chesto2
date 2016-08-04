package shiro.am.i.chesto.activityMain;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import shiro.am.i.chesto.PostList;
import shiro.am.i.chesto.R;

/**
 * Created by UGZ on 8/4/2016.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private static final PostList postList = PostList.getInstance();
    private final Context mContext;

    PostAdapter(Context context) {
        mContext = context;
        postList.setAdapter(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new ImageView(mContext));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(mContext)
                .load(postList.get(position).getPreviewFileUrl())
                .error(R.drawable.ic_image_broken)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final ImageView imageView;

        public ViewHolder(ImageView imageview) {
            super(imageview);
            imageView = imageview;
        }
    }
}
