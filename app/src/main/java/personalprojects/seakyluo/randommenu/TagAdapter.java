package personalprojects.seakyluo.randommenu;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class TagAdapter extends CustomAdapter<ToggleTag> {
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_tag, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.CustomViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        ((ViewHolder)holder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(data.get(position));
            }
        });
    }

    public void SetClose(boolean visible){
        for (ToggleTag tag : data)
            tag.visible = visible;
        notifyDataSetChanged();
    }

    class ViewHolder extends CustomViewHolder {
        TextView tag_content;
        ImageButton close_button;
        ViewHolder(final View view) {
            super(view);
            tag_content = view.findViewById(R.id.tag_content);
            close_button = view.findViewById(R.id.close_button);
        }

        @Override
        void setData(ToggleTag data) {
            tag_content.setText(data.name);
            close_button.setVisibility(data.visible ? View.VISIBLE : View.GONE);
        }

        public void setOnClickListener(View.OnClickListener listener){
            close_button.setOnClickListener(listener);
        }
    }
}
