package shiro.am.i.chesto.serviceImageDownloader;

import android.app.Service;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import shiro.am.i.chesto.R;

/**
 * Created by UGZ on 9/29/2016.
 */

final class NotificationHelper {

    static void bind(Service service) {
        final String title = service.getString(R.string.app_name);
        final int color = ContextCompat.getColor(service, R.color.colorPrimary);

        service.startForeground(1,
                new NotificationCompat.Builder(service)
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
}
