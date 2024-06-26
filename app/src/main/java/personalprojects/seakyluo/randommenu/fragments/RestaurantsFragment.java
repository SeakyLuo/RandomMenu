package personalprojects.seakyluo.randommenu.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.collect.Lists;

import java.util.List;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.activities.EditRestaurantActivity;
import personalprojects.seakyluo.randommenu.activities.EditRestaurantFoodActivity;
import personalprojects.seakyluo.randommenu.activities.EaterStatsActivity;
import personalprojects.seakyluo.randommenu.activities.SearchActivity;
import personalprojects.seakyluo.randommenu.activities.impl.ShowRestaurantActivity;
import personalprojects.seakyluo.randommenu.adapters.impl.RestaurantAdapter;
import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.database.dao.Page;
import personalprojects.seakyluo.randommenu.database.dao.PagedData;
import personalprojects.seakyluo.randommenu.database.services.RestaurantDaoService;
import personalprojects.seakyluo.randommenu.dialogs.RestaurantFilterDialog;
import personalprojects.seakyluo.randommenu.enums.FoodClass;
import personalprojects.seakyluo.randommenu.enums.OperationType;
import personalprojects.seakyluo.randommenu.enums.RestaurantOrderByField;
import personalprojects.seakyluo.randommenu.helpers.PopupMenuHelper;
import personalprojects.seakyluo.randommenu.models.RestaurantFilter;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;
import personalprojects.seakyluo.randommenu.utils.ImageUtils;
import personalprojects.seakyluo.randommenu.utils.ListUtils;
import personalprojects.seakyluo.randommenu.utils.RecyclerViewUtils;
import personalprojects.seakyluo.randommenu.utils.RestaurantUtils;

import static android.app.Activity.RESULT_OK;

import org.apache.commons.collections.CollectionUtils;

