package personalprojects.seakyluo.randommenu;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.ImageButton;

import personalprojects.seakyluo.randommenu.helpers.Helper;
import personalprojects.seakyluo.randommenu.models.vo.RestaurantVO;

public class EditRestaurantActivity extends SwipeBackActivity {
    public static int CODE = 1;
    public static final String DATA = "RESTAURANT", IS_DRAFT = "IsDraft";
    private RestaurantVO currentRestaurant;
    private boolean isDraft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_restaurant);
        ImageButton cancelButton = findViewById(R.id.cancel_button);
        ImageButton confirmButton = findViewById(R.id.confirm_button);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null){
            currentRestaurant = getIntent().getParcelableExtra(DATA);
            isDraft = getIntent().getBooleanExtra(IS_DRAFT, false);
        } else {
            currentRestaurant = savedInstanceState.getParcelable(DATA);
        }

        cancelButton.setOnClickListener(v -> onCancel());
        confirmButton.setOnClickListener(v -> onConfirm());
    }

    private void onCancel(){
        finish();
    }

    private void onConfirm(){
        finish();
    }

    @Override
    public void finish() {
        Helper.save();
        super.finish();
    }
}
