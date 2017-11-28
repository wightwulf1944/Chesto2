package shiro.am.i.chesto.activitymain;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.listener.Listener1;
import shiro.am.i.chesto.model.Post;
import shiro.am.i.chesto.viewmodel.PostAlbum;

import static com.bumptech.glide.load.DecodeFormat.PREFER_RGB_565;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.errorOf;
import static com.bumptech.glide.request.RequestOptions.formatOf;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

/**
 * Created by Shiro on 8/4/2016.
 */
final class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private final AppCompatActivity parentActivity;

    private final PostAlbum postAlbum;

    private Listener1<Integer> itemClickedListener;

    MainAdapter(AppCompatActivity parentActivity, PostAlbum album) {
        this.parentActivity = parentActivity;
        postAlbum = album;
    }

    void setOnItemClickedListener(Listener1<Integer> listener) {
        itemClickedListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        ImageView imageView = new ImageView(context);
        return new ViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post post = postAlbum.get(position);

        Glide.with(parentActivity)
                .load(post.getSmallFileUrl())
                .apply(bitmapTransform(new RoundedCornersTransformation(5, 0)))
                .apply(placeholderOf(R.drawable.image_placeholder))
                .apply(errorOf(R.drawable.image_broken))
                .apply(diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .apply(formatOf(PREFER_RGB_565))
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return postAlbum.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;

        ViewHolder(ImageView v) {
            super(v);
            imageView = v;
            imageView.setOnClickListener(view ->
                    itemClickedListener.onEvent(getAdapterPosition())
            );
        }
    }
}
