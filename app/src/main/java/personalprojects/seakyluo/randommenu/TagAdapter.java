package personalprojects.seakyluo.randommenu;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.Interfaces.OnDataItemClickedListener;
import personalprojects.seakyluo.randommenu.Models.Tag;
import personalprojects.seakyluo.randommenu.Models.ToggleTag;

public class TagAdapter extends CustomAdapter<ToggleTag> {
    private OnDataItemClickedListener<ToggleTag> closeListener, tagListener;
    public void SetTagCloseListener(OnDataItemClickedListener<ToggleTag> listener) { this.closeListener = listener; }
    public void SetTagClickedListener(OnDataItemClickedListener<ToggleTag> listener) { this.tagListener = listener; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_tag, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.CustomViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ToggleTag tag = data.Get(position);
        ((ViewHolder)holder).SetOnCloseClickedListener(v -> {
            remove(tag);
            if (closeListener != null) closeListener.Click(holder, tag);
        });
        holder.view.setOnClickListener(v -> {
            if (tagListener != null) tagListener.Click(holder, tag);
        });
    }

    class ViewHolder extends CustomViewHolder {
        TextView tag_name;
        ImageButton close_button;
        ViewHolder(View view) {
            super(view);
            tag_name = view.findViewById(R.id.tag_name);
            close_button = view.findViewById(R.id.close_button);
        }

        @Override
        void setData(ToggleTag data) {
            tag_name.setText(data.Name);
            close_button.setVisibility(data.visible ? View.VISIBLE : View.GONE);
        }

        void SetCloseButtonVisibility(boolean visible){
            close_button.setVisibility((data.visible = visible) ? View.VISIBLE : View.GONE);
        }

        void SetOnCloseClickedListener(View.OnClickListener listener){
            close_button.setOnClickListener(listener);
        }
    }
}
