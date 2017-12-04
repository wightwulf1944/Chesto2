package shiro.am.i.chesto.activitypost;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.flexbox.FlexboxLayout;

import shiro.am.i.chesto.R;
import shiro.am.i.chesto.model.AlbumStack;
import shiro.am.i.chesto.serviceimagedownloader.DownloadService;
import shiro.am.i.chesto.subscription.Subscription;
import shiro.am.i.chesto.viewmodel.PostAlbum;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by Shiro on 8/18/2016.
 */
public final class PostActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 0;

    private RecyclerView recyclerView;
    private BottomSheetBehavior bottomSheetBehavior;
    private PostAlbum album;
    private Subscription subscription;
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_nav_arrow_back);
        toolbar.setNavigationOnClickListener(view -> finishAndReturnResult());
        toolbar.inflateMenu(R.menu.activity_post);
        toolbar.setOnMenuItemClickListener(this::onMenuItemClicked);

        currentIndex = getIntent().getIntExtra("default", -1);

        album = AlbumStack.getTop();

        FlexboxLayout flexboxLayout = findViewById(R.id.flexboxLayout);
        TagLayoutDecorator tagLayoutDecorator = new TagLayoutDecorator(flexboxLayout);
        tagLayoutDecorator.setPost(album.get(currentIndex));

        ScrollToPageListener indexListener = new ScrollToPageListener();
        indexListener.setOnScrollToPageListener(i -> currentIndex = i);

        ScrollToPageListener postListener = new ScrollToPageListener();
        postListener.setOnScrollToPageListener(i -> tagLayoutDecorator.setPost(album.get(i)));

        PostImageAdapter imageAdapter = new PostImageAdapter(this, album);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(imageAdapter);
        recyclerView.scrollToPosition(currentIndex);
        recyclerView.addOnScrollListener(indexListener);
        recyclerView.addOnScrollListener(postListener);
        recyclerView.setHasFixedSize(true);

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerView);

        ImageButton infoButton = findViewById(R.id.infoButton);
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

        subscription = Subscription.from(
                album.addOnPostAddedListener(imageAdapter::notifyItemInserted),
                album.addOnPostsClearedListener(imageAdapter::notifyDataSetChanged)
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            finishAndReturnResult();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // TODO: grantResults may be empty
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            invokeDownload();
        } else {
            Snackbar.make(recyclerView, "Please allow access to save image", Snackbar.LENGTH_SHORT).show();
        }
    }

    private boolean onMenuItemClicked(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_download:
                invokeDownload();
                return true;
            case R.id.action_open_browser:
                invokeOpenInBrowser();
                return true;
            case R.id.action_share:
                invokeShare();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void invokeDownload() {
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            DownloadService.queue(this, album.get(currentIndex));
        } else {
            String[] permissionStr = {WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissionStr, PERMISSION_REQUEST_CODE);
        }
    }

    private void invokeOpenInBrowser() {
        String webUrl = album.get(currentIndex).getWebUrl();
        Uri webUri = Uri.parse(webUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, webUri);
        startActivity(intent);
    }

    private void finishAndReturnResult() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("default", currentIndex);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void invokeShare() {
        String webUrl = album.get(currentIndex).getWebUrl();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, webUrl);
        intent.setType("text/plain");
        Intent chooserIntent = Intent.createChooser(intent, "Share link - " + webUrl);
        startActivity(chooserIntent);
    }

    public void onInfoButtonClicked(View view) {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }
}
