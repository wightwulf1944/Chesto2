package shiro.am.i.chesto.activitypost;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.flexbox.FlexboxLayout;

import shiro.am.i.chesto.PostStore;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.serviceimagedownloader.ImageDownloaderService;

/**
 * Created by Shiro on 8/18/2016.
 */
public final class PostActivity
        extends AppCompatActivity
        implements PostStore.OnPostAddedListener {

    private BottomSheetBehavior bottomSheetBehavior;
    private PostPagerAdapter adapter;
    private HackyViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        final int postIndex = getIntent().getIntExtra("default", -1);

        FlexboxLayout flexboxLayout = (FlexboxLayout) findViewById(R.id.flexboxLayout);
        TagLayoutDecorator tagLayoutDecorator = new TagLayoutDecorator(flexboxLayout);
        tagLayoutDecorator.setPost(PostStore.get(postIndex));

        adapter = new PostPagerAdapter(this);

        viewPager = (HackyViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(postIndex);
        viewPager.addOnPageChangeListener(new HackyViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tagLayoutDecorator.setPost(PostStore.get(position));
            }
        });

        ImageButton infoButton = (ImageButton) findViewById(R.id.infoButton);
        View bottomBar = findViewById(R.id.bottomBar);

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheet));
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    infoButton.setImageResource(R.drawable.ic_nav_info);
                } else {
                    infoButton.setImageResource(R.drawable.ic_nav_arrow_hide);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                flexboxLayout.setAlpha(slideOffset);
                bottomBar.setAlpha(slideOffset);
            }
        });

        PostStore.addOnPostAddedListener(this);
    }

    @Override
    protected void onDestroy() {
        PostStore.removeOnPostAddedListener(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Uri uri;
        Intent intent;
        switch (item.getItemId()) {

            case android.R.id.home:
                finishAndReturnResult();
                return true;

            case R.id.action_download:
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                return true;

            case R.id.action_open_browser:
                uri = PostStore.get(viewPager.getCurrentItem()).getWebUri();
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;

            case R.id.action_share:
                String url = PostStore.get(viewPager.getCurrentItem()).getWebUrl();
                uri = PostStore.get(viewPager.getCurrentItem()).getWebUri();
                intent = new Intent(Intent.ACTION_SEND, uri);
                intent.putExtra(Intent.EXTRA_TEXT, url);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Share link - " + url));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void finishAndReturnResult() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("default", viewPager.getCurrentItem());
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            finishAndReturnResult();
        }
    }

    public void onInfoButtonClicked(View view) {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this, ImageDownloaderService.class);
            intent.putExtra("default", viewPager.getCurrentItem());
            startService(intent);
            Snackbar.make(viewPager, "Download queued", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(viewPager, "Please allow access to save image", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPostAdded(int position) {
        adapter.notifyDataSetChanged();
    }
}
