package shiro.am.i.chesto.activityPost;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.LinkedList;
import java.util.Queue;

import jp.wasabeef.glide.transformations.BlurTransformation;
import shiro.am.i.chesto.PostStore;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.retrofitDanbooru.Post;

/**
 * Created by Shiro on 8/23/2016.
 */
final class PostPagerAdapter extends PagerAdapter {

    private final AppCompatActivity mParent;

    PostPagerAdapter(AppCompatActivity parent) {
        mParent = parent;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final ImageView imageView = ImageViewRecycler.getView(mParent, container);
        final Post post = PostStore.get(position);

        Glide.with(mParent)
                .load(post.getFileUrl())
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .thumbnail(
                        Glide.with(mParent)
                                .load(post.getPreviewFileUrl())
                                .bitmapTransform(new BlurTransformation(mParent, 1))
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                )
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        final ImageView imageView = (ImageView) object;
        Glide.clear(imageView);
        ImageViewRecycler.recycleView(imageView, container);
    }

    @Override
    public int getCount() {
        return PostStore.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    // Handles preserving and recycling views for this adapter
    private static final class ImageViewRecycler {
        private static final Queue<ImageView> recycledViews = new LinkedList<>();

        private static ImageView getView(Context context, ViewGroup container) {
            final ImageView view;

            if (recycledViews.isEmpty()) {
                view = new ImageView(context);
            } else {
                view = recycledViews.remove();
            }

            container.addView(view);
            return view;
        }

        private static void recycleView(ImageView view, ViewGroup container) {
            recycledViews.add(view);
            container.removeView(view);
        }
    }
}
