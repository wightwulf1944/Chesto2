package shiro.am.i.chesto.databasePost;

import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fivehundredpx.greedolayout.GreedoLayoutSizeCalculator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import shiro.am.i.chesto.Chesto;
import shiro.am.i.chesto.activityMain.MainAdapter;
import shiro.am.i.chesto.retrofitDanbooru.Post;
import timber.log.Timber;

/**
 * Created by Shiro on 8/4/2016.
 * TODO: make this class subscribable that returns a DiffUtil.DiffResult to observers
 */
public final class PostStore
        extends ArrayList<Post>
        implements
        Callback<List<Post>>,
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
        Chesto.getDanbooru()
                .getPosts(currentQuery, ++currentPage)
                .enqueue(this);
    }

    @Override
    public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
        final List<Post> fetchedPosts = response.body();
        final Iterator<Post> i = fetchedPosts.iterator();
        while (i.hasNext()) {
            if (i.next().getPreviewFileUrl() == null) {
                i.remove();
            }
        }

        if (!fetchedPosts.isEmpty()) {
            final int size1 = size();
            removeAll(fetchedPosts);
            final int size2 = size();
            addAll(fetchedPosts);
            final int size3 = size();

            if (mMainAdapter != null) {
                mMainAdapter.notifyItemRangeChanged(size2, size1 - size2);
                mMainAdapter.notifyItemRangeInserted(size1, size3 - size1);
            }
            if (mPagerAdapter != null) {
                mPagerAdapter.notifyDataSetChanged();
            }
        }

        if (mObserver != null) {
            mObserver.onUpdateDone();
        }
    }

    @Override
    public void onFailure(Call<List<Post>> call, Throwable t) {
        Timber.e(t, "Error fetching more posts");
    }

    @Override
    public double aspectRatioForIndex(int i) {
        if (i >= size()) {
            return 1.0;
        } else {
            Post post = get(i);
            return (double) post.getImageWidth() / post.getImageHeight();
        }
    }

    @Override
    public void onRefresh() {
        newSearch(currentQuery);
    }
}
