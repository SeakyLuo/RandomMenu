package personalprojects.seakyluo.randommenu.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_listed_food, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter<Food>.CustomViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.view.setOnClickListener(v -> {
            if (foodClickedListener != null){
                foodClickedListener.click(viewHolder, viewHolder.data);
            }
            if (selectable){
                viewHolder.setSelected(!viewHolder.selected);
                if (selectionChangedListener != null){
                    selectionChangedListener.click(viewHolder, viewHolder.selected);
                }
            }
        });
        viewHolder.adapter.SetTagClickedListener((v, t) -> {
            if (tagClickedListener != null) tagClickedListener.click(v, t);
        });
    }

    public void setSelectedFood(AList<Food> foods){
        selectedFood.copyFrom(foods);
    }

    public class ViewHolder extends BaseViewHolder {
        private ImageView checked_image;
        private TagAdapter adapter = new TagAdapter();
        private boolean selected = false;

        public ViewHolder(View view) {
            super(view);
            checked_image = view.findViewById(R.id.checked_image);
            RecyclerView recyclerView = view.findViewById(R.id.tags_recycler_view);
            recyclerView.setAdapter(adapter);
        }

        public void setSelected(boolean selected){
            checked_image.setVisibility((this.selected = selected) ? View.VISIBLE : View.GONE);
            if (selected) selectedFood.add(data);
            else selectedFood.remove(data);
        }

        @Override
        public void setData(Food data) {
            super.setData(data);
            adapter.setData(data.getTags());
            checked_image.setVisibility(selectedFood.contains(data) ?  View.VISIBLE : View.GONE);
        }
    }
}
