package i.am.shiro.chesto.listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

/**
 * Created by Subaru Tashiro on 7/24/2017.
 */

public class ScrollToPageListener extends RecyclerView.OnScrollListener {

    private Listener1<Integer> onScrollToPageListener;

    public void setOnScrollToPageListener(Listener1<Integer> listener) {
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
