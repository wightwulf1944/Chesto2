package shiro.am.i.chesto.activityPost;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Shiro on 8/25/2016.
 */
final class ImageViewRecycler {
    private static final Queue<ImageView> recycledViews = new LinkedList<>();

    static ImageView getView(Context context, ViewGroup container) {
        final ImageView view;

        if (recycledViews.isEmpty()) {
            view = new ImageView(context);
        } else {
            view = recycledViews.remove();
        }

        container.addView(view);
        return view;
    }

    static void recycleView(ImageView view, ViewGroup container) {
        recycledViews.add(view);
        container.removeView(view);
    }
}
