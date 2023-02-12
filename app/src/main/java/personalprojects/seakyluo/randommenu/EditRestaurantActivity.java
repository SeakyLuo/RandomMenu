package personalprojects.seakyluo.randommenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.apache.commons.collections.CollectionUtils;

import personalprojects.seakyluo.randommenu.adapters.AddressAdapter;
import personalprojects.seakyluo.randommenu.adapters.ConsumeRecordAdapter;
import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.models.FoodType;
import personalprojects.seakyluo.randommenu.models.vo.ConsumeRecordVO;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;

public class EditRestaurantActivity extends SwipeBackActivity {
    public static int CODE = 1;
    public static final String DATA = "RESTAURANT", IS_DRAFT = "IsDraft";
    private boolean isDraft;
    private EditText editName, editComment, editLink;
    private TextView addressPlaceholder, consumeRecordPlaceholder;
    private AddressAdapter addressAdapter;
    private ConsumeRecordAdapter consumeRecordAdapter;


    private AutoCompleteTextView editFoodType;

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

        FragmentManager fragmentManager = getSupportFragmentManager();
        RestaurantVO restaurant;
        if (savedInstanceState == null){
            restaurant = getIntent().getParcelableExtra(DATA);
            isDraft = getIntent().getBooleanExtra(IS_DRAFT, false);
        } else {
            restaurant = savedInstanceState.getParcelable(DATA);
        }
        addressRecyclerView.setAdapter(addressAdapter);
        consumeRecordRecyclerView.setAdapter(consumeRecordAdapter);

        setRestaurant(restaurant);
        cancelButton.setOnClickListener(v -> onCancel());
        confirmButton.setOnClickListener(v -> onConfirm());
    }

    private void onCancel(){
        finish();
    }

    private void onConfirm(){
        finishWithRestaurant(buildRestaurant());
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
            addressPlaceholder.setVisibility(View.INVISIBLE);
        }
        editFoodType.setText(r.getFoodTypeName());
        editComment.setText(r.getComment());
        editLink.setText(r.getLink());
        consumeRecordAdapter.setData(r.getRecords());
        if (CollectionUtils.isEmpty(r.getRecords())){
            consumeRecordPlaceholder.setVisibility(View.VISIBLE);
        } else {
            consumeRecordPlaceholder.setVisibility(View.INVISIBLE);
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
}
