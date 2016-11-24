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
import timber.log.Timber;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by Shiro on 8/23/2016.
 */
final class PostPagerAdapter extends PagerAdapter {

    private final AppCompatActivity mParent;
    private final Queue<ViewHolder> recycleQueue;
    private final Queue<ViewHolder> loadNewQueue;
    //    private final Queue<ViewHolder> setPrimaryQueue;
    private final Queue<PhotoView> recycledPhotoViews;
    private int currentPrimaryPosition;

    PostPagerAdapter(AppCompatActivity parent) {
        mParent = parent;
        recycleQueue = new LinkedList<>();
        loadNewQueue = new LinkedList<>();
//        setPrimaryQueue = new LinkedList<>();
        recycledPhotoViews = new LinkedList<>();
        recycledPhotoViews.add(new PhotoView(parent));
        recycledPhotoViews.add(new PhotoView(parent));
        recycledPhotoViews.add(new PhotoView(parent));
        currentPrimaryPosition = -1;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ViewHolder vh = new ViewHolder(position);
        loadNewQueue.add(vh);
        return vh;
    }

//    @Override
//    public void setPrimaryItem(ViewGroup container, int position, Object object) {
//        if (position != currentPrimaryPosition) {
//            ViewHolder vh = ((ViewHolder) object);
//            setPrimaryQueue.add(vh);
//            currentPrimaryPosition = position;
//        }
//    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewHolder vh = ((ViewHolder) object);
        recycleQueue.add(vh);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        while (!recycleQueue.isEmpty()) {
            final ViewHolder vh = recycleQueue.remove();
            Timber.d("%s Recycled", vh.position);
            recycledPhotoViews.add(vh.photoView);
            container.removeView(vh.photoView);

            Glide.clear(vh.photoView);
        }

        while (!loadNewQueue.isEmpty()) {
            final ViewHolder vh = loadNewQueue.remove();
            Timber.d("%s Loaded", vh.position);
            vh.photoView = recycledPhotoViews.remove();
            container.addView(vh.photoView);

//            Glide.with(mParent)
//                    .load(vh.post.getSmallFileUrl())
//                    .bitmapTransform(new BlurTransformation(mParent, 1))
//                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                    .into(vh.photoView);
            Glide.with(mParent)
                    .load(vh.post.getLargeFileUrl())
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_broken)
                    .thumbnail(
                            Glide.with(mParent)
                                    .load(vh.post.getSmallFileUrl())
                                    .bitmapTransform(new BlurTransformation(mParent, 1))
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    )
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(vh.photoView);
        }

//        while (!setPrimaryQueue.isEmpty()) {
//            final ViewHolder vh = setPrimaryQueue.remove();
//            Timber.d("%s Set primary", vh.position);
//
//            Glide.with(mParent)
//                    .load(vh.post.getLargeFileUrl())
//                    .placeholder(R.drawable.ic_image_placeholder)
//                    .error(R.drawable.ic_image_broken)
//                    .thumbnail(
//                            Glide.with(mParent)
//                                    .load(vh.post.getSmallFileUrl())
//                                    .bitmapTransform(new BlurTransformation(mParent, 1))
//                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                    )
//                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                    .into(vh.photoView);
//        }

        Timber.d("Update finished");
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
        private final Post post;
        private PhotoView photoView;

        private ViewHolder(int position) {
            this.position = position;
            post = PostStore.get(position);
        }
    }
}
