package personalprojects.seakyluo.randommenu.adapters;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;

public class RestaurantAdapter extends CustomAdapter<RestaurantVO> {

    @Override
    public int getLayout(int viewType) {
        return R.layout.view_listed_restaurant;
    }

    @Override
    public void fillViewHolder(CustomViewHolder viewHolder, RestaurantVO data, int position) {

    }
}
