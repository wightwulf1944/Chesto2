package shiro.am.i.chesto.activitySearch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.SearchView;

import shiro.am.i.chesto.R;
import shiro.am.i.chesto.activityMain.MainActivity;

public class SearchActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener {

    private static final String TAG = SearchActivity.class.getSimpleName();
    private SuggestionsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        final SearchView searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(this);

        adapter = new SuggestionsAdapter(this);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        startActivity(new Intent(Intent.ACTION_SEARCH, Uri.parse(s), this, MainActivity.class));
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        adapter.setQuery(s);
        return true;
    }
}
