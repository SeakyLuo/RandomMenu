package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;
import android.widget.ArrayAdapter;
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
import java.util.List;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.adapters.impl.ConsumeFoodAdapter;
import personalprojects.seakyluo.randommenu.dialogs.AddressDialog;
import personalprojects.seakyluo.randommenu.dialogs.RestaurantFoodDialog;
import personalprojects.seakyluo.randommenu.helpers.DragDropCallback;
import personalprojects.seakyluo.randommenu.models.Address;
import personalprojects.seakyluo.randommenu.models.vo.ConsumeRecordVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.utils.SwipeToDeleteUtils;

public class EditConsumeRecordActivity extends AppCompatActivity implements DragDropCallback.DragStartListener<ConsumeRecordVO> {
    public static int CODE = 1;
    public static final String DATA = "CONSUME_RECORD", ADDRESS_LIST = "ADDRESS_LIST", CONSUME_TIME = "CONSUME_TIME";
    public static final String EATER_DELIMITER = "，";
    private Long consumeTime;
    private TextView consumeTimeText, addressText;
    private Spinner addressSpinner;
    private EditText editFriends, editComment, editTotalCost;
    private View consumeFoodPlaceholder;
    private ConsumeFoodAdapter foodAdapter;
    private ItemTouchHelper dragHelper;
    private ArrayList<Address> addressList;

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
        consumeFoodPlaceholder = findViewById(R.id.consume_food_placeholder);
        ImageButton addConsumeFoodButton = findViewById(R.id.add_consume_food_button);
        RecyclerView consumeRecordRecyclerView = findViewById(R.id.consume_record_recycler_view);
        foodAdapter = new ConsumeFoodAdapter();

        ConsumeRecordVO data;
        if (savedInstanceState == null){
            Intent intent = getIntent();
            data = intent.getParcelableExtra(DATA);
            addressList = intent.getParcelableArrayListExtra(ADDRESS_LIST);
        } else {
            data = savedInstanceState.getParcelable(DATA);
            addressList = savedInstanceState.getParcelableArrayList(ADDRESS_LIST);
            consumeTime = savedInstanceState.getLong(CONSUME_TIME);
        }

        setAddress(addressList);
        setData(data);
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
                    .showBackNow(true)
                    .build().show();
        });
        addConsumeFoodButton.setOnClickListener(v -> showFoodDialog(null));
        consumeFoodPlaceholder.setOnClickListener(v -> showFoodDialog(null));
        foodAdapter.setContext(this);
        foodAdapter.setClickedListener((v, d) -> foodAdapter.set(d, v.getBindingAdapterPosition()));
    }

    private void showFoodDialog(RestaurantFoodVO data) {
        Intent intent = new Intent(this, EditRestaurantFoodActivity.class);
        intent.putExtra(EditConsumeRecordActivity.DATA, data);
        startActivityForResult(intent, EditRestaurantFoodActivity.CODE);
        overridePendingTransition(R.anim.push_down_in, 0);
    }

    private void addFood(RestaurantFoodVO item){
        foodAdapter.add(item);
        consumeFoodPlaceholder.setVisibility(View.GONE);
    }

    private RestaurantFoodVO removeFood(int index){
        RestaurantFoodVO item = foodAdapter.getDataAt(index);
        foodAdapter.removeAt(index);
        if (foodAdapter.isEmpty()){
            consumeFoodPlaceholder.setVisibility(View.VISIBLE);
        }
        return item;
    }

    private void onCancel(View view){
        finish();
    }

    private void onConfirm(View view){
        String totalCost = editTotalCost.getText().toString();
        if (!NumberUtils.isParsable(totalCost)){
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
        if (src == null){
            setConsumeTime(System.currentTimeMillis());
            addressSpinner.setSelection(0);
            editTotalCost.setText("0.0");
            return;
        }
        setConsumeTime(src.getConsumeTime());
        List<String> eaters = src.getEaters();
        if (CollectionUtils.isNotEmpty(eaters)){
            editFriends.setText(String.join(EATER_DELIMITER, eaters));
        }
        addressSpinner.setSelection(addressList.indexOf(src.getAddress()));
        editComment.setText(src.getComment());
        editTotalCost.setText(String.valueOf(src.getTotalCost()));
        foodAdapter.setData(src.getFoods());
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

    private void setAddress(List<Address> addressList){
        if (CollectionUtils.isEmpty(addressList)){
            return;
        }
        if (addressList.size() == 1){
            Address address = addressList.get(0);
            addressText.setText(address.buildSimpleAddress());
            addressText.setVisibility(View.VISIBLE);
            addressSpinner.setVisibility(View.GONE);
        } else {
            List<String> addressSelections = addressList.stream().map(Address::buildSimpleAddress).collect(Collectors.toList());
            addressSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, addressSelections));
            addressText.setVisibility(View.GONE);
            addressSpinner.setVisibility(View.VISIBLE);
        }
    }

    private ConsumeRecordVO buildData(){
        ConsumeRecordVO dst = new ConsumeRecordVO();
        dst.setConsumeTime(consumeTime);
        dst.setAddress(addressList.size() == 1 ? addressList.get(0) : (Address) addressSpinner.getSelectedItem());
        dst.setEaters(Arrays.stream(editFriends.getText().toString().trim().split(EATER_DELIMITER)).filter(StringUtils::isNoneBlank).collect(Collectors.toList()));
        String totalCost = editTotalCost.getText().toString();
        if (NumberUtils.isParsable(totalCost)){
            dst.setTotalCost(Double.parseDouble(totalCost));
        }
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
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_down_out, 0);
    }

    @Override
    public void requestDrag(CustomAdapter<ConsumeRecordVO>.CustomViewHolder viewHolder) {
        dragHelper.startDrag(viewHolder);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        RestaurantFoodVO food = data.getParcelableExtra(EditRestaurantFoodActivity.DATA);
        int index = foodAdapter.indexOf(f -> f.getName().equals(food.getName()));
        if (index == -1){
            addFood(food);
        } else {
            foodAdapter.set(food, index);
        }
    }
}
