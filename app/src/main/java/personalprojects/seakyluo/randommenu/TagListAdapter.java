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

public class TagListAdapter extends CustomAdapter<ToggleTag> implements ListAdapter, Filterable {
    private TagClickedListener listener;
    TagListAdapter(TagClickedListener listener) { this.listener = listener; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_listed_tag, parent, false));
    }

    public boolean Contains(Tag target){
        return data.Contains(tag -> tag.equals(target));
    }

    @Override
    public boolean areAllItemsEnabled() { return true; }
    @Override
    public boolean isEnabled(int position) { return true; }
    @Override
    public void registerDataSetObserver(DataSetObserver observer) { }
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) { }
    @Override
    public int getCount() { return data.Count(); }
    @Override
    public Object getItem(int position) { return data.Get(position); }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) { return convertView; }
    @Override
    public int getViewTypeCount() { return 1; }
    @Override
    public boolean isEmpty() { return data.IsEmpty(); }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                AList<ToggleTag> filtered = data.Filter(tag -> tag.Name.contains(constraint));
                results.count = filtered.Count();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data.CopyFrom((AList<ToggleTag>)results.values);
                notifyDataSetChanged();
            }
        };
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
