package shiro.am.i.chesto.serviceImageDownloader;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import shiro.am.i.chesto.R;

/**
 * Created by UGZ on 11/17/2016.
 */

final class NotificationHelper {

    private static final int PERSISTENT_ID = 1;
    private static final int FINISH_ID = 2;

    private final NotificationManager manager;
    private final NotificationCompat.Builder persistentBuilder;
    private final NotificationCompat.Builder finishBuilder;
    private int downloadsQueued;
    private int downloadsFailed;
    private int downloadsFinished;

    NotificationHelper(Service service) {
        manager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);

        final int color = ContextCompat.getColor(service, R.color.colorPrimary);
        final String title = service.getString(R.string.app_name);

        persistentBuilder = new NotificationCompat.Builder(service)
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

        finishBuilder = new NotificationCompat.Builder(service)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setColor(color)
                .setLocalOnly(true)
                .setAutoCancel(true);

        service.startForeground(PERSISTENT_ID, persistentBuilder.build());
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

    void notifyFinished() {
        ++downloadsFinished;
        updateProgress();
    }

    private void updateProgress() {
        if (downloadsFinished < downloadsQueued) {
            persistentBuilder
                    .setProgress(downloadsQueued, downloadsFinished, false)
                    .setContentInfo(String.format("%s/%s", downloadsFinished, downloadsQueued));
            manager.notify(PERSISTENT_ID, persistentBuilder.build());
        } else {
            final String text = String.format(
                    "%s Download(s) finished - %s Failed",
                    downloadsFinished,
                    downloadsFailed
            );
            finishBuilder.setContentText(text);
            manager.notify(FINISH_ID, finishBuilder.build());
        }
    }
}
