package shiro.am.i.chesto;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by UGZ on 8/4/2016.
 */
public final class U {

    private static final Context appContext = Chesto.getAppContext();

    public static int dpToPx(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                appContext.getResources().getDisplayMetrics());
    }
}
