package personalprojects.seakyluo.randommenu.adapters.impl;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;

public class RestaurantAdapter extends CustomAdapter<RestaurantVO> {

    @Override
    protected int getLayout(int viewType) {
        return R.layout.view_listed_restaurant;
    }

    @Override
    protected void fillViewHolder(CustomViewHolder viewHolder, RestaurantVO data, int position) {

    }
}
