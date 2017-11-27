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

import shiro.am.i.chesto.R;
import shiro.am.i.chesto.Settings;
import shiro.am.i.chesto.activitypost.PostActivity;
import shiro.am.i.chesto.activitysearch.SearchActivity;
import shiro.am.i.chesto.model.AlbumStack;
import shiro.am.i.chesto.viewmodel.PostAlbum;
import shiro.am.i.chesto.subscription.Subscription;

public final class MainActivity extends AppCompatActivity {

    private AppBarLayout appbar;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeLayout;
    private PostAlbum postAlbum;
    private GreedoLayoutManager layoutManager;
    private MainAdapter adapter;
    private Snackbar errorSnackbar;
    private Subscription subscription;

    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        postAlbum = getPostAlbum(savedInstanceState);

        appbar = findViewById(R.id.appbar);

        Toolbar toolbar = findViewById(R.id.toolbar);
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

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(spacer);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        swipeLayout = findViewById(R.id.swipeLayout);
        swipeLayout.setColorSchemeResources(R.color.primary_dark);
        swipeLayout.setOnRefreshListener(postAlbum::refresh);

        errorSnackbar = Snackbar.make(recyclerView, "Check your connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", view -> postAlbum.fetchPosts());

        subscription = Subscription.from(
                postAlbum.addOnPostAddedListener(adapter::notifyItemInserted),
                postAlbum.addOnPostsClearedListener(adapter::notifyDataSetChanged),
                postAlbum.addOnLoadingListener(swipeLayout::setRefreshing),
                postAlbum.addOnLoadingListener(b -> errorSnackbar.dismiss()),
                postAlbum.addOnErrorListener(errorSnackbar::show)
        );
        postAlbum.fetchPosts();

        AlbumStack.push(postAlbum);
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
        if (isFinishing()) {
            AlbumStack.pop();
        }
        subscription.unsubscribe();
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
        if (!isTaskRoot() || mBackPressed + 1500 > System.currentTimeMillis()) {
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
}
