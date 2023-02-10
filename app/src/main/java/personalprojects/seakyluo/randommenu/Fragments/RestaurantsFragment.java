package personalprojects.seakyluo.randommenu.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.EditFoodActivity;
import personalprojects.seakyluo.randommenu.EditRestaurantActivity;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.RestaurantAdapter;
import personalprojects.seakyluo.randommenu.converters.RestaurantConverter;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.FoodType;
import personalprojects.seakyluo.randommenu.models.Settings;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;

public class RestaurantsFragment extends Fragment {
    public static final String TAG = "RestaurantsFragment";
    private TextView titleTextView;
    private boolean isLoaded = false;
    private RestaurantAdapter restaurantAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurants, container, false);
        titleTextView = view.findViewById(R.id.title_text_view);
        FloatingActionButton fab = view.findViewById(R.id.restaurant_fab);
        fab.setOnClickListener(v -> createRestaurant());

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
        });
        RecyclerView restaurantRecyclerView = view.findViewById(R.id.restaurant_recycler_view);
        restaurantAdapter = new RestaurantAdapter();
        restaurantRecyclerView.setAdapter(restaurantAdapter);

        setData(Settings.settings.Restaurants.convert(RestaurantConverter::convert));
        return view;
    }

    private void setData(AList<RestaurantVO> restaurants){
        titleTextView.setText(restaurants.isEmpty() ? "探店" : String.format("探店（%d）", restaurants.count()));
        restaurantAdapter.setData(restaurants);
    }

    private void createRestaurant(){
        Intent intent = new Intent(getContext(), EditRestaurantActivity.class);
        startActivityForResult(intent, EditRestaurantActivity.CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
