package shiro.am.i.chesto;

import com.fivehundredpx.greedolayout.GreedoLayoutSizeCalculator;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import shiro.am.i.chesto.retrofitDanbooru.Danbooru;
import shiro.am.i.chesto.retrofitDanbooru.Post;
import timber.log.Timber;

/**
 * Created by Shiro on 8/4/2016.
 */
public final class PostStore {

    private static final EventBus eventBus = EventBus.getDefault();
    private static final ArrayList<Post> list = new ArrayList<>(40);
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

    public static int size() {
        return list.size();
    }

    public static void refresh() {
        newSearch(currentQuery);
    }

    public static void newSearch(String tags) {
        if (!list.isEmpty()) {
            list.clear();
            eventBus.post(new Event.Cleared());
        }

        currentQuery = tags;
        currentPage = 1;
        fetchPosts();
    }

    public static void fetchPosts() {
        Danbooru.api.getPosts(currentQuery, currentPage)
                .subscribeOn(Schedulers.io())
                .flatMap(Observable::from)
                .filter(Post::hasFileUrl)
                .filter(post -> !list.contains(post))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> {
                    isLoading = true;
                    eventBus.post(new Event.LoadStarted());
                })
                .doOnTerminate(() -> {
                    isLoading = false;
                    eventBus.post(new Event.LoadFinished());
                })
                .subscribe(new Observer<Post>() {
                    @Override
                    public void onCompleted() {
                        ++currentPage;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error fetching posts");
                        eventBus.post(new Event.LoadError());
                    }

                    @Override
                    public void onNext(Post post) {
                        list.add(post);
                        eventBus.post(new Event.PostAdded(list.size()));
                    }
                });
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

    public static class Event {
        private Event() {
            throw new AssertionError("Tried to create instance");
        }

        public static class LoadStarted {
        }

        public static class LoadFinished {
        }

        public static class LoadError {
        }

        public static class Cleared {
        }

        public static class PostAdded {
            public int index;

            private PostAdded(int i) {
                index = i;
            }
        }
    }
}