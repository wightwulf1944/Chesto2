package shiro.am.i.chesto.activityPost;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import shiro.am.i.chesto.PostList;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.retrofitDanbooru.Post;

/**
 * Created by UGZ on 8/23/2016.
 */
public class PostPagerAdapter extends PagerAdapter {

    private static final PostList postList = PostList.getInstance();
    private final FragmentActivity mParent;

    PostPagerAdapter(FragmentActivity parent) {
        mParent = parent;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final ImageView imageView = new ImageView(mParent);
        final Post post = postList.get(position);

        final RequestManager requestManager = Glide.with(mParent);
        requestManager
                .load(postList.get(position).getFileUrl())
                .thumbnail(requestManager.load(post.getPreviewFileUrl()))
                .error(R.drawable.ic_image_broken)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
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
