package shiro.am.i.chesto.serviceImageDownloader;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;

import shiro.am.i.chesto.PostStore;
import shiro.am.i.chesto.retrofitDanbooru.Post;
import timber.log.Timber;

/**
 * Created by UGZ on 9/29/2016.
 */

public final class ImageDownloaderService extends IntentService {

    private NotificationHelper notificationHelper;

    public ImageDownloaderService() {
        super(ImageDownloaderService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationHelper = new NotificationHelper(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationHelper.notifyQueued();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final Post post = PostStore.get(intent.getIntExtra("default", -1));
        final File file = getImageFile(post.getId() + ".png");
        boolean isSuccessful = false;

        try {
            isSuccessful = Glide.with(this)
                    .load(post.getLargeFileUrl())
                    .asBitmap()
                    .into(post.getImageWidth(), post.getImageHeight())
                    .get()
                    .compress(Bitmap.CompressFormat.PNG, 0, new FileOutputStream(file));
        } catch (Exception e) {
            notificationHelper.notifyFailed();
            Timber.e(e, "Download error: %s", post.getLargeFileUrl());
        }

        if (isSuccessful) {
            sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)
            ));
        }

        notificationHelper.notifyFinished();
    }

    private static File getImageFile(String fileName) {
        final File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        final File saveDir = new File(picturesDir, "Chesto");
        if (!saveDir.mkdirs()) {
            Timber.d("getImageFile: saveDir not created");
        }
        return new File(saveDir, fileName);
    }
}
