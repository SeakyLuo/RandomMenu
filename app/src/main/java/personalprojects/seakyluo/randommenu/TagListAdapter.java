package personalprojects.seakyluo.randommenu;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TagListAdapter extends CustomAdapter<Tag> implements ListedTagClickedListener{
    private  ListedTagClickedListener listener;
    public TagListAdapter(ListedTagClickedListener listener){
        this.listener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_tag, parent, false));
    }

    @Override
    public void TagClicked(Tag tag) {
        listener.TagClicked(tag);
    }

    class ViewHolder extends CustomViewHolder {
        TextView tag_content;
        ViewHolder(final View view) {
            super(view);
            tag_content = view.findViewById(R.id.tag_content);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        void setData(Tag data) {
            tag_content.setText(data.toString());
        }
    }
}

interface ListedTagClickedListener{
    void TagClicked(Tag tag);
}
