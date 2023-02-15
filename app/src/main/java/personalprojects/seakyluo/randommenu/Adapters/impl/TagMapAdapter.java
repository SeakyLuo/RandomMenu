package personalprojects.seakyluo.randommenu.adapters.impl;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import lombok.Setter;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.interfaces.DataViewOperationListener;
import personalprojects.seakyluo.randommenu.interfaces.DataItemClickedListener;
import personalprojects.seakyluo.randommenu.models.TagMapper;

public class TagMapAdapter extends CustomAdapter<TagMapper> {
    public Context context;
    @Setter
    private DataItemClickedListener<TagMapper> dataItemClickedListener;
    @Setter
    private DataViewOperationListener<TagMapper> moreClickedListener;
    private static final int VIEW_TAG_MAPPER = 0;
    private static final int VIEW_HEADER = 1;

    @Override
    protected int getLayout(int viewType) {
        return viewType == VIEW_HEADER ? R.layout.view_text_view : R.layout.view_tag_mapper;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_HEADER : VIEW_TAG_MAPPER;
    }

    @Override
    protected void fillViewHolder(CustomViewHolder viewHolder, TagMapper data, int position) {
        View view = viewHolder.getView();

        if (position == 0){
            TextView textView = view.findViewById(R.id.textView);
            textView.setText(R.string.tag_map_hint);
        } else {
            TextView keyword = view.findViewById(R.id.keyword_content);
            ImageButton more = view.findViewById(R.id.more_button);
            RecyclerView recyclerView = view.findViewById(R.id.tags_recycler_view);
            TagAdapter adapter = new TagAdapter();
            recyclerView.setAdapter(adapter);
            view.setOnClickListener(v -> {
                dataItemClickedListener.click(viewHolder, data);
            });
            if (moreClickedListener == null){
                more.setVisibility(View.INVISIBLE);
            }else{
                more.setOnClickListener(v -> moreClickedListener.operate(data, more));
            }
            if (data != null){
                keyword.setText(data.key);
                adapter.setData(data.value);
            }
        }
    }
}
