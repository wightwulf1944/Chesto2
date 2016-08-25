package shiro.am.i.chesto;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.fivehundredpx.greedolayout.GreedoLayoutSizeCalculator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import shiro.am.i.chesto.activityMain.MainAdapter;
import shiro.am.i.chesto.retrofitDanbooru.Danbooru;
import shiro.am.i.chesto.retrofitDanbooru.Post;

/**
 * Created by UGZ on 8/4/2016.
 */
public final class PostList extends ArrayList<Post>
        implements Callback<List<Post>>, GreedoLayoutSizeCalculator.SizeCalculatorDelegate, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = PostList.class.getSimpleName();
    private static final Danbooru danbooru = Chesto.getDanbooru();
    private static final PostList instance = new PostList();

    private MainAdapter mAdapter;
    private SwipeRefreshLayout mSwipeLayout;
    private String currentQuery;
    private int currentPage;

    private PostList() {
        // disable instantiation
    }

    public static PostList getInstance() {
        return instance;
    }

    public void setAdapter(MainAdapter adapter) {
        mAdapter = adapter;
    }

    public void setSwipeLayout(SwipeRefreshLayout swipeLayout) {
        mSwipeLayout = swipeLayout;
        mSwipeLayout.setOnRefreshListener(this);
    }

    public void newSearch(String tags) {
        currentQuery = tags;
        currentPage = 0;
        clear();
        requestMorePosts();
    }

    public void requestMorePosts() {
        danbooru.getPosts(currentQuery, ++currentPage).enqueue(this);
    }

    @Override
    public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
        final List<Post> newPostList = response.body();

        final Iterator<Post> i = newPostList.iterator();
        while (i.hasNext()) {
            if (i.next().getPreviewFileUrl() == null) {
                i.remove();
            }
        }

        if (!newPostList.isEmpty()) {
            newPostList.removeAll(this);
            final int positionStart = size();
            final int itemCount = newPostList.size();
            addAll(newPostList);
            mAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        mSwipeLayout.setRefreshing(false);
    }

    @Override
    public void onFailure(Call<List<Post>> call, Throwable t) {
        Log.e(TAG, "Error fetching more posts", t);
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
