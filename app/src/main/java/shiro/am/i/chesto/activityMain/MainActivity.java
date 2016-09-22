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

import shiro.am.i.chesto.PostStore;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.U;
import shiro.am.i.chesto.activitySearch.SearchActivity;
import timber.log.Timber;

public final class MainActivity extends AppCompatActivity {

    private static final PostStore POST_STORE = PostStore.getInstance();

    private Toolbar toolbar;
    private AppBarLayout appbar;
    private GreedoLayoutManager layoutManager;
    private RecyclerView recyclerView;

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

        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        POST_STORE.setSwipeLayout(swipeLayout);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        scrollToTop();
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        Timber.d("ACTION: " + intent.getAction());
        Timber.d("DATA: " + intent.getDataString());

        if (intent.getAction() == Intent.ACTION_SEARCH) {
            final String query = intent.getDataString();
            POST_STORE.newSearch(query);
            toolbar.setSubtitle(query);
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
}
