package personalprojects.seakyluo.randommenu.activities.impl;

import android.os.Bundle;
import android.widget.TextView;

import personalprojects.seakyluo.randommenu.R;
import personalprojects.seakyluo.randommenu.activities.SwipeBackActivity;
import personalprojects.seakyluo.randommenu.database.services.SelfFoodDaoService;
import personalprojects.seakyluo.randommenu.dialogs.AskYesNoDialog;
import personalprojects.seakyluo.randommenu.dialogs.FoodCardBottomDialog;
import personalprojects.seakyluo.randommenu.fragments.FoodListFragment;
import personalprojects.seakyluo.randommenu.models.Tag;
import personalprojects.seakyluo.randommenu.services.SelfMadeFoodService;

public class MyFavoritesActivity extends SwipeBackActivity {
    public static final int REQUEST_CODE = 10;
    private TextView title;
    private FoodListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorites);
        findViewById(R.id.back_button).setOnClickListener(v -> finish());

        title = findViewById(R.id.title_text_view);
        setTitle();

        fragment = (FoodListFragment) getSupportFragmentManager().findFragmentById(R.id.food_list_fragment);
        fragment.setData(SelfMadeFoodService.getFavoriteFoods());
        fragment.setRemovable(true);
        fragment.setShowLikeImage(false);
        fragment.setFoodClickedListener((viewHolder, food) -> {
            FoodCardBottomDialog dialog = new FoodCardBottomDialog();
            dialog.setSelfFoodId(food.getId());
            dialog.setFoodLikedListener(after -> {
                fragment.removeFood(after);
                setTitle();
            });
            dialog.showNow(getSupportFragmentManager(), AskYesNoDialog.TAG);
        });
        fragment.setFoodRemovedListener(data -> {
            data.setFavorite(false);
            SelfMadeFoodService.updateFood(data);
            setTitle();
        });
        fragment.setFoodAddedListener(data -> {
            data.setFavorite(true);
            SelfMadeFoodService.updateFood(data);
            setTitle();
        });
    }

    private void setTitle() { title.setText(Tag.format(this, R.string.my_favorites, SelfFoodDaoService.countFavoriteFoods())); }
}
