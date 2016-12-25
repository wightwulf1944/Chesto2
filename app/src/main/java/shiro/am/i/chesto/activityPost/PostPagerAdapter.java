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
    private final PhotoViewRecycler photoViewRecycler;
    private final Queue<ViewHolder> recycleQueue;
    private final Queue<ViewHolder> loadNewQueue;

    PostPagerAdapter(AppCompatActivity parent) {
        mParent = parent;
        photoViewRecycler = new PhotoViewRecycler(parent, 3);
        recycleQueue = new LinkedList<>();
        loadNewQueue = new LinkedList<>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ViewHolder vh = new ViewHolder(position);
        loadNewQueue.add(vh);
        return vh;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewHolder vh = ((ViewHolder) object);
        recycleQueue.add(vh);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        while (!recycleQueue.isEmpty()) {
            final ViewHolder vh = recycleQueue.remove();
            photoViewRecycler.recyclePhotoView(vh.photoView);
            container.removeView(vh.photoView);

            Glide.clear(vh.photoView);
        }

        while (!loadNewQueue.isEmpty()) {
            final ViewHolder vh = loadNewQueue.remove();
            vh.photoView = photoViewRecycler.getPhotoView();
            container.addView(vh.photoView);

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
