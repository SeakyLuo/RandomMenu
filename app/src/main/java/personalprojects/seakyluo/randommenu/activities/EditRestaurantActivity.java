package personalprojects.seakyluo.randommenu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.adapters.impl.AddressAdapter;
import personalprojects.seakyluo.randommenu.adapters.impl.ConsumeRecordEditAdapter;
import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.controls.EditSpinner;
import personalprojects.seakyluo.randommenu.database.services.RestaurantDaoService;
import personalprojects.seakyluo.randommenu.helpers.DragDropCallback;
import personalprojects.seakyluo.randommenu.models.AList;
import personalprojects.seakyluo.randommenu.models.vo.AddressVO;
import personalprojects.seakyluo.randommenu.models.FoodType;
import personalprojects.seakyluo.randommenu.models.vo.ConsumeRecordVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;
import personalprojects.seakyluo.randommenu.services.FoodTypeService;
import personalprojects.seakyluo.randommenu.utils.ActivityUtils;
import personalprojects.seakyluo.randommenu.utils.ImageUtils;
import personalprojects.seakyluo.randommenu.utils.RestaurantUtils;
import personalprojects.seakyluo.randommenu.utils.SwipeToDeleteUtils;

public class EditRestaurantActivity extends AppCompatActivity implements DragDropCallback.DragStartListener<AddressVO> {
    public static final String DATA_ID = "RESTAURANT_ID", DATA = "RESTAURANT";
    private EditText editName, editComment, editLink;
    private View addressPlaceholder, consumeRecordPlaceholder;
    private RecyclerView consumeRecordRecyclerView;
    private AddressAdapter addressAdapter;
    private ConsumeRecordEditAdapter consumeRecordAdapter;
    private EditSpinner editFoodType;
    private ItemTouchHelper dragHelper;
    private RestaurantVO restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_restaurant);

        ImageButton cancelButton = findViewById(R.id.cancel_button);
        ImageButton confirmButton = findViewById(R.id.confirm_button);
        editName = findViewById(R.id.edit_name);
        editFoodType = findViewById(R.id.edit_food_type);
        editComment = findViewById(R.id.edit_comment);
        editLink = findViewById(R.id.edit_link);
        addressPlaceholder = findViewById(R.id.address_placeholder);
        consumeRecordPlaceholder = findViewById(R.id.consume_record_placeholder);
        RecyclerView addressRecyclerView = findViewById(R.id.address_recycler_view);
        consumeRecordRecyclerView = findViewById(R.id.consume_record_recycler_view);
        addressAdapter = new AddressAdapter(this);
        consumeRecordAdapter = new ConsumeRecordEditAdapter(this);
        ImageButton addAddressButton = findViewById(R.id.add_address_button);
        ImageButton addConsumeRecordButton = findViewById(R.id.add_consume_record_button);

        long restaurantId;
        if (savedInstanceState == null){
            restaurantId = getIntent().getLongExtra(DATA_ID, 0);
            restaurant = getIntent().getParcelableExtra(DATA);
        } else {
            restaurantId = savedInstanceState.getLong(DATA_ID, 0);
            restaurant = savedInstanceState.getParcelable(DATA);
        }
        restaurant = Optional.ofNullable(restaurant).orElse(RestaurantDaoService.selectById(restaurantId));
        editFoodType.setItemData(FoodTypeService.selectAllNames());
        dragHelper = new ItemTouchHelper(new DragDropCallback<>(addressAdapter));
        dragHelper.attachToRecyclerView(addressRecyclerView);
        SwipeToDeleteUtils.apply(addressRecyclerView, this, this::checkAddressBeforeRemoval, this::removeAddress, this::addAddress, AddressVO::getAddress);
        addressAdapter.setDragStartListener(this);
        addressAdapter.setOnAddressChanged(this::addressChanged);
        addressRecyclerView.setAdapter(addressAdapter);
        consumeRecordRecyclerView.setAdapter(consumeRecordAdapter);

        setData(restaurant);
        cancelButton.setOnClickListener(v -> finish());
        confirmButton.setOnClickListener(this::onConfirm);
        addAddressButton.setOnClickListener(this::addAddressRow);
        addressPlaceholder.setOnClickListener(this::addAddressRow);
        addConsumeRecordButton.setOnClickListener(v -> startEditConsumeRecordActivity(null));
        addConsumeRecordButton.setOnLongClickListener(v -> quickCreateConsumeRecord());
        consumeRecordAdapter.setOnRecordClickListener((vh, data) -> startEditConsumeRecordActivity(data));
        consumeRecordPlaceholder.setOnClickListener(v -> startEditConsumeRecordActivity(null));
        consumeRecordAdapter.setOnFoodClickListener((vh, data) -> startEditRestaurantFoodActivity(data));
        consumeRecordAdapter.setOnRecordDeleteListener((vh, data) -> removeConsumeRecord(vh.getBindingAdapterPosition()));
    }

    private void onConfirm(View view){
        if (StringUtils.isBlank(editName.getText().toString())){
            Toast.makeText(this, "店名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtils.isBlank(editFoodType.getText().toString())){
            Toast.makeText(this, "菜系不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isBadAddress()){
            return;
        }
        finishWithData(buildData());
    }

    private RestaurantVO buildData(){
        RestaurantVO i = new RestaurantVO();
        i.setId(restaurant == null ? 0 : restaurant.getId());
        i.setName(editName.getText().toString().trim());
        i.setAddressList(addressAdapter.getData());
        String foodTypeName = editFoodType.getText().trim();
        i.setFoodType(new FoodType(FoodTypeService.getIdByName(foodTypeName), foodTypeName));
        i.setComment(editComment.getText().toString().trim());
        i.setLink(editLink.getText().toString().trim());
        i.setRecords(consumeRecordAdapter.getData());
        return i;
    }

    private void addAddressRow(View view) {
        addressAdapter.add(new AddressVO(), 0);
        addressPlaceholder.setVisibility(View.GONE);
    }

    private void addAddress(int index, AddressVO address) {
        addressAdapter.add(address, index);
        addressPlaceholder.setVisibility(View.GONE);
    }

    private boolean checkAddressBeforeRemoval(int index){
        AddressVO item = addressAdapter.getDataAt(index);
        return consumeRecordAdapter.getData().any(i -> StringUtils.equals(item.buildFullAddress(), i.getAddress().buildFullAddress()));
    }

    private AddressVO removeAddress(int index) {
        AddressVO item = addressAdapter.getDataAt(index);
        addressAdapter.removeAt(index);
        if (addressAdapter.isEmpty()){
            addressPlaceholder.setVisibility(View.VISIBLE);
        }
        return item;
    }

    private void addressChanged(AddressVO before, AddressVO after){
        for (ConsumeRecordVO record : consumeRecordAdapter.getData()) {
            if (record.getAddress().equals(before)){
                record.setAddress(after);
            }
        }
    }

    private boolean quickCreateConsumeRecord(){
        if (isBadAddress()){
            return false;
        }
        ImageUtils.openGallery(this);
        return true;
    }

    private void startEditConsumeRecordActivity(ConsumeRecordVO data){
        if (isBadAddress()){
            return;
        }
        Intent intent = new Intent(this, EditConsumeRecordActivity.class);
        intent.putExtra(EditConsumeRecordActivity.DATA, data);
        intent.putExtra(EditConsumeRecordActivity.ADDRESS_LIST, addressAdapter.getData());
        startActivityForResult(intent, ActivityCodeConstant.EDIT_CONSUME_RECORD);
        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
    }

    private void startEditRestaurantFoodActivity(RestaurantFoodVO food){
        Intent intent = new Intent(this, EditRestaurantFoodActivity.class);
        intent.putExtra(EditRestaurantFoodActivity.DATA, food);
        startActivityForResult(intent, ActivityCodeConstant.EDIT_RESTAURANT_FOOD);
        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
    }

    private boolean isBadAddress(){
        if (addressAdapter.isEmpty()){
            Toast.makeText(this, "地址不能为空", Toast.LENGTH_SHORT).show();
            return true;
        }
        AList<AddressVO> addressList = addressAdapter.getData();
        if (addressList.any(AddressVO::isEmpty)){
            Toast.makeText(this, R.string.incomplete_address, Toast.LENGTH_SHORT).show();
            return true;
        }
        if (consumeRecordAdapter.getData().stream().anyMatch(i -> i.getAddress() == null)){
            if (addressList.size() == 1){
                AddressVO addressVO = addressList.get(0);
                for (ConsumeRecordVO record : consumeRecordAdapter.getData()) {
                    record.setAddress(addressVO);
                }
            } else {
                Toast.makeText(this, R.string.incomplete_address, Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

    private void setData(RestaurantVO src){
        if (src == null){
            addressAdapter.add(new AddressVO());
            return;
        }
        editName.setText(src.getName());
        List<AddressVO> addressList = src.getAddressList();
        if (CollectionUtils.isEmpty(addressList)){
            addressAdapter.add(new AddressVO());
        } else {
            addressAdapter.setData(addressList);
        }
        addressPlaceholder.setVisibility(View.GONE);
        FoodType foodType = src.getFoodType();
        if (foodType != null){
            editFoodType.setText(foodType.getName());
        }
        editComment.setText(src.getComment());
        editLink.setText(src.getLink());
        setRecords(src.getRecords());
        if (consumeRecordAdapter.isEmpty()){
            consumeRecordPlaceholder.setVisibility(View.VISIBLE);
        } else {
            consumeRecordPlaceholder.setVisibility(View.GONE);
        }
    }

    private List<ConsumeRecordVO> setRecords(List<ConsumeRecordVO> records){
        records.sort(Comparator.comparing(ConsumeRecordVO::getConsumeTime).reversed());
        consumeRecordAdapter.setData(records);
        resetRecordIndex(0);
        return records;
    }

    private void addConsumeRecord(ConsumeRecordVO record, int index){
        consumeRecordAdapter.add(record, index);
        consumeRecordPlaceholder.setVisibility(View.GONE);
        resetRecordIndex(index);
    }

    private void updateConsumeRecord(ConsumeRecordVO record){
        consumeRecordAdapter.set(record, record.getIndex());
    }

    private ConsumeRecordVO removeConsumeRecord(int index){
        ConsumeRecordVO item = consumeRecordAdapter.getDataAt(index);
        consumeRecordAdapter.removeAt(index);
        resetRecordIndex(index);
        if (consumeRecordAdapter.isEmpty()){
            consumeRecordPlaceholder.setVisibility(View.VISIBLE);
        }
        Snackbar snackbar = Snackbar.make(consumeRecordRecyclerView, String.format("\"%s\"已被删除", item.formatConsumeTime() + "的记录"), Snackbar.LENGTH_LONG);
        snackbar.setAction("撤销", view -> {
            addConsumeRecord(item, index);
            consumeRecordRecyclerView.smoothScrollToPosition(index);
        });
        snackbar.show();
        return item;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DATA, buildData());
    }

    private void finishWithData(RestaurantVO data){
        try {
            RestaurantDaoService.save(data);
        } catch (Exception e){
            Toast.makeText(this,"保存失败：" + e.toString(), Toast.LENGTH_SHORT).show();
            Log.e("EditRestaurantActivity", "finishWithData failed", e);
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(DATA, data);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void requestDrag(CustomAdapter<AddressVO>.CustomViewHolder viewHolder) {
        dragHelper.startDrag(viewHolder);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == ActivityCodeConstant.EDIT_CONSUME_RECORD){
            ConsumeRecordVO record = data.getParcelableExtra(EditConsumeRecordActivity.DATA);
            if (record.getIndex() == -1){
                addConsumeRecord(record, 0);
            } else {
                updateConsumeRecord(record);
            }
        }
        else if (requestCode == ActivityCodeConstant.EDIT_RESTAURANT_FOOD){
            RestaurantFoodVO food = data.getParcelableExtra(EditRestaurantFoodActivity.DATA);
            consumeRecordAdapter.setFood(food);
        }
        else if (requestCode == ActivityCodeConstant.GALLERY){
            RestaurantVO restaurant = RestaurantUtils.buildFromImages(this, data.getClipData());
            List<AddressVO> addressList = restaurant.getAddressList();
            AList<AddressVO> existing = addressAdapter.getData();
            for (AddressVO address : addressList){
                if (existing.none(i -> StringUtils.equals(address.buildFullAddress(), i.buildFullAddress()))){
                    addressAdapter.add(address);
                }
            }
            for (ConsumeRecordVO record : restaurant.getRecords()){
                addConsumeRecord(record, 0);
            }
        }
    }

    private void resetRecordIndex(int start){
        for (int i = start; i < consumeRecordAdapter.getItemCount(); i++){
            consumeRecordAdapter.getDataAt(i).setIndex(i);
        }
    }

    @Override
    public void finish() {
        ActivityUtils.hideKeyboard(this);
        super.finish();
    }
}
