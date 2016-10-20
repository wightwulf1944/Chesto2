package shiro.am.i.chesto.serviceImageDownloader;

import android.widget.Toast;

import shiro.am.i.chesto.Chesto;

/**
 * Created by UGZ on 9/29/2016.
 */

final class ToastHelper {

    static void show(final String text) {
        Chesto.getMainHandler().post(() ->
                Toast.makeText(Chesto.getInstance(), text, Toast.LENGTH_SHORT).show()
        );
    }
}
