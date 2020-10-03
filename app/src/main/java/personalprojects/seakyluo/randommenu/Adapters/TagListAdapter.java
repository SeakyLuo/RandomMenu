package personalprojects.seakyluo.randommenu.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Collection;

import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.R;

public class TagListAdapter extends CustomAdapter<Tag> {
    private AList<Tag> CheckTags = new AList<>();
    private DataItemClickedListener<Tag> listener;
    public TagListAdapter(Context context, AList<Tag> data, Collection<Tag> checkTags){
        this.context = context;
        setData(data);
        CheckTags.copyFrom(checkTags);
    }
    public void SetTagClickedListener(DataItemClickedListener<Tag> listener) { this.listener = listener; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_listed_tag, parent, false));
    }

    public boolean contains(Tag target){ return data.any(tag -> tag.equals(target)); }
    public void SetTagChecked(Tag tag, boolean checked){
        if (checked) CheckTags.add(tag, 0);
        else CheckTags.remove(tag);
    }
    public void CheckTag(Tag tag, boolean checked){
        ((ViewHolder)viewHolders.first(vh -> vh.data.equals(tag))).SetCheckButtonVisibility(checked);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.CustomViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.SetCheckButtonVisibility(CheckTags.contains(viewHolder.data));
    }

    class ViewHolder extends CustomViewHolder {
        TextView tag_name;
        ImageButton check_button;
        boolean checked = false;
        ViewHolder(View view) {
            super(view);
            tag_name = view.findViewById(R.id.tag_name);
            check_button = view.findViewById(R.id.check_button);
            view.setOnClickListener(v -> OnClick());
            check_button.setOnClickListener(v -> OnClick());
        }

        @Override
        void setData(Tag data) {
            tag_name.setText(Tag.Format(context, data));
            SetCheckButtonVisibility(checked);
        }

        private void OnClick(){
            checked = !checked;
            SetCheckButtonVisibility(checked);
            listener.click(this, data);
        }

        void SetCheckButtonVisibility(boolean visible){
            check_button.setVisibility((checked = visible) ? View.VISIBLE : View.GONE);
        }
    }
}
