package personalprojects.seakyluo.randommenu.adapters.impl;

import android.graphics.Color;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.R;

public class SelectTagAdapter extends CustomAdapter<Tag> {
    private DataItemClickedListener<Tag> listener, longClickListener;
    public SelectTagAdapter(DataItemClickedListener<Tag> listener) { this.listener = listener; }
    public void setLongClickListener(DataItemClickedListener<Tag> longClickListener) { this.longClickListener = longClickListener; }
    private static int HighlightColor = Color.parseColor("#0078D7");
    private Tag pendingTag;
    private CustomViewHolder lastTag;

    @Override
    protected int getLayout(int viewType) {
        return R.layout.view_select_tag;
    }

    @Override
    protected void fillViewHolder(CustomViewHolder viewHolder, Tag data, int position) {
        View view = viewHolder.getView();
        TextView tagName = view.findViewById(R.id.tag_name);

        tagName.setText(data.isAllCategoriesTag() ? context.getString(R.string.all_categories) : data.getName());
        setHighlight(viewHolder,  false);

        view.setOnClickListener(v -> {
            highlightTag(viewHolder);
            listener.click(viewHolder, data);
        });
        view.setOnLongClickListener(v -> {
            boolean success = longClickListener != null;
            if (success) longClickListener.click(viewHolder, data);
            return success;
        });
        if (data.equals(pendingTag) || (lastTag != null && lastTag.getData().equals(data))){
            setHighlight(lastTag = viewHolder, true);
            pendingTag = null;
        }
    }

    public Tag getSelectedTag() { return lastTag == null ? null : lastTag.getData(); }

    public void highlightTag(Tag tag){
        if (tag == null || (lastTag != null && tag.equals(lastTag.getData()))) return;
        CustomViewHolder viewHolder = viewHolders.first(vh -> vh.getData().equals(tag));
        if (viewHolder == null) pendingTag = tag;
        else highlightTag(viewHolder);
    }

    private void highlightTag(CustomViewHolder viewHolder){
        if (viewHolder == lastTag) return;
        if (lastTag != null) setHighlight(lastTag, false);
        setHighlight(lastTag = viewHolder, true);
    }

    private void setHighlight(CustomViewHolder viewHolder, boolean isHighlight){
        View view = viewHolder.getView();
        TextView tagName = view.findViewById(R.id.tag_name);
        ConstraintLayout background = view.findViewById(R.id.select_tag_background);
        if (isHighlight){
            background.setBackgroundColor(HighlightColor);
            tagName.setTextColor(Color.WHITE);
        } else {
            background.setBackgroundColor(Color.TRANSPARENT);
            tagName.setTextColor(Color.BLACK);
        }
    }

    public void setTags(List<Tag> tags){
        if (data.isEmpty()){
            data.add(Tag.AllCategoriesTag);
        } else {
            if (tags.equals(data.after(0))) return;
            data.clear(1);
        }
        data.addAll(tags);
        notifyDataSetChanged();
    }

}
