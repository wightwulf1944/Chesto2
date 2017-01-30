package shiro.am.i.chesto.activitySearch;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Shiro on 30/01/2017.
 */

final class EditTextWrapper
        implements TextWatcher, TextView.OnEditorActionListener {

    interface AfterTextChangedListener {
        void afterTextChanged(String s);
    }

    interface OnEditorSearchListener {
        void onEditorSearch();
    }

    private EditText editText;
    private AfterTextChangedListener afterTextChangedListener;
    private OnEditorSearchListener onEditorSearchListener;

    EditTextWrapper(EditText v) {
        editText = v;
        v.addTextChangedListener(this);
        v.setOnEditorActionListener(this);
    }

    void setText(CharSequence s) {
        editText.setText(s);
    }

    Editable getText() {
        return editText.getText();
    }

    void setAfterTextChangedListener(AfterTextChangedListener l) {
        afterTextChangedListener = l;
    }

    void setOnEditorSearchListener(OnEditorSearchListener l) {
        onEditorSearchListener = l;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        afterTextChangedListener.afterTextChanged(s.toString());
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            onEditorSearchListener.onEditorSearch();
            return true;
        } else {
            return false;
        }
    }
}
