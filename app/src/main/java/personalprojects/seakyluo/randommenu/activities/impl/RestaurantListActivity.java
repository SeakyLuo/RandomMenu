package personalprojects.seakyluo.randommenu.activities.impl;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.activities.SwipeBackActivity;
import personalprojects.seakyluo.randommenu.adapters.impl.RestaurantAdapter;
import personalprojects.seakyluo.randommenu.constants.Constant;
import personalprojects.seakyluo.randommenu.database.dao.Page;
import personalprojects.seakyluo.randommenu.database.dao.PagedData;
import personalprojects.seakyluo.randommenu.database.services.ConsumeRecordDaoService;
import personalprojects.seakyluo.randommenu.database.services.EaterDaoService;
import personalprojects.seakyluo.randommenu.database.services.RestaurantDaoService;
import personalprojects.seakyluo.randommenu.models.RestaurantFilter;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;
import personalprojects.seakyluo.randommenu.utils.RecyclerViewUtils;

public class RestaurantListActivity extends SwipeBackActivity {
    private static final int PAGE_SIZE = 10;
    public static final String TAG_EATER = "eater";

    private RestaurantAdapter adapter;
    private TextView titleTextView;
    private final RestaurantFilter restaurantFilter = new RestaurantFilter();
    private String eater = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        adapter = new RestaurantAdapter(this);
        titleTextView = findViewById(R.id.title_text_view);
        findViewById(R.id.back_button).setOnClickListener(v -> finish());

        recyclerView.setAdapter(adapter);
        RecyclerViewUtils.setAsPaged(recyclerView, PAGE_SIZE, ((pageNum, pageSize, filter) -> RestaurantDaoService.selectByPage(pageNum, pageSize, restaurantFilter)), restaurantFilter);
        Intent intent = getIntent();
        eater = intent.getStringExtra(TAG_EATER);
        if (StringUtils.isNotEmpty(eater)){
            if (Constant.EAT_ALONE.equals(eater)){
                restaurantFilter.setEatAlone(true);
            }
            else {
                restaurantFilter.setEaters(Lists.newArrayList(eater));
            }
        }
        setData(RestaurantDaoService.selectByPage(1, PAGE_SIZE, restaurantFilter));
    }

    private void setData(PagedData<RestaurantVO> pagedData){
        List<RestaurantVO> restaurants = pagedData.getData();
        Page page = pagedData.getPage();
        long total = page.getTotal();
        adapter.setData(restaurants);
        setTitle(total);
    }

    private void setTitle(long total){
        if (eater == null){
            titleTextView.setText(total == 0 ? "探店" : String.format("探店（%d）", total));
            return;
        }
        if (Constant.EAT_ALONE.equals(eater)){
            titleTextView.setText(String.format("单独吃饭%d次，去了%d家店", ConsumeRecordDaoService.countEatAlone(), total));
            return;
        }
        int consumeCount = total == 0 ? 0 : EaterDaoService.countByEater(eater);
        titleTextView.setText(String.format("和%s约饭%d次，去过%d家店", eater, consumeCount, total));
    }
}