public class RestaurantsFragment extends Fragment {
    public static final String TAG = "RestaurantsFragment";
    private static final int PAGE_SIZE = 10;
    private long totalRestaurants;
    private TextView titleTextView;
    private RecyclerView restaurantRecyclerView;
    private RestaurantAdapter restaurantAdapter;
    private ImageButton filterButton, searchButton, statsButton;
    private RestaurantFilter restaurantFilter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurants, container, false);
        titleTextView = view.findViewById(R.id.title_text_view);
        FloatingActionButton fab = view.findViewById(R.id.restaurant_fab);
        restaurantAdapter = new RestaurantAdapter(getContext());
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        restaurantRecyclerView = view.findViewById(R.id.restaurant_recycler_view);
        statsButton = view.findViewById(R.id.stats_button);
        filterButton = view.findViewById(R.id.filter_button);
        searchButton = view.findViewById(R.id.search_button);

        fab.setOnClickListener(this::showCreateRestaurantPopupMenu);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            setFirstPage();
        });
        statsButton.setOnClickListener(this::showStatsPopup);
        filterButton.setOnClickListener(this::showFilterDialog);
        searchButton.setOnClickListener(this::showSearchActivity);
        titleTextView.setOnClickListener(v -> restaurantRecyclerView.smoothScrollToPosition(0));
        restaurantRecyclerView.setAdapter(restaurantAdapter);
        RecyclerViewUtils.setAsPaged(restaurantRecyclerView, PAGE_SIZE, ((pageNum, pageSize, filter) -> RestaurantDaoService.selectByPage(pageNum, pageSize, restaurantFilter)), restaurantFilter);
        setFirstPage();
        return view;
    }

    private void showStatsPopup(View v){
        PopupMenuHelper helper = new PopupMenuHelper(R.menu.restaurant_stats_menu, getContext(), v);
        List<RestaurantOrderByField> orderByDesc = restaurantFilter == null ? null : restaurantFilter.getOrderByDesc();
        if (CollectionUtils.isEmpty(orderByDesc)){
            helper.removeItems(R.id.cancel_order_by);
        }
        else {
            if (orderByDesc.contains(RestaurantOrderByField.consumeCount)){
                helper.removeItems(R.id.order_by_consume_count);
            }
            if (orderByDesc.contains(RestaurantOrderByField.averageCost)){
                helper.removeItems(R.id.order_by_price_desc);
            }
        }
        helper.setOnItemSelectedListener((menuBuilder, menuItem) -> {
            if (restaurantFilter == null){
                restaurantFilter = new RestaurantFilter();
            }
            switch (menuItem.getItemId()){
                case R.id.order_by_consume_count:
                    restaurantFilter.setOrderByDesc(Lists.newArrayList(RestaurantOrderByField.consumeCount));
                    setOrderBy();
                    return true;
                case R.id.order_by_price_desc:
                    restaurantFilter.setOrderByDesc(Lists.newArrayList(RestaurantOrderByField.averageCost));
                    setOrderBy();
                    return true;
                case R.id.cancel_order_by:
                    restaurantFilter.setOrderByDesc(null);
                    setFirstPage();
                    statsButton.setImageResource(R.drawable.ic_stats);
                    return true;
                case R.id.show_eater_stats:
                    showEaterStats();
                    return true;
            }
            return false;
        });
        helper.show();
    }

    private void setOrderBy(){
        setFirstPage();
        statsButton.setImageResource(R.drawable.ic_stats_selected);
    }

    private void showEaterStats(){
        Intent intent = new Intent(getActivity(), EaterStatsActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
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

    private void showFilterDialog(View v){
        RestaurantFilterDialog dialog = new RestaurantFilterDialog();
        dialog.setRestaurantFilter(restaurantFilter);
        dialog.setConfirmListener(filter -> {
            restaurantFilter = filter;
            if (filter == null || filter.isEmpty()){
                filterButton.setImageResource(R.drawable.ic_filter);
            } else {
                filterButton.setImageResource(R.drawable.ic_filtering);
            }
            setFirstPage();
        });
        dialog.show(getChildFragmentManager(), RestaurantFilterDialog.TAG);
    }

    private void showSearchActivity(View v){
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        intent.putExtra(SearchActivity.SEARCH_TYPE, FoodClass.RESTAURANT);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    private void setFirstPage(){
        setData(RestaurantDaoService.selectByPage(1, PAGE_SIZE, restaurantFilter));
    }

    private void setData(PagedData<RestaurantVO> pagedData){
        List<RestaurantVO> restaurants = pagedData.getData();
        Page page = pagedData.getPage();
        long total = page.getTotal();
        restaurantAdapter.setData(restaurants);
        setTitle(total);
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
            saveRestaurant(restaurant);
        }
        else if (requestCode == ActivityCodeConstant.SHOW_RESTAURANT){
            RestaurantVO restaurant = data.getParcelableExtra(EditRestaurantActivity.DATA);
            long id = restaurant.getId();
            int index = restaurantAdapter.indexOf(i -> i.getId() == id);
            OperationType operationType = (OperationType) data.getSerializableExtra(ShowRestaurantActivity.OPERATION_TYPE);
            if (OperationType.DELETE.equals(operationType)){
                restaurantAdapter.removeAt(index);
                Snackbar snackbar = Snackbar.make(restaurantRecyclerView, String.format("\"%s\"已被删除", restaurant.getName()), Snackbar.LENGTH_LONG);
                snackbar.setAction("撤销", vv -> {
                    RestaurantDaoService.insert(restaurant);
                });
                snackbar.show();
            }
            else if (OperationType.UPDATE.equals(operationType)){
                saveRestaurant(restaurant);
            }
        }
        else if (requestCode == ActivityCodeConstant.GALLERY) {
            createRestaurant(RestaurantUtils.buildFromImages(getContext(), data.getClipData()));
        }
        else if (requestCode == ActivityCodeConstant.EDIT_RESTAURANT_FOOD){
            RestaurantFoodVO foodVO = data.getParcelableExtra(EditRestaurantFoodActivity.DATA);
            int index = restaurantAdapter.getData().indexOf(i -> i.getId() == foodVO.getRestaurantId());
            if (index == -1) return;
            RestaurantVO restaurantVO = restaurantAdapter.getDataAt(index);
            int foodIndex = ListUtils.indexOf(restaurantVO.getFoods(), f -> f.getId() == foodVO.getId());
            restaurantVO.getFoods().remove(foodIndex);
            restaurantAdapter.set(restaurantVO, index);
        }
    }

    private void saveRestaurant(RestaurantVO restaurant){
        long id = restaurant.getId();
        int index = restaurantAdapter.indexOf(i -> i.getId() == id);
        RestaurantVO vo = RestaurantDaoService.selectPagedView(id);
        if (index == -1){
            restaurantAdapter.add(vo, 0);
            restaurantRecyclerView.smoothScrollToPosition(0);
            setTitle(totalRestaurants + 1);
        } else {
            restaurantAdapter.set(vo, index);
        }
    }

    private void setTitle(long total){
        totalRestaurants = total;
        titleTextView.setText(totalRestaurants == 0 ? "探店" : String.format("探店（%d）", totalRestaurants));
    }
}
