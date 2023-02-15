package personalprojects.seakyluo.randommenu.adapters.impl;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import personalprojects.seakyluo.randommenu.adapters.BaseFoodListAdapter;
import personalprojects.seakyluo.randommenu.interfaces.CustomDataItemClickedListener;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Food;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.R;

public class FoodListAdapter extends BaseFoodListAdapter {
    public AList<Food> selectedFood = new AList<>();
    private boolean selectable = false;
    private DataItemClickedListener<Tag> tagClickedListener;
    private CustomDataItemClickedListener<Food, Boolean> selectionChangedListener;
    public void setTagClickedListener(DataItemClickedListener<Tag> tagClickedListener) { this.tagClickedListener = tagClickedListener; }
    public void setsSelectionChangedListener(CustomDataItemClickedListener<Food, Boolean> selectionChangedListener) { this.selectionChangedListener = selectionChangedListener; }
    public void setSelectable(boolean selectable) { this.selectable = selectable; }

    @Override
    protected int getLayout(int viewType) {
        return R.layout.view_listed_food;
    }

    @Override
    protected void fillViewHolder(CustomViewHolder viewHolder, Food data, int position) {
        View view = viewHolder.getView();
        ImageView checkedImage = view.findViewById(R.id.checked_image);
        RecyclerView recyclerView = view.findViewById(R.id.tags_recycler_view);
        TagAdapter adapter = new TagAdapter();

        view.setOnClickListener(v -> {
            if (foodClickedListener != null){
                foodClickedListener.click(viewHolder, data);
            }
            if (selectable){
                setSelected(viewHolder, !data.isSelected());
                if (selectionChangedListener != null){
                    selectionChangedListener.click(viewHolder, data.isSelected());
                }
            }
        });
        checkedImage.setVisibility(selectedFood.contains(data) ?  View.VISIBLE : View.GONE);
        adapter.setData(data.getTags());
        adapter.setTagClickedListener((v, t) -> {
            if (tagClickedListener != null) tagClickedListener.click(v, t);
        });
        recyclerView.setAdapter(adapter);
    }

    public void setSelectedFood(AList<Food> foods){
        selectedFood.copyFrom(foods);
    }

    public void setSelected(CustomViewHolder viewHolder, boolean selected){
        Food data = viewHolder.getData();
        data.setSelected(selected);

        View view = viewHolder.getView();
        ImageView checkedImage = view.findViewById(R.id.checked_image);
        checkedImage.setVisibility(selected ? View.VISIBLE : View.GONE);

        if (selected) selectedFood.add(data);
        else selectedFood.remove(data);
    }

}
