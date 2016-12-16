package shiro.am.i.chesto.activityPost;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import org.apmem.tools.layouts.FlowLayout;
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

    private BottomSheetDecorator bottomSheet;
    private PostPagerAdapter adapter;
    private HackyViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        final int postIndex = getIntent().getIntExtra("default", -1);

        final TagLayoutDecorator tagLayoutDecorator = new TagLayoutDecorator((FlowLayout) findViewById(R.id.flowLayout));
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

        bottomSheet = new BottomSheetDecorator(findViewById(R.id.bottomSheet));

        EventBus.getDefault().register(this);
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
        if (bottomSheet.tryIsCollapsed()) {
            finishAndReturnResult();
        }
    }

    public void onUpButtonClicked(View view) {
        finishAndReturnResult();
    }

    public void onBrowserButtonClicked(View view) {
        Intent intent = new Intent(
                Intent.ACTION_VIEW,
                PostStore.get(viewPager.getCurrentItem()).getWebUri()
        );
        startActivity(intent);
    }

    public void onShareButtonClicked(View view) {
        String url = PostStore.get(viewPager.getCurrentItem()).getWebUrl();
        Intent intent = new Intent(Intent.ACTION_SEND, PostStore.get(viewPager.getCurrentItem()).getWebUri());
        intent.putExtra(Intent.EXTRA_TEXT, url);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "Share link - " + url));
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
                Snackbar.make(viewPager, "Download queued", Snackbar.LENGTH_SHORT).show();
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
