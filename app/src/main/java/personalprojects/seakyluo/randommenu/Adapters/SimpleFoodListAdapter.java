package personalprojects.seakyluo.randommenu.adapters;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.R;

public class SimpleFoodListAdapter extends CustomAdapter<String> {
    private DataItemClickedListener<String> listener, deleteListener;
    public void SetOnDataItemClickedListener(DataItemClickedListener<String> listener) { this.listener = listener; }
    public void SetOnDeletedClickedListener(DataItemClickedListener<String> listener) { this.deleteListener = listener; }

    @Override
    public int getLayout(int viewType) {
        return R.layout.view_listed_simple_food;
    }

    @Override
    public void fillViewHolder(CustomViewHolder viewHolder, String data, int position) {
        View view = viewHolder.getView();
        TextView foodName = view.findViewById(R.id.food_name);
        ImageButton deleteButton = view.findViewById(R.id.delete_button);

        foodName.setOnClickListener(v -> {
            if (listener != null) listener.click(viewHolder, data);
        });
        foodName.setText(data);
        deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.click(viewHolder, data);
        });
    }
}
