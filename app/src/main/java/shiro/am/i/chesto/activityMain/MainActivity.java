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

import shiro.am.i.chesto.Chesto;
import shiro.am.i.chesto.PostStore;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.U;
import shiro.am.i.chesto.activitysearch.SearchActivity;

public final class MainActivity
        extends AppCompatActivity
        implements
        PostStore.OnPostAddedListener,
        PostStore.PostStoreListener {

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

        appbar = (AppBarLayout) findViewById(R.id.appbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        layoutManager = new GreedoLayoutManager(new PostStore.RatioCalculator());
        layoutManager.setMaxRowHeight(300);

        adapter = new MainAdapter(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GreedoSpacingItemDecoration(U.dpToPx(8)));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.primary_dark));
        swipeLayout.setOnRefreshListener(PostStore::refresh);

        errorSnackbar = Snackbar.make(recyclerView, "Check your connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", view -> PostStore.fetchPosts());

        PostStore.addPostStoreListener(this);
        PostStore.addOnPostAddedListener(this);

        PostStore.newSearch("");
        handleIntent(getIntent());
    }

    @Override
    protected void onDestroy() {
        PostStore.removePostStoreListener(this);
        PostStore.removeOnPostAddedListener(this);
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        scrollToTop();
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
            final String query = intent.getDataString();
            PostStore.newSearch(query);
            toolbar.setSubtitle(query);
        }
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
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            case R.id.action_hide_nsfw:
                boolean toggled = !item.isChecked();
                item.setChecked(toggled);
                SharedPreferences.Editor editor = Chesto.getPreferences().edit();
                editor.putBoolean("hide_nsfw", toggled);
                editor.apply();
                PostStore.refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed + 1000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            final boolean appbarIsExpanded = appbar.getHeight() - appbar.getBottom() == 0;
            final boolean recyclerViewIsAtTop = layoutManager.findFirstVisibleItemPosition() == 0;
            if (appbarIsExpanded && recyclerViewIsAtTop) {
                Snackbar.make(recyclerView, getString(R.string.snackbar_mainActivity), Snackbar.LENGTH_SHORT).show();
            } else {
                scrollToTop();
                Snackbar.make(recyclerView, getString(R.string.snackbar_mainActivity_scrollToTop), Snackbar.LENGTH_SHORT).show();
            }
        }
        mBackPressed = System.currentTimeMillis();
    }

    private void scrollToTop() {
        recyclerView.stopScroll();
        layoutManager.scrollToPosition(0);
        appbar.setExpanded(true);
    }

    @Override
    public void onLoadStarted() {
        swipeLayout.setRefreshing(true);
        errorSnackbar.dismiss();
    }

    @Override
    public void onLoadFinished() {
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void onLoadError() {
        errorSnackbar.show();
    }

    @Override
    public void onPostsCleared() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPostAdded(int position) {
        adapter.notifyItemInserted(position);
    }
}
