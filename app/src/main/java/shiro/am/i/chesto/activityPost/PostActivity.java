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

import shiro.am.i.chesto.R;
import shiro.am.i.chesto.serviceImageDownloader.ImageDownloaderService;

/**
 * Created by Shiro on 8/18/2016.
 */
public final class PostActivity extends AppCompatActivity {

    private XBottomSheet bottomSheet;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        final int postIndex = getIntent().getIntExtra("default", -1);

        final TagLayout tagLayout = (TagLayout) findViewById(R.id.tagLayout);
        tagLayout.setCurrentPost(postIndex);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new PostPagerAdapter(this));
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
            case PackageManager.PERMISSION_DENIED:
                Toast.makeText(this, "Please allow access to save image", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
