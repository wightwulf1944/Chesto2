package shiro.am.i.chesto;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import shiro.am.i.chesto.retrofitDanbooru.Post;

/**
 * Created by UGZ on 9/15/2016.
 */
public class ImageDownloadService extends IntentService {

    private static final String TAG = ImageDownloadService.class.getName();
    private static final PostList postList = PostList.getInstance();

    public ImageDownloadService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final Post post = postList.get(intent.getIntExtra("default", -1));

        FutureTarget<File> futureFile = Glide.with(this)
                .load(post.getFileUrl())
                .downloadOnly(post.getImageWidth(), post.getImageHeight());

        File imageFile = null;
        try {
            imageFile = saveImage(futureFile.get(), post.getFileName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        notifyMediaScanner(imageFile);
        Toast.makeText(this, "Image Saved", Toast.LENGTH_SHORT).show();
    }

    private void notifyMediaScanner(File file) {
        Uri data = Uri.fromFile(file);
        final Intent mediaIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaIntent.setData(data);
        sendBroadcast(mediaIntent);
    }

    private static File saveImage(File sourceFile, String name) throws IOException {
        final File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        final File saveDir = new File(picturesDir, "Chesto");
        final File destFile = new File(saveDir, name);

        if (!saveDir.mkdirs()) {
            Log.d(TAG, "save folder not created");
        }

        FileChannel inChannel = new FileInputStream(sourceFile).getChannel();
        FileChannel outChannel = new FileOutputStream(destFile).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            inChannel.close();
            outChannel.close();
        }
        return destFile;
    }
}
