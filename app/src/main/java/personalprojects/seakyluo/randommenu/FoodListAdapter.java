package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import personalprojects.seakyluo.randommenu.Models.Food;

public class FoodListAdapter extends CustomAdapter<String> {
    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_listed_food, parent, false));
    }

    class ViewHolder extends CustomViewHolder {
        private TextView food_name;
        ViewHolder(View view) {
            super(view);
            food_name = view.findViewById(R.id.food_name);
        }

        @Override
        void setData(String data) {
            food_name.setText(data);
        }
    }
}
