package shiro.am.i.chesto;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import shiro.am.i.chesto.retrofitDanbooru.Post;

/**
 * Created by UGZ on 9/8/2016.
 */
public class ImageDownloadService extends IntentService {

    private static final String TAG = ImageDownloadService.class.getName();
    private static final PostList postList = PostList.getInstance();
    private static final File saveDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES + "/Chesto/"
    );

    private final RequestManager requestManager;

    public ImageDownloadService() {
        super(TAG);

        requestManager = Glide.with(this);

        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final Post post = postList.get(intent.getIntExtra("DEFAULT", -1));

        requestManager
                .load(post.getFileUrl())
                .downloadOnly(new SimpleTarget<File>() {
                    @Override
                    public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                        try {
                            final File file = saveImage(resource, post.getFileName());
                            final Uri fileUri = Uri.fromFile(file);

                            final Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            intent.setData(fileUri);
                            sendBroadcast(intent);

                        } catch (IOException e) {
                            Log.d(TAG, "Error saving image", e);
                        }
                    }
                });
    }

    private static File saveImage(File sourceFile, String name) throws IOException {

        File destFile = new File(saveDir, name);

        FileChannel inChannel = new FileInputStream(sourceFile).getChannel();
        FileChannel outChannel = new FileOutputStream(destFile).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
        return destFile;
    }
}
