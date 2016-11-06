package shiro.am.i.chesto;

import com.fivehundredpx.greedolayout.GreedoLayoutSizeCalculator;

import org.greenrobot.eventbus.EventBus;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
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
    private static final Realm realm = Realm.getDefaultInstance();
    private static RealmResults<Post> results;
    private static String currentQuery;
    private static int currentPage;
    private static int loadedPosts;
    private static boolean isLoading;

    private PostStore() {
        throw new AssertionError("Tried to create instance");
    }

    public static Post get(int i) {
        if (i > loadedPosts - 5 && !isLoading) {
            fetchPosts();
        }

        return results.get(i);
    }

    public static int size() {
        return results.size();
    }

    public static void refresh() {
        newSearch(currentQuery);
    }

    public static void newSearch(String tags) {
        RealmQuery<Post> query = realm.where(Post.class);
        for (String tag : tags.split(" ")) {
            query = query.contains("tagString", tag, Case.INSENSITIVE);
        }
        results = query.findAllSorted("id", Sort.DESCENDING);
        eventBus.post(new Event.Cleared());

        currentQuery = tags;
        currentPage = 1;
        loadedPosts = 0;
        fetchPosts();
    }

    private static void fetchPosts() {
        isLoading = true;

        Danbooru.api.getPosts(currentQuery, currentPage)
                .subscribeOn(Schedulers.io())
                .flatMap(Observable::from)
                .filter(post -> post.getPreviewFileUrl() != null)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> eventBus.post(new Event.LoadStarted()))
                .doOnTerminate(() -> eventBus.post(new Event.LoadFinished()))
                .doOnNext(post -> {
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(post);
                    realm.commitTransaction();
                    ++loadedPosts;
                })
                .filter(post -> !results.contains(post))
                .subscribe(new Observer<Post>() {
                    @Override
                    public void onCompleted() {
                        ++currentPage;
                        isLoading = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error fetching posts");
                        eventBus.post(new Event.LoadError());
                    }

                    @Override
                    public void onNext(Post post) {
                        eventBus.post(new Event.PostAdded(loadedPosts));
                    }
                });
    }

    public static class RatioCalculator implements GreedoLayoutSizeCalculator.SizeCalculatorDelegate {
        @Override
        public double aspectRatioForIndex(int i) {
            if (i >= results.size()) {
                return 1.0;
            } else {
                final double minRatio = 0.5;
                final double maxRatio = 5;
                final Post post = results.get(i);
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