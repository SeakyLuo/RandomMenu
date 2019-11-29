package personalprojects.seakyluo.randommenu;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.Interfaces.OnDataItemClickedListener;
import personalprojects.seakyluo.randommenu.Models.Settings;

public class SimpleFoodListAdapter extends CustomAdapter<String> {
    private OnDataItemClickedListener<String> listener, deleteListener;
    public void SetOnDataItemClickedListener(OnDataItemClickedListener<String> listener) { this.listener = listener; }
    public void SetOnDeleteClickedListener(OnDataItemClickedListener<String> listener) { this.deleteListener = listener; }
    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_listed_simple_food, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.CustomViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ViewHolder viewHolder = (ViewHolder) holder;
        String name = viewHolder.data;
        viewHolder.food_name.setOnClickListener(v -> {
            if (listener != null) listener.Click(viewHolder, name);
        });
        viewHolder.delete_button.setOnClickListener(v -> {
            if (listener != null) deleteListener.Click(viewHolder, name);
        });
    }

    class ViewHolder extends CustomViewHolder {
        private TextView food_name;
        private ImageButton delete_button;
        ViewHolder(View view) {
            super(view);
            food_name = view.findViewById(R.id.food_name);
            delete_button = view.findViewById(R.id.delete_button);
        }

        @Override
        void SetData(String data) {
            food_name.setText(data);
        }
    }
}
