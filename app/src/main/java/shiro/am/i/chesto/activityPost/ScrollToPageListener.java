package shiro.am.i.chesto.activitypost;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import shiro.am.i.chesto.listener.Listener1;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

/**
 * Created by Shiro on 11/28/2017.
 */

class ScrollToPageListener extends RecyclerView.OnScrollListener {

    private Listener1<Integer> onScrollToPageListener;

    void setOnScrollToPageListener(Listener1<Integer> listener) {
        onScrollToPageListener = listener;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int position = layoutManager.findFirstCompletelyVisibleItemPosition();

        if (position != NO_POSITION) {
            onScrollToPageListener.onEvent(position);
        }
    }
}