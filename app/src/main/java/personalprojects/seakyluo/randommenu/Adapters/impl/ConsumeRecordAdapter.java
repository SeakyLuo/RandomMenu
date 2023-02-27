package personalprojects.seakyluo.randommenu.adapters.impl;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.vo.ConsumeRecordVO;
import personalprojects.seakyluo.randommenu.utils.DoubleUtils;

public class ConsumeRecordAdapter extends CustomAdapter<ConsumeRecordVO> {

    public ConsumeRecordAdapter(Context context){
        this.context = context;
    }

    @Setter
    private DataItemClickedListener<ConsumeRecordVO> onClickListener;

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
        consumeTotalCost.setText("总消费：￥" + DoubleUtils.truncateZero(data.getTotalCost()));
        foodAdapter.setData(data.getFoods());
        foodRecyclerView.setAdapter(foodAdapter);

        view.setOnClickListener(v -> {
            if (onClickListener != null){
                onClickListener.click(viewHolder, data);
            }
        });
    }

}
