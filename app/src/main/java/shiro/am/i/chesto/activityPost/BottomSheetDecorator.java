package shiro.am.i.chesto.activityPost;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;
import android.widget.ImageButton;

import shiro.am.i.chesto.R;

/**
 * Created by Shiro on 9/1/2016.
 */
final class BottomSheetDecorator extends BottomSheetBehavior.BottomSheetCallback {

    private final BottomSheetBehavior bottomSheetBehavior;
    private final ImageButton infoButton;
    private final View bottomSheetHeader;

    BottomSheetDecorator(View view) {
        bottomSheetBehavior = BottomSheetBehavior.from(view);
        bottomSheetBehavior.setBottomSheetCallback(this);

        infoButton = (ImageButton) view.findViewById(R.id.infoButton);
        infoButton.setOnClickListener(v -> toggleState());

        bottomSheetHeader = view.findViewById(R.id.bottomSheetHeader);
    }

    private void toggleState() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    boolean tryIsCollapsed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            return true;
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return false;
        }
    }

    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState) {
        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            infoButton.setImageResource(R.drawable.ic_nav_info);
        } else {
            infoButton.setImageResource(R.drawable.ic_nav_arrow_hide);
        }
    }

    @Override
    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        bottomSheetHeader.setAlpha(slideOffset);
    }
}
