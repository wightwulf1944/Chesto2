package shiro.am.i.chesto.activitymain;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.fivehundredpx.greedolayout.GreedoLayoutManager;
import com.fivehundredpx.greedolayout.GreedoSpacingItemDecoration;
import com.squareup.otto.Subscribe;

import shiro.am.i.chesto.Chesto;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.U;
import shiro.am.i.chesto.activitysearch.SearchActivity;
import shiro.am.i.chesto.models.AlbumStack;
import shiro.am.i.chesto.models.PostAlbum;

public final class MainActivity extends AppCompatActivity {

    private static int mainActivityCount = 0;

    //TODO: try butterknife again to reduce code
    private PostAlbum postAlbum;
    private Toolbar toolbar;
    private AppBarLayout appbar;
    private GreedoLayoutManager layoutManager;
    private MainAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeLayout;
    private Snackbar errorSnackbar;

    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String currentQuery;
        Intent intent = getIntent();
        String intentAction = intent.getAction();
        if (intentAction.equals(Intent.ACTION_MAIN)) {
            currentQuery = "";
        } else if (intentAction.equals(Intent.ACTION_SEARCH)) {
            currentQuery = intent.getDataString();
        } else if (savedInstanceState != null) {
            currentQuery = savedInstanceState.getString("CURRENT_QUERY");
        } else {
            throw new RuntimeException("Unhandled launch");
        }

        appbar = (AppBarLayout) findViewById(R.id.appbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle(currentQuery);
        setSupportActionBar(toolbar);

        postAlbum = new PostAlbum(currentQuery);

        layoutManager = new GreedoLayoutManager(postAlbum);
        layoutManager.setMaxRowHeight(300);

        adapter = new MainAdapter(this, postAlbum);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GreedoSpacingItemDecoration(U.dpToPx(8)));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.primary_dark));
        swipeLayout.setOnRefreshListener(postAlbum::refresh);


        errorSnackbar = Snackbar.make(recyclerView, "Check your connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", view -> postAlbum.fetchPosts());

        Chesto.getEventBus().register(this);
        postAlbum.fetchPosts();

        AlbumStack.push(postAlbum);
        ++mainActivityCount;
    }

    @Override
    protected void onDestroy() {
        --mainActivityCount;
        AlbumStack.pop();
        Chesto.getEventBus().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("CURRENT_QUERY", toolbar.getSubtitle().toString());
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

        boolean hideNsfw = Chesto.getPreferences().getBoolean("hide_nsfw", true);
        menu.findItem(R.id.action_hide_nsfw).setChecked(hideNsfw);

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
                SharedPreferences.Editor editor = Chesto.getPreferences().edit();
                editor.putBoolean("hide_nsfw", toggled);
                editor.apply();
                postAlbum.refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        // TODO: consider priorities here
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
        }
        mBackPressed = System.currentTimeMillis();
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
