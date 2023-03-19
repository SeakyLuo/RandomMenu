package personalprojects.seakyluo.randommenu.adapters.impl;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import personalprojects.seakyluo.randommenu.adapters.BaseFoodListAdapter;
import personalprojects.seakyluo.randommenu.interfaces.CustomDataItemClickedListener;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Food;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.utils.ImageUtils;

public class FoodListAdapter extends BaseFoodListAdapter {

    @Setter
    private boolean selectable = false;
    @Setter
    private CustomDataItemClickedListener<Food, Boolean> selectionChangedListener;

    @Override
    protected int getLayout(int viewType) {
        return R.layout.view_listed_food;
    }

    @Override
    protected void fillViewHolder(CustomViewHolder viewHolder, Food data, int position) {
        super.fillViewHolder(viewHolder, data, position);
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
        checkedImage.setVisibility(data.isSelected() ?  View.VISIBLE : View.GONE);
        adapter.setData(data.getTags());
        recyclerView.setAdapter(adapter);
    }

    public void setSelectedFoods(List<Food> foods){
        Set<Long> foodIds = foods.stream().map(Food::getId).collect(Collectors.toSet());
        for (Food food : data){
            food.setSelected(foodIds.contains(food.getId()));
        }
    }

    public AList<Food> getSelectedFoods(){
        return data.stream().filter(Food::isSelected).collect(Collectors.toCollection(AList::new));
    }

    public void setSelected(CustomViewHolder viewHolder, boolean selected){
        Food data = viewHolder.getData();
        data.setSelected(selected);

        View view = viewHolder.getView();
        ImageView checkedImage = view.findViewById(R.id.checked_image);
        checkedImage.setVisibility(selected ? View.VISIBLE : View.GONE);
    }

}
