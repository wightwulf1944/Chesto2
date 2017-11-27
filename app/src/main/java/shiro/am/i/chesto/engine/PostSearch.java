package shiro.am.i.chesto.engine;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import shiro.am.i.chesto.Chesto;
import shiro.am.i.chesto.model.Post;
import timber.log.Timber;

/**
 * Created by Subaru Tashiro on 6/13/2017.
 */

public final class PostSearch {

    final SearchResults searchResults = new SearchResults();
    final String searchString;

    int currentPage = 1;
    private Subscription currentSubscription;
    boolean isLoading = false;

    public PostSearch(String searchString) {
        this.searchString = searchString;
    }

    public Post get(int i) {
        if (i >= searchResults.size() - 15 && !isLoading) {
            goLoad();
        }

        return searchResults.get(i);
    }

    public String getSearchString() {
        return searchString;
    }

    public int getSize() {
        return searchResults.size();
    }

    public void refresh() {
        currentPage = 0;
        searchResults.clear();

        // notify data has been cleared
        // stop loading in progress

        goLoad();
    }

    public void goLoad() {
        currentSubscription = Chesto.getDanbooru()
                .getPosts(searchString, currentPage)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::setIsLoading)
                .doOnTerminate(this::setIsNotLoading)
                .subscribe(
                        this::add,
                        this::onLoadError,
                        () -> currentPage++
                );
    }

    private void setIsLoading() {
        isLoading = true;
        // notify is loading
    }

    private void setIsNotLoading() {
        isLoading = false;
        // notify stopped loading
    }

    private void add(List<Post> newResults) {
        searchResults.ensureCapacity(searchResults.size() + newResults.size());

        newResults.stream()
                .filter(Post::hasFileUrl)
                .forEach(post -> {
                    int index = searchResults.indexOf(post);
                    if (index == -1) {
                        searchResults.add(post);
                        // notify post added
                    } else {
                        searchResults.set(index, post);
                        // notify post changed
                    }
                });
    }

    private void onLoadError(Throwable throwable) {
        Timber.e(throwable, "Error fetching posts");
        // notify load error
    }
}
