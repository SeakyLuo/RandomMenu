package personalprojects.seakyluo.randommenu.adapters.impl;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.R;

public class TagAdapter extends CustomAdapter<Tag> {
    @Setter
    private boolean closeable = false;
    public TagAdapter() {}
    public TagAdapter(boolean closeable) {
        this.closeable = closeable;
    }

    @Setter
    private DataItemClickedListener<Tag> tagCloseListener, tagClickedListener;

    @Override
    protected int getLayout(int viewType) {
        return R.layout.view_tag;
    }

    @Override
    protected void fillViewHolder(CustomViewHolder viewHolder, Tag data, int position) {
        View view = viewHolder.getView();
        TextView tagName = view.findViewById(R.id.tag_name);
        ImageButton closeButton = view.findViewById(R.id.close_button);

        closeButton.setOnClickListener(v -> {
            remove(data);
            if (tagCloseListener != null) tagCloseListener.click(viewHolder, data);
        });
        view.setOnClickListener(v -> {
            if (tagClickedListener != null) tagClickedListener.click(viewHolder, data);
        });
        tagName.setText(data.getName());
        closeButton.setVisibility(closeable ? View.VISIBLE : View.GONE);
    }

}
