package shiro.am.i.chesto.serviceImageDownloader;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import shiro.am.i.chesto.databasePost.PostStore;
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
        NotificationHelper.bind(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final Post post = PostStore.getInstance().get(intent.getIntExtra("default", -1));

        ToastHelper.show("Saving: " + post.getId() + ".png");

        final Bitmap bitmap = getImageBitmap(post.getFileUrl());
        final File file = getImageFile(post.getFileName());
        if (saveImage(bitmap, file)) {
            notifyMediaScanner(file);
        }

        ToastHelper.show("Saved: " + post.getId() + ".png");
    }

    private Bitmap getImageBitmap(String fileUrl) {
        Bitmap bitmap = null;
        do {
            try {
                bitmap = Picasso.with(this)
                        .load(fileUrl)
                        .get();
            } catch (IOException e) {
                Timber.e(e, "getImageBitmap: error getting bitmap");
            }
        } while (bitmap == null);

        return bitmap;
    }

    private static File getImageFile(String fileName) {
        final File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        final File saveDir = new File(picturesDir, "Chesto");
        final File imageFile = new File(saveDir, fileName);
        if (!saveDir.mkdirs()) {
            Timber.d("getImageFile: saveDir not created");
        }
        return imageFile;
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

    private void notifyMediaScanner(File file) {
        Uri data = Uri.fromFile(file);
        final Intent mediaIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaIntent.setData(data);
        sendBroadcast(mediaIntent);
    }
}
