package shiro.am.i.chesto.serviceImageDownloader;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

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

        try {
            File sourceFile = getSourceFile(post);
            File targetFile = getTargetFile(post);
            copy(sourceFile, targetFile);
            sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(targetFile)
            ));
        } catch (Exception e) {
            notificationHelper.notifyFailed();
            Timber.e(e, "Download error: %s", post.getLargeFileUrl());
        }

        notificationHelper.notifyFinished();
    }

    private File getSourceFile(Post post) throws Exception {
        return Glide.with(this)
                .load(post.getLargeFileUrl())
                .downloadOnly(post.getImageWidth(), post.getImageHeight())
                .get();
    }

    private static File getTargetFile(Post post) {
        final File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        final File saveDir = new File(picturesDir, "Chesto");
        if (!saveDir.mkdirs()) {
            Timber.d("getTargetFile: saveDir not created");
        }
        return new File(saveDir, post.getId() + ".png");
    }

    private static void copy(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inChannel.close();
        outChannel.close();
    }
}
