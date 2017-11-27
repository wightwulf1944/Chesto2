package shiro.am.i.chesto.activitypost;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import java.util.Locale;

import shiro.am.i.chesto.model.Post;
import timber.log.Timber;

/**
 * Created by Shiro on 18/01/2017.
 * TODO
 */

final public class DownloadQualityDialogFragment extends DialogFragment {

    private final Post mPost;

    DownloadQualityDialogFragment(Post post) {
        mPost = post;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // the following dimensions are just approximations
        int resizedWidth = 850;
        int resizedHeight = (mPost.getHeight() / mPost.getWidth()) * resizedWidth;

        CharSequence[] options = new CharSequence[]{
                String.format(Locale.US, "%sx%s", resizedWidth, resizedHeight),
                String.format(Locale.US, "%sx%s", mPost.getWidth(), mPost.getHeight())
        };

        return new AlertDialog.Builder(getActivity())
                .setTitle("Choose image size")
                .setItems(
                        options,
                        (dialog, which) -> Timber.d("%s selected", which)
                )
                .create();
    }
}
