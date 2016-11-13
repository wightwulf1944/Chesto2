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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import shiro.am.i.chesto.PostStore;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.U;
import shiro.am.i.chesto.activitySearch.SearchActivity;
import timber.log.Timber;

public final class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppBarLayout appbar;
    private GreedoLayoutManager layoutManager;
    private MainAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeLayout;

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
        recyclerView.addItemDecoration(new GreedoSpacingItemDecoration(U.dpToPx(4)));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        swipeLayout.setOnRefreshListener(PostStore::refresh);
        
        EventBus.getDefault().register(this);

        PostStore.newSearch("");
        handleIntent(getIntent());
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        final boolean appbarIsExpanded = appbar.getHeight() - appbar.getBottom() != 0;
        final boolean recyclerViewIsAtTop = layoutManager.findFirstVisibleItemPosition() == 0;

        if (appbarIsExpanded || !recyclerViewIsAtTop) {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PostStore.Event.LoadStarted event) {
        swipeLayout.setRefreshing(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PostStore.Event.LoadFinished event) {
        swipeLayout.setRefreshing(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PostStore.Event.LoadError event) {
        Snackbar.make(recyclerView, "Check your connection.", Snackbar.LENGTH_LONG).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PostStore.Event.Cleared event) {
        adapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PostStore.Event.PostAdded event) {
        adapter.notifyItemInserted(event.index);
    }
}
