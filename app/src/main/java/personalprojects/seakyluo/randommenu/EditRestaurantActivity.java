package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.os.Parcelable;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.stream.Collectors;

import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.adapters.impl.AddressAdapter;
import personalprojects.seakyluo.randommenu.adapters.impl.ConsumeRecordAdapter;
import personalprojects.seakyluo.randommenu.database.services.RestaurantDaoService;
import personalprojects.seakyluo.randommenu.dialogs.AddressDialog;
import personalprojects.seakyluo.randommenu.helpers.DragDropCallback;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.models.Address;
import personalprojects.seakyluo.randommenu.models.FoodType;
import personalprojects.seakyluo.randommenu.models.vo.ConsumeRecordVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;
import personalprojects.seakyluo.randommenu.utils.SwipeToDeleteUtils;

public class EditRestaurantActivity extends AppCompatActivity implements DragDropCallback.DragStartListener<Address> {
    public static int CODE = 1;
    public static final String DATA = "RESTAURANT", IS_DRAFT = "IsDraft";
    private boolean isDraft;
    private EditText editName, editComment, editLink;
    private View addressPlaceholder, consumeRecordPlaceholder;
    private AddressAdapter addressAdapter;
    private ConsumeRecordAdapter consumeRecordAdapter;
    private AutoCompleteTextView editFoodType;
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
        RecyclerView consumeRecordRecyclerView = findViewById(R.id.consume_record_recycler_view);
        addressAdapter = new AddressAdapter();
        consumeRecordAdapter = new ConsumeRecordAdapter();
        ImageButton addAddressButton = findViewById(R.id.add_address_button);
        ImageButton addConsumeRecordButton = findViewById(R.id.add_consume_record_button);

        if (savedInstanceState == null){
            restaurant = getIntent().getParcelableExtra(DATA);
            isDraft = getIntent().getBooleanExtra(IS_DRAFT, false);
        } else {
            restaurant = savedInstanceState.getParcelable(DATA);
        }
        dragHelper = new ItemTouchHelper(new DragDropCallback<>(addressAdapter));
        dragHelper.attachToRecyclerView(addressRecyclerView);
        SwipeToDeleteUtils.apply(addressRecyclerView, this, this::removeAddress, this::addAddress, Address::getAddress);
        SwipeToDeleteUtils.apply(consumeRecordRecyclerView, this, this::removeConsumeRecord, this::addConsumeRecord, r -> r.formatConsumeTime() + "的记录");
        addressAdapter.setContext(this);
        addressAdapter.setDragStartListener(this);
        addressAdapter.setClickedListener((viewHolder, data) -> addressAdapter.set(data, viewHolder.getAdapterPosition()));
        addressRecyclerView.setAdapter(addressAdapter);
        consumeRecordRecyclerView.setAdapter(consumeRecordAdapter);

        setData(restaurant);
        cancelButton.setOnClickListener(this::onCancel);
        confirmButton.setOnClickListener(this::onConfirm);
        addressPlaceholder.setOnClickListener(this::showAddressDialog);
        addAddressButton.setOnClickListener(this::showAddressDialog);
        addConsumeRecordButton.setOnClickListener(v -> showEditConsumeRecordActivity(null));
        consumeRecordAdapter.setContext(this);
        consumeRecordAdapter.setOnClickListener((vh, data) -> showEditConsumeRecordActivity(data));
        consumeRecordPlaceholder.setOnClickListener(v -> showEditConsumeRecordActivity(null));
    }

    private void onCancel(View view){
        finish();
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
        if (CollectionUtils.isEmpty(addressAdapter.getData())){
            Toast.makeText(this, "地址不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        finishWithData(buildData());
    }

    private RestaurantVO buildData(){
        RestaurantVO i = new RestaurantVO();
        i.setId(restaurant == null ? 0 : restaurant.getId());
        i.setName(editName.getText().toString().trim());
        i.setAddressList(addressAdapter.getData());
        String foodTypeName = editFoodType.getText().toString().trim();
        i.setFoodTypeName(foodTypeName);
        i.setFoodTypeCode(FoodType.getCodeByName(foodTypeName));
        i.setComment(editComment.getText().toString().trim());
        i.setLink(editLink.getText().toString().trim());
        i.setRecords(consumeRecordAdapter.getData());
        return i;
    }

    private void showAddressDialog(View view) {
        AddressDialog dialog = new AddressDialog();
        dialog.setConfirmListener(this::addAddress);
        dialog.showNow(getSupportFragmentManager(), AddressDialog.TAG);
    }

    private void addAddress(Address address) {
        addressAdapter.add(address);
        addressPlaceholder.setVisibility(View.GONE);
    }

    private Address removeAddress(int index) {
        Address item = addressAdapter.getDataAt(index);
        addressAdapter.removeAt(index);
        if (addressAdapter.isEmpty()){
            addressPlaceholder.setVisibility(View.VISIBLE);
        }
        return item;
    }

    private void showEditConsumeRecordActivity(ConsumeRecordVO data){
        if (addressAdapter.isEmpty()){
            Toast.makeText(this, "请先添加地址！", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, EditConsumeRecordActivity.class);
        intent.putExtra(EditConsumeRecordActivity.DATA, data);
        intent.putExtra(EditConsumeRecordActivity.ADDRESS_LIST, addressAdapter.getData());
        startActivityForResult(intent, EditConsumeRecordActivity.CODE);
        overridePendingTransition(R.anim.push_down_in, 0);
    }

    private void setData(RestaurantVO src){
        if (src == null){
            return;
        }
        editName.setText(src.getName());
        addressAdapter.setData(src.getAddressList());
        if (CollectionUtils.isEmpty(src.getAddressList())){
            addressPlaceholder.setVisibility(View.VISIBLE);
        } else {
            addressPlaceholder.setVisibility(View.GONE);
        }
        editFoodType.setText(src.getFoodTypeName());
        editComment.setText(src.getComment());
        editLink.setText(src.getLink());
        consumeRecordAdapter.setData(src.getRecords().stream()
                .sorted(Comparator.comparing(ConsumeRecordVO::getConsumeTime).reversed())
                .collect(Collectors.toList()));
        if (CollectionUtils.isEmpty(src.getRecords())){
            consumeRecordPlaceholder.setVisibility(View.VISIBLE);
        } else {
            consumeRecordPlaceholder.setVisibility(View.GONE);
        }
    }

    private void addConsumeRecord(ConsumeRecordVO record){
        consumeRecordAdapter.add(record, 0);
        consumeRecordPlaceholder.setVisibility(View.GONE);
        resetRecordIndex(0);
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
        return item;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DATA, buildData());
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_down_out, 0);
    }

    private void finishWithData(RestaurantVO data){
        RestaurantDaoService.save(data);
        Intent intent = new Intent();
        intent.putExtra(DATA, data);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void requestDrag(CustomAdapter<Address>.CustomViewHolder viewHolder) {
        dragHelper.startDrag(viewHolder);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        ConsumeRecordVO record = data.getParcelableExtra(EditConsumeRecordActivity.DATA);
        if (record.getIndex() == -1){
            addConsumeRecord(record);
        } else {
            updateConsumeRecord(record);
        }
    }

    private void resetRecordIndex(int start){
        for (int i = start; i < consumeRecordAdapter.getItemCount(); i++){
            consumeRecordAdapter.getDataAt(i).setIndex(i);
        }
    }
}
