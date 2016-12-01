package shiro.am.i.chesto.serviceImageDownloader;

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
    private static final int SUMMARY_ID = 2;

    private final NotificationManager manager;
    private final NotificationCompat.Builder progressBuilder;
    private final NotificationCompat.Builder summaryBuilder;
    private final NotificationCompat.Builder finishBuilder;
    private final NotificationCompat.BigPictureStyle bigPictureStyle;
    private int downloadsQueued;
    private int downloadsFailed;
    private int downloadsFinished;

    NotificationHelper(Service service) {
        manager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);

        final int color = ContextCompat.getColor(service, R.color.colorPrimary);
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
                .setProgress(downloadsQueued, downloadsFinished, true)
                .setContentText("Downloading");

        summaryBuilder = new NotificationCompat.Builder(service)
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
        ++downloadsFinished;
        updateProgress();
    }

    void notifyFinished(File file, int id) {
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
                .setStyle(bigPictureStyle);

        manager.notify(id, finishBuilder.build());

        ++downloadsFinished;
        updateProgress();
    }

    private void updateProgress() {
        if (downloadsFinished < downloadsQueued) {
            progressBuilder
                    .setProgress(downloadsQueued, downloadsFinished, false)
                    .setContentInfo(String.format("%s/%s", downloadsFinished, downloadsQueued));
            manager.notify(PROGRESS_ID, progressBuilder.build());
        } else if (downloadsFailed > 0) {
            final String text = String.format(
                    "%s Download(s) finished - %s Failed",
                    downloadsFinished,
                    downloadsFailed
            );
            summaryBuilder.setContentText(text);
            manager.notify(SUMMARY_ID, summaryBuilder.build());
        }
    }
}
