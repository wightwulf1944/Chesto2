package shiro.am.i.chesto;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

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

    public static void picassoCombo(final ImageView imageView,
                                    final RequestCreator thumbnail,
                                    final RequestCreator large) {
        final Target previousReq = (Target) imageView.getTag();
        if (previousReq != null) {
            Picasso.with(Chesto.getInstance()).cancelRequest(previousReq);
        }

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                imageView.setImageDrawable(errorDrawable);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                thumbnail.into(imageView);
            }
        };

        imageView.setTag(target);
        large.into(target);
    }
}
