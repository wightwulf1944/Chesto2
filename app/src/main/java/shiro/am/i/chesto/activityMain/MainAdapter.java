package shiro.am.i.chesto.activitymain;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import shiro.am.i.chesto.R;
import shiro.am.i.chesto.model.Post;
import shiro.am.i.chesto.viewmodel.PostAlbum;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.errorOf;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

/**
 * Created by Shiro on 8/4/2016.
 */
final class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private final PostAlbum postAlbum;
    private OnItemClickedListener itemClickedListener;

    MainAdapter(PostAlbum album) {
        postAlbum = album;
    }

    void setOnItemClickedListener(OnItemClickedListener listener) {
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
        ImageView imageView = holder.imageView;
        AppCompatActivity parentActivity = (AppCompatActivity) imageView.getContext();

        Glide.with(parentActivity)
                .load(post.getSmallFileUrl())
                .apply(placeholderOf(R.drawable.image_placeholder))
                .apply(errorOf(R.drawable.image_broken))
                .apply(diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .into(imageView);
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
                    itemClickedListener.onItemClicked(getAdapterPosition())
            );
        }
    }

    interface OnItemClickedListener {
        void onItemClicked(int position);
    }
}
