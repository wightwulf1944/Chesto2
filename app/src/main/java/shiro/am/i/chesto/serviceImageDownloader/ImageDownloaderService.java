package shiro.am.i.chesto.serviceImageDownloader;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

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
    protected void onHandleIntent(Intent intent) {
        final Post post = PostStore.getInstance().get(intent.getIntExtra("default", -1));

        Toast.makeText(this, "Saving: " + post.getId() + ".png", Toast.LENGTH_SHORT).show();

        final Bitmap bitmap = getImageBitmap(post.getFileUrl());
        final File file = getImageFile(post.getFileName());
        if (saveImage(bitmap, file)) {
            notifyMediaScanner(file);
        }

        Toast.makeText(this, "Saved: " + post.getId() + ".png", Toast.LENGTH_SHORT).show();
    }

    private Bitmap getImageBitmap(String fileUrl) {
        Bitmap bitmap = null;
        try {
            bitmap = Picasso.with(this)
                    .load(fileUrl)
                    .get();
        } catch (IOException e) {
            Timber.e(e, "getImageBitmap: error getting bitmap");
        }
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

    private boolean saveImage(Bitmap bitmap, File file) {
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
