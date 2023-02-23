package personalprojects.seakyluo.randommenu.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.EditRestaurantActivity;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.impl.RestaurantAdapter;
import personalprojects.seakyluo.randommenu.converters.RestaurantConverter;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.Settings;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;

import static android.app.Activity.RESULT_OK;

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
        restaurantAdapter = new RestaurantAdapter();
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        RecyclerView restaurantRecyclerView = view.findViewById(R.id.restaurant_recycler_view);

        fab.setOnClickListener(v -> createRestaurant());
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
        });
        restaurantAdapter.setContext(getActivity());
        restaurantRecyclerView.setAdapter(restaurantAdapter);

        setData(Settings.settings.Restaurants.convert(RestaurantConverter::convert));
        return view;
    }

    private void setData(AList<RestaurantVO> restaurants){
        titleTextView.setText(restaurants.isEmpty() ? "探店" : String.format("探店（%d）", restaurants.size()));
        restaurantAdapter.setData(restaurants);
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
        restaurantAdapter.add(restaurant);
    }

}
