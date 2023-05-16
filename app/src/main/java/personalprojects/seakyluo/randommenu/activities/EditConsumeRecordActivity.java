package personalprojects.seakyluo.randommenu.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loper7.date_time_picker.dialog.CardDatePickerDialog;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.adapters.impl.ConsumeFoodAdapter;
import personalprojects.seakyluo.randommenu.constants.ActivityCodeConstant;
import personalprojects.seakyluo.randommenu.database.services.AddressDaoService;
import personalprojects.seakyluo.randommenu.fragments.HorizontalImageViewFragment;
import personalprojects.seakyluo.randommenu.helpers.DragDropCallback;
import personalprojects.seakyluo.randommenu.models.vo.AddressVO;
import personalprojects.seakyluo.randommenu.models.vo.ConsumeRecordVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;
import personalprojects.seakyluo.randommenu.utils.ActivityUtils;
import personalprojects.seakyluo.randommenu.utils.DoubleUtils;
import personalprojects.seakyluo.randommenu.utils.ImageUtils;
import personalprojects.seakyluo.randommenu.utils.JsonUtils;
import personalprojects.seakyluo.randommenu.utils.RestaurantUtils;
import personalprojects.seakyluo.randommenu.utils.SwipeToDeleteUtils;

public class EditConsumeRecordActivity extends AppCompatActivity implements DragDropCallback.DragStartListener<ConsumeRecordVO> {
    public static final String DATA = "CONSUME_RECORD", ADDRESS_LIST = "ADDRESS_LIST", CONSUME_TIME = "CONSUME_TIME";
    public static final String EATER_DELIMITER = "，";
    private Long consumeTime;
    private TextView consumeTimeText, addressText;
    private Spinner addressSpinner;
    private EditText editFriends, editComment, editTotalCost;
    private CheckBox autoCostCheckBox;
    private View consumeFoodPlaceholder;
    private ConsumeFoodAdapter foodAdapter;
    private ItemTouchHelper dragHelper;
    private ArrayList<AddressVO> addressList;
    private ConsumeRecordVO currentRecord;
    private HorizontalImageViewFragment imageAdderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_consume_record);

        ImageButton cancelButton = findViewById(R.id.cancel_button);
        ImageButton confirmButton = findViewById(R.id.confirm_button);
        consumeTimeText = findViewById(R.id.consume_time_text);
        addressText = findViewById(R.id.address_text);
        addressSpinner = findViewById(R.id.address_spinner);
        editFriends = findViewById(R.id.edit_friends);
        editComment = findViewById(R.id.edit_comment);
        editTotalCost = findViewById(R.id.edit_total_cost_text);
        autoCostCheckBox = findViewById(R.id.autoCostCheckBox);
        consumeFoodPlaceholder = findViewById(R.id.consume_food_placeholder);
        ImageButton addConsumeFoodButton = findViewById(R.id.add_consume_food_button);
        RecyclerView consumeRecordRecyclerView = findViewById(R.id.consume_record_recycler_view);
        foodAdapter = new ConsumeFoodAdapter(this, true);

        if (savedInstanceState == null){
            Intent intent = getIntent();
            currentRecord = intent.getParcelableExtra(DATA);
            addressList = intent.getParcelableArrayListExtra(ADDRESS_LIST);
        } else {
            currentRecord = savedInstanceState.getParcelable(DATA);
            addressList = savedInstanceState.getParcelableArrayList(ADDRESS_LIST);
            consumeTime = savedInstanceState.getLong(CONSUME_TIME);
        }
        imageAdderFragment = (HorizontalImageViewFragment) getSupportFragmentManager().findFragmentById(R.id.image_viewer_fragment);

        if (addressList == null){
            addressList = (ArrayList<AddressVO>) AddressDaoService.selectByRestaurant(currentRecord.getRestaurantId());
        }
        imageAdderFragment.setEditable(true);
        setData(currentRecord);
        dragHelper = new ItemTouchHelper(new DragDropCallback<>(foodAdapter));
        dragHelper.attachToRecyclerView(consumeRecordRecyclerView);
        SwipeToDeleteUtils.apply(consumeRecordRecyclerView, this, this::removeFood, this::addFood, RestaurantFoodVO::getName);
        consumeRecordRecyclerView.setAdapter(foodAdapter);
        cancelButton.setOnClickListener(this::onCancel);
        confirmButton.setOnClickListener(this::onConfirm);
        consumeTimeText.setOnClickListener(v -> {
            new CardDatePickerDialog.Builder(this)
                    .setLabelText("年","月","日","时","分")
                    .setOnChoose("选择", this::setConsumeTime)
                    .setDefaultTime(Optional.ofNullable(consumeTime).orElse(System.currentTimeMillis()))
                    .showBackNow(true)
                    .build().show();
        });
        addConsumeFoodButton.setOnClickListener(v -> editRestaurantFood(null));
        addConsumeFoodButton.setOnLongClickListener(v -> {
            ImageUtils.openGallery(this);
            return true;
        });
        autoCostCheckBox.setOnCheckedChangeListener((v, c) -> autoResetTotalCost());
        consumeFoodPlaceholder.setOnClickListener(v -> editRestaurantFood(null));
    }

    private void editRestaurantFood(RestaurantFoodVO data) {
        Intent intent = new Intent(this, EditRestaurantFoodActivity.class);
        intent.putExtra(EditConsumeRecordActivity.DATA, data);
        startActivityForResult(intent, ActivityCodeConstant.EDIT_RESTAURANT_FOOD);
        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
    }

    private void addFood(int index, RestaurantFoodVO item){
        item.setIndex(index);
        foodAdapter.add(item, index);
        consumeFoodPlaceholder.setVisibility(View.GONE);
        autoResetTotalCost();
    }

    private RestaurantFoodVO removeFood(int index){
        RestaurantFoodVO item = foodAdapter.getDataAt(index);
        foodAdapter.removeAt(index);
        for (int i = index; i < foodAdapter.getItemCount(); i++) {
            foodAdapter.getDataAt(i).setIndex(i);
        }
        if (foodAdapter.isEmpty()){
            consumeFoodPlaceholder.setVisibility(View.VISIBLE);
        }
        autoResetTotalCost();
        return item;
    }

    private void onCancel(View view){
        finish();
    }

    private void onConfirm(View view){
        String totalCost = editTotalCost.getText().toString();
        if (StringUtils.isEmpty(totalCost)){
            editTotalCost.setText("0");
        }
        else if (!NumberUtils.isParsable(totalCost)){
            Toast.makeText(this, "总金额不合法！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (foodAdapter.isEmpty()){
            Toast.makeText(this, "菜品不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        finishWithData(buildData());
    }

    private void setData(ConsumeRecordVO src){
        setAddress(addressList);
        if (src == null){
            setConsumeTime(System.currentTimeMillis());
            addressSpinner.setSelection(0);
            return;
        }
        setConsumeTime(src.getConsumeTime());
        List<String> eaters = src.getEaters();
        if (CollectionUtils.isNotEmpty(eaters)){
            editFriends.setText(String.join(EATER_DELIMITER, eaters));
        }
        AddressVO address = src.getAddress();
        if (address == null){
            addressSpinner.setSelection(0);
        } else {
            addressSpinner.setSelection(addressList.indexOf(address));
        }
        editComment.setText(src.getComment());
        autoCostCheckBox.setChecked(src.isAutoCost());
        editTotalCost.setText(DoubleUtils.truncateZero(src.getTotalCost()));
        imageAdderFragment.setData(src.getEnvironmentPictures());
        foodAdapter.setData(src.getFoods());
        foodAdapter.getData().For(i -> foodAdapter.getDataAt(i).setIndex(i));
        if (CollectionUtils.isEmpty(src.getFoods())){
            consumeFoodPlaceholder.setVisibility(View.VISIBLE);
        } else {
            consumeFoodPlaceholder.setVisibility(View.GONE);
        }
    }

    private void setConsumeTime(long time){
        consumeTime = time;
        consumeTimeText.setText(DateFormatUtils.format(time, ConsumeRecordVO.CONSUME_TIME_FORMAT));
    }

    private void setAddress(List<AddressVO> addressList){
        if (CollectionUtils.isEmpty(addressList)){
            return;
        }
        if (addressList.size() == 1){
            AddressVO address = addressList.get(0);
            addressText.setText(address.buildSimpleAddress());
            addressText.setVisibility(View.VISIBLE);
            addressSpinner.setVisibility(View.GONE);
        } else {
            List<String> addressSelections = addressList.stream().map(AddressVO::buildSimpleAddress).collect(Collectors.toList());
            addressSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, addressSelections));
            addressText.setVisibility(View.GONE);
            addressSpinner.setVisibility(View.VISIBLE);
        }
    }

    private ConsumeRecordVO buildData(){
        ConsumeRecordVO dst = currentRecord == null ? new ConsumeRecordVO() : JsonUtils.copy(currentRecord);
        dst.setConsumeTime(consumeTime);
        int selectedItemPosition = addressSpinner.getSelectedItemPosition();
        if (selectedItemPosition == -1){
            dst.setAddress(addressList.get(0));
        } else {
            dst.setAddress(addressList.get(selectedItemPosition));
        }
        dst.setEaters(Arrays.stream(editFriends.getText().toString().trim().split(EATER_DELIMITER)).filter(StringUtils::isNoneBlank).collect(Collectors.toList()));
        dst.setAutoCost(autoCostCheckBox.isChecked());
        String totalCost = editTotalCost.getText().toString();
        if (NumberUtils.isParsable(totalCost)){
            dst.setTotalCost(Double.parseDouble(totalCost));
        }
        dst.setEnvironmentPictures(imageAdderFragment.getData());
        dst.setComment(editComment.getText().toString());
        dst.setFoods(foodAdapter.getData());
        return dst;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DATA, buildData());
        outState.putParcelableArrayList(ADDRESS_LIST, addressList);
        outState.putLong(CONSUME_TIME, consumeTime);
    }

    private void finishWithData(ConsumeRecordVO data){
        Intent intent = new Intent();
        intent.putExtra(DATA, data);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void requestDrag(CustomAdapter<ConsumeRecordVO>.CustomViewHolder viewHolder) {
        dragHelper.startDrag(viewHolder);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == ActivityCodeConstant.EDIT_RESTAURANT_FOOD){
            RestaurantFoodVO food = data.getParcelableExtra(EditRestaurantFoodActivity.DATA);
            int index = food.getIndex();
            if (index == -1){
                addFood(foodAdapter.getItemCount(), food);
            } else {
                foodAdapter.set(food, index);
                autoResetTotalCost();
            }
        }
        else if (requestCode == ActivityCodeConstant.GALLERY){
            if (ImageUtils.GALLERY_CALLER != 0){
                ActivityUtils.spreadOnActivityResult(this, requestCode, resultCode, data);
                return;
            }
            RestaurantVO restaurantVO = RestaurantUtils.buildFromImages(this, data.getClipData());
            List<ConsumeRecordVO> records = restaurantVO.getRecords();
            Optional<Long> minConsumeTimeOp = records.stream().map(ConsumeRecordVO::getConsumeTime).min(Comparator.comparing(Function.identity()));
            if (minConsumeTimeOp.isPresent()){
                long minConsumeTime = minConsumeTimeOp.get();
                if (minConsumeTime != 0 && minConsumeTime < consumeTime){
                    setConsumeTime(minConsumeTime);
                }
            }
            foodAdapter.add(records.stream().flatMap(i -> i.getFoods().stream()).collect(Collectors.toList()));
            consumeFoodPlaceholder.setVisibility(View.GONE);
        }
    }

    private void autoResetTotalCost(){
        if (!autoCostCheckBox.isChecked()){
            return;
        }
        String price = DoubleUtils.truncateZero(foodAdapter.getData().stream().mapToDouble(RestaurantFoodVO::getPrice).sum());
        editTotalCost.setText(price);
    }

    @Override
    public void finish() {
        ActivityUtils.hideKeyboard(this);
        super.finish();
    }
}
