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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import shiro.am.i.chesto.PostStore;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.activityMain.MainActivity;

//TODO: cleanup

public final class SearchActivity extends AppCompatActivity {

    private EditText editText;
    private MenuItem clearButton;
    private SearchAdapter searchAdapter;
    private String currentQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        editText = (EditText) findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                onTextEdit(s.toString());
            }
        });
        editText.setOnEditorActionListener(new OnEditorSearchListener() {
            @Override
            void onEditorSearch() {
                onGo();
            }
        });

        searchAdapter = new SearchAdapter(itemName -> {
            String text = editText.getText()
                    .toString()
                    .replaceFirst(currentQuery, itemName);
            editText.setText(text);
        });

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setAdapter(searchAdapter);
        recyclerView.setHasFixedSize(true);

        editText.setText(PostStore.getCurrentQuery());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        clearButton = menu.add(R.string.action_clear)
                .setIcon(R.drawable.ic_search_clear)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                .setOnMenuItemClickListener(item -> {
                    onClear();
                    return true;
                });
        menu.add(R.string.action_go)
                .setIcon(R.drawable.ic_search_go)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                .setOnMenuItemClickListener(item -> {
                    onGo();
                    return true;
                });

        clearButton.setVisible(false);
        return true;
    }

    private void onTextEdit(String s) {
        currentQuery = getLastWord(s);
        searchAdapter.setQuery(currentQuery);
        if (clearButton != null) {
            clearButton.setVisible(!s.isEmpty());
        }
    }

    private void onClear() {
        editText.setText("");
    }

    private void onGo() {
        Uri uri = Uri.parse(editText.getText().toString());
        Intent intent = new Intent(
                Intent.ACTION_SEARCH,
                uri,
                this,
                MainActivity.class
        );
        startActivity(intent);
    }

    private static String getLastWord(String s) {
        final int spaceIndex = s.lastIndexOf(" ");
        if (spaceIndex != -1) {
            return s.substring(spaceIndex + 1);
        } else {
            return s;
        }
    }
}
