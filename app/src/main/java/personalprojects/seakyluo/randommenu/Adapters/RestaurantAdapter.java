package personalprojects.seakyluo.randommenu.adapters;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;

public class RestaurantAdapter extends CustomAdapter<RestaurantVO> {
    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_listed_tag, viewGroup, false));
    }

    class ViewHolder extends CustomViewHolder {
        TextView tag_name;
        ImageButton check_button;

        ViewHolder(View view) {
            super(view);
            tag_name = view.findViewById(R.id.tag_name);
            check_button = view.findViewById(R.id.check_button);
        }

        @Override
        void setData(RestaurantVO data) {
        }
    }
}
