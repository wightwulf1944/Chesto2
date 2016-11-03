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
public final class PostStore
        extends ArrayList<Post>
        implements GreedoLayoutSizeCalculator.SizeCalculatorDelegate {

    private static final PostStore instance = new PostStore();
    private static final EventBus eventBus = EventBus.getDefault();
    private String currentQuery;
    private int currentPage;

    private PostStore() {
        // disable instantiation
    }

    public static PostStore getInstance() {
        return instance;
    }

    public void refresh() {
        newSearch(currentQuery);
    }

    public void newSearch(String tags) {
        if (!isEmpty()) {
            clear();
            eventBus.post(new Event.Cleared());
        }
        currentQuery = tags;
        currentPage = 1;
        requestMorePosts();
    }

    public void requestMorePosts() {
        Danbooru.api.getPosts(currentQuery, currentPage)
                .subscribeOn(Schedulers.io())
                .flatMap(Observable::from)
                .filter(post -> post.getPreviewFileUrl() != null)
                .filter(post -> !contains(post))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> eventBus.post(new Event.LoadStarted()))
                .doOnTerminate(() -> eventBus.post(new Event.LoadFinished()))
                .subscribe(new Observer<Post>() {
                    @Override
                    public void onCompleted() {
                        ++currentPage;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error getting posts");
                        eventBus.post(new Event.LoadError());
                    }

                    @Override
                    public void onNext(Post post) {
                        add(post);
                        Event.PostAdded event = new Event.PostAdded();
                        event.index = size();
                        eventBus.post(event);
                    }
                });
    }

    @Override
    public double aspectRatioForIndex(int i) {
        if (i >= size()) {
            return 1.0;
        } else {
            final double minRatio = 0.5;
            final double maxRatio = 5;
            final Post post = get(i);
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

    public static class Event {
        private Event() {
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
        }
    }
}