package personalprojects.seakyluo.randommenu.activities;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.impl.SimpleStringListAdapter;
import personalprojects.seakyluo.randommenu.database.services.EaterDaoService;
import personalprojects.seakyluo.randommenu.models.EaterCount;

public class EaterStatsActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eater_stats);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        SimpleStringListAdapter adapter = new SimpleStringListAdapter();
        List<EaterCount> list = EaterDaoService.getCount();
        List<String> data = list.stream().map(i -> i.getEater() + "（" + i.getCount() + "）").collect(Collectors.toList());
        adapter.setData(data);
        recyclerView.setAdapter(adapter);
    }
}
