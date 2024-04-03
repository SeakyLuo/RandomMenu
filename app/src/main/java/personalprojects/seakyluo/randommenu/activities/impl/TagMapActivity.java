package personalprojects.seakyluo.randommenu.activities.impl;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.activities.SwipeBackActivity;
import personalprojects.seakyluo.randommenu.adapters.impl.TagMapperAdapter;
import personalprojects.seakyluo.randommenu.database.services.AutoTagMapperDaoService;

public class TagMapActivity extends SwipeBackActivity {
    private RecyclerView tagMapRecyclerView;
    private TagMapperAdapter tagMapperAdapter = new TagMapperAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_map);

        tagMapRecyclerView = findViewById(R.id.tag_map_recycler_view);
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        findViewById(R.id.tm_fab).setOnClickListener(v -> tagMapperAdapter.showTagMapperDialog(null));
        findViewById(R.id.tm_toolbar).setOnClickListener(v -> tagMapRecyclerView.smoothScrollToPosition(0));

        tagMapperAdapter.setData(AutoTagMapperDaoService.selectAll());
        tagMapRecyclerView.setAdapter(tagMapperAdapter);
    }

}
