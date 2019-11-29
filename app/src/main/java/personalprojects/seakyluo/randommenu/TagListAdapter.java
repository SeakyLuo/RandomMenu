package personalprojects.seakyluo.randommenu;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Collection;

import personalprojects.seakyluo.randommenu.Interfaces.OnDataItemClickedListener;
import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Tag;

public class TagListAdapter extends CustomAdapter<Tag> {
    private AList<Tag> CheckTags = new AList<>();
    private OnDataItemClickedListener<Tag> listener;
    public TagListAdapter(AList<Tag> data, Collection<Tag> checkTags){
        SetData(data);
        CheckTags.CopyFrom(checkTags);
    }
    public void SetTagClickedListener(OnDataItemClickedListener<Tag> listener) { this.listener = listener; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_listed_tag, parent, false));
    }

    public boolean Contains(Tag target){ return data.Any(tag -> tag.equals(target)); }
    public void SetTagChecked(Tag tag, boolean checked){
        if (checked) CheckTags.Add(tag, 0);
        else CheckTags.Remove(tag);
    }
    public void CheckTag(Tag tag, boolean checked){
        ((ViewHolder)viewHolders.Find(vh -> vh.data.equals(tag))).SetCheckButtonVisibility(checked);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.CustomViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.SetCheckButtonVisibility(CheckTags.Contains(viewHolder.data));
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

        private void OnClick(){
            checked = !checked;
            SetCheckButtonVisibility(checked);
            listener.Click(this, data);
        }

        @Override
        void SetData(Tag data) {
            tag_name.setText(data.toString());
            SetCheckButtonVisibility(checked);
        }

        void SetCheckButtonVisibility(boolean visible){
            check_button.setVisibility((checked = visible) ? View.VISIBLE : View.GONE);
        }
    }
}
