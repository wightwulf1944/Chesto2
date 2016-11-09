package shiro.am.i.chesto.activityPost;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import shiro.am.i.chesto.PostStore;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.serviceImageDownloader.ImageDownloaderService;

/**
 * Created by Shiro on 8/18/2016.
 */
public final class PostActivity extends AppCompatActivity {

    private XBottomSheet bottomSheet;
    private PostPagerAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        EventBus.getDefault().register(this);

        final int postIndex = getIntent().getIntExtra("default", -1);

        final TagLayout tagLayout = (TagLayout) findViewById(R.id.tagLayout);
        tagLayout.setCurrentPost(postIndex);

        adapter = new PostPagerAdapter(this);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(postIndex);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tagLayout.setCurrentPost(position);
            }
        });

        bottomSheet = new XBottomSheet(findViewById(R.id.bottomSheet));
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void finishAndReturnResult() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("default", viewPager.getCurrentItem());
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (bottomSheet.toggleIsCollapsed()) {
            finishAndReturnResult();
        }
    }

    public void onUpButtonClicked(View view) {
        finishAndReturnResult();
    }

    public void onInfoButtonClicked(View view) {
        bottomSheet.toggleState();
    }

    public void onDownloadButtonClicked(View view) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (grantResults[0]) {
            case PackageManager.PERMISSION_GRANTED:
                Intent intent = new Intent(this, ImageDownloaderService.class);
                intent.putExtra("default", viewPager.getCurrentItem());
                startService(intent);
                break;
            default:
                Toast.makeText(this, "Please allow access to save image", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PostStore.Event.PostAdded event) {
        adapter.notifyDataSetChanged();
    }
}
