package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;

import org.apache.commons.collections.CollectionUtils;

import lombok.NonNull;
import personalprojects.seakyluo.randommenu.adapters.CustomAdapter;
import personalprojects.seakyluo.randommenu.adapters.impl.AddressAdapter;
import personalprojects.seakyluo.randommenu.adapters.impl.ConsumeRecordAdapter;
import personalprojects.seakyluo.randommenu.dialogs.AddressDialog;
import personalprojects.seakyluo.randommenu.helpers.DragDropCallback;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.helpers.SwipeToDeleteCallback;
import personalprojects.seakyluo.randommenu.interfaces.AddressOperateListener;
import personalprojects.seakyluo.randommenu.models.Address;
import personalprojects.seakyluo.randommenu.models.FoodType;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;

public class EditRestaurantActivity extends SwipeBackActivity implements DragDropCallback.StartDragListener<Address>, AddressOperateListener {
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

        FragmentManager fragmentManager = getSupportFragmentManager();
        RestaurantVO restaurant;
        if (savedInstanceState == null){
            restaurant = getIntent().getParcelableExtra(DATA);
            isDraft = getIntent().getBooleanExtra(IS_DRAFT, false);
        } else {
            restaurant = savedInstanceState.getParcelable(DATA);
        }
        dragHelper = new ItemTouchHelper(new DragDropCallback<>(addressAdapter));
        dragHelper.attachToRecyclerView(addressRecyclerView);
        enableSwipeToDeleteAndUndo(addressRecyclerView);

        addressAdapter.setContext(this);
        addressAdapter.setStartDragListener(this);
        addressAdapter.setAddressOperateListener(this);
        addressRecyclerView.setAdapter(addressAdapter);
        consumeRecordRecyclerView.setAdapter(consumeRecordAdapter);

        setRestaurant(restaurant);
        cancelButton.setOnClickListener(this::onCancel);
        confirmButton.setOnClickListener(this::onConfirm);
        addressPlaceholder.setOnClickListener(this::showAddressDialog);
        addAddressButton.setOnClickListener(this::showAddressDialog);
    }

    private void enableSwipeToDeleteAndUndo(RecyclerView recyclerView) {
        new ItemTouchHelper(new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int position = viewHolder.getAdapterPosition();
                Address item = removeAddress(position);
                Snackbar snackbar = Snackbar.make(recyclerView, String.format("地址\"%s\"已被删除", item.getAddress()), Snackbar.LENGTH_LONG);
                snackbar.setAction("撤销", view -> {
                    addAddress(item);
                    recyclerView.scrollToPosition(position);
                });
                snackbar.show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void onCancel(View view){
        finish();
    }

    private void onConfirm(View view){
        finishWithRestaurant(buildRestaurant());
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

    private void setRestaurant(RestaurantVO r){
        if (r == null){
            return;
        }
        editName.setText(r.getName());
        addressAdapter.setData(r.getAddressList());
        if (CollectionUtils.isEmpty(r.getAddressList())){
            addressPlaceholder.setVisibility(View.VISIBLE);
        } else {
            addressPlaceholder.setVisibility(View.GONE);
        }
        editFoodType.setText(r.getFoodTypeName());
        editComment.setText(r.getComment());
        editLink.setText(r.getLink());
        consumeRecordAdapter.setData(r.getRecords());
        if (CollectionUtils.isEmpty(r.getRecords())){
            consumeRecordPlaceholder.setVisibility(View.VISIBLE);
        } else {
            consumeRecordPlaceholder.setVisibility(View.GONE);
        }
    }

    private RestaurantVO buildRestaurant(){
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
        outState.putParcelable(DATA, buildRestaurant());
    }

    @Override
    public void finish() {
        Helper.save();
        super.finish();
    }

    private void finishWithRestaurant(RestaurantVO data){
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
    public void edited(Address address, int index) {
        addressAdapter.set(address, index);
    }
}
