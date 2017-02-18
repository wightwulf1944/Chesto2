package shiro.am.i.chesto.activityPost;

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
    private final Queue<ViewHolder> createQueue;
    private final Queue<ViewHolder> destroyQueue;
    private final Queue<PhotoView> usablePhotoViews;

    PostPagerAdapter(AppCompatActivity parent) {
        mParent = parent;
        createQueue = new LinkedList<>();
        destroyQueue = new LinkedList<>();

        usablePhotoViews = new LinkedList<>();
        usablePhotoViews.add(new PhotoView(mParent));
        usablePhotoViews.add(new PhotoView(mParent));
        usablePhotoViews.add(new PhotoView(mParent));
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ViewHolder vh = new ViewHolder(position);
        createQueue.add(vh);
        return vh;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewHolder vh = ((ViewHolder) object);
        destroyQueue.add(vh);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (!createQueue.isEmpty() && !destroyQueue.isEmpty()) {
            final ViewHolder newVH = createQueue.remove();
            final ViewHolder oldVH = destroyQueue.remove();
            newVH.photoView = oldVH.photoView;
            glide(newVH);
        }

        while (!createQueue.isEmpty()) {
            final ViewHolder vh = createQueue.remove();
            vh.photoView = usablePhotoViews.remove();

            if (vh.photoView.getParent() == null) {
                container.addView(vh.photoView);
            }

            glide(vh);
        }

        while (!destroyQueue.isEmpty()) {
            final ViewHolder vh = destroyQueue.remove();
            usablePhotoViews.add(vh.photoView);
            Glide.clear(vh.photoView);
        }
    }

    private void glide(ViewHolder vh) {
        final Post post = PostStore.get(vh.position);
        Glide.with(mParent)
                .load(post.getLargeFileUrl())
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_broken)
                .thumbnail(
                        Glide.with(mParent)
                                .load(post.getSmallFileUrl())
                                .bitmapTransform(new BlurTransformation(mParent, 1))
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                )
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(vh.photoView);
    }

    @Override
    public int getCount() {
        return PostStore.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((ViewHolder) object).photoView.equals(view);
    }

    private static final class ViewHolder {
        private final int position;
        private PhotoView photoView;

        private ViewHolder(int position) {
            this.position = position;
        }
    }
}
