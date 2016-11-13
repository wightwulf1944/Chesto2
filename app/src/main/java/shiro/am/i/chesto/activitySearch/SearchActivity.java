package shiro.am.i.chesto.activitySearch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.SearchView;

import shiro.am.i.chesto.R;
import shiro.am.i.chesto.activityMain.MainActivity;

public final class SearchActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener {

    private SearchAdapter adapter;

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
        searchView.setSubmitButtonEnabled(true);
        searchView.setIconified(false);

        adapter = new SearchAdapter(this, searchView);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        startActivity(
                new Intent(Intent.ACTION_SEARCH,
                        Uri.parse(s),
                        this,
                        MainActivity.class)
        );
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        final int spaceIndex = s.lastIndexOf(" ");
        if (spaceIndex != -1) {
            adapter.setQuery(s.substring(spaceIndex + 1));
        } else {
            adapter.setQuery(s);
        }
        return true;
    }
}
