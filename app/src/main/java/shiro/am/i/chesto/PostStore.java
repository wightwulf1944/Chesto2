package shiro.am.i.chesto;

import com.fivehundredpx.greedolayout.GreedoLayoutSizeCalculator;

import java.util.ArrayList;
import java.util.LinkedList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import shiro.am.i.chesto.retrofitDanbooru.Danbooru;
import shiro.am.i.chesto.retrofitDanbooru.Post;
import timber.log.Timber;

/**
 * Created by Shiro on 8/4/2016.
 */
public final class PostStore {

    private static final ArrayList<Post> list = new ArrayList<>();
    private static String currentQuery;
    private static int currentPage;
    private static boolean isLoading;

    private PostStore() {
        throw new AssertionError("Tried to create instance");
    }

    public static Post get(int i) {
        if (i >= list.size() - 15 && !isLoading) {
            fetchPosts();
        }

        return list.get(i);
    }

    public static String getCurrentQuery() {
        return currentQuery;
    }

    public static int size() {
        return list.size();
    }

    public static void refresh() {
        newSearch(currentQuery);
    }

    public static void newSearch(String tags) {
        if (!list.isEmpty()) {
            list.clear();
            notifyPostsCleared();
        }

        currentQuery = tags;
        currentPage = 1;
        fetchPosts();
    }

    public static void fetchPosts() {
        Chesto.getDanbooru()
                .getPosts(currentQuery, currentPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(Observable::from)
                .filter(post -> !list.contains(post))
                .filter(Post::hasFileUrl)
                .toList()
                .doOnSubscribe(() -> {
                    isLoading = true;
                    notifyLoadStarted();
                })
                .doOnTerminate(() -> {
                    isLoading = false;
                    notifyLoadFinished();
                })
                .subscribe(
                        posts -> {
                            ++currentPage;
                            int start = list.size();
                            int count = posts.size();
                            list.addAll(posts);
                            notifyPostsAdded(start, count);
                        },
                        throwable -> {
                            Timber.e(throwable, "Error fetching posts");
                            notifyLoadError();
                        }
                );
    }

    public static class RatioCalculator implements GreedoLayoutSizeCalculator.SizeCalculatorDelegate {
        @Override
        public double aspectRatioForIndex(int i) {
            if (i >= list.size()) {
                return 1.0;
            } else {
                final double minRatio = 0.5;
                final double maxRatio = 5;
                final Post post = list.get(i);
                final double ratio = (double) post.getImageWidth() / post.getImageHeight();

                if (ratio < minRatio) {
                    return minRatio;
                } else if (ratio > maxRatio) {
                    return maxRatio;
                } else {
                    return ratio;
                }
            }
        }
    }

    public interface OnPostsAddedListener {
        void onPostsAdded(int start, int count);
    }

    public interface PostStoreListener {
        void onPostsCleared();

        void onLoadStarted();

        void onLoadFinished();

        void onLoadError();
    }

    private static final LinkedList<OnPostsAddedListener> onPostsAddedListeners = new LinkedList<>();

    private static final LinkedList<PostStoreListener> postStoreListeners = new LinkedList<>();

    public static void addOnPostsAddedListener(OnPostsAddedListener listener) {
        onPostsAddedListeners.add(listener);
    }

    public static void removeOnPostsAddedListener(OnPostsAddedListener listener) {
        onPostsAddedListeners.remove(listener);
    }

    private static void notifyPostsAdded(int start, int count) {
        for (OnPostsAddedListener listener : onPostsAddedListeners) {
            listener.onPostsAdded(start, count);
        }
    }

    public static void addPostStoreListener(PostStoreListener listener) {
        postStoreListeners.add(listener);
    }

    public static void removePostStoreListener(PostStoreListener listener) {
        postStoreListeners.remove(listener);
    }

    private static void notifyPostsCleared() {
        for (PostStoreListener listener : postStoreListeners) {
            listener.onPostsCleared();
        }
    }

    private static void notifyLoadStarted() {
        for (PostStoreListener listener : postStoreListeners) {
            listener.onLoadStarted();
        }
    }

    private static void notifyLoadFinished() {
        for (PostStoreListener listener : postStoreListeners) {
            listener.onLoadFinished();
        }
    }

    private static void notifyLoadError() {
        for (PostStoreListener listener : postStoreListeners) {
            listener.onLoadError();
        }
    }
}