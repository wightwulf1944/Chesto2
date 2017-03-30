package shiro.am.i.chesto.models;

import com.fivehundredpx.greedolayout.GreedoLayoutSizeCalculator.SizeCalculatorDelegate;
import com.squareup.otto.Bus;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import shiro.am.i.chesto.Chesto;
import timber.log.Timber;

/**
 * Created by Shiro on 3/30/2017.
 * Collection of posts
 * Observable
 * has methods to populate itself
 */

public final class PostAlbum implements SizeCalculatorDelegate {

    private final Bus eventBus;
    private final ArrayList<Post> list;
    private final String mQuery;

    private int currentPage;
    private Subscription currentSubscription;
    private boolean isLoading;

    public PostAlbum(String query) {
        eventBus = Chesto.getEventBus();
        list = new ArrayList<>();
        mQuery = query;

        currentPage = 1;
        isLoading = false;
    }

    public Post get(int i) {
        if (i >= list.size() - 15 && !isLoading) {
            fetchPosts();
        }

        return list.get(i);
    }

    public String getQuery() {
        return mQuery;
    }

    public int size() {
        return list.size();
    }

    public void refresh() {
        list.clear();
        eventBus.post(new OnPostsClearedEvent());

        currentPage = 1;
        currentSubscription.unsubscribe();
        fetchPosts();
    }

    public void fetchPosts() {
        currentSubscription = Chesto.getDanbooru()
                .getPosts(mQuery, currentPage)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::setIsLoading)
                .doOnTerminate(this::setIsNotLoading)
                .flatMap(Observable::from)
                .filter(Post::hasFileUrl)
                .filter(post -> !list.contains(post))
                .subscribe(
                        this::add,
                        this::onLoadError,
                        () -> ++currentPage
                );
    }

    private void setIsLoading() {
        isLoading = true;
        eventBus.post(new OnLoadStartedEvent());
    }

    private void setIsNotLoading() {
        isLoading = false;
        eventBus.post(new OnLoadFinishedEvent());
    }

    private void add(Post post) {
        list.add(post);
        eventBus.post(new OnPostAddedEvent(list.size()));
    }

    private void onLoadError(Throwable throwable) {
        Timber.e(throwable, "Error fetching posts");
        eventBus.post(new OnLoadErrorEvent());
    }

    @Override
    public double aspectRatioForIndex(int i) {
        if (i >= list.size()) {
            return 1.0;
        } else {
            final double minRatio = 0.5;
            final double maxRatio = 5;
            final Post post = list.get(i);
            final double ratio = (double) post.getWidth() / post.getHeight();

            if (ratio < minRatio) {
                return minRatio;
            } else if (ratio > maxRatio) {
                return maxRatio;
            } else {
                return ratio;
            }
        }
    }

    public static class OnPostAddedEvent {
        public int position;

        private OnPostAddedEvent(int position) {
            this.position = position;
        }
    }

    public static class OnPostsClearedEvent {
    }

    public static class OnLoadStartedEvent {
    }

    public static class OnLoadFinishedEvent {
    }

    public static class OnLoadErrorEvent {
    }
}
