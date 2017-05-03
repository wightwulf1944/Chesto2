package shiro.am.i.chesto.activitymain;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.fivehundredpx.greedolayout.GreedoLayoutManager;
import com.fivehundredpx.greedolayout.GreedoSpacingItemDecoration;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import shiro.am.i.chesto.Chesto;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.Settings;
import shiro.am.i.chesto.activitypost.PostActivity;
import shiro.am.i.chesto.activitysearch.SearchActivity;
import shiro.am.i.chesto.models.AlbumStack;
import shiro.am.i.chesto.models.PostAlbum;

public final class MainActivity extends AppCompatActivity {

    private static int mainActivityCount = 0;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.appbar) AppBarLayout appbar;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.swipeLayout) SwipeRefreshLayout swipeLayout;
    private PostAlbum postAlbum;
    private GreedoLayoutManager layoutManager;
    private MainAdapter adapter;
    private Snackbar errorSnackbar;

    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        postAlbum = getPostAlbum(savedInstanceState);

        toolbar.setSubtitle(postAlbum.getQuery());
        setSupportActionBar(toolbar);

        RatioDelegate ratioDelegate = new RatioDelegate(postAlbum);
        layoutManager = new GreedoLayoutManager(ratioDelegate);
        layoutManager.setMaxRowHeight(300);

        int spacingPx = (int) (8 * getResources().getDisplayMetrics().density);
        GreedoSpacingItemDecoration spacer = new GreedoSpacingItemDecoration(spacingPx);

        adapter = new MainAdapter(postAlbum);
        adapter.setOnItemClickedListener(position -> {
            Intent intent = new Intent(this, PostActivity.class);
            intent.putExtra("default", position);
            startActivityForResult(intent, 0);
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(spacer);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        swipeLayout.setColorSchemeResources(R.color.primary_dark);
        swipeLayout.setOnRefreshListener(postAlbum::refresh);

        errorSnackbar = Snackbar.make(recyclerView, "Check your connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", view -> postAlbum.fetchPosts());

        Chesto.getEventBus().register(this);
        postAlbum.fetchPosts();

        AlbumStack.push(postAlbum);
        ++mainActivityCount;
    }

    private PostAlbum getPostAlbum(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String intentAction = intent.getAction();
        if (intentAction.equals(Intent.ACTION_MAIN)) {
            return new PostAlbum("");
        } else if (intentAction.equals(Intent.ACTION_SEARCH)) {
            return new PostAlbum(intent.getDataString());
        } else if (savedInstanceState != null) {
            return AlbumStack.getTop();
        } else {
            throw new RuntimeException("Unhandled launch");
        }
    }

    @Override
    protected void onDestroy() {
        --mainActivityCount;
        if (isFinishing()) {
            AlbumStack.pop();
        }
        Chesto.getEventBus().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            final int result = data.getIntExtra("default", -1);
            recyclerView.scrollToPosition(result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);

        menu.findItem(R.id.action_hide_nsfw)
                .setChecked(Settings.hideNsfw());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_hide_nsfw:
                boolean toggled = !item.isChecked();
                item.setChecked(toggled);
                Settings.setHideNsfw(toggled);
                postAlbum.refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mainActivityCount > 1) {
            super.onBackPressed();
        } else if (mBackPressed + 1500 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            boolean appbarIsExpanded = appbar.getHeight() - appbar.getBottom() == 0;
            boolean recyclerViewIsAtTop = layoutManager.findFirstVisibleItemPosition() == 0;
            if (appbarIsExpanded && recyclerViewIsAtTop) {
                Snackbar.make(recyclerView, R.string.snackbar_mainActivity, Snackbar.LENGTH_SHORT).show();
            } else {
                recyclerView.stopScroll();
                layoutManager.scrollToPosition(0);
                appbar.setExpanded(true);
                Snackbar.make(recyclerView, R.string.snackbar_mainActivity_scrollToTop, Snackbar.LENGTH_SHORT).show();
            }
            mBackPressed = System.currentTimeMillis();
        }
    }

    @Subscribe
    public void onLoadStarted(PostAlbum.OnLoadStartedEvent event) {
        swipeLayout.setRefreshing(true);
        errorSnackbar.dismiss();
    }

    @Subscribe
    public void onLoadFinished(PostAlbum.OnLoadFinishedEvent event) {
        swipeLayout.setRefreshing(false);
    }

    @Subscribe
    public void onLoadError(PostAlbum.OnLoadErrorEvent event) {
        errorSnackbar.show();
    }

    @Subscribe
    public void onPostCleared(PostAlbum.OnPostsClearedEvent event) {
        adapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onPostAdded(PostAlbum.OnPostAddedEvent event) {
        adapter.notifyItemInserted(event.position);
    }
}
