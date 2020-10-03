package personalprojects.seakyluo.randommenu.adapters;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.R;

public class TagAdapter extends CustomAdapter<Tag> {
    private boolean closeable = false;
    public TagAdapter() {}
    public TagAdapter(boolean closeable) { SetCloseable(closeable); }
    public void SetCloseable(boolean closeable){ this.closeable = closeable; }
    private DataItemClickedListener<Tag> closeListener, tagListener;
    public void SetTagCloseListener(DataItemClickedListener<Tag> listener) { this.closeListener = listener; }
    public void SetTagClickedListener(DataItemClickedListener<Tag> listener) { this.tagListener = listener; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_tag, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter<Tag>.CustomViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Tag tag = data.get(position);
        ((ViewHolder)holder).SetOnCloseClickedListener(v -> {
            remove(tag);
            if (closeListener != null) closeListener.click(holder, tag);
        });
        holder.view.setOnClickListener(v -> {
            if (tagListener != null) tagListener.click(holder, tag);
        });
    }

    public void add(String tag){
        add(new Tag(tag));
    }

    public void add(String tag, int index){
        add(new Tag(tag), index);
    }
    
    class ViewHolder extends CustomViewHolder {
        TextView tag_name;
        ImageButton close_button;
        ViewHolder(View view) {
            super(view);
            tag_name = view.findViewById(R.id.tag_name);
            close_button = view.findViewById(R.id.close_button);
        }

        @Override
        void setData(Tag data) {
            tag_name.setText(data.Name);
            close_button.setVisibility(closeable ? View.VISIBLE : View.GONE);
        }

        void SetOnCloseClickedListener(View.OnClickListener listener){
            close_button.setOnClickListener(listener);
        }
    }
}
