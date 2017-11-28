package shiro.am.i.chesto.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import java.io.File;

import shiro.am.i.chesto.R;

import static android.support.v4.app.NotificationCompat.CATEGORY_SERVICE;
import static android.support.v4.app.NotificationCompat.PRIORITY_LOW;

/**
 * Created by Shiro on 11/28/2017.
 */

public class NotificationUtil {

    private static final int NOTIFICATION_ID = 1;

    private final Service parentService;

    private final NotificationManager manager;

    private int downloadsQueued;

    private int downloadsDone;

    public NotificationUtil(Service service) {
        parentService = service;
        manager = (NotificationManager) parentService.getSystemService(Context.NOTIFICATION_SERVICE);
        parentService.startForeground(NOTIFICATION_ID, makeProgressNotification());
    }

    public void notifyDownloadQueued() {
        downloadsQueued++;
        manager.notify(NOTIFICATION_ID, makeProgressNotification());
    }

    public void notifyDownloadDone() {
        downloadsDone++;
        manager.notify(NOTIFICATION_ID, makeProgressNotification());
    }

    public void notifyDownloadSuccess(int id, File file) {
        manager.notify(id, makeSuccessNotification(file));
    }

    public void notifyDownloadFailed(int id) {
        manager.notify(id, makeFailedNotification());
    }

    private Notification makeProgressNotification() {
        String contentInfo = String.format("%s/%s", downloadsDone, downloadsQueued);
        String contentText = "Saving image";
        if (downloadsQueued > 1) {
            contentText = contentText + "s";
        }

        return makeBaseNotification()
                .setContentText(contentText)
                .setOngoing(true)
                .setPriority(PRIORITY_LOW)
                .setCategory(CATEGORY_SERVICE)
                .setProgress(downloadsQueued, downloadsDone, false)
                .setContentInfo(contentInfo)
                .build();
    }

    private Notification makeSuccessNotification(File file) {
        Uri uri = Uri.fromFile(file);
        Intent viewIntent = new Intent(Intent.ACTION_VIEW);
        viewIntent.setDataAndType(uri, "image/*");
        viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(
                parentService,
                0,
                viewIntent,
                PendingIntent.FLAG_ONE_SHOT
        );

        String contentText = "Image saved. Tap to View.";

        String absolutePath = file.getAbsolutePath();
        Bitmap bitmap = BitmapFactory.decodeFile(absolutePath);
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle()
                .setSummaryText(contentText)
                .bigPicture(bitmap);

        return makeBaseNotification()
                .setContentText(contentText)
                .setAutoCancel(true)
                .setContentIntent(viewPendingIntent)
                .setStyle(bigPictureStyle)
                .build();
    }

    private Notification makeFailedNotification() {
        return makeBaseNotification()
                .setContentText("Failed to save image. Tap to retry.")
                .setAutoCancel(true)
                .build();
    }

    private NotificationCompat.Builder makeBaseNotification() {
        String notificationTitle = parentService.getString(R.string.app_name);
        int notificationColor = ContextCompat.getColor(parentService, R.color.primary);

        return new NotificationCompat.Builder(parentService)
                .setContentTitle(notificationTitle)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(notificationColor)
                .setLocalOnly(true);
    }
}
