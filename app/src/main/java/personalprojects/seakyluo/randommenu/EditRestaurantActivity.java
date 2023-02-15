package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.apache.commons.collections.CollectionUtils;

import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.adapters.impl.AddressAdapter;
import personalprojects.seakyluo.randommenu.adapters.impl.ConsumeRecordAdapter;
import personalprojects.seakyluo.randommenu.dialogs.AddressDialog;
import personalprojects.seakyluo.randommenu.helpers.DragDropCallback;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.models.Address;
import personalprojects.seakyluo.randommenu.models.FoodType;
import personalprojects.seakyluo.randommenu.models.vo.ConsumeRecordVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;
import personalprojects.seakyluo.randommenu.utils.SwipeToDeleteUtils;

public class EditRestaurantActivity extends SwipeBackActivity implements DragDropCallback.DragStartListener<Address> {
    public static int CODE = 1;
    public static final String DATA = "RESTAURANT", IS_DRAFT = "IsDraft";
    private boolean isDraft;
    private EditText editName, editComment, editLink;
    private View addressPlaceholder, consumeRecordPlaceholder;
    private AddressAdapter addressAdapter;
    private ConsumeRecordAdapter consumeRecordAdapter;
    private AutoCompleteTextView editFoodType;
    private ItemTouchHelper dragHelper;

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

        RestaurantVO restaurant;
        if (savedInstanceState == null){
            restaurant = getIntent().getParcelableExtra(DATA);
            isDraft = getIntent().getBooleanExtra(IS_DRAFT, false);
        } else {
            restaurant = savedInstanceState.getParcelable(DATA);
        }
        dragHelper = new ItemTouchHelper(new DragDropCallback<>(addressAdapter));
        dragHelper.attachToRecyclerView(addressRecyclerView);
        SwipeToDeleteUtils.apply(addressRecyclerView, this, this::removeAddress, this::addAddress, Address::getAddress);
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
        consumeRecordAdapter.setOnClickListener((vh, data) -> showEditConsumeRecordActivity(data));
        consumeRecordPlaceholder.setOnClickListener(v -> showEditConsumeRecordActivity(null));
    }

    private void onCancel(View view){
        finish();
    }

    private void onConfirm(View view){
        finishWithData(buildData());
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
        consumeRecordAdapter.setData(src.getRecords());
        if (CollectionUtils.isEmpty(src.getRecords())){
            consumeRecordPlaceholder.setVisibility(View.VISIBLE);
        } else {
            consumeRecordPlaceholder.setVisibility(View.GONE);
        }
    }

    private RestaurantVO buildData(){
        RestaurantVO i = new RestaurantVO();
        i.setName(editName.getText().toString());
        i.setAddressList(addressAdapter.getData());
        String foodTypeName = editFoodType.getText().toString();
        i.setFoodTypeName(foodTypeName);
        i.setFoodTypeCode(FoodType.getCodeByName(foodTypeName));
        i.setComment(editComment.getText().toString());
        i.setLink(editLink.getText().toString());
        i.setRecords(consumeRecordAdapter.getData());
        return i;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DATA, buildData());
    }

    @Override
    public void finish() {
        Helper.save();
        super.finish();
        overridePendingTransition(R.anim.push_down_out, 0);
    }

    private void finishWithData(RestaurantVO data){
        Intent intent = new Intent();
        intent.putExtra(DATA, data);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void requestDrag(CustomAdapter<Address>.CustomViewHolder viewHolder) {
        dragHelper.startDrag(viewHolder);
    }
}
