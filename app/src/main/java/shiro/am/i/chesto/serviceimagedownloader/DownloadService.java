package shiro.am.i.chesto.serviceimagedownloader;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import shiro.am.i.chesto.model.Post;
import shiro.am.i.chesto.util.NotificationUtil;
import timber.log.Timber;

import static android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE;
import static android.os.Environment.DIRECTORY_PICTURES;

/**
 * Created by UGZ on 9/29/2016.
 */

public final class DownloadService extends IntentService {

    private NotificationUtil notificationUtil;

    public DownloadService() {
        super(DownloadService.class.getName());
    }

    public static void queue(Context context, Post post) {
        Intent starter = new Intent(context, DownloadService.class);
        starter.putExtra("id", post.getId());
        starter.putExtra("url", post.getOriginalFileUrl());
        starter.putExtra("filename", post.getFileName());
        context.startService(starter);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationUtil = new NotificationUtil(this);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        notificationUtil.notifyDownloadQueued();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) throw new NullPointerException("DownloadService intent is null");

        final int id = intent.getIntExtra("id", -1);
        final String url = intent.getStringExtra("url");
        final String filename = intent.getStringExtra("filename");

        try {
            File sourceFile = getSourceFile(url);
            File targetFile = getTargetFile(filename);
            copy(sourceFile, targetFile);

            Uri fileUri = Uri.fromFile(targetFile);
            Intent newImageIntent = new Intent(ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri);
            sendBroadcast(newImageIntent);

            notificationUtil.notifyDownloadSuccess(id, targetFile);
        } catch (Exception e) {
            notificationUtil.notifyDownloadFailed(id);
            Timber.e(e, "Download error: %s", url);
        }

        notificationUtil.notifyDownloadDone();
    }

    private File getSourceFile(String url) throws Exception {
        return Glide.with(this)
                .download(url)
                .submit()
                .get();
    }

    private File getTargetFile(String filename) {
        File picturesDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES);
        File saveDir = new File(picturesDir, "Chesto");
        if (!saveDir.mkdirs()) {
            Timber.d("getTargetFile: saveDir not created");
        }
        return new File(saveDir, filename);
    }

    private void copy(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inChannel.close();
        outChannel.close();
    }
}
