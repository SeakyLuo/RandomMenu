package personalprojects.seakyluo.randommenu.fragments;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.adapters.FoodListAdapter;
import personalprojects.seakyluo.randommenu.interfaces.OnDataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Food;
import personalprojects.seakyluo.randommenu.R;

public class FoodListFragment extends Fragment {
    public static final String TAG = "FoodListFragment";
    private boolean selectable = false;
    private RecyclerView recyclerView;
    private FoodListAdapter adapter = new FoodListAdapter();
    private AList<Food> data = new AList<>();
    private OnDataItemClickedListener<Food> foodRemovedListener;
    private OnDataItemClickedListener<Boolean> foodSelectedListener;
    private Food removedFood;
    private int removedIndex;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_linear_recycler_view, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        adapter.setSelectable(selectable);
        adapter.setsSelectionChangedListener(foodSelectedListener);
        adapter.context = getContext();
        recyclerView.setAdapter(adapter);
        if (foodRemovedListener != null) addSwipeControl();
        return view;
    }

    public void setSelectedFood(AList<Food> data){ adapter.setSelectedFood(data); }
    public AList<Food> GetSelectedFood(){ return adapter.selectedFood; }
    public void setData(AList<Food> data){ this.data.copyFrom(data); adapter.setData(data); }
    public void setData(ArrayList<Food> data){ this.data.copyFrom(data); adapter.setData(data); }
    public AList<Food> GetData(){ return data; }
    public void Clear() { this.data.clear(); adapter.clear(); }
    public void SetSelectable(boolean selectable) { this.selectable = selectable; }
    public int RemoveFood(Food food) {
        removedIndex = adapter.data.indexOf(removedFood = food);
        adapter.data.pop(removedIndex);
        adapter.notifyItemRemoved(removedIndex);
        return removedIndex;
    }
    public void unselectFood(String food){
        ((FoodListAdapter.ViewHolder)adapter.viewHolders.first(vh -> ((FoodListAdapter.ViewHolder)vh).data.Name.equals(food))).setSelected(false);
    }
    public void cancelRemoval(){
        adapter.data.add(removedFood, removedIndex);
        adapter.notifyItemInserted(removedIndex);
    }
    public void filter(String keyword){
        adapter.setData(data.find(f -> f.Name.contains(keyword)));
    }
    public void cancelFilter(){
        adapter.setData(data);
        recyclerView.smoothScrollToPosition(0);
    }
    public void updateFood(Food before, Food after){
        int index = adapter.getData().indexOf(before);
        if (index > -1){
            adapter.update(after, index);
        }
    }
    public void setFoodClickedListener(OnDataItemClickedListener<Food> listener){ adapter.setFoodClickedListener(listener); }
    public void setFoodSelectedListener(OnDataItemClickedListener<Boolean> listener){ foodSelectedListener = listener; }
    public void setFoodRemovedListener(OnDataItemClickedListener<Food> listener){ foodRemovedListener = listener; addSwipeControl(); }
    public void setShowLikeImage(boolean showLikeImage) { adapter.setShowLikeImage(showLikeImage); }
    public void addSwipeControl(){
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                foodRemovedListener.click((CustomAdapter.CustomViewHolder)viewHolder, adapter.data.get(viewHolder.getAdapterPosition()));
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
