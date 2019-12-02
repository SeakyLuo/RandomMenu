package personalprojects.seakyluo.randommenu;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.Interfaces.OnDataItemClickedListener;
import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Tag;

public class SelectTagAdapter extends CustomAdapter<Tag> {
    private OnDataItemClickedListener<Tag> listener, longClickListener;
    public SelectTagAdapter(OnDataItemClickedListener<Tag> listener) { this.listener = listener; }
    public void SetLongClickListener(OnDataItemClickedListener<Tag> longClickListener) { this.longClickListener = longClickListener; }
    private static int HighlightColor = Color.parseColor("#0078D7");
    private Tag pendingTag;
    private ViewHolder lastTag;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_select_tag, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.CustomViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        Tag tag = data.Get(position);
        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.view.setOnClickListener(v -> {
            HighlightTag(viewHolder);
            listener.Click(holder, tag);
        });
        viewHolder.view.setOnLongClickListener(v -> {
            boolean success = longClickListener != null;
            if (success) longClickListener.Click(holder, tag);
            return success;
        });
        if (tag.equals(pendingTag)){
            lastTag = viewHolder;
            viewHolder.SetHighlight(true);
            pendingTag = null;
        }
    }

    public Tag GetSelectedTag() { return lastTag == null ? null : lastTag.data; }

    public void HighlightTag(Tag tag){
        if (tag == null) return;
        ViewHolder viewHolder = (ViewHolder) viewHolders.Find(vh -> vh.data.equals(tag));
        if (viewHolder == null) pendingTag = tag;
        else HighlightTag(viewHolder);
    }
    private void HighlightTag(ViewHolder viewHolder){
        if (viewHolder == lastTag) return;
        if (lastTag != null) lastTag.SetHighlight(false);
        (lastTag = viewHolder).SetHighlight(true);
    }

    public void SetTags(AList<Tag> tags){
        if (data.Count() == 0) data.Add(Tag.AllCategoriesTag);
        else data.Clear(1);
        data.AddAll(tags);
        notifyDataSetChanged();
    }

    class ViewHolder extends CustomViewHolder {
        TextView tag_name;
        ConstraintLayout background;
        boolean selected = false;
        ViewHolder(View view) {
            super(view);
            tag_name = view.findViewById(R.id.tag_name);
            background = view.findViewById(R.id.select_tag_background);
        }

        @Override
        void SetData(Tag data) {
            tag_name.setText(data.Name);
            SetHighlight(selected);
        }

        void SetHighlight(boolean isHighlight){
            if (selected = isHighlight){
                background.setBackgroundColor(HighlightColor);
                tag_name.setTextColor(Color.WHITE);
            }else{
                background.setBackgroundColor(Color.TRANSPARENT);
                tag_name.setTextColor(Color.BLACK);
            }
        }
    }
}
