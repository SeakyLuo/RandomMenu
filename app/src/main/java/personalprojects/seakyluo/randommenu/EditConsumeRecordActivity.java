package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.adapters.impl.ConsumeFoodAdapter;
import personalprojects.seakyluo.randommenu.helpers.DragDropCallback;
import personalprojects.seakyluo.randommenu.models.Address;
import personalprojects.seakyluo.randommenu.models.vo.ConsumeRecordVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantFoodVO;
import personalprojects.seakyluo.randommenu.utils.SwipeToDeleteUtils;

public class EditConsumeRecordActivity extends SwipeBackActivity implements DragDropCallback.DragStartListener<ConsumeRecordVO> {
    public static int CODE = 1;
    public static final String DATA = "CONSUME_RECORD", ADDRESS_LIST = "ADDRESS_LIST";
    private static final String EATER_DELIMITER = "，";
    private Long consumeTime;
    private TextView consumeTimeText;
    private Spinner addressSpinner;
    private EditText editFriends;
    private View consumeFoodPlaceholder;
    private ConsumeFoodAdapter foodAdapter;
    private ItemTouchHelper dragHelper;
    private List<Address> addressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_consume_record);

        ImageButton cancelButton = findViewById(R.id.cancel_button);
        ImageButton confirmButton = findViewById(R.id.confirm_button);
        consumeTimeText = findViewById(R.id.consume_time_text);
        // TODO 只有1个地址的时候不用Spinner直接TextView即可
        addressSpinner = findViewById(R.id.address_spinner);
        editFriends = findViewById(R.id.edit_friends);
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
            addressList = savedInstanceState.getParcelable(ADDRESS_LIST);
        }

        List<String> addressSelections = Optional.ofNullable(addressList).orElse(new ArrayList<>()).stream().map(Address::getAddress).collect(Collectors.toList());
        addressSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, addressSelections));
        setData(data);
        dragHelper = new ItemTouchHelper(new DragDropCallback<>(foodAdapter));
        dragHelper.attachToRecyclerView(consumeRecordRecyclerView);
        SwipeToDeleteUtils.apply(consumeRecordRecyclerView, this, this::removeFood, this::addFood, RestaurantFoodVO::getName);
        consumeRecordRecyclerView.setAdapter(foodAdapter);
        cancelButton.setOnClickListener(this::onCancel);
        confirmButton.setOnClickListener(this::onConfirm);
        consumeTimeText.setOnClickListener(v -> {
            // TODO
        });
        addConsumeFoodButton.setOnClickListener(v -> {
            // TODO
        });
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
        finishWithData(buildData());
    }

    private void setData(ConsumeRecordVO src){
        if (src == null){
            addressSpinner.setSelection(0);
            return;
        }
        consumeTime = src.getConsumeTime();
        if (consumeTime != null){
            consumeTimeText.setText(DateFormatUtils.format(consumeTime, "yyyy-MM-dd HH:mm"));
        }
        List<String> eaters = src.getEaters();
        if (CollectionUtils.isNotEmpty(eaters)){
            editFriends.setText(String.join(EATER_DELIMITER, eaters));
        }
        addressSpinner.setSelection(addressList.indexOf(src.getAddress()));
        foodAdapter.setData(src.getFoods());
        if (CollectionUtils.isEmpty(src.getFoods())){
            consumeFoodPlaceholder.setVisibility(View.VISIBLE);
        } else {
            consumeFoodPlaceholder.setVisibility(View.GONE);
        }
    }

    private ConsumeRecordVO buildData(){
        ConsumeRecordVO dst = new ConsumeRecordVO();
        dst.setConsumeTime(consumeTime);
        dst.setAddress((Address) addressSpinner.getSelectedItem());
        dst.setEaters(Arrays.asList(editFriends.getText().toString().split(EATER_DELIMITER)));
        dst.setFoods(foodAdapter.getData());
        return dst;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DATA, buildData());
        outState.putParcelable(ADDRESS_LIST, buildData());
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
}
