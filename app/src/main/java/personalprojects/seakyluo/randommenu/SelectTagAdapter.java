package personalprojects.seakyluo.randommenu;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Tag;
import personalprojects.seakyluo.randommenu.Models.ToggleTag;

public class SelectTagAdapter extends CustomAdapter<ToggleTag> {
    private TagClickedListener listener;
    public SelectTagAdapter(TagClickedListener listener) { this.listener = listener; }
    private static int HighlightColor = Color.parseColor("#0078D7");
    private Tag pendingTag;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_select_tag, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.CustomViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        ToggleTag tag = data.Get(position);
        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.view.setOnClickListener(v -> {
            HighlightTag(tag);
            listener.TagClicked(holder, tag);
        });
        if (tag.equals(pendingTag)){
            viewHolder.SetHighlight(true);
            pendingTag = null;
        }
    }

    public void HighlightTag(Tag tag){
        if (IsLoaded()) viewHolders.ForEach(vh -> ((ViewHolder)vh).SetHighlight(vh.data.equals(tag)));
        else pendingTag = tag;
    }

    public void SetData(AList<Tag> tags){
        data.Clear();
        data.Add(new ToggleTag(Tag.AllCategoriesTag, false));
        data.AddAll(new AList<>(tags).Convert(t -> new ToggleTag(t, false)));
        notifyDataSetChanged();
    }

    class ViewHolder extends CustomViewHolder {
        TextView tag_name;
        ConstraintLayout background;
        ViewHolder(View view) {
            super(view);
            tag_name = view.findViewById(R.id.tag_name);
            background = view.findViewById(R.id.select_tag_background);
        }

        @Override
        void setData(ToggleTag data) {
            tag_name.setText(data.equals(Tag.AllCategoriesTag) ? data.Name : data.toString());
            SetHighlight(data.visible);
        }

        void SetHighlight(boolean isHighlight){
            if (data.visible = isHighlight){
                background.setBackgroundColor(HighlightColor);
                tag_name.setTextColor(Color.WHITE);
            }else{
                background.setBackgroundColor(Color.TRANSPARENT);
                tag_name.setTextColor(Color.BLACK);
            }
        }
    }
}
