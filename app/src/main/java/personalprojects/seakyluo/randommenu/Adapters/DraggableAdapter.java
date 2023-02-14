package personalprojects.seakyluo.randommenu.adapters;

import android.view.MotionEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import personalprojects.seakyluo.randommenu.helpers.DragDropCallback;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class DraggableAdapter<T> extends CustomAdapter<T> implements DragDropCallback.DragDropListener<T> {

    protected DragDropCallback.DragStartListener<T> dragStartListener;

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        data.move(fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowClear(CustomViewHolder myViewHolder) {

    }

    @Override
    public void onRowSelected(CustomViewHolder myViewHolder) {

    }

    protected boolean dragStart(CustomViewHolder viewHolder, MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (dragStartListener != null){
                dragStartListener.requestDrag(viewHolder);
            }
        }
        return false;
    }
}