package shiro.am.i.chesto.activitySearch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.List;

import shiro.am.i.chesto.PostStore;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.activityMain.MainActivity;
import shiro.am.i.chesto.retrofitDanbooru.Tag;

public final class SearchActivity extends AppCompatActivity {

    private EditTextWrapper editTextWrapper;
    private TagRepository tagRepository;
    private SearchAdapter searchAdapter;
    private MenuItem clearButton;
    private String currentQuery;

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

        editTextWrapper = new EditTextWrapper((EditText) findViewById(R.id.editText));
        editTextWrapper.setAfterTextChangedListener(this::onTextChanged);
        editTextWrapper.setOnEditorSearchListener(this::invokeSearch);

        tagRepository = new TagRepository();
        tagRepository.setOnTagRepoUpdateListener(this::onTagRepoUpdated);

        searchAdapter = new SearchAdapter(this);
        searchAdapter.setOnItemClickListener(this::onAdapterItemClicked);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setAdapter(searchAdapter);
        recyclerView.setHasFixedSize(true);

        editTextWrapper.setText(PostStore.getCurrentQuery());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_search, menu);
        clearButton = menu.findItem(R.id.clear);
        clearButton.setVisible(!editTextWrapper.getText().toString().isEmpty());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear:
                editTextWrapper.setText("");
                return true;
            case R.id.go:
                invokeSearch();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onTextChanged(String s) {
        int spaceIndex = s.lastIndexOf(" ");
        currentQuery = s.substring(spaceIndex + 1);

        tagRepository.setQuery(currentQuery);
        if (clearButton != null) {
            clearButton.setVisible(!s.isEmpty());
        }
    }

    private void onTagRepoUpdated(List<Tag> data) {
        searchAdapter.setData(data);
        searchAdapter.notifyDataSetChanged();
    }

    private void onAdapterItemClicked(String itemName) {
        String text = editTextWrapper.getText()
                .toString()
                .replaceFirst(currentQuery, itemName);
        editTextWrapper.setText(text);
    }

    private void invokeSearch() {
        Uri uri = Uri.parse(editTextWrapper.getText().toString());
        Intent intent = new Intent(
                Intent.ACTION_SEARCH,
                uri,
                this,
                MainActivity.class
        );
        startActivity(intent);
    }
}
