package shiro.am.i.chesto.activitypost;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;

import jp.wasabeef.glide.transformations.BlurTransformation;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.model.Post;
import shiro.am.i.chesto.viewmodel.PostAlbum;

import static com.bumptech.glide.load.DecodeFormat.PREFER_RGB_565;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.errorOf;
import static com.bumptech.glide.request.RequestOptions.formatOf;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

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

        RequestBuilder<Drawable> thumb = Glide.with(parentActivity)
                .load(post.getSmallFileUrl())
                .apply(bitmapTransform(new BlurTransformation(1)))
                .apply(formatOf(PREFER_RGB_565))
                .apply(diskCacheStrategyOf(DiskCacheStrategy.DATA));

        Glide.with(parentActivity)
                .load(post.getLargeFileUrl())
                .thumbnail(thumb)
                .apply(placeholderOf(R.drawable.image_placeholder))
                .apply(errorOf(R.drawable.image_broken))
                .apply(diskCacheStrategyOf(DiskCacheStrategy.DATA))
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
