package shiro.am.i.chesto.activityPost;

import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.databasePost.PostStore;
import shiro.am.i.chesto.retrofitDanbooru.Post;

/**
 * Created by Shiro on 8/23/2016.
 */
final class PostPagerAdapter extends PagerAdapter {

    private static final PostStore POST_STORE = PostStore.getInstance();
    private final AppCompatActivity mParent;

    PostPagerAdapter(AppCompatActivity parent) {
        mParent = parent;
        POST_STORE.setPagerAdapter(this);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final ImageView imageView = ImageViewRecycler.getView(mParent, container);
        final Post post = POST_STORE.get(position);

        Picasso.with(mParent)
                .load(post.getFileUrl())
                .fetch();

        Picasso.with(mParent)
                .load(post.getPreviewFileUrl())
                .placeholder(R.drawable.ic_image_placeholder)
                .transform(new BlurTransformation(mParent, 1))
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        loadLarge();
                    }

                    @Override
                    public void onError() {
                        loadLarge();
                    }

                    void loadLarge() {
                        Picasso.with(mParent)
                                .load(post.getFileUrl())
                                .error(R.drawable.ic_image_placeholder)
                                .noPlaceholder()
                                .fit()
                                .centerInside()
                                .into(imageView);
                    }
                });

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
