package personalprojects.seakyluo.randommenu;

import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.Models.AList;
import personalprojects.seakyluo.randommenu.Models.Tag;
import personalprojects.seakyluo.randommenu.Models.ToggleTag;

public class TagListAdapter extends CustomAdapter<ToggleTag> {
    private AList<ViewHolder> viewHolders = new AList<>();
    private TagClickedListener listener;
    TagListAdapter(TagClickedListener listener) { this.listener = listener; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return viewHolders.Add(new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_listed_tag, parent, false)));
    }


    public boolean Contains(Tag target){
        return data.Any(tag -> tag.equals(target));
    }

    public void SetTagVisible(Tag tag, boolean visible){
        viewHolders.Get(data.IndexOf(t -> t.equals(tag))).SetCheckButtonVisibility(visible);
    }

    class ViewHolder extends CustomViewHolder {
        TextView tag_name;
        ImageButton check_button;
        ViewHolder(View view) {
            super(view);
            tag_name = view.findViewById(R.id.tag_name);
            check_button = view.findViewById(R.id.check_button);
            view.setOnClickListener(v -> OnClick());
            check_button.setOnClickListener(v -> OnClick());
        }

        private void OnClick(){
            data.visible = !data.visible;
            SetCheckButtonVisibility(data.visible);
            listener.TagClicked(this, data);
        }

        @Override
        void setData(ToggleTag data) {
            tag_name.setText(data.toString());
            SetCheckButtonVisibility(data.visible);
        }

        void SetCheckButtonVisibility(boolean visible){
            check_button.setVisibility((data.visible = visible) ? View.VISIBLE : View.GONE);
        }
    }
}

interface TagClickedListener {
    void TagClicked(CustomAdapter.CustomViewHolder viewHolder, ToggleTag tag);
}
