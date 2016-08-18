package shiro.am.i.chesto.activityPost;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import shiro.am.i.chesto.R;

/**
 * Created by UGZ on 8/18/2016.
 */
public final class PostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
    }

    public void onUpButtonClicked(View view) {
        finish();
    }
}
