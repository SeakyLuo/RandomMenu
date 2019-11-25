package personalprojects.seakyluo.randommenu;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.Models.ToggleTag;

public class TagAdapter extends CustomAdapter<ToggleTag> {
    private TagClickedListener listener;
    public void SetTagClickedListener(TagClickedListener listener) { this.listener = listener; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_tag, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.CustomViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        ((ViewHolder)holder).SetOnCloseClickedListener(v -> {
            ToggleTag tag = data.Get(position);
            remove(tag);
            if (listener != null) listener.TagClicked(holder, tag);
        });
    }

    class ViewHolder extends CustomViewHolder {
        TextView tag_name;
        ImageButton close_button;
        ViewHolder(final View view) {
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
