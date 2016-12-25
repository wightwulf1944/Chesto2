package shiro.am.i.chesto;

import android.util.TypedValue;

/**
 * Created by Shiro on 8/4/2016.
 */
public final class U {

    private U() {
        throw new AssertionError("Tried to create instance");
    }

    public static int dpToPx(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Chesto.getInstance().getResources().getDisplayMetrics());
    }
}
