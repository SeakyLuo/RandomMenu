package personalprojects.seakyluo.randommenu.adapters.impl;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;

@Setter
public class SimpleStringListAdapter extends CustomAdapter<String> {
    private DataItemClickedListener<String> onItemClickedListener;

    @Override
    protected int getLayout(int viewType) {
        return R.layout.view_listed_string;
    }

    @Override
    protected void fillViewHolder(CustomViewHolder viewHolder, String data, int position) {
        View view = viewHolder.getView();
        TextView textContent = view.findViewById(R.id.text_content);
        textContent.setText(data);
        textContent.setOnClickListener(v -> {
            if (onItemClickedListener != null) onItemClickedListener.click(viewHolder, data);
        });
    }
}
