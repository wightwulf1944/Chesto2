package shiro.am.i.chesto.activityPost;

import android.content.Context;

import java.util.LinkedList;
import java.util.Queue;

import timber.log.Timber;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by Shiro on 25/12/2016.
 */

final class PhotoViewRecycler {

    private final Queue<PhotoView> recycledPhotoViews = new LinkedList<>();

    PhotoViewRecycler(Context context, int count) {
        for (int i = 0; i < count; i++) {
            recycledPhotoViews.add(new PhotoView(context));
        }
        Timber.d("%s", recycledPhotoViews.size());
    }

    void recyclePhotoView(PhotoView view) {
        recycledPhotoViews.add(view);
    }

    PhotoView getPhotoView() {
        return recycledPhotoViews.remove();
    }
}
