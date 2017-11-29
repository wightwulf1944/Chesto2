package shiro.am.i.chesto.viewmodel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import shiro.am.i.chesto.Chesto;
import shiro.am.i.chesto.listener.Listener0;
import shiro.am.i.chesto.listener.Listener1;
import shiro.am.i.chesto.model.Post;
import shiro.am.i.chesto.model.PostJson;
import shiro.am.i.chesto.notifier.Notifier0;
import shiro.am.i.chesto.notifier.Notifier1;
import timber.log.Timber;

/**
 * Created by Shiro on 3/30/2017.
 * Collection of posts
 * Observable
 * has methods to populate itself
 */

public final class PostAlbum {

    private final Notifier1<Integer> onPostAddedNotifier = new Notifier1<>();

    private final Notifier0 onPostsClearedNotifier = new Notifier0();

    private final Notifier1<Boolean> onLoadingNotifier = new Notifier1<>();

    private final Notifier0 onSuccessNotifier = new Notifier0();

    private final Notifier0 onErrorNotifier = new Notifier0();

    private final ArrayList<Post> list = new ArrayList<>();

    private final String mQuery;

    private int currentPage;

    private boolean isLoading;

    private Subscription currentSubscription;

    public PostAlbum(String query) {
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
        onPostsClearedNotifier.fireEvent();

        currentPage = 1;
        currentSubscription.unsubscribe();
        fetchPosts();
    }

    public void fetchPosts() {
        if (isLoading) return;
        setIsLoading(true);

        currentSubscription = Chesto.getDanbooru()
                .getPosts(mQuery, currentPage)
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(() -> setIsLoading(false))
                .flatMap(Observable::from)
                .filter(PostJson::hasImageUrls)
                .map(Post::new)
                .toList()
                .subscribe(
                        this::onLoadSuccess,
                        this::onLoadError,
                        () -> ++currentPage
                );
    }

    private void setIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
        onLoadingNotifier.fireEvent(isLoading);
    }

    private void onLoadSuccess(List<Post> newPosts) {
        HashSet<Post> postSet = new HashSet<>(list);

        for (Post newPost : newPosts) {
            if (postSet.contains(newPost)) {
                int index = list.lastIndexOf(newPost);
                list.set(index, newPost);
            } else {
                list.add(newPost);
                onPostAddedNotifier.fireEvent(list.size());
            }
        }

        onSuccessNotifier.fireEvent();
    }

    private void onLoadError(Throwable throwable) {
        Timber.e(throwable, "Error fetching posts");

        onErrorNotifier.fireEvent();
    }

    public shiro.am.i.chesto.subscription.Subscription addOnPostAddedListener(Listener1<Integer> listener) {
        return onPostAddedNotifier.addListener(listener);
    }

    public shiro.am.i.chesto.subscription.Subscription addOnPostsClearedListener(Listener0 listener) {
        return onPostsClearedNotifier.addListener(listener);
    }

    public shiro.am.i.chesto.subscription.Subscription addOnLoadingListener(Listener1<Boolean> listener) {
        return onLoadingNotifier.addListener(listener);
    }

    public shiro.am.i.chesto.subscription.Subscription addOnSuccessListener(Listener0 listener) {
        return onSuccessNotifier.addListener(listener);
    }

    public shiro.am.i.chesto.subscription.Subscription addOnErrorListener(Listener0 listener) {
        return onErrorNotifier.addListener(listener);
    }
}
