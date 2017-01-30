package shiro.am.i.chesto.activitySearch;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

/**
 * Created by Shiro on 30/01/2017.
 */

abstract class OnEditorSearchListener implements TextView.OnEditorActionListener {
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            onEditorSearch();
            return true;
        } else {
            return false;
        }
    }

    abstract void onEditorSearch();
}
