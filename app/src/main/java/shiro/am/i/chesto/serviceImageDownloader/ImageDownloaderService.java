package shiro.am.i.chesto.serviceImageDownloader;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import shiro.am.i.chesto.PostStore;
import shiro.am.i.chesto.R;
import shiro.am.i.chesto.retrofitDanbooru.Post;
import timber.log.Timber;

/**
 * Created by UGZ on 9/29/2016.
 */

public final class ImageDownloaderService extends IntentService {

    public ImageDownloaderService() {
        super(ImageDownloaderService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final String title = getString(R.string.app_name);
        final int color = ContextCompat.getColor(this, R.color.colorPrimary);
        startForeground(1,
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText("Downloading image(s)")
                        .setColor(color)
                        .setLocalOnly(true)
                        .setOngoing(true)
                        .setShowWhen(false)
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                        .setCategory(NotificationCompat.CATEGORY_SERVICE)
                        .build()
        );
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final Post post = PostStore.get(intent.getIntExtra("default", -1));
        final String fileName = post.getId() + ".png";


        final Bitmap bitmap = getImageBitmap(this, post);
        final File file = getImageFile(fileName);
        if (saveImage(bitmap, file)) {
            notifyMediaScanner(this, file);
        }

    }

    private static Bitmap getImageBitmap(Context context, Post post) {
        Bitmap bitmap = null;
        do {
            try {
                bitmap = Glide.with(context)
                        .load(post.getLargeFileUrl())
                        .asBitmap()
                        .into(post.getImageWidth(), post.getImageHeight())
                        .get();
            } catch (Exception e) {
                Timber.e(e, "getImageBitmap: error getting bitmap");
            }
        } while (bitmap == null);

        return bitmap;
    }

    private static File getImageFile(String fileName) {
        final File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        final File saveDir = new File(picturesDir, "Chesto");
        if (!saveDir.mkdirs()) {
            Timber.d("getImageFile: saveDir not created");
        }
        return new File(saveDir, fileName);
    }

    private static boolean saveImage(Bitmap bitmap, File file) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Timber.e(e, "saveImage: error creating FileOutputStream");
        }
        return bitmap.compress(Bitmap.CompressFormat.PNG, 0, out);
    }

    private static void notifyMediaScanner(Context context, File file) {
        Uri data = Uri.fromFile(file);
        final Intent mediaIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaIntent.setData(data);
        context.sendBroadcast(mediaIntent);
    }
}
