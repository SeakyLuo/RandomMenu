package personalprojects.seakyluo.randommenu.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Collection;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.R;

public class TagListAdapter extends CustomAdapter<Tag> {
    private AList<Tag> CheckTags = new AList<>();
    @Setter
    private DataItemClickedListener<Tag> tagClickedListener;
    public TagListAdapter(Context context, AList<Tag> data, Collection<Tag> checkTags){
        this.context = context;
        setData(data);
        CheckTags.copyFrom(checkTags);
    }

    @Override
    public int getLayout(int viewType) {
        return R.layout.view_listed_tag;
    }

    @Override
    public void fillViewHolder(CustomViewHolder viewHolder, Tag data, int position) {
        View view = viewHolder.getView();
        TextView tagName = view.findViewById(R.id.tag_name);
        ImageButton checkButton = view.findViewById(R.id.check_button);

        tagName.setText(Tag.format(context, data));
        view.setOnClickListener(v -> onClick(viewHolder));
        checkButton.setOnClickListener(v -> onClick(viewHolder));
        setCheckButtonVisibility(viewHolder, CheckTags.contains(data));
    }

    private void onClick(CustomViewHolder viewHolder){
        Tag data = viewHolder.getData();
        boolean isSelected = !data.isSelected();

        setCheckButtonVisibility(viewHolder, isSelected);
        tagClickedListener.click(viewHolder, data);
    }

    private void setCheckButtonVisibility(CustomViewHolder viewHolder, boolean visible){
        View view = viewHolder.getView();
        Tag data = viewHolder.getData();
        ImageButton checkButton = view.findViewById(R.id.check_button);

        data.setSelected(visible);
        checkButton.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public boolean contains(Tag target){ return data.any(tag -> tag.equals(target)); }

    public void checkTag(Tag tag, boolean checked){
        setCheckButtonVisibility(viewHolders.first(vh -> vh.getData().equals(tag)), checked);
    }

}
