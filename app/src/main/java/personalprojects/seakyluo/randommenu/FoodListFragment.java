package personalprojects.seakyluo.randommenu;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import personalprojects.seakyluo.randommenu.Interfaces.OnDataItemClickedListener;
import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Food;

public class FoodListFragment extends Fragment {
    public static final String TAG = "FoodListFragment";
    private boolean selectable = false;
    private RecyclerView recyclerView;
    private FoodListAdapter adapter = new FoodListAdapter();
    private AList<Food> data = new AList<>();
    private AList<Food> selected = new AList<>();
    private OnDataItemClickedListener<Food> foodRemovedListener;
    private Food removedFood;
    private int removedIndex;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_linear_recycler_view, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        adapter.SetSelectable(selectable);
        adapter.SetsSelectionChangedListener((vh, selected) -> {
            ((FoodListAdapter.ViewHolder)vh).setSelected(selected);
        });
        adapter.SetContext(getContext());
        recyclerView.setAdapter(adapter);
        if (foodRemovedListener != null) AddSwipeControl();
        return view;
    }

    public void SetSelectedFood(AList<Food> data){
        adapter.SetSelectedFood(data);
    }
    public AList<Food> GetSelectedFood(){
        return selected;
    }
    public void SetData(AList<Food> data){ this.data.CopyFrom(data); adapter.SetData(data); }
    public void SetData(ArrayList<Food> data){ this.data.CopyFrom(data); adapter.SetData(data); }
    public AList<Food> GetData(){ return data; }
    public void Clear() { this.data.Clear(); adapter.Clear(); }
    public void SetSelectable(boolean selectable) { this.selectable = selectable; }
    public int RemoveFood(Food food) {
        removedIndex = adapter.data.IndexOf(removedFood = food);
        adapter.data.Pop(removedIndex);
        adapter.notifyItemRemoved(removedIndex);
        return removedIndex;
    }
    public void CancelRemoval(){
        adapter.data.Add(removedFood, removedIndex);
        adapter.notifyItemInserted(removedIndex);
    }
    public void Filter(String keyword){
        adapter.SetData(data.Find(f -> f.Name.contains(keyword)));
    }
    public void CancelFilter(){
        adapter.SetData(data);
        recyclerView.smoothScrollToPosition(0);
    }
    public void SetFoodClickedListener(OnDataItemClickedListener<Food> listener){ adapter.SetFoodClickedListener(listener); }
    public void SetFoodRemovedListener(OnDataItemClickedListener<Food> listener){ foodRemovedListener = listener; AddSwipeControl(); }
    public void SetShowLikeImage(boolean showLikeImage) { adapter.SetShowLikeImage(showLikeImage); }
    public void AddSwipeControl(){
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                foodRemovedListener.Click((CustomAdapter.CustomViewHolder)viewHolder, adapter.data.Get(viewHolder.getAdapterPosition()));
            }

            @Override
            public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                double itemHeight = itemView.getBottom() - itemView.getTop();
                if (dX == 0f && !isCurrentlyActive) {
                    Paint clearPaint = new Paint();
                    clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                    canvas.drawRect(itemView.getRight() + (int)dX, itemView.getTop(), itemView.getRight(), itemView.getBottom(),clearPaint);
                }else{
                    ColorDrawable background = new ColorDrawable();
                    int backgroundColor = Color.parseColor("#f44336");
                    background.setColor(backgroundColor);
                    background.setBounds(
                            itemView.getRight() + (int)dX,
                            itemView.getTop(),
                            itemView.getRight(),
                            itemView.getBottom()
                    );
                    background.draw(canvas);
                    // Calculate position of delete icon
                    Drawable deleteIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_delete_white);
                    double intrinsicWidth = deleteIcon.getIntrinsicWidth(),
                            intrinsicHeight = deleteIcon.getIntrinsicHeight(), deleteIconMargin = (itemHeight - intrinsicHeight) / 2,
                            deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2,
                            deleteIconLeft = itemView.getRight() - deleteIconMargin - intrinsicWidth,
                            deleteIconRight = itemView.getRight() - deleteIconMargin,
                            deleteIconBottom = deleteIconTop + intrinsicHeight;

                    // Draw the delete icon
                    deleteIcon.setBounds((int)deleteIconLeft, (int)deleteIconTop, (int)deleteIconRight, (int)deleteIconBottom);
                    deleteIcon.draw(canvas);
                }
                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);
    }
}
