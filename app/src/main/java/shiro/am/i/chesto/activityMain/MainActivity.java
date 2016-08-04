package shiro.am.i.chesto.activityMain;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.fivehundredpx.greedolayout.GreedoLayoutManager;
import com.fivehundredpx.greedolayout.GreedoSpacingItemDecoration;

import shiro.am.i.chesto.PostList;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.U;
import shiro.am.i.chesto.activitySearch.SearchActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final PostList postList = PostList.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        swipeLayout.setOnRefreshListener(postList);

        final GreedoLayoutManager layoutManager = new GreedoLayoutManager(postList);
        final int maxRowHeight = getResources().getDisplayMetrics().heightPixels / 3;
        layoutManager.setMaxRowHeight(maxRowHeight);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GreedoSpacingItemDecoration(U.dpToPx(4)));
        recyclerView.setAdapter(new PostAdapter(this));
        recyclerView.setLayoutManager(layoutManager);

        postList.search("");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent: " + intent.getDataString());
        postList.search(intent.getDataString());
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
}
