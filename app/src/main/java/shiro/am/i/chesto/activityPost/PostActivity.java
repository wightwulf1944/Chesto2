package shiro.am.i.chesto.activityPost;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import shiro.am.i.chesto.ImageDownloadService;
import shiro.am.i.chesto.R;

/**
 * Created by UGZ on 8/18/2016.
 */
public final class PostActivity extends AppCompatActivity {

    private static final String TAG = PostActivity.class.getName();
    private XBottomSheet bottomSheet;
    private int postIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postIndex = getIntent().getIntExtra("default", -1);

        final TagLayout tagLayout = (TagLayout) findViewById(R.id.tagLayout);
        tagLayout.setCurrentPost(postIndex);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new PostPagerAdapter(this));
        viewPager.setCurrentItem(postIndex);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tagLayout.setCurrentPost(position);
                postIndex = position;
            }
        });

        bottomSheet = new XBottomSheet(findViewById(R.id.bottomSheet));
    }

    @Override
    public void onBackPressed() {
        if (bottomSheet.toggleIsCollapsed()) {
            super.onBackPressed();
        }
    }

    public void onUpButtonClicked(View view) {
        finish();
    }

    public void onInfoButtonClicked(View view) {
        bottomSheet.toggleState();
    }

    public void onDownloadButtonClicked(View view) {
        Intent intent = new Intent(this, ImageDownloadService.class);
        intent.putExtra("default", postIndex);
        startService(intent);
    }
}
