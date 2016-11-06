package shiro.am.i.chesto.serviceImageDownloader;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import shiro.am.i.chesto.PostStore;
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
        final Post post = PostStore.get(intent.getIntExtra("default", -1));
        final String fileName = post.getId() + ".png";

        ToastHelper.show("Saving: " + fileName);

        final Bitmap bitmap = getImageBitmap(this, post.getFileUrl());
        final File file = getImageFile(fileName);
        if (saveImage(bitmap, file)) {
            notifyMediaScanner(this, file);
        }

        ToastHelper.show("Saved: " + fileName);
    }

    private static Bitmap getImageBitmap(Context context, String fileUrl) {
        Bitmap bitmap = null;
        do {
            try {
                bitmap = Picasso.with(context)
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
