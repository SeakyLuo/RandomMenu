package personalprojects.seakyluo.randommenu.adapters.impl;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.R;

public class TagAdapter extends CustomAdapter<Tag> {
    private boolean closeable = false;
    public TagAdapter() {}
    public TagAdapter(boolean closeable) { SetCloseable(closeable); }
    public void SetCloseable(boolean closeable){ this.closeable = closeable; }
    private DataItemClickedListener<Tag> closeListener, tagListener;
    public void SetTagCloseListener(DataItemClickedListener<Tag> listener) { this.closeListener = listener; }
    public void setTagClickedListener(DataItemClickedListener<Tag> listener) { this.tagListener = listener; }


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
            if (closeListener != null) closeListener.click(viewHolder, data);
        });
        view.setOnClickListener(v -> {
            if (tagListener != null) tagListener.click(viewHolder, data);
        });
        tagName.setText(data.Name);
        closeButton.setVisibility(closeable ? View.VISIBLE : View.GONE);
    }

    public void add(String tag){
        add(new Tag(tag));
    }

    public void add(String tag, int index){
        add(new Tag(tag), index);
    }

}
