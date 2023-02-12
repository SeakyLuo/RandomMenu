package personalprojects.seakyluo.randommenu.helpers;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;

// for drag and drop
public class DragDropCallback<T> extends ItemTouchHelper.Callback {

    private final DragDropListener<T> listener;

    public DragDropCallback(DragDropListener<T> listener) {
        this.listener = listener;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        listener.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof CustomAdapter.CustomViewHolder) {
                CustomAdapter<T>.CustomViewHolder myViewHolder = (CustomAdapter<T>.CustomViewHolder) viewHolder;
                listener.onRowSelected(myViewHolder);
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    // gets triggered when the user interaction stops with the RecyclerView row.
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof CustomAdapter.CustomViewHolder) {
            CustomAdapter<T>.CustomViewHolder myViewHolder = (CustomAdapter<T>.CustomViewHolder) viewHolder;
            listener.onRowClear(myViewHolder);
        }
    }

    public interface DragDropListener<T> {
        void onRowMoved(int fromPosition, int toPosition);
        void onRowSelected(CustomAdapter<T>.CustomViewHolder myViewHolder);
        void onRowClear(CustomAdapter<T>.CustomViewHolder myViewHolder);
    }

    public interface StartDragListener<T> {
        void requestDrag(CustomAdapter<T>.CustomViewHolder viewHolder);
    }

}