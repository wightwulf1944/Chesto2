package shiro.am.i.chesto.serviceimagedownloader;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import java.io.File;

import shiro.am.i.chesto.Chesto;
import shiro.am.i.chesto.R;

/**
 * Created by UGZ on 11/17/2016.
 */

final class NotificationHelper {

    private static final int PROGRESS_ID = 1;
    private static final int FAILED_ID = 2;

    private final NotificationManager manager;
    private final NotificationCompat.Builder progressBuilder;
    private final NotificationCompat.Builder failedBuilder;
    private final NotificationCompat.Builder finishBuilder;
    private final NotificationCompat.BigPictureStyle bigPictureStyle;
    private int downloadsQueued;
    private int downloadsFailed;
    private int downloadsSuccessful;

    NotificationHelper(Service service) {
        manager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);

        final int color = ContextCompat.getColor(service, R.color.primary);
        final String title = service.getString(R.string.app_name);

        progressBuilder = new NotificationCompat.Builder(service)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setColor(color)
                .setLocalOnly(true)
                .setShowWhen(false)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setProgress(0, 0, true)
                .setContentText("Downloading");

        failedBuilder = new NotificationCompat.Builder(service)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setColor(color)
                .setLocalOnly(true)
                .setAutoCancel(true);

        finishBuilder = new NotificationCompat.Builder(service)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setColor(color)
                .setLocalOnly(true)
                .setContentText("Image Saved")
                .setAutoCancel(true);

        bigPictureStyle = new NotificationCompat.BigPictureStyle()
                .setSummaryText("Image saved");

        service.startForeground(PROGRESS_ID, progressBuilder.build());
    }

    void notifyQueued() {
        ++downloadsQueued;
        updateProgress();
    }

    void notifyFailed() {
        ++downloadsFailed;
        updateProgress();
    }

    void notifySuccessful(File file, int id) {
        ++downloadsSuccessful;
        updateProgress();

        Intent viewIntent = new Intent(Intent.ACTION_VIEW);
        viewIntent.setDataAndType(Uri.fromFile(file), "image/*");
        viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(
                Chesto.getInstance(),
                0,
                viewIntent,
                PendingIntent.FLAG_ONE_SHOT
        );

        bigPictureStyle.bigPicture(BitmapFactory.decodeFile(file.getAbsolutePath()));

        finishBuilder
                .setContentIntent(viewPendingIntent)
                .setStyle(bigPictureStyle)
                .setWhen(System.currentTimeMillis());

        manager.notify(id, finishBuilder.build());
    }

    private void updateProgress() {
        int downloadsCompleted = downloadsFailed + downloadsSuccessful;
        if (downloadsCompleted < downloadsQueued) {
            progressBuilder
                    .setProgress(downloadsQueued, downloadsCompleted, false)
                    .setContentInfo(String.format("%s/%s", downloadsCompleted, downloadsQueued));
            manager.notify(PROGRESS_ID, progressBuilder.build());
        } else if (downloadsFailed > 0) {
            String text;
            if (downloadsFailed == 1) {
                text = String.valueOf(downloadsFailed) + "Download Failed";
            } else {
                text = String.valueOf(downloadsFailed) + "Downloads Failed";
            }
            failedBuilder.setContentText(text);
            manager.notify(FAILED_ID, failedBuilder.build());
        }
    }
}
