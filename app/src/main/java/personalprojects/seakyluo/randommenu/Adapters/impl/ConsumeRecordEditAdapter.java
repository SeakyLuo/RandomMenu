package personalprojects.seakyluo.randommenu.adapters.impl;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.helpers.PopupMenuHelper;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.vo.ConsumeRecordVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.utils.DoubleUtils;

public class ConsumeRecordEditAdapter extends CustomAdapter<ConsumeRecordVO> {

    public ConsumeRecordEditAdapter(Context context){
        this.context = context;
    }

    @Setter
    private DataItemClickedListener<ConsumeRecordVO> onRecordClickListener;

    @Setter
    private DataItemClickedListener<ConsumeRecordVO> onRecordDeleteListener;

    @Setter
    private DataItemClickedListener<RestaurantFoodVO> onFoodClickListener;

    @Override
    protected int getLayout(int viewType) {
        return R.layout.view_listed_consume_record;
    }

    @Override
    protected void fillViewHolder(CustomViewHolder viewHolder, ConsumeRecordVO data, int position) {
        View view = viewHolder.getView();

        TextView consumeTime = view.findViewById(R.id.consume_time);
        TextView consumeTotalCost = view.findViewById(R.id.consume_total_cost);
        RecyclerView foodRecyclerView = view.findViewById(R.id.food_recycler_view);
        RestaurantFoodAdapter foodAdapter = new RestaurantFoodAdapter(context);

        consumeTime.setText(data.formatConsumeTime());
        setTotalCost(consumeTotalCost, data.getTotalCost());
        foodAdapter.setOnFoodClickListener(onFoodClickListener);
        foodAdapter.setData(data.getFoods());
        foodRecyclerView.setAdapter(foodAdapter);

        view.setOnClickListener(v -> {
            if (onRecordClickListener != null){
                onRecordClickListener.click(viewHolder, data);
            }
        });
        view.setOnLongClickListener(v -> {
            PopupMenuHelper helper = new PopupMenuHelper(R.menu.long_click_consume_record, context, view);
            helper.setOnItemSelectedListener((menuBuilder, menuItem) -> {
                if (menuItem.getItemId() == R.id.delete_item) {
                    if (onRecordDeleteListener != null){
                        onRecordDeleteListener.click(viewHolder, data);
                    }
                    return true;
                }
                return false;
            });
            helper.show();
            return true;
        });
    }

    public void setFood(RestaurantFoodVO food){
        int consumeRecordIndex = food.getConsumeRecordIndex();
        CustomViewHolder viewHolder = viewHolders.get(consumeRecordIndex);
        ConsumeRecordVO record = data.get(consumeRecordIndex);
        View view = viewHolder.getView();
        RecyclerView recyclerView = view.findViewById(R.id.food_recycler_view);
        RestaurantFoodAdapter adapter = (RestaurantFoodAdapter) recyclerView.getAdapter();
        int index = food.getIndex();
        adapter.set(food, index);
        List<RestaurantFoodVO> foods = record.getFoods();
        foods.set(index, food);
        if (record.isAutoCost()){
            TextView totalCostTextView = view.findViewById(R.id.consume_total_cost);
            double totalCost = foods.stream().mapToDouble(RestaurantFoodVO::getPrice).sum();
            setTotalCost(totalCostTextView, totalCost);
            record.setTotalCost(totalCost);
        }
    }

    private void setTotalCost(TextView consumeTotalCost, double totalCost){
        consumeTotalCost.setText("总消费：￥" + DoubleUtils.truncateZero(totalCost));
    }
}
