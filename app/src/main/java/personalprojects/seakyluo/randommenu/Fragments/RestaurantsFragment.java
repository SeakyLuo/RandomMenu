package personalprojects.seakyluo.randommenu.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import personalprojects.seakyluo.randommenu.activities.impl.EditRestaurantActivity;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.impl.RestaurantAdapter;
import personalprojects.seakyluo.randommenu.database.services.RestaurantDaoService;
import personalprojects.seakyluo.randommenu.interfaces.RestaurantListener;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;

import static android.app.Activity.RESULT_OK;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

public class RestaurantsFragment extends Fragment implements RestaurantListener {
    public static final String TAG = "RestaurantsFragment";
    private static final int PAGE_SIZE = 20;
    private TextView titleTextView;
    private RestaurantAdapter restaurantAdapter;
    private int currentPage = 1;
    private int lastItemPosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurants, container, false);
        titleTextView = view.findViewById(R.id.title_text_view);
        FloatingActionButton fab = view.findViewById(R.id.restaurant_fab);
        restaurantAdapter = new RestaurantAdapter(getContext());
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        RecyclerView restaurantRecyclerView = view.findViewById(R.id.restaurant_recycler_view);

        RestaurantDaoService.addListener(this);
        fab.setOnClickListener(v -> createRestaurant());
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            setData(RestaurantDaoService.selectByPage(currentPage = 1, PAGE_SIZE));
        });
        restaurantRecyclerView.setAdapter(restaurantAdapter);
        restaurantRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == SCROLL_STATE_IDLE && lastItemPosition == restaurantAdapter.getItemCount()){
                    restaurantAdapter.add(RestaurantDaoService.selectByPage(++currentPage, PAGE_SIZE));
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager){
                    LinearLayoutManager manager = (LinearLayoutManager) layoutManager;
                    int firstItem = manager.findFirstVisibleItemPosition();
                    int lastItem = manager.findLastCompletelyVisibleItemPosition();
                    lastItemPosition = firstItem + (lastItem - firstItem) + 1;
                }
            }
        });

        setData(RestaurantDaoService.selectByPage(currentPage = 1, PAGE_SIZE));
        return view;
    }

    private void setData(List<RestaurantVO> restaurants){
        restaurantAdapter.setData(restaurants);
        setTitle();
    }

    private void createRestaurant(){
        Intent intent = new Intent(getContext(), EditRestaurantActivity.class);
        startActivityForResult(intent, EditRestaurantActivity.CODE);
        getActivity().overridePendingTransition(R.anim.push_down_in, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        RestaurantVO restaurant = data.getParcelableExtra(EditRestaurantActivity.DATA);
        restaurantAdapter.add(restaurant, 0);
        setTitle();
    }

    private void setTitle(){
        List<RestaurantVO> restaurants = restaurantAdapter.getData();
        titleTextView.setText(restaurants.isEmpty() ? "探店" : String.format("探店（%d）", restaurants.size()));
    }

    @Override
    public void onUpdate(RestaurantVO data) {
        long id = data.getId();
        int index = restaurantAdapter.indexOf(i -> i.getId() == id);
        if (index != -1){
            restaurantAdapter.set(RestaurantDaoService.selectPagedView(id), index);
        }
    }
}
