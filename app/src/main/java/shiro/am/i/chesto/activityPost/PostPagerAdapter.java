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

import shiro.am.i.chesto.PostList;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.retrofitDanbooru.Post;

/**
 * Created by UGZ on 8/23/2016.
 */
public final class PostPagerAdapter extends PagerAdapter {

    private static final PostList postList = PostList.getInstance();
    private final AppCompatActivity mParent;

    PostPagerAdapter(AppCompatActivity parent) {
        mParent = parent;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final ImageView imageView = ImageViewRecycler.getView(mParent, container);
        final Post post = postList.get(position);

        final RequestManager requestManager = Glide.with(mParent);

        final DrawableRequestBuilder thumbnail = requestManager
                .load(post.getPreviewFileUrl())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);

        requestManager
                .load(postList.get(position).getFileUrl())
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
        return postList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
