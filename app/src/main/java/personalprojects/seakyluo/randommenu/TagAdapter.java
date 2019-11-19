package personalprojects.seakyluo.randommenu;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class TagAdapter extends CustomAdapter<Tag> {
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_tag, parent, false));
    }

    class ViewHolder extends CustomViewHolder {
        TextView tag_content;
        ImageButton close_button;
        ViewHolder(final View view) {
            super(view);
            tag_content = view.findViewById(R.id.tag_content);
        }

        @Override
        void setData(Tag data) {
            tag_content.setText(data.getName());
        }
    }
}
