package shiro.am.i.chesto.activityPost;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.LinkedList;
import java.util.Queue;

import jp.wasabeef.glide.transformations.BlurTransformation;
import shiro.am.i.chesto.PostStore;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.retrofitDanbooru.Post;
import uk.co.senab.photoview.PhotoView;

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
        final PhotoView photoView = PhotoViewRecycler.getView(mParent, container);
        final Post post = PostStore.get(position);

        Glide.with(mParent)
                .load(post.getLargeFileUrl())
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .thumbnail(
                        Glide.with(mParent)
                                .load(post.getSmallFileUrl())
                                .bitmapTransform(new BlurTransformation(mParent, 1))
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                )
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(photoView);

        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        final PhotoView photoView = (PhotoView) object;
        Glide.clear(photoView);
        PhotoViewRecycler.recycleView(photoView, container);
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
    private static final class PhotoViewRecycler {
        private static final Queue<PhotoView> recycledViews = new LinkedList<>();

        private static PhotoView getView(Context context, ViewGroup container) {
            final PhotoView view;

            if (recycledViews.isEmpty()) {
                view = new PhotoView(context);
            } else {
                view = recycledViews.remove();
            }

            container.addView(view);
            return view;
        }

        private static void recycleView(PhotoView view, ViewGroup container) {
            recycledViews.add(view);
            container.removeView(view);
        }
    }
}
