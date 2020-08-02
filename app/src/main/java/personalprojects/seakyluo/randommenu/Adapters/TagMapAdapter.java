package personalprojects.seakyluo.randommenu.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Objects;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.interfaces.DataViewOperationListener;
import personalprojects.seakyluo.randommenu.interfaces.OnDataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.TagMapper;

public class TagMapAdapter extends CustomAdapter<TagMapper> {
    public Context context;
    private OnDataItemClickedListener<TagMapper> onDataItemClickedListener;
    private DataViewOperationListener<TagMapper> moreClickedListener;
    private static final int VIEW_TAG_MAPPER = 0;
    private static final int VIEW_HEADER = 1;

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_HEADER)
            return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_text_view, parent, false));
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_tag_mapper, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter<TagMapper>.CustomViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (position == 0){
            ((HeaderViewHolder) holder).setText(R.string.tag_map_hint);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return VIEW_HEADER;
        return VIEW_TAG_MAPPER;
    }

    public void setOnItemClickListener(OnDataItemClickedListener<TagMapper> listener){
        onDataItemClickedListener = listener;
    }

    public void setMoreClickedListener(DataViewOperationListener<TagMapper> listener){
        moreClickedListener = listener;
    }

    class ViewHolder extends CustomViewHolder {
        private TextView keyword;
        private TagAdapter adapter = new TagAdapter();
        private ImageButton more;

        ViewHolder(View view) {
            super(view);
            keyword = view.findViewById(R.id.keyword_content);
            more = view.findViewById(R.id.more_button);
            RecyclerView recyclerView = view.findViewById(R.id.tags_recycler_view);
            recyclerView.setAdapter(adapter);
            view.setOnClickListener(v -> {
                onDataItemClickedListener.click(this, data);
            });
            if (moreClickedListener == null){
                more.setVisibility(View.INVISIBLE);
            }else{
                more.setOnClickListener(v -> moreClickedListener.operate(data, more));
            }
        }

        @Override
        void setData(TagMapper data) {
            this.data = data;
            if (Objects.nonNull(data)){
                keyword.setText(data.key);
                adapter.setData(data.value);
            }
        }
    }

    class HeaderViewHolder extends CustomViewHolder{
        private TextView textView;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }

        @Override
        void setData(TagMapper data) {}

        public void setText(int resourceId){
            textView.setText(resourceId);
        }

        public void setText(String text){
            textView.setText(text);
        }
    }
}
