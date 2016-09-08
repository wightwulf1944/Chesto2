package shiro.am.i.chesto.activitySearch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import shiro.am.i.chesto.R;
import shiro.am.i.chesto.activityMain.MainActivity;

public final class SearchActivity extends AppCompatActivity
        implements TextView.OnEditorActionListener, TextWatcher {

    private static final String TAG = SearchActivity.class.getSimpleName();
    private SearchAdapter adapter;
    private EditText searchField;

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

        searchField = (EditText) findViewById(R.id.edit_text);
        searchField.addTextChangedListener(this);
        searchField.setOnEditorActionListener(this);

        adapter = new SearchAdapter(this, searchField);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    private void goSearch() {
        startActivity(
                new Intent(Intent.ACTION_SEARCH,
                        Uri.parse(searchField.getText().toString()),
                        this,
                        MainActivity.class)
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_clear:
                searchField.setText("");
                return true;
            case R.id.action_go:
                goSearch();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        switch (i) {
            case EditorInfo.IME_ACTION_SEARCH:
                goSearch();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        final String s = charSequence.toString();
        final int iBeginTemp = s.lastIndexOf(' ', i);
        final int iBegin = iBeginTemp == -1 ? 0 : iBeginTemp;
        final int iEndTemp = s.indexOf(' ', i);
        final int iEnd = iEndTemp == -1 ? s.length() : iEndTemp;
        adapter.setQuery(s.substring(iBegin, iEnd).trim());
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
