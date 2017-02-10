package cool.eye.ridding.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import cool.eye.ridding.widget.SwipeItemLayout;
import cool.eye.ridding.widget.stickyview.StickyRecyclerHeadersAdapter;


/**
 * Created by cool on 16-6-13.
 */
public abstract class BaseSwipeAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH>
        implements View.OnClickListener, SwipeItemLayout.SwipeItemLayoutDelegate,
        StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    public void closeOpenedSwipeItem() {
        closeOpenedSwipeItemLayoutWithAnim();
    }

    public void closeOpenedSwipeItemLayoutWithAnim() {
    }

    @Override
    public void onSwipeItemLayoutOpened(SwipeItemLayout swipeItemLayout) {
        closeOpenedSwipeItemLayoutWithAnim();
    }

    @Override
    public void onSwipeItemLayoutClosed(SwipeItemLayout swipeItemLayout) {

    }

    @Override
    public void onSwipeItemLayoutStartOpen(SwipeItemLayout swipeItemLayout) {
        closeOpenedSwipeItemLayoutWithAnim();
    }

    @Override
    public void onClick(View v) {
    }
}
