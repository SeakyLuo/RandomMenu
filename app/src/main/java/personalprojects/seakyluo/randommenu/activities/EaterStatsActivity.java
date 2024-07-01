package personalprojects.seakyluo.randommenu.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.activities.impl.RestaurantListActivity;
import personalprojects.seakyluo.randommenu.adapters.impl.SimpleStringListAdapter;
import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.database.services.EaterDaoService;
import personalprojects.seakyluo.randommenu.models.EaterCount;

public class EaterStatsActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eater_stats);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        SimpleStringListAdapter adapter = new SimpleStringListAdapter();
        List<EaterCount> list = EaterDaoService.getCountList();
        List<String> data = list.stream().map(i -> i.getEater() + "（" + i.getCount() + "）").collect(Collectors.toList());
        adapter.setData(data);
        adapter.setOnItemClickedListener((vh, s) -> clickEater(s.split("（")[0]));
        recyclerView.setAdapter(adapter);
    }

    private void clickEater(String eater){
        Intent intent = new Intent(this, RestaurantListActivity.class);
        intent.putExtra(RestaurantListActivity.TAG_EATER, eater);
        startActivityForResult(intent, ActivityCodeConstant.RESTAURANT_LIST);
        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
    }
}
