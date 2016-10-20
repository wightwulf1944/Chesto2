package shiro.am.i.chesto.databasePost;

import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fivehundredpx.greedolayout.GreedoLayoutSizeCalculator;

import java.util.ArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import shiro.am.i.chesto.activityMain.MainAdapter;
import shiro.am.i.chesto.retrofitDanbooru.Danbooru;
import shiro.am.i.chesto.retrofitDanbooru.Post;
import timber.log.Timber;

/**
 * Created by Shiro on 8/4/2016.
 */
public final class PostStore
        extends ArrayList<Post>
        implements
        GreedoLayoutSizeCalculator.SizeCalculatorDelegate,
        SwipeRefreshLayout.OnRefreshListener {

    private static final PostStore instance = new PostStore();

    private MainAdapter mMainAdapter;
    private PagerAdapter mPagerAdapter;
    private Observer mObserver;
    private String currentQuery;
    private int currentPage;

    private PostStore() {
        // disable instantiation
        currentQuery = "lowres rating:safe";
        requestMorePosts();
    }

    public static PostStore getInstance() {
        return instance;
    }

    public void setAdapter(MainAdapter adapter) {
        mMainAdapter = adapter;
    }

    public void setPagerAdapter(PagerAdapter adapter) {
        mPagerAdapter = adapter;
    }

    public void setObserver(Observer observer) {
        mObserver = observer;
    }

    public void newSearch(String tags) {
        currentQuery = tags;
        currentPage = 0;
        clear();
        requestMorePosts();
    }

    public void requestMorePosts() {
        Danbooru.api.getPosts(currentQuery, ++currentPage)
                .subscribeOn(Schedulers.io())
                .flatMap(Observable::from)
                .filter(post -> post.getPreviewFileUrl() != null)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> mObserver.onUpdateDone())
                .doOnTerminate(() -> mObserver.onUpdateDone())
                .subscribe(

                        post -> {
                            final int i = lastIndexOf(post);
                            if (i != -1) {
                                set(i, post);
                                mMainAdapter.notifyItemChanged(i);
                            } else {
                                add(post);
                                mMainAdapter.notifyItemInserted(size());
                            }
                        },

                        throwable -> Timber.e(throwable, "Error fetching more posts"),

                        () -> mPagerAdapter.notifyDataSetChanged()
                );
    }

    @Override
    public double aspectRatioForIndex(int i) {
        if (i >= size()) {
            return 1.0;
        } else {
            final Post post = get(i);
            return (double) post.getImageWidth() / post.getImageHeight();
        }
    }

    @Override
    public void onRefresh() {
        newSearch(currentQuery);
    }
}
