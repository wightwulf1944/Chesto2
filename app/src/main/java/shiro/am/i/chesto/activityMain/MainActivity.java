package shiro.am.i.chesto.activityMain;

import android.content.Intent;
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

import shiro.am.i.chesto.R;
import shiro.am.i.chesto.U;
import shiro.am.i.chesto.activitySearch.SearchActivity;
import shiro.am.i.chesto.databasePost.Observer;
import shiro.am.i.chesto.databasePost.PostStore;
import timber.log.Timber;

public final class MainActivity extends AppCompatActivity implements Observer {

    private static final PostStore POST_STORE = PostStore.getInstance();

    private Toolbar toolbar;
    private AppBarLayout appbar;
    private GreedoLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appbar = (AppBarLayout) findViewById(R.id.appbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        layoutManager = new GreedoLayoutManager(POST_STORE);
        final int maxRowHeight = getResources().getDisplayMetrics().heightPixels / 3;
        layoutManager.setMaxRowHeight(maxRowHeight);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GreedoSpacingItemDecoration(U.dpToPx(4)));
        recyclerView.setAdapter(new MainAdapter(this));
        recyclerView.setLayoutManager(layoutManager);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        swipeLayout.setOnRefreshListener(POST_STORE);

        POST_STORE.setObserver(this);
        POST_STORE.newSearch("");

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        scrollToTop();
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        Timber.d("ACTION: %s", intent.getAction());
        Timber.d("DATA: %s", intent.getDataString());

        if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
            final String query = intent.getDataString();
            POST_STORE.newSearch(query);
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        final boolean appbarIsCollapsed = (appbar.getHeight() - appbar.getBottom()) != 0;
        final boolean recyclerViewIsAtTop = layoutManager.findFirstVisibleItemPosition() == 0;

        if (appbarIsCollapsed || !recyclerViewIsAtTop) {
            scrollToTop();
            Snackbar.make(recyclerView, getString(R.string.snackbar_mainActivity), Snackbar.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    private void scrollToTop() {
        recyclerView.stopScroll();
        layoutManager.scrollToPosition(0);
        appbar.setExpanded(true);
    }

    @Override
    public void onUpdateStart() {
        swipeLayout.setRefreshing(true);
    }

    @Override
    public void onUpdateDone() {
        swipeLayout.setRefreshing(false);
    }
}
