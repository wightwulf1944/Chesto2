package shiro.am.i.chesto.activitysearch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import io.realm.Realm;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.activitymain.MainActivity;
import shiro.am.i.chesto.model.AlbumStack;
import shiro.am.i.chesto.subscription.Subscription;
import shiro.am.i.chesto.viewmodel.PostAlbum;

public final class SearchActivity extends AppCompatActivity {

    private final Realm realm = Realm.getDefaultInstance();
    private final TagStore tagStore = new TagStore(realm);
    private EditTextWrapper editTextWrapper;
    private MenuItem clearButton;
    private String currentQuery;
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        SearchAdapter searchAdapter = new SearchAdapter();
        searchAdapter.setData(tagStore.getResults());
        searchAdapter.setOnItemClickListener(this::onAdapterItemClicked);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexWrap(FlexWrap.WRAP);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(searchAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        PostAlbum album = AlbumStack.getTop();

        editTextWrapper = new EditTextWrapper(findViewById(R.id.editText));
        editTextWrapper.setAfterTextChangedListener(this::onTextChanged);
        editTextWrapper.setOnEditorSearchListener(this::invokeSearch);
        editTextWrapper.setText(album.getQuery());

        subscription = tagStore.addOnDatasetChangedListener(searchAdapter::setData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        subscription.unsubscribe();
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
            case android.R.id.home:
                finish();
                return true;
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
        tagStore.searchTags(currentQuery);

        if (clearButton != null) {
            clearButton.setVisible(!s.isEmpty());
        }
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
        finish();
    }
}
