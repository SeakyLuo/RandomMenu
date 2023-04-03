package personalprojects.seakyluo.randommenu.adapters.impl;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.R;

public class StringListAdapter extends CustomAdapter<String> {
    @Setter
    private DataItemClickedListener<String> itemClickedListener, itemDeletedListener;

    @Override
    protected int getLayout(int viewType) {
        return R.layout.view_listed_simple_food;
    }

    @Override
    protected void fillViewHolder(CustomViewHolder viewHolder, String data, int position) {
        View view = viewHolder.getView();
        TextView foodName = view.findViewById(R.id.food_name);
        ImageButton deleteButton = view.findViewById(R.id.delete_button);

        foodName.setOnClickListener(v -> {
            if (itemClickedListener != null) itemClickedListener.click(viewHolder, data);
        });
        foodName.setText(data);
        deleteButton.setOnClickListener(v -> {
            remove(data);
            if (itemDeletedListener != null) itemDeletedListener.click(viewHolder, data);
        });
    }
}
