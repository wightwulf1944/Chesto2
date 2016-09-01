package shiro.am.i.chesto.activityPost;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;
import android.widget.ImageButton;

import shiro.am.i.chesto.R;

/**
 * Created by UGZ on 9/1/2016.
 */
final class XBottomSheet extends BottomSheetBehavior.BottomSheetCallback {

    private BottomSheetBehavior bottomSheetBehavior;
    private ImageButton infoButton;
    private View bottomSheetHeader;

    XBottomSheet(View view) {
        bottomSheetBehavior = BottomSheetBehavior.from(view);
        bottomSheetBehavior.setBottomSheetCallback(this);

        infoButton = (ImageButton) view.findViewById(R.id.infoButton);

        bottomSheetHeader = view.findViewById(R.id.bottomSheetHeader);
    }

    void toggleState() {
        switch (bottomSheetBehavior.getState()) {
            case BottomSheetBehavior.STATE_COLLAPSED:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            default:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
        }
    }

    boolean toggleIsCollapsed() {
        switch (bottomSheetBehavior.getState()) {
            case BottomSheetBehavior.STATE_COLLAPSED:
                return true;
            default:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                return false;
        }
    }

    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState) {
        switch (newState) {
            case BottomSheetBehavior.STATE_COLLAPSED:
                infoButton.setImageResource(R.drawable.ic_info);
                break;
            default:
                infoButton.setImageResource(R.drawable.ic_arrow_hide);
                break;
        }
    }

    @Override
    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        bottomSheetHeader.setAlpha(slideOffset);
    }
}
