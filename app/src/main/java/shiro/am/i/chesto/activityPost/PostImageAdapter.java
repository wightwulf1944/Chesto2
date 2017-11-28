package shiro.am.i.chesto.activitypost;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;

import jp.wasabeef.glide.transformations.BlurTransformation;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.model.Post;
import shiro.am.i.chesto.viewmodel.PostAlbum;

/**
 * Created by Shiro on 11/28/2017.
 */

public class PostImageAdapter extends RecyclerView.Adapter<PostImageAdapter.ViewHolder> {

    private final AppCompatActivity parentActivity;
    private final PostAlbum postAlbum;

    PostImageAdapter(AppCompatActivity parentActivity, PostAlbum postAlbum) {
        this.parentActivity = parentActivity;
        this.postAlbum = postAlbum;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_post_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post post = postAlbum.get(position);

        holder.itemView.setVisibility(View.VISIBLE);

        DrawableRequestBuilder thumb = Glide.with(parentActivity)
                .load(post.getSmallFileUrl())
                .bitmapTransform(new BlurTransformation(parentActivity, 1))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);

        Glide.with(parentActivity)
                .load(post.getLargeFileUrl())
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_broken)
                .thumbnail(thumb)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into((PhotoView) holder.itemView);
    }

    @Override
    public int getItemCount() {
        return postAlbum.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
