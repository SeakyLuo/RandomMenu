package personalprojects.seakyluo.randommenu.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.interfaces.OnDataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.R;

public class SelectTagAdapter extends CustomAdapter<Tag> {
    private OnDataItemClickedListener<Tag> listener, longClickListener;
    public SelectTagAdapter(OnDataItemClickedListener<Tag> listener) { this.listener = listener; }
    public void setLongClickListener(OnDataItemClickedListener<Tag> longClickListener) { this.longClickListener = longClickListener; }
    private static int HighlightColor = Color.parseColor("#0078D7");
    private Tag pendingTag;
    private ViewHolder lastTag;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_select_tag, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.CustomViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Tag tag = data.get(position);
        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.view.setOnClickListener(v -> {
            HighlightTag(viewHolder);
            listener.click(holder, tag);
        });
        viewHolder.view.setOnLongClickListener(v -> {
            boolean success = longClickListener != null;
            if (success) longClickListener.click(holder, tag);
            return success;
        });
        if (tag.equals(pendingTag) || (lastTag != null && lastTag.data.equals(tag))){
            (lastTag = viewHolder).SetHighlight(true);
            pendingTag = null;
        }
    }

    public Tag GetSelectedTag() { return lastTag == null ? null : lastTag.data; }

    public void HighlightTag(Tag tag){
        if (tag == null || (lastTag != null && tag.equals(lastTag.data))) return;
        ViewHolder viewHolder = (ViewHolder) viewHolders.first(vh -> vh.data.equals(tag));
        if (viewHolder == null) pendingTag = tag;
        else HighlightTag(viewHolder);
    }
    private void HighlightTag(ViewHolder viewHolder){
        if (viewHolder == lastTag) return;
        if (lastTag != null) lastTag.SetHighlight(false);
        (lastTag = viewHolder).SetHighlight(true);
    }

    public void SetTags(AList<Tag> tags){
        if (tags.equals(data.after(0))) return;
        if (data.count() == 0) data.add(Tag.AllCategoriesTag);
        else data.clear(1);
        data.addAll(tags);
        notifyDataSetChanged();
    }

    public class ViewHolder extends CustomViewHolder {
        TextView tag_name;
        ConstraintLayout background;
        ViewHolder(View view) {
            super(view);
            tag_name = view.findViewById(R.id.tag_name);
            background = view.findViewById(R.id.select_tag_background);
        }

        @Override
        void setData(Tag data) {
            tag_name.setText(data.IsAllCategoriesTag() ? context.getString(R.string.all_categories) : data.Name);
            SetHighlight(false);
        }

        void SetHighlight(boolean isHighlight){
            if (isHighlight){
                background.setBackgroundColor(HighlightColor);
                tag_name.setTextColor(Color.WHITE);
            }else{
                background.setBackgroundColor(Color.TRANSPARENT);
                tag_name.setTextColor(Color.BLACK);
            }
        }
    }
}
