package personalprojects.seakyluo.randommenu.fragments;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.collect.Lists;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import personalprojects.seakyluo.randommenu.activities.impl.EditRestaurantActivity;
import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.impl.RestaurantAdapter;
import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.database.services.RestaurantDaoService;
import personalprojects.seakyluo.randommenu.helpers.PopupMenuHelper;
import personalprojects.seakyluo.randommenu.interfaces.RestaurantListener;
import personalprojects.seakyluo.randommenu.models.AddressVO;
import personalprojects.seakyluo.randommenu.models.vo.ConsumeRecordVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;
import personalprojects.seakyluo.randommenu.utils.AddressUtils;
import personalprojects.seakyluo.randommenu.utils.FileUtils;
import personalprojects.seakyluo.randommenu.utils.ImageUtils;

import static android.app.Activity.RESULT_OK;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

public class RestaurantsFragment extends Fragment {
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

        fab.setOnClickListener(this::showCreateRestaurantPopupMenu);
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

    private void setData(List<RestaurantVO> restaurants){
        restaurantAdapter.setData(restaurants);
        setTitle();
    }

    private void createRestaurant(RestaurantVO data){
        Intent intent = new Intent(getContext(), EditRestaurantActivity.class);
        intent.putExtra(EditRestaurantActivity.DATA, data);
        startActivityForResult(intent, ActivityCodeConstant.EDIT_RESTAURANT);
        getActivity().overridePendingTransition(R.anim.push_down_in, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == ActivityCodeConstant.EDIT_RESTAURANT) {
            RestaurantVO restaurant = data.getParcelableExtra(EditRestaurantActivity.DATA);
            long id = restaurant.getId();
            if (id == 0){
                restaurantAdapter.add(RestaurantDaoService.selectPagedView(restaurant.getId()), 0);
                setTitle();
            } else {
                int index = restaurantAdapter.indexOf(i -> i.getId() == id);
                if (index != -1){
                    restaurantAdapter.set(RestaurantDaoService.selectPagedView(id), index);
                }
            }
        }
        else if (requestCode == ActivityCodeConstant.GALLERY) {
            ClipData clipData = data.getClipData();
            createRestaurant(buildRestaurantFromImages(clipData));
        }
    }

    private RestaurantVO buildRestaurantFromImages(ClipData clipData){
        int count = clipData.getItemCount();
        if (count == 0) return null;
        Context context = getContext();
        Set<AddressVO> addressSet = new HashSet<>();
        ConsumeRecordVO record = new ConsumeRecordVO();
        List<RestaurantFoodVO> foods = new ArrayList<>();
        for (int i = 0; i < count; i++){
            Uri uri = clipData.getItemAt(i).getUri();
            String fileName = ImageUtils.newImageFileName(i);
            if (!ImageUtils.saveImage(context, uri, fileName)){
                continue;
            }
            RestaurantFoodVO food = new RestaurantFoodVO();
            foods.add(food);
            food.setPictureUri(fileName);
            String path = ImageUtils.getImagePath(fileName);
            ExifInterface exifInterface;
            try {
                exifInterface = new ExifInterface(path);
            } catch (Exception e) {
                Log.w("buildRestaurantFromImages", "ExifInterface", e);
                continue;
            }
            try {
                String dateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                long consumeTime;
                if (dateTime == null){
                    consumeTime = Files.readAttributes(Paths.get(FileUtils.getPath(path)), BasicFileAttributes.class).creationTime().toMillis();;
                } else {
                    consumeTime = Long.parseLong(dateTime);
                }
                record.setConsumeTime(Math.min(record.getConsumeTime(), consumeTime));
            } catch (Exception e){
                Log.w("buildRestaurantFromImages", "consumeTime", e);
                continue;
            }
            String latValue = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String lonValue = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String latRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String lonRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            AddressVO address = AddressUtils.getAddress(context, latValue, latRef, lonValue, lonRef);
            if (address != null){
                addressSet.add(address);
                record.setAddress(address);
            }
        }
        record.setFoods(foods);
        RestaurantVO vo = new RestaurantVO();
        vo.setAddressList(new ArrayList<>(addressSet));
        vo.setRecords(Lists.newArrayList(record));
        return vo;
    }

    private void setTitle(){
        List<RestaurantVO> restaurants = restaurantAdapter.getData();
        titleTextView.setText(restaurants.isEmpty() ? "探店" : String.format("探店（%d）", restaurants.size()));
    }

}
