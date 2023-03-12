package personalprojects.seakyluo.randommenu.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.activities.impl.EditRestaurantActivity;
import personalprojects.seakyluo.randommenu.adapters.impl.RestaurantAdapter;
import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.database.dao.Page;
import personalprojects.seakyluo.randommenu.database.dao.PagedData;
import personalprojects.seakyluo.randommenu.database.services.RestaurantDaoService;
import personalprojects.seakyluo.randommenu.helpers.PopupMenuHelper;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;
import personalprojects.seakyluo.randommenu.utils.ImageUtils;
import personalprojects.seakyluo.randommenu.utils.RecyclerViewUtils;
import personalprojects.seakyluo.randommenu.utils.RestaurantUtils;

import static android.app.Activity.RESULT_OK;

public class RestaurantsFragment extends Fragment {
    public static final String TAG = "RestaurantsFragment";
    private static final int PAGE_SIZE = 20;
    private TextView titleTextView;
    private RecyclerView restaurantRecyclerView;
    private RestaurantAdapter restaurantAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurants, container, false);
        titleTextView = view.findViewById(R.id.title_text_view);
        FloatingActionButton fab = view.findViewById(R.id.restaurant_fab);
        restaurantAdapter = new RestaurantAdapter(getContext());
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        restaurantRecyclerView = view.findViewById(R.id.restaurant_recycler_view);

        fab.setOnClickListener(this::showCreateRestaurantPopupMenu);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            setData(RestaurantDaoService.selectByPage(1, PAGE_SIZE));
        });
        restaurantRecyclerView.setAdapter(restaurantAdapter);
        RecyclerViewUtils.setAsPaged(restaurantRecyclerView, RestaurantDaoService::selectByPage);
        setData(RestaurantDaoService.selectByPage(1, PAGE_SIZE));
        return view;
    }

    private void showCreateRestaurantPopupMenu(View view){
        PopupMenuHelper helper = new PopupMenuHelper(R.menu.create_restaurant_menu, getContext(), view);
        helper.setOnItemSelectedListener((menuBuilder, menuItem) -> {
            switch (menuItem.getItemId()){
                case R.id.standard_create_restaurant:
                    createRestaurant(null);
                    return true;
                case R.id.quick_create_restaurant:
                    ImageUtils.openGallery(getActivity());
                    return true;
            }
            return false;
        });
        helper.show();
    }

    private void setData(PagedData<RestaurantVO> pagedData){
        List<RestaurantVO> restaurants = pagedData.getData();
        Page page = pagedData.getPage();
        long total = page.getTotal();
        restaurantAdapter.setData(restaurants);
        titleTextView.setText(total == 0 ? "探店" : String.format("探店（%d）", total));
    }

    private void createRestaurant(RestaurantVO data){
        Intent intent = new Intent(getContext(), EditRestaurantActivity.class);
        intent.putExtra(EditRestaurantActivity.DATA, data);
        startActivityForResult(intent, ActivityCodeConstant.EDIT_RESTAURANT);
        getActivity().overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == ActivityCodeConstant.EDIT_RESTAURANT) {
            RestaurantVO restaurant = data.getParcelableExtra(EditRestaurantActivity.DATA);
            long id = restaurant.getId();
            RestaurantVO vo = RestaurantDaoService.selectPagedView(id);
            int index = restaurantAdapter.indexOf(i -> i.getId() == id);
            if (index == -1){
                restaurantAdapter.add(vo, 0);
                restaurantRecyclerView.scrollToPosition(0);
            } else {
                restaurantAdapter.set(vo, index);
            }
        }
        else if (requestCode == ActivityCodeConstant.GALLERY) {
            createRestaurant(RestaurantUtils.buildFromImages(getContext(), data.getClipData()));
        }
    }
}
