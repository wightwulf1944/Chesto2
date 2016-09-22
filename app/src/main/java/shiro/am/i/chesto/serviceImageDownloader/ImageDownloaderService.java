package shiro.am.i.chesto.serviceImageDownloader;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import shiro.am.i.chesto.PostList;
import shiro.am.i.chesto.retrofitDanbooru.Post;

/**
 * Created by Shiro on 9/22/2016.
 * TODO: this service does not stop on it's own
 */

public final class ImageDownloaderService extends Service {

    private static final String TAG = ImageDownloaderService.class.getName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void notifyStarted(Post post) {
        Toast.makeText(this, "Saving: " + post.getFileName(), Toast.LENGTH_SHORT).show();
    }

    private void notifyFinished(Post post) {
        Toast.makeText(this, "Saved: " + post.getFileName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        final Post post = PostList.getInstance().get(intent.getIntExtra("default", -1));
        final File destinationFile = createSavedFile(post);

        notifyStarted(post);

        Glide.with(this)
                .load(post.getFileUrl())
                .downloadOnly(new SimpleTarget<File>() {
                    @Override
                    public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                        copyFile(resource, destinationFile);
                        notifyMediaScanner(destinationFile);
                        notifyFinished(post);
                    }
                });

        return super.onStartCommand(intent, flags, startId);
    }

    private File createSavedFile(Post post) {
        final File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        final File saveDir = new File(picturesDir, "Chesto");
        if (!saveDir.mkdirs()) {
            Log.d(TAG, "createSavedFile: save folder not created");
        }
        return new File(saveDir, post.getFileName());
    }

    private static void copyFile(File sourceFile, File destinationFile) {
        try {
            FileChannel inChannel = new FileInputStream(sourceFile).getChannel();
            FileChannel outChannel = new FileOutputStream(destinationFile).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inChannel.close();
            outChannel.close();
        } catch (IOException e) {
            Log.e(TAG, "copyFile: error copying file");
        }
    }

    private void notifyMediaScanner(File file) {
        Uri data = Uri.fromFile(file);
        final Intent mediaIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaIntent.setData(data);
        sendBroadcast(mediaIntent);
    }
}
