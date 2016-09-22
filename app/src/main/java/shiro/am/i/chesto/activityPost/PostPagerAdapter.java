package shiro.am.i.chesto.activityPost;

import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import shiro.am.i.chesto.PostStore;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.retrofitDanbooru.Post;

/**
 * Created by Shiro on 8/23/2016.
 */
final class PostPagerAdapter extends PagerAdapter {

    private static final PostStore POST_STORE = PostStore.getInstance();
    private final AppCompatActivity mParent;

    PostPagerAdapter(AppCompatActivity parent) {
        mParent = parent;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final ImageView imageView = ImageViewRecycler.getView(mParent, container);
        final Post post = POST_STORE.get(position);

        final RequestManager requestManager = Glide.with(mParent);

        final DrawableRequestBuilder thumbnail = requestManager
                .load(post.getPreviewFileUrl())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);

        requestManager
                .load(POST_STORE.get(position).getFileUrl())
                .thumbnail(thumbnail)
                .error(R.drawable.ic_image_broken)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ImageViewRecycler.recycleView((ImageView) object, container);
    }

    @Override
    public int getCount() {
        return POST_STORE.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
